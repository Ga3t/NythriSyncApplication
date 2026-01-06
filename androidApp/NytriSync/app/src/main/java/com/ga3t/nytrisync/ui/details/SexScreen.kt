package com.ga3t.nytrisync.ui.details
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ga3t.nytrisync.data.model.SexType
@Composable
fun SexScreen(
    selected: SexType?,
    onSelect: (SexType) -> Unit,
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
                Text(
                    "Select your sex",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            },
            subtitle = {
                Text(
                    "Choose one option.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            minHeightDp = 320,
            backgroundGradient = softGreenGradient
        )
        SheetBlock {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChoiceButton("Male", selected == SexType.MALE) { onSelect(SexType.MALE) }
                ChoiceButton("Female", selected == SexType.FEMALE) { onSelect(SexType.FEMALE) }
            }
            Box(Modifier.fillMaxWidth()) {
                NextFab(
                    enabled = selected != null,
                    onClick = onNext
                )
            }
        }
    }
}