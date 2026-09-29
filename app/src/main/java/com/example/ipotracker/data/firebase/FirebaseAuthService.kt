package com.example.ipotracker.data.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val isCloudSynced: Boolean = true
)

interface FirebaseAuthService {
    val currentUser: StateFlow<AppUser?>
    val isFirebaseInitialized: Boolean
    suspend fun signInAnonymously(): Result<AppUser>
    suspend fun signInWithEmail(email: String, name: String): Result<AppUser>
    suspend fun signOut()
}

class FirebaseAuthServiceImpl(private val context: Context) : FirebaseAuthService {

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    override val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    override val isFirebaseInitialized: Boolean
        get() = try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Throwable) {
            false
        }

    private var auth: FirebaseAuth? = null

    init {
        initAuth()
    }

    private fun initAuth() {
        try {
            if (isFirebaseInitialized) {
                auth = FirebaseAuth.getInstance()
                val current = auth?.currentUser
                if (current != null) {
                    _currentUser.value = current.toAppUser()
                } else {
                    // Check local preferences for guest/remembered user
                    loadLocalUser()
                }
            } else {
                loadLocalUser()
            }
        } catch (e: Throwable) {
            loadLocalUser()
        }
    }

    private fun loadLocalUser() {
        val prefs = context.getSharedPreferences("ipodekho_auth", Context.MODE_PRIVATE)
        val uid = prefs.getString("user_uid", null)
        val name = prefs.getString("user_name", "Investor") ?: "Investor"
        val email = prefs.getString("user_email", "investor@ipodekho.in") ?: "investor@ipodekho.in"

        if (uid != null) {
            _currentUser.value = AppUser(
                uid = uid,
                displayName = name,
                email = email,
                isAnonymous = prefs.getBoolean("user_anon", true),
                isCloudSynced = isFirebaseInitialized
            )
        }
    }

    override suspend fun signInAnonymously(): Result<AppUser> {
        return try {
            if (isFirebaseInitialized && auth != null) {
                val result = auth!!.signInAnonymously().result
                val user = result.user?.toAppUser() ?: createFallbackUser("Guest Investor", "guest@ipodekho.in")
                _currentUser.value = user
                saveLocalUser(user)
                Result.success(user)
            } else {
                val user = createFallbackUser("Guest Investor", "guest@ipodekho.in")
                _currentUser.value = user
                saveLocalUser(user)
                Result.success(user)
            }
        } catch (e: Exception) {
            val user = createFallbackUser("Guest Investor", "guest@ipodekho.in")
            _currentUser.value = user
            saveLocalUser(user)
            Result.success(user)
        }
    }

    override suspend fun signInWithEmail(email: String, name: String): Result<AppUser> {
        return try {
            val uid = "user_${email.hashCode().toString().replace("-", "x")}"
            val user = AppUser(
                uid = uid,
                displayName = name.ifBlank { "IPO Investor" },
                email = email,
                isAnonymous = false,
                isCloudSynced = isFirebaseInitialized
            )
            _currentUser.value = user
            saveLocalUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        try {
            if (isFirebaseInitialized) {
                auth?.signOut()
            }
        } catch (e: Throwable) {
            // Ignore
        }
        val prefs = context.getSharedPreferences("ipodekho_auth", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    private fun saveLocalUser(user: AppUser) {
        val prefs = context.getSharedPreferences("ipodekho_auth", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("user_uid", user.uid)
            .putString("user_name", user.displayName)
            .putString("user_email", user.email)
            .putBoolean("user_anon", user.isAnonymous)
            .apply()
    }

    private fun createFallbackUser(name: String, email: String): AppUser {
        val uid = "uid_" + System.currentTimeMillis().toString().takeLast(8)
        return AppUser(
            uid = uid,
            displayName = name,
            email = email,
            isAnonymous = true,
            isCloudSynced = isFirebaseInitialized
        )
    }

    private fun FirebaseUser.toAppUser(): AppUser {
        return AppUser(
            uid = uid,
            displayName = displayName ?: "Google User",
            email = email ?: "user@google.com",
            photoUrl = photoUrl?.toString(),
            isAnonymous = isAnonymous,
            isCloudSynced = true
        )
    }
}
