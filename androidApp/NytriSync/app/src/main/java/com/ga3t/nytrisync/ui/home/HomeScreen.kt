package com.ga3t.nytrisync.ui.home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ga3t.nytrisync.data.model.MainPageResponse
import com.ga3t.nytrisync.data.model.MealType
import java.math.BigDecimal
import java.util.Calendar
import kotlin.math.min
import com.ga3t.nytrisync.R
import kotlinx.coroutines.launch
import java.math.RoundingMode
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
@Composable
fun HomeScreen(
    onRequireOnboarding: () -> Unit,
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onAddMealClick: (MealType) -> Unit = {},
    onChartClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.factory())
    val state = vm.ui
    LaunchedEffect(state.requireOnboarding) {
        if (state.requireOnboarding) {
            onRequireOnboarding()
            vm.clearOnboardingFlag()
        }
    }
    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (state.error != null && state.data == null && !state.requireOnboarding) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            Button(onClick = vm::refresh) { Text("Retry") }
        }
        return
    }
    val data = state.data!!
    var showAddWater by remember { mutableStateOf(false) }
    var waterSelected by remember { mutableStateOf(250) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HeaderIcon(
                    icon = Icons.Outlined.Notifications,
                    desc = "Notifications",
                    onClick = onNotificationsClick
                )
                HeaderIcon(icon = Icons.Outlined.Person, desc = "Profile", onClick = onProfileClick)
            }
        }
        WeekOvals(data.weekCalory)
        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Daily Overview",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HeaderIcon(icon = Icons.Outlined.BarChart, desc = "Stats", onClick = onChartClick)
                HeaderIcon(icon = Icons.Outlined.CalendarToday, desc = "Calendar", onClick = onCalendarClick)
            }
        }
        TodayCaloriesBlock(data.todayCalory)
        WaterBlock(
            water = data.todayWater,
            onAddWaterClick = { showAddWater = true }
        )
        if (showAddWater) {
            ModalBottomSheet(
                onDismissRequest = { showAddWater = false },
                sheetState = sheetState
            ) {
                AddWaterSheet(
                    initial = waterSelected,
                    onConfirm = { ml ->
                        waterSelected = ml
                        vm.addWater(ml)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showAddWater = false
                        }
                    },
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showAddWater = false
                        }
                    }
                )
            }
        }
        MacrosRow(
            carbs = data.todayCarbs,
            protein = data.todayProtein,
            fat = data.todayFat
        )
        MealsList(
            meals = data.mealPage,
            onAddMealClick = onAddMealClick
        )
        Spacer(Modifier.windowInsetsPadding(WindowInsets.navigationBars))
    }
}
@Composable
private fun WeekOvals(week: MainPageResponse.WeekCalory) {
    val dayKeys = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val labels  = listOf("M","T","W","T","F","S","S")
    val cal = Calendar.getInstance()
    val dow = cal.get(Calendar.DAY_OF_WEEK)
    val todayIndex = ((dow + 5) % 7)
    val startCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -todayIndex) }
    val dayNumbers = (0..6).map { i ->
        (startCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, i) }
            .get(Calendar.DAY_OF_MONTH)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dayKeys.forEachIndexed { i, key ->
            val cons = week.thisWeekCaloryCons.getIgnoreCase(key)?.toFloat() ?: 0f
            val norm = week.thisWeekCaloryNorm.getIgnoreCase(key)?.toFloat() ?: 0f
            val hasData = norm > 0f
            val ratio = if (hasData) (cons / norm).coerceIn(0f, 1f) else 0f
            val isToday = i == todayIndex
            val ovalBg = when {
                isToday -> Color(0xFF4CAF50)
                hasData -> MaterialTheme.colorScheme.surfaceVariant
                else    -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
            val dayLabelColor = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            val ringColor = if (isToday) MaterialTheme.colorScheme.onPrimary else Color(0xFF4CAF50)
            val trackColor = if (isToday) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            val centerTextColor = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.width(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(86.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ovalBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            labels[i],
                            style = MaterialTheme.typography.labelLarge,
                            color = dayLabelColor,
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )
                        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                            RingProgress(
                                progress = ratio,
                                size = 28.dp,
                                thickness = 4.dp,
                                color = ringColor,
                                trackColor = trackColor,
                                startAngle = -90f
                            )
                            Text(
                                text = dayNumbers[i].toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = centerTextColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
private fun <T> Map<String, T?>.getIgnoreCase(key: String): T? {
    this[key]?.let { return it }
    this.entries.firstOrNull { it.key.equals(key, ignoreCase = true) }?.value?.let { return it }
    val expanded = when (key.lowercase()) {
        "mon" -> listOf("monday")
        "tue" -> listOf("tuesday", "tues")
        "wed" -> listOf("wednesday")
        "thu" -> listOf("thursday", "thur", "thurs")
        "fri" -> listOf("friday")
        "sat" -> listOf("saturday")
        "sun" -> listOf("sunday")
        else -> emptyList()
    }
    return this.entries.firstOrNull { e ->
        expanded.any { e.key.equals(it, ignoreCase = true) }
    }?.value
}
private val WATER_VALUES = (50..5000 step 50).toList()
@Composable
private fun AddWaterSheet(
    initial: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember(initial) { mutableStateOf(
        WATER_VALUES.minByOrNull { kotlin.math.abs(it - initial) } ?: 250
    ) }
    Column(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add water", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text("${selected} ml", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        WaterWheelPicker(
            selected = selected,
            onSelectedChange = { selected = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large
            ) { Text("Cancel") }
            Button(
                onClick = { onConfirm(selected) },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large
            ) { Text("Add") }
        }
        Spacer(Modifier.height(8.dp))
    }
}
@Composable
private fun WaterWheelPicker(
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val values = remember { WATER_VALUES }
    val labels = remember { values.map { it.toString() }.toTypedArray() }
    val initIndex = remember(selected) { values.indexOf(selected).coerceAtLeast(0) }
    val textColor = MaterialTheme.colorScheme.onSurface
    val androidColor = remember(textColor) {
        android.graphics.Color.argb(
            (textColor.alpha * 255).toInt(),
            (textColor.red * 255).toInt(),
            (textColor.green * 255).toInt(),
            (textColor.blue * 255).toInt()
        )
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            android.widget.NumberPicker(context).apply {
                descendantFocusability = android.widget.NumberPicker.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = true
                minValue = 0
                maxValue = values.size - 1
                displayedValues = labels
                value = initIndex
                tag = androidColor
                setOnValueChangedListener { _, _, newIndex ->
                    onSelectedChange(values[newIndex])
                    post {
                        val color = tag as? Int ?: androidColor
                        setTextColorRecursiveWithAndroidColor(this, color)
                    }
                }
                addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
                    val color = view.tag as? Int ?: androidColor
                    view.post {
                        setTextColorRecursiveWithAndroidColor(view, color)
                    }
                }
                post {
                    setTextColorRecursiveWithAndroidColor(this, androidColor)
                }
            }
        },
        update = { picker ->
            val idx = values.indexOf(selected).coerceAtLeast(0)
            if (picker.value != idx) picker.value = idx
            picker.tag = androidColor
            picker.post {
                setTextColorRecursiveWithAndroidColor(picker, androidColor)
            }
        }
    )
}
private fun setTextColorRecursive(view: android.view.View, color: androidx.compose.ui.graphics.Color) {
    if (view is android.widget.TextView) {
        view.setTextColor(android.graphics.Color.argb(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        ))
    } else if (view is android.view.ViewGroup) {
        for (i in 0 until view.childCount) {
            setTextColorRecursive(view.getChildAt(i), color)
        }
    }
}
private fun setTextColorRecursiveWithAndroidColor(view: android.view.View, color: Int) {
    if (view is android.widget.TextView) {
        view.setTextColor(color)
    } else if (view is android.view.ViewGroup) {
        for (i in 0 until view.childCount) {
            setTextColorRecursiveWithAndroidColor(view.getChildAt(i), color)
        }
    }
}
    @Composable
    fun NotificationsScreen(onBack: () -> Unit) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Notifications") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, null) }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Notifications settings")
            }
        }
    }