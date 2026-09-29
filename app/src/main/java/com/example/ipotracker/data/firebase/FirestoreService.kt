package com.example.ipotracker.data.firebase

import android.content.Context
import com.example.ipotracker.data.remote.gemini.ChatMessage
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class FirestoreDematProfile(
    val applicantName: String = "",
    val panNumber: String = "",
    val depositoryType: String = "CDSL",
    val addBankDetails: Boolean = false,
    val bankName: String = "",
    val bankAccountNumber: String = "",
    val upiId: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

interface FirestoreService {
    val isFirestoreAvailable: Boolean
    suspend fun saveWatchlistIpo(userId: String, ipoId: String, isWatched: Boolean)
    suspend fun saveDematProfile(userId: String, profile: FirestoreDematProfile): Result<Unit>
    suspend fun getDematProfile(userId: String): Result<FirestoreDematProfile?>
    suspend fun persistChatMessage(userId: String, message: ChatMessage)
}

class FirestoreServiceImpl(private val context: Context) : FirestoreService {

    override val isFirestoreAvailable: Boolean
        get() = try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Throwable) {
            false
        }

    private val firestore: FirebaseFirestore?
        get() = if (isFirestoreAvailable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Throwable) {
                null
            }
        } else {
            null
        }

    override suspend fun saveWatchlistIpo(userId: String, ipoId: String, isWatched: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                val db = firestore
                if (db != null) {
                    val docRef = db.collection("users").document(userId).collection("watchlist").document(ipoId)
                    if (isWatched) {
                        val data = mapOf(
                            "ipoId" to ipoId,
                            "updatedAt" to System.currentTimeMillis()
                        )
                        docRef.set(data)
                    } else {
                        docRef.delete()
                    }
                } else {
                    saveWatchlistLocal(userId, ipoId, isWatched)
                }
            } catch (e: Throwable) {
                saveWatchlistLocal(userId, ipoId, isWatched)
            }
        }
    }

    override suspend fun saveDematProfile(userId: String, profile: FirestoreDematProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val db = firestore
            if (db != null) {
                val data = mapOf(
                    "applicantName" to profile.applicantName,
                    "panNumber" to profile.panNumber,
                    "depositoryType" to profile.depositoryType,
                    "addBankDetails" to profile.addBankDetails,
                    "bankName" to profile.bankName,
                    "bankAccountNumber" to profile.bankAccountNumber,
                    "upiId" to profile.upiId,
                    "updatedAt" to profile.updatedAt
                )
                db.collection("users").document(userId).collection("demat_profiles").document("primary").set(data)
            }
            // Also store in SharedPreferences for seamless offline sync
            val prefs = context.getSharedPreferences("ipodekho_demat_$userId", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("applicantName", profile.applicantName)
                .putString("panNumber", profile.panNumber)
                .putString("depositoryType", profile.depositoryType)
                .putBoolean("addBankDetails", profile.addBankDetails)
                .putString("bankName", profile.bankName)
                .putString("bankAccountNumber", profile.bankAccountNumber)
                .putString("upiId", profile.upiId)
                .putLong("updatedAt", profile.updatedAt)
                .apply()

            Result.success(Unit)
        } catch (e: Throwable) {
            Result.success(Unit)
        }
    }

    override suspend fun getDematProfile(userId: String): Result<FirestoreDematProfile?> = withContext(Dispatchers.IO) {
        try {
            val prefs = context.getSharedPreferences("ipodekho_demat_$userId", Context.MODE_PRIVATE)
            val name = prefs.getString("applicantName", null)
            if (name != null) {
                val profile = FirestoreDematProfile(
                    applicantName = name,
                    panNumber = prefs.getString("panNumber", "") ?: "",
                    depositoryType = prefs.getString("depositoryType", "CDSL") ?: "CDSL",
                    addBankDetails = prefs.getBoolean("addBankDetails", false),
                    bankName = prefs.getString("bankName", "") ?: "",
                    bankAccountNumber = prefs.getString("bankAccountNumber", "") ?: "",
                    upiId = prefs.getString("upiId", "") ?: "",
                    updatedAt = prefs.getLong("updatedAt", System.currentTimeMillis())
                )
                Result.success(profile)
            } else {
                Result.success(null)
            }
        } catch (e: Throwable) {
            Result.success(null)
        }
    }

    override suspend fun persistChatMessage(userId: String, message: ChatMessage) {
        withContext(Dispatchers.IO) {
            try {
                val db = firestore
                if (db != null) {
                    val data = mapOf(
                        "role" to message.role,
                        "text" to message.text,
                        "timestamp" to message.timestamp,
                        "modelTier" to (message.modelTier?.name ?: "DEFAULT")
                    )
                    db.collection("users").document(userId).collection("chat_history").document(message.id).set(data)
                }
            } catch (e: Throwable) {
                // Local fallback handled via ViewModel in-memory state
            }
        }
    }

    private fun saveWatchlistLocal(userId: String, ipoId: String, isWatched: Boolean) {
        val prefs = context.getSharedPreferences("ipodekho_cloud_watchlist_$userId", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("watched_ids", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if (isWatched) set.add(ipoId) else set.remove(ipoId)
        prefs.edit().putStringSet("watched_ids", set).apply()
    }
}
