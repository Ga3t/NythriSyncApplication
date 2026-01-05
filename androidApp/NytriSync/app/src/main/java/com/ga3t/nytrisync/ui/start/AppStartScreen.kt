package com.ga3t.nytrisync.ui.start

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ga3t.nytrisync.data.local.TokenStorage

@Composable
fun AppStartScreen(
    goLogin: () -> Unit,
    goApp: () -> Unit
) {
    LaunchedEffect(Unit) {
        val jwt = TokenStorage.getJwt()
        if (jwt.isNullOrBlank()) goLogin() else goApp()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}