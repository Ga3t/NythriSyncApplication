package com.ga3t.nytrisync.ui.details
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ga3t.nytrisync.data.model.*
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
private fun isoDate(y: Int, m: Int, d: Int) = "%04d-%02d-%02d".format(y, m, d)
data class OnboardingState(
    val currentWeight: BigDecimal = BigDecimal("70"),
    val height: BigDecimal = BigDecimal("170"),
    val birthYear: Int = 1998,
    val birthMonth: Int = 1,
    val birthDay: Int = 1,
    val sex: SexType? = null,
    val goal: GoalType? = GoalType.MAINTENANCE,
    val activityIndex: Int = 2,
    val wantedWeight: BigDecimal = BigDecimal("70"),
    val isLoading: Boolean = false,
    val error: String? = null,
    val result: UserDetailsResponse? = null,
    val showGoalMismatchWarning: Boolean = false
)
private val activityEntries = listOf(
    "MINIMUM_ACTIVITY" to BigDecimal("1.2"),
    "LOW_LEVEL_ACTIVITY" to BigDecimal("1.375"),
    "MEDIUM_ACTIVITY" to BigDecimal("1.55"),
    "HIGH_LEVEL_ACTIVITY" to BigDecimal("1.73"),
    "EXTREMELY_ACTIVITY" to BigDecimal("1.9")
)
class OnboardingViewModel(
    private val repo: UserDetailsRepository
) : ViewModel() {
    var ui by mutableStateOf(OnboardingState())
        private set
    fun setCurrentWeight(v: Float) { ui = ui.copy(currentWeight = BigDecimal(v.toString())) }
    fun setHeight(v: Float) { ui = ui.copy(height = BigDecimal(v.toString())) }
    fun setBirth(y: Int? = null, m: Int? = null, d: Int? = null) {
        ui = ui.copy(
            birthYear = y ?: ui.birthYear,
            birthMonth = m ?: ui.birthMonth,
            birthDay = d ?: ui.birthDay
        )
    }
    fun setSex(s: SexType) { ui = ui.copy(sex = s) }
    fun setGoal(g: GoalType) {
        val warn = shouldWarn(ui.currentWeight, ui.wantedWeight, g)
        ui = ui.copy(goal = g, showGoalMismatchWarning = warn)
    }
    fun setActivityIndex(idx: Int) {
        val clamped = idx.coerceIn(0, activityEntries.lastIndex)
        ui = ui.copy(activityIndex = clamped)
    }
    fun setWantedWeight(v: Float) {
        val wanted = BigDecimal(v.toString())
        ui = ui.copy(
            wantedWeight = wanted,
            showGoalMismatchWarning = shouldWarn(ui.currentWeight, wanted, ui.goal)
        )
    }
    private fun shouldWarn(current: BigDecimal, wanted: BigDecimal, goal: GoalType?): Boolean {
        if (goal == null) return false
        return when (goal) {
            GoalType.GAIN -> wanted < current
            GoalType.LOSS -> wanted > current
            GoalType.MAINTENANCE -> wanted != current
        }
    }
    fun applySuggestedGoal() {
        val newGoal = when {
            ui.wantedWeight > ui.currentWeight -> GoalType.GAIN
            ui.wantedWeight < ui.currentWeight -> GoalType.LOSS
            else -> GoalType.MAINTENANCE
        }
        ui = ui.copy(goal = newGoal, showGoalMismatchWarning = false)
    }
    fun submit() {
        val sex = ui.sex ?: return
        val goal = ui.goal ?: GoalType.MAINTENANCE
        val (_, activityCoef) = activityEntries[ui.activityIndex]
        val date = isoDate(ui.birthYear, ui.birthMonth, ui.birthDay)
        val dto = UserDetailsDto(
            currentWeight = ui.currentWeight,
            birthDay = date,
            sex = sex,
            activityType = activityCoef,
            goalType = goal,
            wantedWeight = ui.wantedWeight,
            height = ui.height
        )
        viewModelScope.launch {
            ui = ui.copy(isLoading = true, error = null)
            val res = repo.setDetails(dto)
            ui = ui.copy(isLoading = false)
            res.onSuccess { resp -> ui = ui.copy(result = resp) }
                .onFailure { e -> ui = ui.copy(error = e.message ?: "Submit failed") }
        }
    }
    fun activityLabel(): String = activityEntries[ui.activityIndex].first
    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = UserDetailsRepository(RetrofitProvider.userDetailsApi)
                return OnboardingViewModel(repo) as T
            }
        }
    }
}