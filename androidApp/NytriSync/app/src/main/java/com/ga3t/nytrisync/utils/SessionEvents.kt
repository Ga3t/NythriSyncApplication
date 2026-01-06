package com.ga3t.nytrisync.utils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
object SessionEvents {
    private val _logout = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logout = _logout.asSharedFlow()
    fun fireLogout() {
        _logout.tryEmit(Unit)
    }
}