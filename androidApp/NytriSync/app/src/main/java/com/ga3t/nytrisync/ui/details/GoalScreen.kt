package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ga3t.nytrisync.data.model.GoalType

@Composable
fun GoalScreen(
    selected: GoalType?,
    onSelect: (GoalType) -> Unit,
    onNext: () -> Unit
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderBlock(
            title = {
                Text(
                    "Select your goal",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            subtitle = {
                Text(
                    "Loss, Gain, or Maintenance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            },
            minHeightDp = 320
        )
        SheetBlock {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChoiceButton("LOSS", selected == GoalType.LOSS) { onSelect(GoalType.LOSS) }
                ChoiceButton("GAIN", selected == GoalType.GAIN) { onSelect(GoalType.GAIN) }
                ChoiceButton("MAINTENANCE", selected == GoalType.MAINTENANCE) { onSelect(GoalType.MAINTENANCE) }
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