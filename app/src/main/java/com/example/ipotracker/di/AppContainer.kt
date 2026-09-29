package com.example.ipotracker.di

import android.content.Context
import com.example.ipotracker.data.local.AppDatabase
import com.example.ipotracker.data.remote.IpoApiService
import com.example.ipotracker.data.remote.IpoRemoteDataSource
import com.example.ipotracker.data.remote.IpoRemoteDataSourceImpl
import com.example.ipotracker.data.remote.RetrofitClient
import com.example.ipotracker.data.repository.IpoRepositoryImpl
import com.example.ipotracker.domain.repository.IpoRepository

interface AppContainer {
    val ipoApiService: IpoApiService
    val ipoRemoteDataSource: IpoRemoteDataSource
    val ipoRepository: IpoRepository
    val geminiService: com.example.ipotracker.data.remote.gemini.GeminiService
    val firebaseAuthService: com.example.ipotracker.data.firebase.FirebaseAuthService
    val firestoreService: com.example.ipotracker.data.firebase.FirestoreService
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val ipoApiService: IpoApiService by lazy {
        RetrofitClient.createIpoApiService()
    }

    override val ipoRemoteDataSource: IpoRemoteDataSource by lazy {
        IpoRemoteDataSourceImpl(apiService = ipoApiService)
    }

    override val ipoRepository: IpoRepository by lazy {
        IpoRepositoryImpl(
            remoteDataSource = ipoRemoteDataSource,
            watchlistDao = database.watchlistDao(),
            searchHistoryDao = database.searchHistoryDao()
        )
    }

    override val geminiService: com.example.ipotracker.data.remote.gemini.GeminiService by lazy {
        com.example.ipotracker.data.remote.gemini.GeminiServiceImpl()
    }

    override val firebaseAuthService: com.example.ipotracker.data.firebase.FirebaseAuthService by lazy {
        com.example.ipotracker.data.firebase.FirebaseAuthServiceImpl(context)
    }

    override val firestoreService: com.example.ipotracker.data.firebase.FirestoreService by lazy {
        com.example.ipotracker.data.firebase.FirestoreServiceImpl(context)
    }
}
