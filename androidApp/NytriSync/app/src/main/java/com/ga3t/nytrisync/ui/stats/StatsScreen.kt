package com.ga3t.nytrisync.ui.stats
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ga3t.nytrisync.data.model.ReportResponse
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.AnalyseRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.max
enum class RangePreset(val title: String, val days: Long) {
    D7("7 days", 7),
    D30("30 days", 30),
    M6("6 months", 182),
    Y1("1 year", 365)
}
data class StatsUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val data: ReportResponse? = null,
    val preset: RangePreset = RangePreset.D7
)
class StatsViewModel(
    private val repo: AnalyseRepository
) : ViewModel() {
    var ui by mutableStateOf(StatsUiState())
        private set
    private val fmt = DateTimeFormatter.ISO_DATE
    init {
        loadPreset(RangePreset.D7)
    }
    fun loadPreset(preset: RangePreset) {
        ui = ui.copy(loading = true, error = null, preset = preset)
    }
    suspend fun fetchFromUi() {
        val end = LocalDate.now()
        val start = when (ui.preset) {
            RangePreset.D7 -> end.minusDays(6)
            RangePreset.D30 -> end.minusDays(29)
            RangePreset.M6 -> end.minusDays(181)
            RangePreset.Y1 -> end.minusDays(364)
        }
        val res = repo.range(start.toString(), end.toString())
        res.onSuccess { ui = ui.copy(loading = false, data = it, error = null) }
            .onFailure { e -> ui = ui.copy(loading = false, error = e.message ?: "Load failed") }
    }
    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StatsViewModel(AnalyseRepository(RetrofitProvider.analyseApi)) as T
            }
        }
    }
}
@Composable
fun StatsScreen(onBack: () -> Unit) {
    val vm: StatsViewModel = viewModel(factory = StatsViewModel.factory())
    val ui = vm.ui
    LaunchedEffect(ui.preset) {
        vm.fetchFromUi()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        if (ui.loading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        if (ui.error != null) {
            Column(
                Modifier.padding(padding).fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: ${ui.error}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { vm.loadPreset(ui.preset) }) { Text("Retry") }
            }
            return@Scaffold
        }
        val report = ui.data!!
        val locale = Locale.getDefault()
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RangePreset.values().forEach { p ->
                    FilterChip(
                        selected = ui.preset == p,
                        onClick = { vm.loadPreset(p) },
                        label = { Text(p.title) }
                    )
                }
            }
            val calAgg     = remember(ui.data, ui.preset) { aggregateTwo(report.anlyses, ui.preset, { it.kcalCons },    { it.kcalNorm },    locale) }
            val waterAgg   = remember(ui.data, ui.preset) { aggregateTwo(report.anlyses, ui.preset, { it.waterCons },   { it.waterNorm },   locale) }
            val sugarAgg   = remember(ui.data, ui.preset) { aggregateTwo(report.anlyses, ui.preset, { it.sugarCons },   { it.sugarNorm },   locale) }
            val fiberAgg   = remember(ui.data, ui.preset) { aggregateTwo(report.anlyses, ui.preset, { it.fiberCons },   { it.fiberNorm },   locale) }
            val carbsAgg   = remember(ui.data, ui.preset) { aggregateTwo(report.anlyses, ui.preset, { it.carbsCons },   { it.carbsNorm },   locale) }
            val proteinAgg = remember(ui.data, ui.preset) { aggregateTwo(report.anlyses, ui.preset, { it.proteinCons }, { it.proteinNorm }, locale) }
            val fatAgg     = remember(ui.data, ui.preset) { aggregateTwo(report.anlyses, ui.preset, { it.fatCons },     { it.fatNorm },     locale) }
            val weightAgg  = remember(ui.data, ui.preset) { aggregateSingle(report.anlyses, ui.preset, { it.weight ?: BigDecimal.ZERO }, locale) }
            val calorieAdvice = remember(calAgg.cons, calAgg.norm) {
                calculateCalorieAdvice(calAgg.cons, calAgg.norm)
            }
            val waterAdvice = remember(waterAgg.cons, waterAgg.norm) {
                calculateWaterAdvice(waterAgg.cons, waterAgg.norm)
            }
            val sugarAdvice = remember(sugarAgg.cons, sugarAgg.norm) {
                calculateSugarAdvice(sugarAgg.cons, sugarAgg.norm)
            }
            val fiberAdvice = remember(fiberAgg.cons, fiberAgg.norm) {
                calculateFiberAdvice(fiberAgg.cons, fiberAgg.norm)
            }
            val carbsAdvice = remember(carbsAgg.cons, carbsAgg.norm) {
                calculateCarbsAdvice(carbsAgg.cons, carbsAgg.norm)
            }
            val proteinAdvice = remember(proteinAgg.cons, proteinAgg.norm) {
                calculateProteinAdvice(proteinAgg.cons, proteinAgg.norm)
            }
            val fatAdvice = remember(fatAgg.cons, fatAgg.norm) {
                calculateFatAdvice(fatAgg.cons, fatAgg.norm)
            }
            ChartCard(
                title = "Calories",
                advice = calorieAdvice,
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Consumed" to Color(0xFF4CAF50),
                            "Norm" to Color(0xFF81C784)
                        )
                    )
                },
                content = {
                    LineChartTwoSeries(calAgg.labels, calAgg.cons, calAgg.norm, Color(0xFF4CAF50), Color(0xFF81C784))
                }
            )
            ChartCard(
                title = "Water",
                advice = waterAdvice,
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Consumed" to Color(0xFF00CED1),
                            "Norm" to Color(0xFF40E0D0)
                        )
                    )
                }
            ) {
                LineChartTwoSeries(waterAgg.labels, waterAgg.cons, waterAgg.norm, Color(0xFF00CED1), Color(0xFF40E0D0))
            }
            ChartCard(
                title = "Sugar",
                advice = sugarAdvice,
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Consumed" to Color(0xFFE91E63),
                            "Norm" to Color(0xFF9C27B0)
                        )
                    )
                }
            ) {
                LineChartTwoSeries(sugarAgg.labels, sugarAgg.cons, sugarAgg.norm, Color(0xFFE91E63), Color(0xFF9C27B0))
            }
            ChartCard(
                title = "Fiber",
                advice = fiberAdvice,
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Consumed" to Color(0xFF9370DB),
                            "Norm" to Color(0xFFBA55D3)
                        )
                    )
                }
            ) {
                LineChartTwoSeries(fiberAgg.labels, fiberAgg.cons, fiberAgg.norm, Color(0xFF9370DB), Color(0xFFBA55D3))
            }
            ChartCard(
                title = "Carbs",
                advice = carbsAdvice,
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Consumed" to Color(0xFF32CD32),
                            "Norm" to Color(0xFF00FF7F)
                        )
                    )
                }
            ) {
                LineChartTwoSeries(carbsAgg.labels, carbsAgg.cons, carbsAgg.norm, Color(0xFF32CD32), Color(0xFF00FF7F))
            }
            ChartCard(
                title = "Protein",
                advice = proteinAdvice,
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Consumed" to Color(0xFF1E90FF),
                            "Norm" to Color(0xFF00BFFF)
                        )
                    )
                }
            ) {
                LineChartTwoSeries(proteinAgg.labels, proteinAgg.cons, proteinAgg.norm, Color(0xFF1E90FF), Color(0xFF00BFFF))
            }
            ChartCard(
                title = "Fat",
                advice = fatAdvice,
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Consumed" to Color(0xFFFF6B35),
                            "Norm" to Color(0xFFFFA500)
                        )
                    )
                }
            ) {
                LineChartTwoSeries(fatAgg.labels, fatAgg.cons, fatAgg.norm, Color(0xFFFF6B35), Color(0xFFFFA500))
            }
            ChartCard(
                title = "Weight",
                legend = {
                    ChartLegend(
                        items = listOf(
                            "Weight" to Color(0xFFFFD700)
                        )
                    )
                }
            ) {
                LineChartSingleSeries(weightAgg.labels, weightAgg.values, Color(0xFFFFD700))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
@Composable
private fun ChartLegend(
    items: List<Pair<String, Color>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items.forEach { (label, color) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(color, RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
@Composable
private fun ChartCard(
    title: String,
    advice: String? = null,
    legend: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ElevatedCard(shape = MaterialTheme.shapes.medium) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                content()
            }
            legend?.invoke()
            advice?.let {
                val adviceColor = when {
                    it.contains("🚨") -> MaterialTheme.colorScheme.error
                    it.contains("⚠️") -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                }
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = adviceColor,
                    modifier = Modifier.padding(top = 4.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
private fun calculateCalorieAdvice(cons: List<Float>, norm: List<Float>): String? {
    if (cons.isEmpty() || norm.isEmpty()) return null
    if (norm.all { it == 0f }) return null
    val daysAbove20Percent = cons.zip(norm).count { (c, n) ->
        n > 0 && ((c - n) / n) * 100.0 > 20.0
    }
    val percentDaysAbove20 = (daysAbove20Percent.toDouble() / cons.size) * 100.0
    val daysBelowNorm = cons.zip(norm).count { (c, n) ->
        n > 0 && c < n
    }
    val percentDaysBelowNorm = (daysBelowNorm.toDouble() / cons.size) * 100.0
    return when {
        percentDaysAbove20 > 50.0 ->
            "⚠️ Excessive consumption can lead to obesity."
        percentDaysBelowNorm > 50.0 ->
            "⚠️ Malnutrition can lead to poor health."
        else -> null
    }
}
private fun calculateWaterAdvice(cons: List<Float>, norm: List<Float>): String? {
    if (cons.isEmpty() || norm.isEmpty()) return null
    if (norm.all { it == 0f }) return null
    val avgCons = cons.average()
    val avgNorm = norm.average()
    if (avgNorm == 0.0) return null
    val percentageDiff = ((avgCons - avgNorm) / avgNorm) * 100.0
    val daysAbove40Percent = cons.zip(norm).count { (c, n) ->
        n > 0 && ((c - n) / n) * 100.0 >= 40.0
    }
    val percentDaysAbove40 = (daysAbove40Percent.toDouble() / cons.size) * 100.0
    return when {
        percentageDiff <= -20.0 ->
            "⚠️ Can lead to dry skin and dehydration."
        percentDaysAbove40 >= 80.0 ->
            "🚨 Excessive water consumption can lead to the leaching of electrolytes from the body."
        percentDaysAbove40 >= 50.0 ->
            "⚠️ Excessive water consumption can lead to the leaching of electrolytes from the body."
        else -> null
    }
}
private fun calculateProteinAdvice(cons: List<Float>, norm: List<Float>): String? {
    if (cons.isEmpty() || norm.isEmpty()) return null
    if (norm.all { it == 0f }) return null
    val avgCons = cons.average()
    val avgNorm = norm.average()
    if (avgNorm == 0.0) return null
    val percentageDiff = ((avgCons - avgNorm) / avgNorm) * 100.0
    return when {
        percentageDiff <= -20.0 ->
            "⚠️ Low consumption can lead to muscle loss."
        else -> null
    }
}
private fun calculateFatAdvice(cons: List<Float>, norm: List<Float>): String? {
    if (cons.isEmpty() || norm.isEmpty()) return null
    if (norm.all { it == 0f }) return null
    val avgCons = cons.average()
    val avgNorm = norm.average()
    if (avgNorm == 0.0) return null
    val percentageDiff = ((avgCons - avgNorm) / avgNorm) * 100.0
    return when {
        percentageDiff >= 15.0 ->
            "⚠️ Risk of obesity."
        percentageDiff <= -30.0 ->
            "⚠️ Risk of deficiency in fat-soluble vitamins such as A, D, and K."
        else -> null
    }
}
private fun calculateCarbsAdvice(cons: List<Float>, norm: List<Float>): String? {
    if (cons.isEmpty() || norm.isEmpty()) return null
    if (norm.all { it == 0f }) return null
    val avgCons = cons.average()
    val avgNorm = norm.average()
    if (avgNorm == 0.0) return null
    val percentageDiff = ((avgCons - avgNorm) / avgNorm) * 100.0
    return when {
        percentageDiff <= -20.0 ->
            "⚠️ There may be a decline in energy and a vitamin deficiency."
        else -> null
    }
}
private fun calculateSugarAdvice(cons: List<Float>, norm: List<Float>): String? {
    if (cons.isEmpty() || norm.isEmpty()) return null
    if (norm.all { it == 0f }) return null
    val avgCons = cons.average()
    val avgNorm = norm.average()
    if (avgNorm == 0.0) return null
    val percentageDiff = ((avgCons - avgNorm) / avgNorm) * 100.0
    return when {
        percentageDiff > 5.0 ->
            "⚠️ Risk of obesity and diabetes."
        else -> null
    }
}
private fun calculateFiberAdvice(cons: List<Float>, norm: List<Float>): String? {
    if (cons.isEmpty() || norm.isEmpty()) return null
    if (norm.all { it == 0f }) return null
    val avgCons = cons.average()
    val avgNorm = norm.average()
    if (avgNorm == 0.0) return null
    val percentageDiff = ((avgCons - avgNorm) / avgNorm) * 100.0
    return when {
        percentageDiff <= -15.0 ->
            "⚠️ Disruption of the cryptobiota and a risk of diabetes."
        else -> null
    }
}
private data class TwoAgg(val labels: List<String>, val cons: List<Float>, val norm: List<Float>)
private data class SingleAgg(val labels: List<String>, val values: List<Float>)
private fun aggregateTwo(
    items: List<ReportResponse.DayAnalyse>,
    preset: RangePreset,
    consSel: (ReportResponse.DayAnalyse) -> BigDecimal,
    normSel: (ReportResponse.DayAnalyse) -> BigDecimal,
    locale: Locale
): TwoAgg {
    return if (preset == RangePreset.D7 || preset == RangePreset.D30) {
        val sorted = items.sortedBy { LocalDate.parse(it.date) }
        val labels = sorted.map { it.date.substring(5) }
        TwoAgg(labels, sorted.map { consSel(it).toFloat() }, sorted.map { normSel(it).toFloat() })
    } else {
        val byYm = items.groupBy { YearMonth.from(LocalDate.parse(it.date)) }.toSortedMap()
        val labels = mutableListOf<String>()
        val cons = mutableListOf<Float>()
        val norm = mutableListOf<Float>()
        byYm.forEach { (ym, list) ->
            labels += ym.month.getDisplayName(TextStyle.SHORT, locale)
            cons += list.map { consSel(it).toFloat() }.average().toFloat()
            norm += list.map { normSel(it).toFloat() }.average().toFloat()
        }
        TwoAgg(labels, cons, norm)
    }
}
private fun aggregateSingle(
    items: List<ReportResponse.DayAnalyse>,
    preset: RangePreset,
    valueSel: (ReportResponse.DayAnalyse) -> BigDecimal,
    locale: Locale
): SingleAgg {
    return if (preset == RangePreset.D7 || preset == RangePreset.D30) {
        val sorted = items.sortedBy { LocalDate.parse(it.date) }
        val labels = sorted.map { it.date.substring(5) }
        SingleAgg(labels, sorted.map { valueSel(it).toFloat() })
    } else {
        val byYm = items.groupBy { YearMonth.from(LocalDate.parse(it.date)) }.toSortedMap()
        val labels = mutableListOf<String>()
        val vals = mutableListOf<Float>()
        byYm.forEach { (ym, list) ->
            labels += ym.month.getDisplayName(TextStyle.SHORT, locale)
            val arr = list.map { valueSel(it).toFloat() }.filter { it > 0f }
            vals += (if (arr.isEmpty()) 0f else arr.average().toFloat())
        }
        SingleAgg(labels, vals)
    }
}
@Composable
private fun LineChartTwoSeries(
    labels: List<String>,
    y1: List<Float>,
    y2: List<Float>,
    color1: Color,
    color2: Color
) {
    var touchedIndex by remember { mutableStateOf<Int?>(null) }
    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    val textPaintColorInt = android.graphics.Color.GRAY
    val visibleLabelIndices = remember(labels.size) {
        if (labels.size <= 5) {
            labels.indices.toList()
        } else {
            listOf(0, labels.size / 2, labels.size - 1)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val n = labels.size
                        if (n > 0 && canvasSize.width > 0) {
                            val leftPad = 24f
                            val rightPad = 12f
                            val usableWidth = canvasSize.width - leftPad - rightPad
                            val touchX = offset.x
                            var minDist = Float.MAX_VALUE
                            var closestIdx = -1
                            for (i in labels.indices) {
                                val xAt = leftPad + if (n == 1) usableWidth / 2f else (usableWidth * i / (n - 1).toFloat())
                                val dist = abs(touchX - xAt)
                                if (dist < minDist && dist < 50f) {
                                    minDist = dist
                                    closestIdx = i
                                }
                            }
                            touchedIndex = if (closestIdx >= 0) closestIdx else null
                        }
                    }
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            touchedIndex = null
                            touchOffset = null
                        }
                    ) { change, _ ->
                        touchOffset = change.position
                        val n = labels.size
                        if (n > 0 && canvasSize.width > 0) {
                            val leftPad = 24f
                            val rightPad = 12f
                            val usableWidth = canvasSize.width - leftPad - rightPad
                            val touchX = change.position.x
                            var minDist = Float.MAX_VALUE
                            var closestIdx = -1
                            for (i in labels.indices) {
                                val xAt = leftPad + if (n == 1) usableWidth / 2f else (usableWidth * i / (n - 1).toFloat())
                                val dist = abs(touchX - xAt)
                                if (dist < minDist && dist < 50f) {
                                    minDist = dist
                                    closestIdx = i
                                }
                            }
                            touchedIndex = if (closestIdx >= 0) closestIdx else null
                        }
                    }
                }
        ) {
            val n = labels.size
            if (n == 0) return@Canvas
            val maxVal = max((y1.maxOrNull() ?: 0f), (y2.maxOrNull() ?: 0f)).coerceAtLeast(1f)
            val leftPad = 24f
            val rightPad = 12f
            val topPad = size.height * 0.12f
            val bottomPad = size.height * 0.18f
            val usableWidth = size.width - leftPad - rightPad
            val usableHeight = size.height - topPad - bottomPad
            fun xAt(i: Int): Float =
                leftPad + if (n == 1) usableWidth / 2f else (usableWidth * i / (n - 1).toFloat())
            fun yAt(v: Float): Float = topPad + (usableHeight * (1f - (v / maxVal)))
            val axisY = size.height - bottomPad
            drawLine(
                color = axisColor,
                start = Offset(leftPad, axisY),
                end = Offset(size.width - rightPad, axisY),
                strokeWidth = 2f
            )
            fun drawSmoothPath(points: List<Float>, color: Color, strokeWidth: Float) {
                if (points.size < 2) return
                val path = Path()
                path.moveTo(xAt(0), yAt(points[0]))
                for (i in 0 until points.size - 1) {
                    val x0 = xAt(i)
                    val y0 = yAt(points[i])
                    val x1 = xAt(i + 1)
                    val y1 = yAt(points[i + 1])
                    if (i == 0) {
                        val x2 = if (i + 2 < points.size) xAt(i + 2) else x1
                        val y2 = if (i + 2 < points.size) yAt(points[i + 2]) else y1
                        val cp1x = x0 + (x1 - x0) * 0.5f
                        val cp1y = y0
                        val cp2x = x1 - (x2 - x0) * 0.1f
                        val cp2y = y1
                        path.cubicTo(cp1x, cp1y, cp2x, cp2y, x1, y1)
                    } else if (i == points.size - 2) {
                        val xPrev = xAt(i - 1)
                        val yPrev = yAt(points[i - 1])
                        val cp1x = x0 + (x1 - xPrev) * 0.1f
                        val cp1y = y0
                        val cp2x = x1 - (x1 - x0) * 0.5f
                        val cp2y = y1
                        path.cubicTo(cp1x, cp1y, cp2x, cp2y, x1, y1)
                    } else {
                        val xPrev = xAt(i - 1)
                        val xNext = xAt(i + 2)
                        val cp1x = x0 + (x1 - xPrev) * 0.15f
                        val cp1y = y0
                        val cp2x = x1 - (xNext - x0) * 0.15f
                        val cp2y = y1
                        path.cubicTo(cp1x, cp1y, cp2x, cp2y, x1, y1)
                    }
                }
                drawPath(path, color, style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
            drawSmoothPath(y1, color1, 5f)
            drawSmoothPath(y2, color2, 5f)
            for (i in 0 until n) {
                val isTouched = touchedIndex == i
                val pointRadius = if (isTouched) 10f else 6f
                val strokeWidth = if (isTouched) 4f else 2f
                if (isTouched) {
                    drawCircle(
                        color = color1.copy(alpha = 0.3f),
                        radius = pointRadius + 8f,
                        center = Offset(xAt(i), yAt(y1[i]))
                    )
                    drawCircle(
                        color = color2.copy(alpha = 0.3f),
                        radius = pointRadius + 8f,
                        center = Offset(xAt(i), yAt(y2[i]))
                    )
                }
                drawCircle(color = color1, radius = pointRadius, center = Offset(xAt(i), yAt(y1[i])), style = Stroke(width = strokeWidth))
                drawCircle(color = color2, radius = pointRadius, center = Offset(xAt(i), yAt(y2[i])), style = Stroke(width = strokeWidth))
            }
            val p = android.graphics.Paint().apply {
                isAntiAlias = true
                setColor(textPaintColorInt)
                textSize = 12f * density.density
                textAlign = android.graphics.Paint.Align.CENTER
            }
            visibleLabelIndices.forEach { i ->
                drawContext.canvas.nativeCanvas.drawText(labels[i], xAt(i), axisY + 22f * density.density, p)
            }
            touchedIndex?.let { idx ->
                if (idx in labels.indices) {
                    val pointX = xAt(idx)
                    val pointY = minOf(yAt(y1[idx]), yAt(y2[idx]))
                    val tooltipHeight = 70f
                    val tooltipY = (pointY - tooltipHeight - 10f).coerceAtLeast(10f)
                    val linePath = Path()
                    linePath.moveTo(pointX, pointY)
                    linePath.lineTo(pointX, tooltipY + tooltipHeight)
                    drawPath(
                        linePath,
                        color = color1.copy(alpha = 0.5f),
                        style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f)))
                    )
                }
            }
        }
        touchedIndex?.let { idx ->
            if (idx in labels.indices) {
                val leftPad = 24f
                val rightPad = 12f
                val topPad = canvasSize.height * 0.12f
                val bottomPad = canvasSize.height * 0.18f
                val usableWidth = canvasSize.width - leftPad - rightPad
                val n = labels.size
                val pointX = leftPad + if (n == 1) usableWidth / 2f else (usableWidth * idx / (n - 1).toFloat())
                val maxVal = max((y1.maxOrNull() ?: 0f), (y2.maxOrNull() ?: 0f)).coerceAtLeast(1f)
                val usableHeight = canvasSize.height - topPad - bottomPad
                val yAt1 = topPad + (usableHeight * (1f - (y1[idx] / maxVal)))
                val yAt2 = topPad + (usableHeight * (1f - (y2[idx] / maxVal)))
                val pointY = minOf(yAt1, yAt2)
                val tooltipWidthDp = 140f
                val tooltipHeightDp = 70f
                val tooltipWidthPx = tooltipWidthDp * density.density
                val tooltipHeightPx = tooltipHeightDp * density.density
                val boxPaddingPx = 8f * density.density
                val tooltipY = (pointY - tooltipHeightPx - 10f).coerceIn(
                    topPad,
                    canvasSize.height - bottomPad - tooltipHeightPx - 10f
                )
                val tooltipX = (pointX - tooltipWidthPx / 2f).coerceIn(
                    boxPaddingPx,
                    canvasSize.width - tooltipWidthPx - boxPaddingPx
                )
                Card(
                    modifier = Modifier
                        .offset(x = (tooltipX / density.density).dp, y = (tooltipY / density.density).dp)
                        .width(tooltipWidthDp.dp)
                        .height(tooltipHeightDp.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isSystemInDarkTheme()) {
                                    Color.Black.copy(alpha = 0.4f)
                                } else {
                                    Color.White.copy(alpha = 0.6f)
                                },
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = labels[idx],
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                fontSize = 9.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Consumed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = color1.copy(alpha = 0.9f),
                                        fontSize = 8.sp
                                    )
                                    Text(
                                        text = String.format("%.1f", y1[idx]),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = color1,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Norm",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = color2.copy(alpha = 0.9f),
                                        fontSize = 8.sp
                                    )
                                    Text(
                                        text = String.format("%.1f", y2[idx]),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = color2,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun LineChartSingleSeries(
    labels: List<String>,
    y: List<Float>,
    color: Color
) {
    var touchedIndex by remember { mutableStateOf<Int?>(null) }
    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    val textPaintColorInt = android.graphics.Color.GRAY
    val visibleLabelIndices = remember(labels.size) {
        if (labels.size <= 5) {
            labels.indices.toList()
        } else {
            listOf(0, labels.size / 2, labels.size - 1)
        }
    }
    val minVal = remember(y) { (y.minOrNull() ?: 0f) }
    val maxVal = remember(y) { (y.maxOrNull() ?: 0f).coerceAtLeast(1f) }
    val range = remember(minVal, maxVal) { maxVal - minVal }
    val paddedMin = remember(minVal, range) { (minVal - range * 0.1f).coerceAtLeast(0f) }
    val paddedMax = remember(maxVal, range) { maxVal + range * 0.1f }
    val adjustedRange = remember(paddedMin, paddedMax) { paddedMax - paddedMin }
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val n = labels.size
                        if (n > 0 && canvasSize.width > 0) {
                            val leftPad = 24f
                            val rightPad = 12f
                            val usableWidth = canvasSize.width - leftPad - rightPad
                            val touchX = offset.x
                            var minDist = Float.MAX_VALUE
                            var closestIdx = -1
                            for (i in labels.indices) {
                                val xAt = leftPad + if (n == 1) usableWidth / 2f else (usableWidth * i / (n - 1).toFloat())
                                val dist = abs(touchX - xAt)
                                if (dist < minDist && dist < 50f) {
                                    minDist = dist
                                    closestIdx = i
                                }
                            }
                            touchedIndex = if (closestIdx >= 0) closestIdx else null
                        }
                    }
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            touchedIndex = null
                            touchOffset = null
                        }
                    ) { change, _ ->
                        touchOffset = change.position
                        val n = labels.size
                        if (n > 0 && canvasSize.width > 0) {
                            val leftPad = 24f
                            val rightPad = 12f
                            val usableWidth = canvasSize.width - leftPad - rightPad
                            val touchX = change.position.x
                            var minDist = Float.MAX_VALUE
                            var closestIdx = -1
                            for (i in labels.indices) {
                                val xAt = leftPad + if (n == 1) usableWidth / 2f else (usableWidth * i / (n - 1).toFloat())
                                val dist = abs(touchX - xAt)
                                if (dist < minDist && dist < 50f) {
                                    minDist = dist
                                    closestIdx = i
                                }
                            }
                            touchedIndex = if (closestIdx >= 0) closestIdx else null
                        }
                    }
                }
        ) {
            val n = labels.size
            if (n == 0) return@Canvas
            val leftPad = 24f
            val rightPad = 12f
            val topPad = size.height * 0.12f
            val bottomPad = size.height * 0.18f
            val usableWidth = size.width - leftPad - rightPad
            val usableHeight = size.height - topPad - bottomPad
            fun xAt(i: Int): Float =
                leftPad + if (n == 1) usableWidth / 2f else (usableWidth * i / (n - 1).toFloat())
            fun yAt(v: Float): Float = topPad + (usableHeight * (1f - ((v - paddedMin) / adjustedRange)))
            val axisY = size.height - bottomPad
            drawLine(
                color = axisColor,
                start = Offset(leftPad, axisY),
                end = Offset(size.width - rightPad, axisY),
                strokeWidth = 2f
            )
            fun drawSmoothPath(points: List<Float>, color: Color, strokeWidth: Float) {
                if (points.size < 2) return
                val path = Path()
                path.moveTo(xAt(0), yAt(points[0]))
                for (i in 0 until points.size - 1) {
                    val x0 = xAt(i)
                    val y0 = yAt(points[i])
                    val x1 = xAt(i + 1)
                    val y1 = yAt(points[i + 1])
                    if (i == 0) {
                        val x2 = if (i + 2 < points.size) xAt(i + 2) else x1
                        val y2 = if (i + 2 < points.size) yAt(points[i + 2]) else y1
                        val cp1x = x0 + (x1 - x0) * 0.5f
                        val cp1y = y0
                        val cp2x = x1 - (x2 - x0) * 0.1f
                        val cp2y = y1
                        path.cubicTo(cp1x, cp1y, cp2x, cp2y, x1, y1)
                    } else if (i == points.size - 2) {
                        val xPrev = xAt(i - 1)
                        val yPrev = yAt(points[i - 1])
                        val cp1x = x0 + (x1 - xPrev) * 0.1f
                        val cp1y = y0
                        val cp2x = x1 - (x1 - x0) * 0.5f
                        val cp2y = y1
                        path.cubicTo(cp1x, cp1y, cp2x, cp2y, x1, y1)
                    } else {
                        val xPrev = xAt(i - 1)
                        val xNext = xAt(i + 2)
                        val cp1x = x0 + (x1 - xPrev) * 0.15f
                        val cp1y = y0
                        val cp2x = x1 - (xNext - x0) * 0.15f
                        val cp2y = y1
                        path.cubicTo(cp1x, cp1y, cp2x, cp2y, x1, y1)
                    }
                }
                drawPath(path, color, style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
            drawSmoothPath(y, color, 5f)
            for (i in 0 until n) {
                val isTouched = touchedIndex == i
                val pointRadius = if (isTouched) 10f else 6f
                val strokeWidth = if (isTouched) 4f else 2f
                if (isTouched) {
                    drawCircle(
                        color = color.copy(alpha = 0.3f),
                        radius = pointRadius + 8f,
                        center = Offset(xAt(i), yAt(y[i]))
                    )
                }
                drawCircle(color = color, radius = pointRadius, center = Offset(xAt(i), yAt(y[i])), style = Stroke(width = strokeWidth))
            }
            val p = android.graphics.Paint().apply {
                isAntiAlias = true
                setColor(textPaintColorInt)
                textSize = 12f * density.density
                textAlign = android.graphics.Paint.Align.CENTER
            }
            visibleLabelIndices.forEach { i ->
                val labelX = xAt(i).coerceIn(leftPad, size.width - rightPad)
                drawContext.canvas.nativeCanvas.drawText(labels[i], labelX, axisY + 22f * density.density, p)
            }
            touchedIndex?.let { idx ->
                if (idx in labels.indices) {
                    val pointX = xAt(idx)
                    val pointY = yAt(y[idx])
                    val tooltipHeight = 60f
                    val tooltipY = (pointY - tooltipHeight - 10f).coerceAtLeast(10f)
                    val linePath = Path()
                    linePath.moveTo(pointX, pointY)
                    linePath.lineTo(pointX, tooltipY + tooltipHeight)
                    drawPath(
                        linePath,
                        color = color.copy(alpha = 0.5f),
                        style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f)))
                    )
                }
            }
        }
        touchedIndex?.let { idx ->
            if (idx in labels.indices) {
                val leftPad = 24f
                val rightPad = 12f
                val topPad = canvasSize.height * 0.12f
                val bottomPad = canvasSize.height * 0.18f
                val usableWidth = canvasSize.width - leftPad - rightPad
                val n = labels.size
                val pointX = leftPad + if (n == 1) usableWidth / 2f else (usableWidth * idx / (n - 1).toFloat())
                val usableHeight = canvasSize.height - topPad - bottomPad
                val pointY = topPad + (usableHeight * (1f - ((y[idx] - paddedMin) / adjustedRange)))
                val tooltipWidthDp = 110f
                val tooltipHeightDp = 60f
                val tooltipWidthPx = tooltipWidthDp * density.density
                val tooltipHeightPx = tooltipHeightDp * density.density
                val boxPaddingPx = 8f * density.density
                val tooltipY = (pointY - tooltipHeightPx - 10f).coerceIn(
                    topPad,
                    canvasSize.height - bottomPad - tooltipHeightPx - 10f
                )
                val tooltipX = (pointX - tooltipWidthPx / 2f).coerceIn(
                    boxPaddingPx,
                    canvasSize.width - tooltipWidthPx - boxPaddingPx
                )
                Card(
                    modifier = Modifier
                        .offset(x = (tooltipX / density.density).dp, y = (tooltipY / density.density).dp)
                        .width(tooltipWidthDp.dp)
                        .height(tooltipHeightDp.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isSystemInDarkTheme()) {
                                    Color.Black.copy(alpha = 0.4f)
                                } else {
                                    Color.White.copy(alpha = 0.6f)
                                },
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = labels[idx],
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                fontSize = 9.sp
                            )
                            Text(
                                text = String.format("%.1f", y[idx]),
                                style = MaterialTheme.typography.bodyMedium,
                                color = color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}