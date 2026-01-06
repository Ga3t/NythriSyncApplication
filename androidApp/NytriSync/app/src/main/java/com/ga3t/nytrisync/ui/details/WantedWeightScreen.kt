package com.ga3t.nytrisync.ui.details
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    val softGreenGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF81C784),
            Color(0xFF66BB6A)
        )
    )
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderBlock(
            title = {
                Text("Target weight", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = Color.White)
            },
            subtitle = {
                Text("Set the weight you aim for.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            },
            minHeightDp = 360,
            backgroundGradient = softGreenGradient
        )
        SheetBlock {
            if (showWarning) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Your goal doesn't match the target weight.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                        )
                        TextButton(
                            onClick = onApplySuggestedGoal,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Change goal?", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            ElevatedCard(
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${"%.1f".format(wantedKg)} kg",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            RulerSlider(
                value = wantedKg,
                onValueChange = onWantedChange,
                valueRange = 30f..250f,
                step = 0.5f,
                majorEvery = 10f,
                mediumEvery = 5f,
                unitLabel = "kg"
            )
            if (wantedKg < currentKg) {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "The target is slightly smaller than your current weight.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Box(Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    onClick = onNext,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    containerColor = Color(0xFF66BB6A)
                ) { Icon(Icons.Rounded.ArrowForward, contentDescription = "Next") }
            }
        }
    }
}