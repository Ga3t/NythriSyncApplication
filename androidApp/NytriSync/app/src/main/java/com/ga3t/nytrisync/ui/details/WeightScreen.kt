package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun WeightScreen(
    title: String,
    subtitle: String,
    valueKg: Float,
    onValueChange: (Float) -> Unit,
    onNext: () -> Unit
) {
    var showManualInput by remember { mutableStateOf(false) }
    var manualInputText by remember(valueKg) { mutableStateOf("%.1f".format(valueKg)) }
    
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
                    title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            },
            subtitle = {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            minHeightDp = 360,
            backgroundGradient = softGreenGradient
        )
        SheetBlock {
            // Блок с текущим значением
            ElevatedCard(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.clickable { 
                    manualInputText = "%.1f".format(valueKg)
                    showManualInput = true 
                }
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${"%.1f".format(valueKg)} kg",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }


            RulerSlider(
                value = valueKg,
                onValueChange = onValueChange,
                valueRange = 30f..250f,
                step = 0.5f,
                majorEvery = 10f,
                mediumEvery = 5f,
                unitLabel = "kg"
            )

            Box(Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    onClick = onNext,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) { Icon(Icons.Rounded.ArrowForward, contentDescription = "Next") }
            }
        }
    }
    if (showManualInput) {
        AlertDialog(
            onDismissRequest = { showManualInput = false },
            title = { Text("Enter weight manually") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = manualInputText,
                        onValueChange = { text ->
                            val filtered = text.filter { it.isDigit() || it == '.' || it == ',' }
                            manualInputText = filtered.replace(',', '.')
                        },
                        label = { Text("Weight (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newValue = manualInputText.toFloatOrNull()
                        if (newValue != null && newValue in 30f..250f) {
                            onValueChange(newValue)
                            showManualInput = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInput = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}