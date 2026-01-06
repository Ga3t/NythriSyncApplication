package com.ga3t.nytrisync.ui.details
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
fun ActivityScreen(
    label: String,
    onPrevLevel: () -> Unit,
    onNextLevel: () -> Unit,
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
                Text("Activity level", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = Color.White)
            },
            subtitle = {
                Text("Pick your daily activity.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            },
            minHeightDp = 320,
            backgroundGradient = softGreenGradient
        )
        SheetBlock {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = label.replace("_", " ").lowercase().replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
                FilledTonalButton(
                    onClick = onPrevLevel,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF66BB6A)),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) { Icon(Icons.Rounded.ArrowBack, null) }
                FilledTonalButton(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF66BB6A)),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) { Icon(Icons.Rounded.ArrowForward, null) }
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