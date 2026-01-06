package com.ga3t.nytrisync.ui.notifications
import android.content.Context
import com.ga3t.nytrisync.NytriSync
import com.google.gson.Gson
data class NotificationsConfig(
    val breakfastEnabled: Boolean = false,
    val breakfastTimes: List<String> = emptyList(),
    val lunchEnabled: Boolean = false,
    val lunchTimes: List<String> = emptyList(),
    val dinnerEnabled: Boolean = false,
    val dinnerTimes: List<String> = emptyList(),
    val snackEnabled: Boolean = false,
    val snackMode: String = "ON_TIME",
    val snackTimes: List<String> = emptyList(),
    val snackIntervals: List<Int> = emptyList(),
    val waterEnabled: Boolean = false,
    val waterMode: String = "ON_TIME",
    val waterTimes: List<String> = emptyList(),
    val waterIntervals: List<Int> = emptyList()
)
object NotificationsPrefs {
    private const val PREFS = "notif_prefs"
    private const val KEY = "config"
    private val gson = Gson()
    private fun prefs() =
        NytriSync.instance.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    fun load(): NotificationsConfig? {
        val s = prefs().getString(KEY, null) ?: return null
        val cfg = runCatching { gson.fromJson(s, NotificationsConfig::class.java) }.getOrNull() ?: return null
        val fixedWaterMode = if (cfg.waterMode == "BY_TIMES") "ON_TIME" else cfg.waterMode
        return cfg.copy(waterMode = fixedWaterMode)
    }
    fun save(cfg: NotificationsConfig) {
        prefs().edit().putString(KEY, gson.toJson(cfg)).apply()
    }
}