package com.ga3t.nytrisync.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ga3t.nytrisync.R
import com.ga3t.nytrisync.data.model.MainPageResponse
import com.ga3t.nytrisync.data.model.MealType
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min

// --- Header Icon ---
@Composable
fun HeaderIcon(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
        )
    }
}

// --- Calories Block ---
@Composable
fun TodayCaloriesBlock(today: MainPageResponse.TodayCalory) {
    val cons = today.todayCaloryCons.toInt()
    val norm = today.todayCaloryNorm.toInt()
    val diff = norm - cons
    val leftMode = diff >= 0
    val absDiff = kotlin.math.abs(diff)
    val verb = if (leftMode) "LEFT" else "MORE"
    val amountText = "$absDiff kcal"

    val yellowSoft = Color(0xFFFEF3C7)
    val yellowDark = Color(0xFFB39B38)
    val yellowText = Color(0xFF78350F)

    val purplSoft = Color(0xFFC7CAFF)
    val purplDark = Color(0x91717291)
    val purplText = Color(0x80252548)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .weight(0.58f)
                .fillMaxHeight(),
            colors = CardDefaults.elevatedCardColors(containerColor = purplSoft)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(purplDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.normal_meal_icon),
                            contentDescription = "Goal",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(verb, style = MaterialTheme.typography.titleMedium, color = purplText)
                    Text(amountText, style = MaterialTheme.typography.bodyMedium, color = purplText)
                }
            }
        }

        ElevatedCard(
            modifier = Modifier
                .weight(0.42f)
                .fillMaxHeight(),
            colors = CardDefaults.elevatedCardColors(containerColor = yellowSoft)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(yellowDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.consumed_meal_icon),
                            contentDescription = "Eaten",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        "EATEN",
                        style = MaterialTheme.typography.bodySmall,
                        color = yellowText.copy(alpha = 0.75f)
                    )
                    Text(
                        "$cons kcal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = yellowText
                    )
                }
            }
        }
    }
}

// --- Water Block ---
@Composable
fun WaterBlock(
    water: MainPageResponse.TodayWater,
    onAddWaterClick: () -> Unit
) {
    val cons = water.todayWaterCons.toInt()
    val need = water.todayWaterNeeds.toInt()
    val diff = need - cons
    val leftMode = diff >= 0
    val absDiff = kotlin.math.abs(diff)
    val verb = if (leftMode) "LEFT" else "MORE"
    val amountText = "$absDiff ml"

    val aquaSoft = Color(0xFFE0F7FA)
    val aquaDark = Color(0xFF26C6DA)
    val aquaText = Color(0xFF004D40)

    val blueSoft = Color(0xFFE3F2FD)
    val blueDark = Color(0xFF64B5F6)
    val blueText = Color(0xFF0D47A1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .weight(0.58f)
                .fillMaxHeight(),
            colors = CardDefaults.elevatedCardColors(containerColor = aquaSoft)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(aquaDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.normal_water_icon),
                            contentDescription = "Water goal",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(verb, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = aquaText)
                    Text(amountText, style = MaterialTheme.typography.bodyMedium, color = aquaText)
                }
            }
        }

        ElevatedCard(
            onClick = onAddWaterClick,
            modifier = Modifier
                .weight(0.42f)
                .fillMaxHeight(),
            colors = CardDefaults.elevatedCardColors(containerColor = blueSoft)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add water",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(blueDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.consumed_water_icon),
                            contentDescription = "Water consumed",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        "CONSUMED",
                        style = MaterialTheme.typography.bodySmall,
                        color = blueText.copy(alpha = 0.75f)
                    )
                    Text(
                        "$cons ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = blueText
                    )
                }
            }
        }
    }
}

// --- Macros ---
@Composable
fun MacrosRow(
    carbs: MainPageResponse.TodayCarbs,
    protein: MainPageResponse.TodayProtein,
    fat: MainPageResponse.TodayFat
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MacroCard("Carbs", carbs.todayCarbsCons, carbs.todayCarbsNorm, color = Color(0xFF7CB342), modifier = Modifier.weight(1f))
        MacroCard("Protein", protein.todayProteinCons, protein.todayProteinNorm, color = Color(0xFF42A5F5), modifier = Modifier.weight(1f))
        MacroCard("Fat", fat.todayFatCons, fat.todayFatNorm, color = Color(0xFFEF5350), modifier = Modifier.weight(1f))
    }
}

@Composable
fun MacroCard(
    title: String,
    cons: BigDecimal,
    norm: BigDecimal,
    color: Color,
    modifier: Modifier = Modifier
) {
    val consInt = cons.setScale(0, RoundingMode.HALF_UP).toInt()
    val normInt = norm.setScale(0, RoundingMode.HALF_UP).toInt()

    val ratio = if (normInt > 0) min(1f, consInt.toFloat() / normInt.toFloat()) else 0f
    val container = softContainerColor(color, MaterialTheme.colorScheme.surface, tintWeight = 0.16f)
    val track = color.copy(alpha = 0.18f)

    ElevatedCard(
        modifier = modifier.heightIn(min = 156.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = container)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 156.dp)
                .padding(14.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = ratio,
                    strokeWidth = 7.dp,
                    color = color,
                    trackColor = track,
                    modifier = Modifier.size(72.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = consInt.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "/${normInt}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// --- Meals List ---
@Composable
fun MealsList(
    meals: List<MainPageResponse.MealPage>,
    onAddMealClick: (MealType) -> Unit
) {
    val types = listOf("BREAKFAST","LUNCH","DINNER","SNACK")
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        types.forEach { t ->
            val cal = meals.find { it.mealType.equals(t, ignoreCase = true) }?.caloryCons?.toInt() ?: 0
            ElevatedCard {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(t, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        Text("$cal kcal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    FilledTonalButton(
                        onClick = { onAddMealClick(MealType.valueOf(t)) },
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF66BB6A))
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}


@Composable
fun RingProgress(
    progress: Float,
    size: Dp,
    thickness: Dp,
    color: Color,
    trackColor: Color,
    startAngle: Float = -90f
) {
    val animated = animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700),
        label = "ring-progress"
    ).value

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = thickness.toPx()
            val canvasSize = this.size
            val diameter = kotlin.math.min(canvasSize.width, canvasSize.height)
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(
                (canvasSize.width - diameter) / 2f,
                (canvasSize.height - diameter) / 2f
            )

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = 360f * animated,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )
        }
    }
}

fun softContainerColor(base: Color, surface: Color, tintWeight: Float = 0.16f): Color {
    val t = tintWeight.coerceIn(0f, 1f)
    val r = base.red * t + surface.red * (1f - t)
    val g = base.green * t + surface.green * (1f - t)
    val b = base.blue * t + surface.blue * (1f - t)
    return Color(r, g, b, 1f)
}