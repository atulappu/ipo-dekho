package com.example.ipotracker

import android.app.Application
import com.example.ipotracker.di.AppContainer
import com.example.ipotracker.di.DefaultAppContainer

class IpoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
