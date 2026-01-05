package com.ga3t.nytrisync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class NytriSync : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                "reminders",
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Meal and water reminders"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
    }

    companion object {
        lateinit var instance: NytriSync
            private set
    }
}