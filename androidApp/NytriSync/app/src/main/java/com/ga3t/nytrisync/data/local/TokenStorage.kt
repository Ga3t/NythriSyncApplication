package com.ga3t.nytrisync.data.local
import android.content.Context
import com.ga3t.nytrisync.NytriSync
object TokenStorage {
    private const val PREFS = "auth_prefs"
    private const val KEY_JWT = "jwt"
    private const val KEY_REFRESH = "refresh"
    private fun prefs() =
        NytriSync.instance.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    fun getJwt(): String? = prefs().getString(KEY_JWT, null)
    fun getRefresh(): String? = prefs().getString(KEY_REFRESH, null)
    fun save(jwt: String?, refresh: String?) {
        prefs().edit().apply {
            if (jwt != null) putString(KEY_JWT, jwt) else remove(KEY_JWT)
            if (refresh != null) putString(KEY_REFRESH, refresh) else remove(KEY_REFRESH)
        }.apply()
    }
    fun clear() = prefs().edit().clear().apply()
    fun isLoggedIn(): Boolean = !getJwt().isNullOrBlank()
}