package com.ga3t.nytrisync.ui.details
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun ChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        FilledTonalButton(
            onClick = onClick,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF66BB6A))
        ) { Text(text) }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = MaterialTheme.shapes.large
        ) { Text(text) }
    }
}
@Composable
fun NextFab(
    enabled: Boolean,
    onClick: () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFF66BB6A)),
        modifier = Modifier.size(56.dp)
    ) {
        Icon(Icons.Rounded.ArrowForward, contentDescription = "Next")
    }
}