package com.ga3t.nytrisync.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object ReminderScheduler {

    private fun flags(): Int =
        PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0

    private fun pi(context: Context, id: Int, title: String, text: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("text", text)
        }
        return PendingIntent.getBroadcast(context, id, intent, flags())
    }

    fun scheduleExact(context: Context, triggerAtMillis: Long, id: Int, title: String, text: String) {
        val am = context.getSystemService(AlarmManager::class.java)
        val pending = pi(context, id, title, text)
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
            am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            return
        }
        if (Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        }
    }

    fun cancel(context: Context, id: Int, title: String, text: String) {
        context.getSystemService(AlarmManager::class.java)
            .cancel(pi(context, id, title, text))
    }

    fun nextTimeMillis(h: Int, m: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
        }
        if (cal.timeInMillis <= System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR, 1)
        return cal.timeInMillis
    }

    private fun id(base: Int, index: Int) = base + index
    private fun parseHM(s: String): Pair<Int, Int> = s.substring(0, 2).toInt() to s.substring(3, 5).toInt()

    fun scheduleAll(context: Context, cfg: NotificationsConfig) {
        if (cfg.breakfastEnabled) cfg.breakfastTimes.forEachIndexed { i, t ->
            val (h, m) = parseHM(t)
            scheduleExact(context, nextTimeMillis(h, m), id(1000, i), "Breakfast", "Time to have breakfast")
        }
        if (cfg.lunchEnabled) cfg.lunchTimes.forEachIndexed { i, t ->
            val (h, m) = parseHM(t)
            scheduleExact(context, nextTimeMillis(h, m), id(2000, i), "Lunch", "Time to have lunch")
        }
        if (cfg.dinnerEnabled) cfg.dinnerTimes.forEachIndexed { i, t ->
            val (h, m) = parseHM(t)
            scheduleExact(context, nextTimeMillis(h, m), id(3000, i), "Dinner", "Time to have dinner")
        }
        if (cfg.snackEnabled) cfg.snackTimes.forEachIndexed { i, t ->
            val (h, m) = parseHM(t)
            scheduleExact(context, nextTimeMillis(h, m), id(4000, i), "Snack", "Time for a snack")
        }
        if (cfg.waterEnabled) {
            if (cfg.waterMode == "BY_TIMES") {
                cfg.waterTimes.forEachIndexed { i, t ->
                    val (h, m) = parseHM(t)
                    scheduleExact(context, nextTimeMillis(h, m), id(5000, i), "Water", "Drink water")
                }
            } else {
                val now = System.currentTimeMillis()
                cfg.waterIntervals.forEachIndexed { i, min ->
                    scheduleExact(context, now + min * 60_000L, id(6000, i), "Water", "Drink water")
                }
            }
        }
    }

    fun cancelAll(context: Context, cfg: NotificationsConfig) {
        cfg.breakfastTimes.forEachIndexed { i, _ -> cancel(context, id(1000, i), "Breakfast", "Time to have breakfast") }
        cfg.lunchTimes.forEachIndexed { i, _ -> cancel(context, id(2000, i), "Lunch", "Time to have lunch") }
        cfg.dinnerTimes.forEachIndexed { i, _ -> cancel(context, id(3000, i), "Dinner", "Time to have dinner") }
        cfg.snackTimes.forEachIndexed { i, _ -> cancel(context, id(4000, i), "Snack", "Time for a snack") }

        if (cfg.waterMode == "BY_TIMES") {
            cfg.waterTimes.forEachIndexed { i, _ -> cancel(context, id(5000, i), "Water", "Drink water") }
        } else {
            cfg.waterIntervals.forEachIndexed { i, _ -> cancel(context, id(6000, i), "Water", "Drink water") }
        }
    }
}