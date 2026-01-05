package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WantedWeightScreen(
    currentKg: Float,
    wantedKg: Float,
    showWarning: Boolean,
    onWantedChange: (Float) -> Unit,
    onApplySuggestedGoal: () -> Unit,
    onNext: () -> Unit
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderBlock(
            title = {
                Text("Target weight", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
            },
            subtitle = {
                Text("Set the weight you aim for.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            },
            minHeightDp = 360
        )
        SheetBlock {
            if (showWarning) {
                ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Your goal doesn’t match the target weight.", color = MaterialTheme.colorScheme.onErrorContainer)
                        TextButton(onClick = onApplySuggestedGoal) { Text("Change goal?") }
                    }
                }
            }
            Text("${"%.1f".format(wantedKg)} kg", style = MaterialTheme.typography.headlineMedium)
            Slider(
                value = wantedKg,
                onValueChange = onWantedChange,
                valueRange = 30f..250f,
                steps = (250 - 30) * 2,
                modifier = Modifier.fillMaxWidth()
            )
            Box(Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    onClick = onNext,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) { Icon(Icons.Rounded.ArrowForward, contentDescription = "Next") }
            }
        }
    }
}