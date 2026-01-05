package com.ga3t.nytrisync.ui.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ga3t.nytrisync.data.model.UpdateUserDetailsDto
import com.ga3t.nytrisync.data.model.UserInfoResponse
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ProfileUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val info: UserInfoResponse? = null,
    val editingOther: Boolean = false,
    val showOtherWarn: Boolean = false,
    val editBirth: LocalDate? = null,
    val editSex: String? = null,
    val editHeight: BigDecimal? = null,
    val editWanted: BigDecimal? = null,
    val editActivity: BigDecimal? = null,
    val editGoal: String? = null
)

class ProfileViewModel(
    private val repo: UserDetailsRepository
) : ViewModel() {

    var ui by mutableStateOf(ProfileUiState())
        private set

    private val iso = DateTimeFormatter.ISO_DATE

    init { load() }

    fun load() {
        ui = ui.copy(loading = true, error = null)
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            repo.userInfo()
                .onSuccess { info ->
                    ui = ui.copy(
                        loading = false,
                        info = info,
                        editBirth = LocalDate.parse(info.birthday_date),
                        editSex = info.sex,
                        editHeight = info.height,
                        editWanted = info.weight,
                        editActivity = info.activity_type.toBigDecimalOrNull() ?: BigDecimal("1.2"),
                        editGoal = info.goalType
                    )
                }
                .onFailure { e ->
                    ui = ui.copy(loading = false, error = e.message ?: "Load failed")
                }
        }
    }

    fun setNewWeight(kg: BigDecimal) {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            repo.setNewWeighing(kg)
                .onSuccess { newW ->
                    ui.info?.let { cur -> ui = ui.copy(info = cur.copy(weight = newW)) }
                }
                .onFailure { e -> ui = ui.copy(error = e.message ?: "Weighing failed") }
        }
    }

    fun updateActivity(target: BigDecimal) =
        applyUpdate(activity = target)

    fun updateGoal(target: String) =
        applyUpdate(goal = target)

    fun showOtherWarning() { ui = ui.copy(showOtherWarn = true) }
    fun cancelOtherWarning() { ui = ui.copy(showOtherWarn = false) }
    fun startOtherEdit() { ui = ui.copy(showOtherWarn = false, editingOther = true) }
    fun cancelOtherEdit() { ui = ui.copy(editingOther = false) }

    fun setBirth(date: LocalDate) { ui = ui.copy(editBirth = date) }
    fun setSex(sex: String) { ui = ui.copy(editSex = sex) }
    fun setHeight(h: BigDecimal) { ui = ui.copy(editHeight = h) }
    fun setWanted(w: BigDecimal) { ui = ui.copy(editWanted = w) }

    fun saveOther() =
        applyUpdate(
            birth = ui.editBirth,
            sex = ui.editSex,
            height = ui.editHeight,
            wanted = ui.editWanted
        )

    private fun applyUpdate(
        activity: BigDecimal? = null,
        goal: String? = null,
        birth: LocalDate? = null,
        sex: String? = null,
        height: BigDecimal? = null,
        wanted: BigDecimal? = null
    ) {
        val info = ui.info ?: return
        val dto = UpdateUserDetailsDto(
            curentWeight = info.weight,
            birthDay = (birth ?: LocalDate.parse(info.birthday_date)).format(iso),
            sex = sex ?: info.sex,
            activityType = activity ?: (info.activity_type.toBigDecimalOrNull() ?: BigDecimal("1.2")),
            goalType = goal ?: info.goalType,
            wantedWeight = wanted ?: info.weight,
            height = height ?: info.height
        )
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            repo.updateUserDetails(dto)
                .onSuccess { load() }
                .onFailure { e -> ui = ui.copy(error = e.message ?: "Update failed") }
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(UserDetailsRepository(RetrofitProvider.userDetailsApi)) as T
            }
        }
    }
}

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory())
    val ui = vm.ui

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            ui.loading -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            ui.error != null -> Column(
                Modifier.padding(padding).fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: ${ui.error}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = vm::load) { Text("Retry") }
            }
            else -> {
                val info = ui.info!!
                Column(
                    Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ElevatedCard {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Current weight", style = MaterialTheme.typography.titleMedium)
                            Text("${info.weight} kg", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                            NewWeightSection(
                                initial = info.weight,
                                onConfirm = { vm.setNewWeight(it) }
                            )
                        }
                    }

                    ElevatedCard {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Activity level", style = MaterialTheme.typography.titleMedium)
                            ActivityChips(
                                selected = info.activity_type.toBigDecimalOrNull() ?: BigDecimal("1.2"),
                                onSelect = { vm.updateActivity(it) }
                            )
                        }
                    }


                    ElevatedCard {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Goal", style = MaterialTheme.typography.titleMedium)
                            GoalChips(selected = info.goalType, onSelect = { vm.updateGoal(it) })
                        }
                    }

                    ElevatedCard {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Other", style = MaterialTheme.typography.titleMedium)
                            if (!ui.editingOther) {
                                Text("Birth date: ${info.birthday_date}")
                                Text("Sex: ${info.sex}")
                                Text("Height: ${info.height} cm")
                                Spacer(Modifier.height(6.dp))
                                OutlinedButton(onClick = vm::showOtherWarning, shape = MaterialTheme.shapes.large) {
                                    Text("Edit other")
                                }
                            } else {
                                OtherEditor(
                                    birth = ui.editBirth ?: LocalDate.parse(info.birthday_date),
                                    sex = ui.editSex ?: info.sex,
                                    height = ui.editHeight ?: info.height,
                                    wanted = ui.editWanted ?: info.weight,
                                    onBirth = vm::setBirth,
                                    onSex = vm::setSex,
                                    onHeight = vm::setHeight,
                                    onWanted = vm::setWanted,
                                    onSave = vm::saveOther,
                                    onCancel = vm::cancelOtherEdit
                                )
                            }
                        }
                    }
                }

                if (ui.showOtherWarn) {
                    AlertDialog(
                        onDismissRequest = vm::cancelOtherWarning,
                        title = { Text("Warning") },
                        text = { Text("Changing these fields may affect your previous data. Continue?") },
                        confirmButton = { TextButton(onClick = vm::startOtherEdit) { Text("Continue") } },
                        dismissButton = { TextButton(onClick = vm::cancelOtherWarning) { Text("Cancel") } }
                    )
                }
            }
        }
    }
}

@Composable
private fun NewWeightSection(
    initial: BigDecimal,
    onConfirm: (BigDecimal) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    if (!show) {
        OutlinedButton(onClick = { show = true }, shape = MaterialTheme.shapes.large) { Text("New weighing") }
        return
    }
    var current by remember(initial) { mutableStateOf(initial.toInt()) }
    AlertDialog(
        onDismissRequest = { show = false },
        title = { Text("New weight") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${current} kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    factory = { ctx ->
                        android.widget.NumberPicker(ctx).apply {
                            descendantFocusability = android.widget.NumberPicker.FOCUS_BLOCK_DESCENDANTS
                            wrapSelectorWheel = true
                            minValue = 30
                            maxValue = 300
                            value = current
                            setOnValueChangedListener { _, _, newVal -> current = newVal }
                        }
                    },
                    update = { picker ->
                        if (picker.value != current) picker.value = current
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(BigDecimal(current))
                show = false
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = { show = false }) { Text("Cancel") } }
    )
}

@Composable
private fun ActivityChips(
    selected: BigDecimal,
    onSelect: (BigDecimal) -> Unit
) {

    val presets = listOf(
        BigDecimal("1.2")   to "MINIMUM_ACTIVITY",
        BigDecimal("1.375") to "LOW_LEVEL_ACTIVITY",
        BigDecimal("1.55")  to "MEDIUM_ACTIVITY",
        BigDecimal("1.73")  to "HIGH_LEVEL_ACTIVITY",
        BigDecimal("1.9")   to "EXTREMELY_ACTIVITY"
    )

    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { (v, label) ->
            FilterChip(
                selected = selected.compareTo(v) == 0,
                onClick = { onSelect(v) },
                label = { Text(label) }
            )
        }
    }
}
@Composable
private fun GoalChips(
    selected: String,
    onSelect: (String) -> Unit
) {

    val options = listOf(
        "LOSS"     to "Lose",
        "MAINTAIN" to "Maintain",
        "GAIN"     to "Gain"
    )

    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (v, label) ->
            FilterChip(
                selected = selected == v,
                onClick = { onSelect(v) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun OtherEditor(
    birth: LocalDate,
    sex: String,
    height: BigDecimal,
    wanted: BigDecimal,
    onBirth: (LocalDate) -> Unit,
    onSex: (String) -> Unit,
    onHeight: (BigDecimal) -> Unit,
    onWanted: (BigDecimal) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val ctx = LocalContext.current
    val iso = DateTimeFormatter.ISO_DATE
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Birth date: ${birth.format(iso)}")
            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        ctx,
                        { _, y, m, d -> onBirth(LocalDate.of(y, m + 1, d)) },
                        birth.year, birth.monthValue - 1, birth.dayOfMonth
                    ).show()
                },
                shape = MaterialTheme.shapes.large
            ) { Text("Change") }
        }
        Text("Sex")
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("MALE" to "Male", "FEMALE" to "Female").forEach { (v, label) ->
                FilterChip(selected = sex == v, onClick = { onSex(v) }, label = { Text(label) })
            }
        }

        Text("Height (cm)")
        InlineNumberPicker(
            value = height.toInt(),
            range = 120..220,
            onChange = { onHeight(BigDecimal(it)) }
        )

        Text("Wanted weight (kg)")
        InlineNumberPicker(
            value = wanted.toInt(),
            range = 30..300,
            onChange = { onWanted(BigDecimal(it)) }
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large) { Text("Cancel") }
            Button(onClick = onSave, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large) { Text("Save") }
        }
    }
}

@Composable
private fun InlineNumberPicker(
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        factory = { ctx ->
            android.widget.NumberPicker(ctx).apply {
                descendantFocusability = android.widget.NumberPicker.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = true
                minValue = range.first
                maxValue = range.last
                this.value = value
                setOnValueChangedListener { _, _, newVal -> onChange(newVal) }
            }
        },
        update = { picker ->
            if (picker.value != value) picker.value = value
        }
    )
}