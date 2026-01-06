package com.ga3t.nytrisync.ui.calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ga3t.nytrisync.data.model.MainPageResponse
import com.ga3t.nytrisync.data.model.MealType
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.CalorieRepository
import com.ga3t.nytrisync.ui.home.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
class DayDetailViewModel(private val repository: CalorieRepository) : ViewModel() {
    var uiState by mutableStateOf(DetailUiState())
        private set
    fun load(date: String) {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true)
            val res = repository.getPageByDate(date)
            res.onSuccess {
                uiState = uiState.copy(loading = false, data = it)
            }.onFailure {
                uiState = uiState.copy(loading = false, error = it.message)
            }
        }
    }
    data class DetailUiState(
        val loading: Boolean = false,
        val error: String? = null,
        val data: MainPageResponse? = null
    )
    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DayDetailViewModel(CalorieRepository(RetrofitProvider.calorieApi)) as T
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    date: String,
    onBack: () -> Unit,
    onAddMealClick: (MealType) -> Unit
) {
    val vm: DayDetailViewModel = viewModel(factory = DayDetailViewModel.factory())
    LaunchedEffect(date) {
        vm.load(date)
    }
    val state = vm.uiState
    val title = try {
        val parsed = LocalDate.parse(date)
        parsed.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
    } catch (e: Exception) { date }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else if (state.data != null) {
                val d = state.data!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TodayCaloriesBlock(d.todayCalory)
                    WaterBlock(d.todayWater, onAddWaterClick = { })
                    MacrosRow(
                        carbs = d.todayCarbs,
                        protein = d.todayProtein,
                        fat = d.todayFat
                    )
                    MealsList(
                        meals = d.mealPage,
                        onAddMealClick = onAddMealClick
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}