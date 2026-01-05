package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun RulerSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float,
    majorEvery: Float,
    mediumEvery: Float,
    unitLabel: String,
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    stepSpacing: Dp = 12.dp,
    minorHeight: Dp = 8.dp,
    mediumHeight: Dp = 16.dp,
    majorHeight: Dp = 24.dp
) {
    val density = LocalDensity.current
    val stepPx = with(density) { stepSpacing.toPx() }
    val minorH = with(density) { minorHeight.toPx() }
    val mediumH = with(density) { mediumHeight.toPx() }
    val majorH = with(density) { majorHeight.toPx() }

    val centerOverhangPx = with(density) { 6.dp.toPx() }
    val centerExtraPx = with(density) { 8.dp.toPx() }


    val baseOnSurface = MaterialTheme.colorScheme.onSurface
    val centerColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)

    val minVal = valueRange.start
    val maxVal = valueRange.endInclusive

    fun clamp(v: Float) = max(minVal, min(maxVal, v))
    fun roundToStep(v: Float): Float {
        val steps = ((v - minVal) / step).roundToInt()
        return clamp(minVal + steps * step)
    }

    val dragModifier = Modifier.pointerInput(stepPx, value, minVal, maxVal, step) {
        detectDragGestures(
            onDrag = { _, dragAmount ->
                val deltaSteps = -dragAmount.x / stepPx
                val newV = clamp(value + (deltaSteps * step))
                onValueChange(newV)
            },
            onDragEnd = { onValueChange(roundToStep(value)) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(dragModifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = unitLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))

        Surface(
            color = trackColor,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
                val w = size.width
                val h = size.height
                val centerX = w / 2f
                val baselineY = h * 0.75f

                val halfStepsVisible = ceil(w / stepPx / 2f).toInt() + 2

                val currentStepFloat = (value - minVal) / step
                val currentStepIndex = currentStepFloat.toInt()

                for (i in -halfStepsVisible..halfStepsVisible) {
                    val stepIndex = currentStepIndex + i
                    val tickValue = minVal + stepIndex * step
                    if (tickValue < minVal - 1e-3 || tickValue > maxVal + 1e-3) continue

                    val x = centerX + (tickValue - value) / step * stepPx
                    if (x < -4f || x > w + 4f) continue

                    val fromMinSteps = ((tickValue - minVal) / step).roundToInt().let { abs(it) }
                    val majorEverySteps = (majorEvery / step).roundToInt().coerceAtLeast(1)
                    val mediumEverySteps = (mediumEvery / step).roundToInt().coerceAtLeast(1)

                    val isMajor = (fromMinSteps % majorEverySteps == 0)
                    val isMedium = !isMajor && (fromMinSteps % mediumEverySteps == 0)
                    val lineH = when {
                        isMajor -> majorH
                        isMedium -> mediumH
                        else -> minorH
                    }

                    val tickColor = baseOnSurface.copy(
                        alpha = when {
                            isMajor -> 0.9f
                            isMedium -> 0.7f
                            else -> 0.45f
                        }
                    )

                    drawLine(
                        color = tickColor,
                        start = Offset(x, baselineY),
                        end = Offset(x, baselineY - lineH),
                        strokeWidth = if (isMajor) 3f else 2f
                    )
                }

                drawLine(
                    color = centerColor,
                    start = Offset(centerX, baselineY + centerOverhangPx),
                    end = Offset(centerX, baselineY - (majorH + centerExtraPx)),
                    strokeWidth = 4f
                )
            }
        }
    }
}