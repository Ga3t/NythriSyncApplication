package com.ga3t.nytrisync.ui.common
import android.graphics.PathMeasure
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min
@Composable
fun LoadingTrackSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    bgOpacity: Float = 0.10f,
    speedMillis: Int = 1750
) {
    val svgPathData =
        "M0.625 21.5 h10.25 l3.75 -5.875 l7.375 15 l9.75 -30 l7.375 20.875 v0 h10.25"
    val viewBoxW = 50f
    val viewBoxH = 31.25f
    val aspect = viewBoxH / viewBoxW
    val rawPath = remember {
        PathParser().parsePathString(svgPathData).toPath()
    }
    val rawLength = remember {
        PathMeasure(rawPath.asAndroidPath(), false).length
    }
    val infinite = rememberInfiniteTransition(label = "spinner")
    val phaseFrac = infinite.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = speedMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val alphaCar = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = speedMillis
                0f at 0
                1f at (speedMillis * 0.20f).toInt()
                1f at (speedMillis * 0.55f).toInt()
                0f at speedMillis
            }
        ),
        label = "alpha"
    )
    androidx.compose.foundation.Canvas(
        modifier = modifier.size(size, (size * aspect))
    ) {
        val scale = min(size.toPx() / viewBoxW, (size * aspect).toPx() / viewBoxH)
        val strokeWidthPx = 4f * scale
        val pathLengthPx = rawLength * scale
        val dashEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(pathLengthPx, pathLengthPx),
            phase = phaseFrac.value * pathLengthPx
        )
        withTransform({
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            drawPath(
                path = rawPath,
                color = color.copy(alpha = bgOpacity),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            drawPath(
                path = rawPath,
                color = color.copy(alpha = alphaCar.value),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = dashEffect
                )
            )
        }
    }
}