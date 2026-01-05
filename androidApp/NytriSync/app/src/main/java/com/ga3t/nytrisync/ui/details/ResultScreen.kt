package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ga3t.nytrisync.data.model.UserDetailsResponse

@Composable
fun ResultScreen(
    result: UserDetailsResponse,
    onNext: () -> Unit
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderBlock(
            title = {
                Text("Your plan", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
            },
            subtitle = {
                Text("Recommended weight and daily calories.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            },
            minHeightDp = 300
        )
        SheetBlock {
            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Recommended weight: ${result.avgweight.stripTrailingZeros().toPlainString()} kg")
                    Text("Daily calories (BMR): ${result.bmr.stripTrailingZeros().toPlainString()} kcal")
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