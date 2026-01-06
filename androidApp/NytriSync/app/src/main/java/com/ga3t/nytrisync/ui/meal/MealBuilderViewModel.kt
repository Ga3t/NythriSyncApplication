package com.ga3t.nytrisync.ui.meal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ga3t.nytrisync.data.model.*
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.CalorieRepository
import com.ga3t.nytrisync.data.repository.FoodRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
data class MealItem(
    val type: String,
    val code: String,
    val name: String,
    val grams: BigDecimal,
    val per100: Nutrients100g
) {
    fun toDish(): MealDto.Dish {
        fun part(x: BigDecimal) = x.multiply(grams).divide(BigDecimal(100), 4, RoundingMode.HALF_UP)
        return MealDto.Dish(
            type = type,
            code = code,
            name = name,
            calories = part(per100.calories),
            fat = part(per100.fat),
            protein = part(per100.protein),
            carbohydrates = part(per100.carbohydrates),
            sugars = part(per100.sugars),
            fiber = part(per100.fiber)
        )
    }
}
data class Nutrients100g(
    val calories: BigDecimal,
    val fat: BigDecimal,
    val protein: BigDecimal,
    val carbohydrates: BigDecimal,
    val sugars: BigDecimal,
    val fiber: BigDecimal,
    val cholesterol: BigDecimal
)
data class DailyNorms(
    val protein: BigDecimal = BigDecimal.ZERO,
    val carbs: BigDecimal = BigDecimal.ZERO,
    val fat: BigDecimal = BigDecimal.ZERO,
    val sugar: BigDecimal = BigDecimal("50"),
    val cholesterol: BigDecimal = BigDecimal("300")
)
data class CustomFoodInput(
    val name: String = "",
    val grams: String = "100",
    val calories: String = "",
    val protein: String = "",
    val fat: String = "",
    val carbohydrates: String = "",
    val sugar: String = "",
    val fiber: String = "",
    val cholesterol: String = ""
)
data class MealBuilderUiState(
    val mealType: MealType,
    val targetDate: String,
    val isLoading: Boolean = false,
    val error: String? = null,
    val consumedTodayForMeal: BigDecimal = BigDecimal.ZERO,
    val dailyNorms: DailyNorms = DailyNorms(),
    val query: String = "",
    val page: Int = 0,
    val totalResults: String = "0",
    val results: List<FoodSearchResponse.FoodItem> = emptyList(),
    val isSearching: Boolean = false,
    val selected: FoodResponse? = null,
    val gramsInput: String = "100",
    val selectedBarcode: FoodDataResponse? = null,
    val barcodeGramsInput: String = "100",
    val showCustomFood: Boolean = false,
    val customFoodInput: CustomFoodInput = CustomFoodInput(),
    val existingDishes: List<MealDto.Dish> = emptyList(),
    val items: List<MealItem> = emptyList()
) {
    val totals: Nutrients100g
        get() {
            val existing = existingDishes
            val newDishes = items.map { it.toDish() }
            fun sum(selector: (MealDto.Dish) -> BigDecimal): BigDecimal =
                (existing + newDishes).fold(BigDecimal.ZERO) { acc, d -> acc + selector(d) }
            return Nutrients100g(
                calories = sum { it.calories },
                fat = sum { it.fat },
                protein = sum { it.protein },
                carbohydrates = sum { it.carbohydrates },
                sugars = sum { it.sugars },
                fiber = sum { it.fiber },
                cholesterol = BigDecimal.ZERO
            )
        }
}
class MealBuilderViewModel(
    private val mealType: MealType,
    private val targetDate: String,
    private val calorieRepo: CalorieRepository,
    private val foodRepo: FoodRepository
) : ViewModel() {
    var ui by mutableStateOf(MealBuilderUiState(mealType = mealType, targetDate = targetDate))
        private set
    init {
        viewModelScope.launch {
            calorieRepo.getMainPage().onSuccess { mp ->
                val cal = mp.mealPage.find { it.mealType.equals(mealType.name, true) }?.caloryCons ?: BigDecimal.ZERO
                ui = ui.copy(
                    consumedTodayForMeal = cal,
                    dailyNorms = DailyNorms(
                        protein = mp.todayProtein.todayProteinNorm,
                        carbs = mp.todayCarbs.todayCarbsNorm,
                        fat = mp.todayFat.todayFatNorm
                    )
                )
            }
        }
        viewModelScope.launch {
            ui = ui.copy(isLoading = true, error = null)
            val res = calorieRepo.getMealByDate(date = targetDate, mealType = mealType.name)
            ui = ui.copy(isLoading = false)
            res.onSuccess { r ->
                val existing = r.mealDto?.dishes?.dish ?: emptyList()
                ui = ui.copy(existingDishes = existing)
            }.onFailure { e ->
                ui = ui.copy(error = e.message ?: "Show meal error (ignored)")
            }
        }
    }
    fun onQueryChange(q: String) { ui = ui.copy(query = q) }
    private var searchJob: Job? = null
    fun search(page: Int = 0) {
        val q = ui.query.trim()
        if (q.isEmpty()) { ui = ui.copy(results = emptyList(), page = 0, totalResults = "0"); return }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            ui = ui.copy(isSearching = true, error = null)
            val res = foodRepo.search(q, page)
            ui = ui.copy(isSearching = false)
            res.onSuccess { r ->
                ui = ui.copy(
                    page = page,
                    totalResults = r.totalResults,
                    results = if (page == 0) r.items else ui.results + r.items
                )
            }.onFailure { e ->
                ui = ui.copy(error = e.message ?: "Search error")
            }
        }
    }
    fun loadMore() = search(ui.page + 1)
    fun selectFood(id: String) {
        viewModelScope.launch {
            ui = ui.copy(isLoading = true, error = null)
            val res = foodRepo.details(id)
            ui = ui.copy(isLoading = false)
            res.onSuccess { f -> ui = ui.copy(selected = f, gramsInput = "100") }
                .onFailure { e -> ui = ui.copy(error = e.message ?: "Details error") }
        }
    }
    fun clearSelection() { ui = ui.copy(selected = null) }
    fun setGramsInput(v: String) { ui = ui.copy(gramsInput = v.filter { it.isDigit() }) }
    fun addSelectedFood() {
        val f = ui.selected ?: return
        val grams = ui.gramsInput.toBigDecimalOrNull() ?: BigDecimal("100")
        val item = MealItem(
            type = "FOODSECRET",
            code = f.foodId,
            name = f.name,
            grams = grams,
            per100 = Nutrients100g(
                calories = f.calories,
                fat = f.fat,
                protein = f.protein,
                carbohydrates = f.carbohydrate,
                sugars = f.sugar ?: BigDecimal.ZERO,
                fiber = f.fiber ?: BigDecimal.ZERO,
                cholesterol = BigDecimal.ZERO
            )
        )
        ui = ui.copy(items = ui.items + item, selected = null)
    }
    fun removeItem(index: Int) {
        if (index in ui.items.indices) {
            val newList = ui.items.toMutableList().also { it.removeAt(index) }
            ui = ui.copy(items = newList)
        }
    }
    fun clearResults() { ui = ui.copy(results = emptyList(), page = 0, totalResults = "0") }
    fun onBarcodeDetected(barcode: String) {
        viewModelScope.launch {
            ui = ui.copy(isLoading = true, error = null)
            val res = foodRepo.byBarcode(barcode)
            ui = ui.copy(isLoading = false)
            res.onSuccess { fd -> ui = ui.copy(selectedBarcode = fd, barcodeGramsInput = "100") }
                .onFailure { e -> ui = ui.copy(error = e.message ?: "Barcode error") }
        }
    }
    fun setBarcodeGramsInput(v: String) { ui = ui.copy(barcodeGramsInput = v.filter { c -> c.isDigit() || c == '.' }) }
    fun clearBarcodeSelection() { ui = ui.copy(selectedBarcode = null) }
    fun showCustomFood() { ui = ui.copy(showCustomFood = true) }
    fun hideCustomFood() { ui = ui.copy(showCustomFood = false, customFoodInput = CustomFoodInput()) }
    fun updateCustomFoodInput(input: CustomFoodInput) { ui = ui.copy(customFoodInput = input) }
    fun addCustomFood() {
        val input = ui.customFoodInput
        val name = input.name.trim()
        if (name.isEmpty()) {
            ui = ui.copy(error = "Enter food name")
            return
        }
        val grams = input.grams.toBigDecimalOrNull() ?: BigDecimal("100")
        if (grams <= BigDecimal.ZERO) {
            ui = ui.copy(error = "Enter valid weight")
            return
        }
        val calories = input.calories.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val protein = input.protein.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val fat = input.fat.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val carbohydrates = input.carbohydrates.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val sugar = input.sugar.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val fiber = input.fiber.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val cholesterol = input.cholesterol.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val ratio = BigDecimal(100).divide(grams, 4, RoundingMode.HALF_UP)
        val per100 = Nutrients100g(
            calories = calories.multiply(ratio),
            protein = protein.multiply(ratio),
            fat = fat.multiply(ratio),
            carbohydrates = carbohydrates.multiply(ratio),
            sugars = sugar.multiply(ratio),
            fiber = fiber.multiply(ratio),
            cholesterol = cholesterol.multiply(ratio)
        )
        val item = MealItem(
            type = "CUSTOM",
            code = "custom_${System.currentTimeMillis()}",
            name = name,
            grams = grams,
            per100 = per100
        )
        ui = ui.copy(items = ui.items + item, showCustomFood = false, customFoodInput = CustomFoodInput())
    }
    fun addBarcodeSelected() {
        val fd = ui.selectedBarcode ?: return
        val grams = ui.barcodeGramsInput.toBigDecimalOrNull() ?: BigDecimal("100")
        val item = MealItem(
            type = "OPENFOODFACT",
            code = fd.code,
            name = fd.name,
            grams = grams,
            per100 = Nutrients100g(
                calories = fd.fat.multiply(BigDecimal.valueOf(9))
                    .add(fd.carbohydrates.multiply(BigDecimal.valueOf(4)))
                    .add(fd.protein.multiply(BigDecimal.valueOf(4))),
                fat = fd.fat,
                protein = fd.protein,
                carbohydrates = fd.carbohydrates,
                sugars = fd.sugar ?: BigDecimal.ZERO,
                fiber = fd.fiber ?: BigDecimal.ZERO,
                cholesterol = fd.cholesterol ?: BigDecimal.ZERO
            )
        )
        ui = ui.copy(items = ui.items + item, selectedBarcode = null)
    }
    fun save(onSuccess: () -> Unit) {
        val combinedDishes = ui.existingDishes + ui.items.map { it.toDish() }
        if (combinedDishes.isEmpty()) { ui = ui.copy(error = "Add at least one item"); return }
        val dto = MealDto(
            dishes = MealDto.Dishes(combinedDishes)
        )
        viewModelScope.launch {
            ui = ui.copy(isLoading = true, error = null)
            val res = calorieRepo.saveMeal(date = ui.targetDate, mealType = ui.mealType.name, dto = dto)
            ui = ui.copy(isLoading = false)
            res.onSuccess { onSuccess() }
                .onFailure { e -> ui = ui.copy(error = e.message ?: "Save error") }
        }
    }
    companion object {
        fun factory(mealType: MealType, date: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val calRepo = CalorieRepository(RetrofitProvider.calorieApi)
                val foodRepo = FoodRepository(RetrofitProvider.foodSecretApi, RetrofitProvider.productApi)
                return MealBuilderViewModel(mealType, date, calRepo, foodRepo) as T
            }
        }
    }
}