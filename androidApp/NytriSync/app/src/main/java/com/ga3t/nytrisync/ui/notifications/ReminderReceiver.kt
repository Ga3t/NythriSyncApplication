package com.ga3t.nytrisync.ui.notifications
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ga3t.nytrisync.R
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: "Reminder"
        val text = intent.getStringExtra("text") ?: ""
        val notification = NotificationCompat.Builder(context, "reminders")
            .setSmallIcon(R.drawable.normal_meal_icon)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
    }
}