package com.ga3t.nytrisync.ui.calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ga3t.nytrisync.data.model.CalendarResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onDayClick: (String) -> Unit
) {
    val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.factory())
    val state = vm.uiState
    val listState = rememberLazyListState()
    LaunchedEffect(state.calendarData, vm.selectedYear) {
        if (!state.loading && state.calendarData.isNotEmpty()) {
            val now = LocalDate.now()
            if (vm.selectedYear == now.year) {
                val currentMonthIndex = now.monthValue - 1
                listState.scrollToItem(currentMonthIndex)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.prevYear() }) {
                        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "Prev year")
                    }
                    Text(
                        text = vm.selectedYear.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { vm.nextYear() }) {
                        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, "Next year")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                CalendarContent(
                    data = state.calendarData,
                    onDayClick = onDayClick,
                    listState = listState
                )
            }
        }
    }
}
@Composable
fun CalendarContent(
    data: List<CalendarResponse.CaloryDays>,
    onDayClick: (String) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    val grouped = remember(data) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val allMonths = (1..12).associateWith { month ->
            data.filter {
                try {
                    val date = LocalDate.parse(it.date, formatter)
                    date.monthValue == month
                } catch (e: Exception) { false }
            }
        }
        allMonths
    }
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        grouped.forEach { (monthInt, days) ->
            item {
                MonthSection(monthInt, days, onDayClick)
            }
        }
    }
}
@Composable
fun MonthSection(
    monthInt: Int,
    days: List<CalendarResponse.CaloryDays>,
    onDayClick: (String) -> Unit
) {
    val monthName = java.time.Month.of(monthInt).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val year = if (days.isNotEmpty()) LocalDate.parse(days[0].date).year else LocalDate.now().year
    val firstDayOfMonth = LocalDate.of(year, monthInt, 1)
    val startOffset = firstDayOfMonth.dayOfWeek.value - 1
    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(monthName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
            }
        }
        val totalSlots = startOffset + daysInMonth
        val rows = (totalSlots + 6) / 7
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (r in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (c in 0 until 7) {
                        val index = r * 7 + c
                        val dayNum = index - startOffset + 1
                        if (index >= startOffset && dayNum <= daysInMonth) {
                            val dateStr = LocalDate.of(year, monthInt, dayNum).toString()
                            val dayData = days.find { it.date == dateStr }
                            DayCell(
                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                dayNum = dayNum,
                                dayData = dayData,
                                dateStr = dateStr,
                                onClick = { onDayClick(dateStr) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DayCell(
    modifier: Modifier,
    dayNum: Int,
    dayData: CalendarResponse.CaloryDays?,
    dateStr: String,
    onClick: () -> Unit
) {
    val norm = dayData?.caloryNorm?.toFloat() ?: 0f
    val cons = dayData?.caloryCons?.toFloat() ?: 0f
    val ratio = if (norm > 0) (cons / norm).coerceIn(0f, 1f) else 0f
    val isGreen = cons > 0f
    val isFuture = try {
        LocalDate.parse(dateStr).isAfter(LocalDate.now())
    } catch (e: Exception) { true }
    val backgroundColor = if (isGreen) {
        Color(0xFF4CAF50).copy(alpha = 0.2f + (ratio * 0.8f))
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }
    val finalBg = if (isFuture) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) else backgroundColor
    val textColor = if (isGreen && ratio > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
    val finalTextColor = if (isFuture) MaterialTheme.colorScheme.onSurface.copy(alpha=0.3f) else textColor
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(finalBg)
            .clickable(enabled = !isFuture, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayNum.toString(),
            fontSize = 12.sp,
            color = finalTextColor
        )
    }
}