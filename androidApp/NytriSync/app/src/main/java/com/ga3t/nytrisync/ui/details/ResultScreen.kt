package com.ga3t.nytrisync.ui.details
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ga3t.nytrisync.data.model.UserDetailsResponse
import java.math.RoundingMode
@Composable
fun ResultScreen(
    result: UserDetailsResponse,
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
                Text("Your plan", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = Color.White)
            },
            subtitle = {
                Text("Recommended weight and daily calories.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            },
            minHeightDp = 300,
            backgroundGradient = softGreenGradient
        )
        SheetBlock {
            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Recommended weight: ${result.avgweight.setScale(1, RoundingMode.HALF_UP).toPlainString()} kg",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Daily calories (BMR): ${result.bmr.setScale(0, RoundingMode.HALF_UP).toPlainString()} kcal",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A))
            ) {
                Text("Next")
            }
        }
    }
}