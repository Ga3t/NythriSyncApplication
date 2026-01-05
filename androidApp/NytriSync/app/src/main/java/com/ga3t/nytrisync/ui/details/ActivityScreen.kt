package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ActivityScreen(
    label: String,
    onPrevLevel: () -> Unit,
    onNextLevel: () -> Unit,
    onNext: () -> Unit
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderBlock(
            title = {
                Text("Activity level", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
            },
            subtitle = {
                Text("Pick your daily activity.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            },
            minHeightDp = 320
        )
        SheetBlock {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledTonalButton(
                    onClick = onPrevLevel,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF66BB6A))
                ) { Icon(Icons.Rounded.ArrowBack, null) }
                Text(label, style = MaterialTheme.typography.titleLarge)
                FilledTonalButton(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF66BB6A))
                ) { Icon(Icons.Rounded.ArrowForward, null) }
            }
            Box(Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    onClick = onNext,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) { Icon(Icons.Rounded.ArrowForward, contentDescription = "Next") }
            }
        }
    }
}