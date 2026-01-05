package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HeaderBlock(
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    minHeightDp: Int = 320,
    backgroundGradient: Brush? = null
) {
    val defaultGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B0F14), Color(0xFF11151C))
    )
    val headerGradient = backgroundGradient ?: defaultGradient
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(minHeightDp.dp)
            .background(headerGradient)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.widthIn(max = 520.dp)
        ) {
            title()
            subtitle?.invoke()
        }
    }
}

@Composable
fun ColumnScope.SheetBlock(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp),
        tonalElevation = 2.dp,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}