package com.ga3t.nytrisync.ui.details
import android.widget.NumberPicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.time.YearMonth
@Composable
fun BirthDateScreen(
    year: Int, month: Int, day: Int,
    onChange: (y: Int, m: Int, d: Int) -> Unit,
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
                Text("Your birth date", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = Color.White)
            },
            subtitle = {
                Text("Scroll wheels to set day, month, and year.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            },
            minHeightDp = 360,
            backgroundGradient = softGreenGradient
        )
        SheetBlock {
            var y by remember { mutableStateOf(year) }
            var m by remember { mutableStateOf(month) }
            var d by remember { mutableStateOf(day) }
            val daysInMonth = YearMonth.of(y, m).lengthOfMonth()
            if (d > daysInMonth) d = daysInMonth
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberWheel(min = 1, max = 31, value = d) { newD ->
                    d = newD; onChange(y, m, d)
                }
                NumberWheel(min = 1, max = 12, value = m) { newM ->
                    m = newM; val dim = YearMonth.of(y, m).lengthOfMonth()
                    if (d > dim) d = dim
                    onChange(y, m, d)
                }
                NumberWheel(min = 1900, max = 2100, value = y) { newY ->
                    y = newY; val dim = YearMonth.of(y, m).lengthOfMonth()
                    if (d > dim) d = dim
                    onChange(y, m, d)
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
@Composable
private fun NumberWheel(min: Int, max: Int, value: Int, onChange: (Int) -> Unit) {
    AndroidView(
        modifier = Modifier.height(160.dp).width(100.dp),
        factory = { context ->
            NumberPicker(context).apply {
                minValue = min
                maxValue = max
                this.value = value
                setOnValueChangedListener { _, _, newVal -> onChange(newVal) }
                wrapSelectorWheel = true
            }
        },
        update = { picker ->
            if (picker.minValue != min) picker.minValue = min
            if (picker.maxValue != max) picker.maxValue = max
            if (picker.value != value) picker.value = value
        }
    )
}