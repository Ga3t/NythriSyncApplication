@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.ga3t.nytrisync.ui.notifications

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.util.Locale

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current

    var brEnabled by remember { mutableStateOf(false) }
    val brTimes = remember { mutableStateListOf<String>() }

    var lnEnabled by remember { mutableStateOf(false) }
    val lnTimes = remember { mutableStateListOf<String>() }

    var dnEnabled by remember { mutableStateOf(false) }
    val dnTimes = remember { mutableStateListOf<String>() }

    var snEnabled by remember { mutableStateOf(false) }
    var snMode by remember { mutableStateOf("ON_TIME") }
    val snTimes = remember { mutableStateListOf<String>() }
    val snIntervals = remember { mutableStateListOf<Int>() }

    var waterEnabled by remember { mutableStateOf(false) }
    var waterMode by remember { mutableStateOf("ON_TIME") }
    val waterTimes = remember { mutableStateListOf<String>() }
    val waterIntervals = remember { mutableStateListOf<Int>() }


    LaunchedEffect(Unit) {
        NotificationsPrefs.load()?.let { cfg ->
            brEnabled = cfg.breakfastEnabled
            brTimes.clear(); brTimes.addAll(cfg.breakfastTimes.take(1))

            lnEnabled = cfg.lunchEnabled
            lnTimes.clear(); lnTimes.addAll(cfg.lunchTimes.take(1))

            dnEnabled = cfg.dinnerEnabled
            dnTimes.clear(); dnTimes.addAll(cfg.dinnerTimes.take(1))

            snEnabled = cfg.snackEnabled
            snMode = cfg.snackMode
            snTimes.clear(); snTimes.addAll(cfg.snackTimes)
            snIntervals.clear(); snIntervals.addAll(cfg.snackIntervals)

            waterEnabled = cfg.waterEnabled
            waterMode = cfg.waterMode
            waterTimes.clear(); waterTimes.addAll(cfg.waterTimes)
            waterIntervals.clear(); waterIntervals.addAll(cfg.waterIntervals)
        }
    }

    fun currentConfig(): NotificationsConfig = NotificationsConfig(
        breakfastEnabled = brEnabled,
        breakfastTimes = brTimes.take(1),
        lunchEnabled = lnEnabled,
        lunchTimes = lnTimes.take(1),
        dinnerEnabled = dnEnabled,
        dinnerTimes = dnTimes.take(1),
        snackEnabled = snEnabled,
        snackMode = snMode,
        snackTimes = snTimes.toList(),
        snackIntervals = snIntervals.toList(),
        waterEnabled = waterEnabled,
        waterMode = waterMode,
        waterTimes = waterTimes.toList(),
        waterIntervals = waterIntervals.toList()
    )

    fun requestExactIfNeeded(): Boolean {
        if (Build.VERSION.SDK_INT >= 31) {
            val am = ctx.getSystemService(AlarmManager::class.java)
            if (!am.canScheduleExactAlarms()) {
                ctx.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                return false
            }
        }
        return true
    }

    fun persistNow() {
        val prev = NotificationsPrefs.load()
        val cfg = currentConfig()
        if (prev != null) ReminderScheduler.cancelAll(ctx, prev)
        NotificationsPrefs.save(cfg)
        ReminderScheduler.scheduleAll(ctx, cfg)
    }

    fun ensureSingleTime(list: MutableList<String>) {
        if (list.isEmpty()) list += "12:00" else if (list.size > 1) {
            val first = list.first()
            list.clear(); list += first
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Meals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)


            MealToggleRow(
                title = "BREAKFAST",
                enabled = brEnabled,
                onToggle = { enabled ->
                    brEnabled = enabled
                    if (enabled) { ensureSingleTime(brTimes) } else brTimes.clear()
                    persistNow()
                },
                time = brTimes.firstOrNull(),
                onTimeClick = {
                    pickTime(ctx) { t -> brTimes.clear(); brTimes += t; persistNow() }
                }
            )


            MealToggleRow(
                title = "LUNCH",
                enabled = lnEnabled,
                onToggle = { enabled ->
                    lnEnabled = enabled
                    if (enabled) { ensureSingleTime(lnTimes) } else lnTimes.clear()
                    persistNow()
                },
                time = lnTimes.firstOrNull(),
                onTimeClick = {
                    pickTime(ctx) { t -> lnTimes.clear(); lnTimes += t; persistNow() }
                }
            )


            MealToggleRow(
                title = "DINNER",
                enabled = dnEnabled,
                onToggle = { enabled ->
                    dnEnabled = enabled
                    if (enabled) { ensureSingleTime(dnTimes) } else dnTimes.clear()
                    persistNow()
                },
                time = dnTimes.firstOrNull(),
                onTimeClick = {
                    pickTime(ctx) { t -> dnTimes.clear(); dnTimes += t; persistNow() }
                }
            )


            ElevatedCard {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SNACK", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        GreenSwitch(
                            checked = snEnabled,
                            onCheckedChange = {
                                snEnabled = it
                                if (!it) {
                                    snTimes.clear(); snIntervals.clear()
                                } else {
                                    if (snMode == "ON_TIME" && snTimes.isEmpty()) snTimes += "12:00"
                                    if (snMode == "PERIOD" && snIntervals.isEmpty()) snIntervals += 30
                                }
                                persistNow()
                            }
                        )
                    }

                    if (snEnabled) {
                        ModeChips(
                            selected = snMode,
                            onSelect = {
                                snMode = it
                                if (it == "ON_TIME") { if (snTimes.isEmpty()) snTimes += "12:00"; snIntervals.clear() }
                                else { if (snIntervals.isEmpty()) snIntervals += 30; snTimes.clear() }
                                persistNow()
                            }
                        )

                        if (snMode == "ON_TIME") {

                            TimesEditor(
                                times = snTimes,
                                onAdd = { pickTime(ctx) { t -> snTimes += t; persistNow() } },
                                onRemove = { idx -> snTimes.removeAt(idx); persistNow() }
                            )
                        } else {
                            IntervalSingleSelector(
                                selected = snIntervals.firstOrNull(),
                                onSelect = { m -> snIntervals.clear(); snIntervals += m; persistNow() }
                            )
                        }
                    }
                }
            }


            ElevatedCard {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("WATER", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        GreenSwitch(
                            checked = waterEnabled,
                            onCheckedChange = {
                                waterEnabled = it
                                if (!it) {
                                    waterTimes.clear(); waterIntervals.clear()
                                } else {
                                    if (waterMode == "ON_TIME" && waterTimes.isEmpty()) waterTimes += "12:00"
                                    if (waterMode == "PERIOD" && waterIntervals.isEmpty()) waterIntervals += 30
                                }
                                persistNow()
                            }
                        )
                    }

                    if (waterEnabled) {
                        ModeChips(
                            selected = waterMode,
                            onSelect = {
                                waterMode = it
                                if (it == "ON_TIME") { if (waterTimes.isEmpty()) waterTimes += "12:00"; waterIntervals.clear() }
                                else { if (waterIntervals.isEmpty()) waterIntervals += 30; waterTimes.clear() }
                                persistNow()
                            }
                        )

                        if (waterMode == "ON_TIME") {
                            TimesEditor(
                                times = waterTimes,
                                onAdd = { pickTime(ctx) { t -> waterTimes += t; persistNow() } },
                                onRemove = { idx -> waterTimes.removeAt(idx); persistNow() }
                            )
                        } else {
                            IntervalSingleSelector(
                                selected = waterIntervals.firstOrNull(),
                                onSelect = { m -> waterIntervals.clear(); waterIntervals += m; persistNow() }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SingleTimeEditor(
    time: String,
    onClick: () -> Unit
) {
    FilledTonalButton (
        onClick = onClick,
        shape = MaterialTheme.shapes.large
    ) {
        Text(time)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntervalSingleSelector(
    selected: Int?,
    onSelect: (Int) -> Unit
) {
    val presets = listOf(15, 30, 45, 60, 90)


    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { m ->
                FilterChip(
                    selected = (selected == m),
                    onClick = { onSelect(m) },
                    label = { Text("${m}m", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}


@Composable
private fun GreenSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color(0xFF2E7D32),
            checkedBorderColor = Color(0xFF2E7D32),
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun MealToggleRow(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    time: String?,
    onTimeClick: () -> Unit
) {
    ElevatedCard {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                GreenSwitch(checked = enabled, onCheckedChange = onToggle)
            }
            if (enabled) {
                OutlinedButton(onClick = onTimeClick, shape = MaterialTheme.shapes.large) {
                    Text(time ?: "12:00")
                }
            }
        }
    }
}

@Composable
private fun ModeChips(selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilterChip(
            selected = selected == "PERIOD",
            onClick = { onSelect("PERIOD") },
            label = { Text("Period") }
        )
        FilterChip(
            selected = selected == "ON_TIME",
            onClick = { onSelect("ON_TIME") },
            label = { Text("On time") }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimesEditor(
    times: MutableList<String>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit
) {
    if (times.isEmpty()) {
        Text("No times yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                times.forEachIndexed { i, t ->
                    InputChip(
                        selected = false,
                        onClick = { },
                        label = { Text(t, style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.clickable { onRemove(i) }
                            )
                        }
                    )
                }
            }
        }
    }
    OutlinedButton(onClick = onAdd, shape = MaterialTheme.shapes.large) { Text("+ Add time") }
}

@Composable
private fun IntervalEditor(
    intervals: MutableList<Int>,
    onAddQuick: (Int) -> Unit,
    onAddCustom: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(15, 30, 45, 60, 90).forEach { m ->
            AssistChip(onClick = { onAddQuick(m) }, label = { Text("${m}m") })
        }
    }
    Spacer(Modifier.height(8.dp))
    if (intervals.isEmpty()) {
        Text("No intervals yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        LazyRow(contentPadding = PaddingValues(end = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(intervals) { i, m ->
                InputChip(
                    selected = false,
                    onClick = { },
                    label = { Text("${m}m") },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.clickable { onRemove(i) }
                        )
                    }
                )
            }
        }
    }
    var custom by remember { mutableStateOf("") }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = custom,
            onValueChange = { v -> custom = v.filter { it.isDigit() } },
            label = { Text("Custom (min, <= 1440)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(
            onClick = { custom.toIntOrNull()?.let { if (it in 1..1440) onAddCustom(it); custom = "" } },
            shape = MaterialTheme.shapes.large
        ) { Text("Add") }
    }
}

private fun pickTime(ctx: android.content.Context, onPicked: (String) -> Unit) {
    val now = LocalTime.now()
    TimePickerDialog(
        ctx,
        { _, h, m -> onPicked(String.format(Locale.getDefault(), "%02d:%02d", h, m)) },
        now.hour,
        now.minute,
        true
    ).show()
}