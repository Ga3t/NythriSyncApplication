@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.ga3t.nytrisync.ui.meal
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ga3t.nytrisync.data.model.FoodSearchResponse
import com.ga3t.nytrisync.data.model.MealType
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min
private fun BigDecimal?.fmt(): String =
    this?.stripTrailingZeros()?.toPlainString() ?: "—"
@Composable
fun MealBuilderScreen(
    mealType: MealType,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onScanClick: () -> Unit = {},
    scannedBarcode: String? = null,
    clearScannedBarcode: () -> Unit = {},
    date: String
) {
    val vm: MealBuilderViewModel = viewModel(factory = MealBuilderViewModel.factory(mealType, date))
    val ui = vm.ui
    LaunchedEffect(scannedBarcode) {
        if (!scannedBarcode.isNullOrBlank()) {
            vm.onBarcodeDetected(scannedBarcode)
            clearScannedBarcode()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mealType.name.lowercase().replaceFirstChar { it.titlecase() }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large
                    ) { Text("Cancel") }
                    Button(
                        onClick = { vm.save { onSaved() } },
                        enabled = !ui.isLoading && ui.items.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (ui.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Save meal")
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            Column(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ui.query,
                        onValueChange = { vm.onQueryChange(it) },
                        label = { Text("Search food") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onScanClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scan barcode")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.search(0) }, enabled = !ui.isSearching) { Text("Search") }
                    OutlinedButton(onClick = { vm.showCustomFood() }) { Text("Add custom") }
                }
                ElevatedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "Consumed: ${ui.consumedTodayForMeal.stripTrailingZeros().toPlainString()} kcal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                SummaryMacros(ui)
                if (ui.items.isNotEmpty()) {
                    Text("Added items", style = MaterialTheme.typography.titleMedium)
                    ui.items.forEachIndexed { index, it ->
                        ElevatedCard {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(it.name, fontWeight = FontWeight.Medium)
                                    Text("${it.grams.stripTrailingZeros().toPlainString()} g")
                                }
                                TextButton(onClick = { vm.removeItem(index) }) { Text("Remove") }
                            }
                        }
                    }
                }
                if (ui.error != null) {
                    Text(ui.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            if (ui.isSearching || ui.results.isNotEmpty()) {
                ResultsOverlay(
                    query = ui.query,
                    onQueryChange = vm::onQueryChange,
                    onSubmitSearch = { vm.search(0) },
                    total = ui.totalResults,
                    items = ui.results,
                    isLoading = ui.isSearching,
                    onItemClick = { item ->
                        vm.selectFood(item.id)
                        vm.clearResults()
                    },
                    onLoadMore = { vm.loadMore() },
                    onClose = { vm.clearResults() }
                )
            }
            ui.selected?.let { s ->
                ModalBottomSheet(onDismissRequest = { vm.clearSelection() }) {
                        Column(
                        Modifier
                            .padding(16.dp)
                            .imePadding()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(s.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = ui.gramsInput,
                            onValueChange = { vm.setGramsInput(it) },
                            label = { Text("Amount, g") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        val g = ui.gramsInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        val ratio = if (g > BigDecimal.ZERO) g.divide(BigDecimal(100), 4, RoundingMode.HALF_UP) else BigDecimal.ZERO
                        val p = s.protein.multiply(ratio)
                        val c = s.carbohydrate.multiply(ratio)
                        val f = s.fat.multiply(ratio)
                        val sugar = (s.sugar ?: BigDecimal.ZERO).multiply(ratio)
                        val cholesterol = (s.cholesterol ?: BigDecimal.ZERO).multiply(ratio)
                        val proteinCal = p.multiply(BigDecimal(4))
                        val carbsCal = c.multiply(BigDecimal(4))
                        val fatCal = f.multiply(BigDecimal(9))
                        val totalCal = proteinCal.add(carbsCal).add(fatCal)
                        val norms = ui.dailyNorms
                        val proteinPercent = if (norms.protein > BigDecimal.ZERO)
                            p.multiply(BigDecimal(100)).divide(norms.protein, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val carbsPercent = if (norms.carbs > BigDecimal.ZERO)
                            c.multiply(BigDecimal(100)).divide(norms.carbs, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val fatPercent = if (norms.fat > BigDecimal.ZERO)
                            f.multiply(BigDecimal(100)).divide(norms.fat, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val sugarPercent = if (norms.sugar > BigDecimal.ZERO)
                            sugar.multiply(BigDecimal(100)).divide(norms.sugar, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val cholesterolPercent = if (norms.cholesterol > BigDecimal.ZERO)
                            cholesterol.multiply(BigDecimal(100)).divide(norms.cholesterol, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        ElevatedCard {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("For ${g.stripTrailingZeros().toPlainString()} g", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text("Calories: ${totalCal.stripTrailingZeros().toPlainString()} kcal", style = MaterialTheme.typography.bodyMedium)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MacroSemiCircle(
                                        title = "Protein",
                                        grams = p,
                                        percent = proteinPercent,
                                        color = Color(0xFF42A5F5),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MacroSemiCircle(
                                        title = "Carbs",
                                        grams = c,
                                        percent = carbsPercent,
                                        color = Color(0xFF7CB342),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MacroSemiCircle(
                                        title = "Fat",
                                        grams = f,
                                        percent = fatPercent,
                                        color = Color(0xFFEF5350),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MacroSemiCircle(
                                        title = "Sugar",
                                        grams = sugar,
                                        percent = sugarPercent,
                                        color = Color(0xFFFFA726),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MacroSemiCircle(
                                        title = "Cholesterol",
                                        grams = cholesterol,
                                        percent = cholesterolPercent,
                                        color = Color(0xFFAB47BC),
                                        modifier = Modifier.weight(1f),
                                        unit = "mg"
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { vm.clearSelection() }) { Text("Close") }
                            Button(onClick = { vm.addSelectedFood() }) { Text("Add to meal") }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            if (ui.showCustomFood) {
                ModalBottomSheet(onDismissRequest = { vm.hideCustomFood() }) {
                    CustomFoodInputSheet(
                        input = ui.customFoodInput,
                        onInputChange = vm::updateCustomFoodInput,
                        onAdd = { vm.addCustomFood() },
                        onCancel = { vm.hideCustomFood() }
                    )
                }
            }
            ui.selectedBarcode?.let { b ->
                ModalBottomSheet(onDismissRequest = { vm.clearBarcodeSelection() }) {
                        Column(
                        Modifier
                            .padding(16.dp)
                            .imePadding()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(b.name.lowercase().replaceFirstChar { it.titlecase() }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = ui.barcodeGramsInput,
                            onValueChange = { vm.setBarcodeGramsInput(it) },
                            label = { Text("Amount, g") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        val g = ui.barcodeGramsInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        val ratio = if (g > BigDecimal.ZERO) g.divide(BigDecimal(100), 4, RoundingMode.HALF_UP) else BigDecimal.ZERO
                        val p = b.protein.multiply(ratio)
                        val c = b.carbohydrates.multiply(ratio)
                        val f = b.fat.multiply(ratio)
                        val sugar = (b.sugar ?: BigDecimal.ZERO).multiply(ratio)
                        val cholesterol = (b.cholesterol ?: BigDecimal.ZERO).multiply(ratio)
                        val proteinCal = p.multiply(BigDecimal(4))
                        val carbsCal = c.multiply(BigDecimal(4))
                        val fatCal = f.multiply(BigDecimal(9))
                        val totalCal = proteinCal.add(carbsCal).add(fatCal)
                        val norms = ui.dailyNorms
                        val proteinPercent = if (norms.protein > BigDecimal.ZERO)
                            p.multiply(BigDecimal(100)).divide(norms.protein, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val carbsPercent = if (norms.carbs > BigDecimal.ZERO)
                            c.multiply(BigDecimal(100)).divide(norms.carbs, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val fatPercent = if (norms.fat > BigDecimal.ZERO)
                            f.multiply(BigDecimal(100)).divide(norms.fat, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val sugarPercent = if (norms.sugar > BigDecimal.ZERO)
                            sugar.multiply(BigDecimal(100)).divide(norms.sugar, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        val cholesterolPercent = if (norms.cholesterol > BigDecimal.ZERO)
                            cholesterol.multiply(BigDecimal(100)).divide(norms.cholesterol, 2, RoundingMode.HALF_UP)
                        else BigDecimal.ZERO
                        ElevatedCard {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("For ${g.stripTrailingZeros().toPlainString()} g", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text("Calories: ${totalCal.stripTrailingZeros().toPlainString()} kcal", style = MaterialTheme.typography.bodyMedium)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MacroSemiCircle(
                                        title = "Protein",
                                        grams = p,
                                        percent = proteinPercent,
                                        color = Color(0xFF42A5F5),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MacroSemiCircle(
                                        title = "Carbs",
                                        grams = c,
                                        percent = carbsPercent,
                                        color = Color(0xFF7CB342),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MacroSemiCircle(
                                        title = "Fat",
                                        grams = f,
                                        percent = fatPercent,
                                        color = Color(0xFFEF5350),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MacroSemiCircle(
                                        title = "Sugar",
                                        grams = sugar,
                                        percent = sugarPercent,
                                        color = Color(0xFFFFA726),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MacroSemiCircle(
                                        title = "Cholesterol",
                                        grams = cholesterol,
                                        percent = cholesterolPercent,
                                        color = Color(0xFFAB47BC),
                                        modifier = Modifier.weight(1f),
                                        unit = "mg"
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { vm.clearBarcodeSelection() }) { Text("Close") }
                            Button(onClick = { vm.addBarcodeSelected() }) { Text("Add to meal") }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
@Composable
private fun ResultRow(item: FoodSearchResponse.FoodItem, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(item.name, fontWeight = FontWeight.Medium)
            Text(
                item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
private fun ResultsOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmitSearch: () -> Unit,
    total: String,
    items: List<FoodSearchResponse.FoodItem>,
    isLoading: Boolean,
    onItemClick: (FoodSearchResponse.FoodItem) -> Unit,
    onLoadMore: () -> Unit,
    onClose: () -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(items.size, isLoading) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalCount = listState.layoutInfo.totalItemsCount
            last to totalCount
        }.collect { (last, totalCount) ->
            if (!isLoading && totalCount > 0 && last >= totalCount - 5) onLoadMore()
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = { Text("Search food") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions (onSearch = { onSubmitSearch() })
                )
                TextButton(onClick = onSubmitSearch, enabled = !isLoading) { Text("Search") }
                TextButton(onClick = onClose) { Text("Close") }
            }
            Divider()
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items.size) { idx ->
                    val item = items[idx]
                    ResultRow(item) { onItemClick(item) }
                }
                if (isLoading) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) { CircularProgressIndicator() }
                    }
                }
            }
        }
    }
}
@Composable
private fun SummaryMacros(ui: MealBuilderUiState) {
    val t = ui.totals
    val totalShare = listOf(t.carbohydrates, t.protein, t.fat, t.sugars, t.fiber)
        .fold(BigDecimal.ZERO) { acc, x -> acc + x }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MacroShareCard(
                title = "Carbs",
                grams = t.carbohydrates,
                total = totalShare,
                color = Color(0xFF7CB342),
                modifier = Modifier.weight(1f)
            )
            MacroShareCard(
                title = "Protein",
                grams = t.protein,
                total = totalShare,
                color = Color(0xFF42A5F5),
                modifier = Modifier.weight(1f)
            )
            MacroShareCard(
                title = "Fat",
                grams = t.fat,
                total = totalShare,
                color = Color(0xFFEF5350),
                modifier = Modifier.weight(1f)
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MacroShareCard(
                title = "Sugar",
                grams = t.sugars,
                total = totalShare,
                color = Color(0xFFFFA726),
                modifier = Modifier.weight(1f)
            )
            MacroShareCard(
                title = "Fiber",
                grams = t.fiber,
                total = totalShare,
                color = Color(0xFFAB47BC),
                modifier = Modifier.weight(1f)
            )
        }
        CholesterolCard(value = t.cholesterol)
    }
}
@Composable
private fun MacroShareCard(
    title: String,
    grams: BigDecimal,
    total: BigDecimal,
    color: Color,
    modifier: Modifier = Modifier
) {
    val percent = if (total > BigDecimal.ZERO)
        grams.multiply(BigDecimal(100)).divide(total, 2, RoundingMode.HALF_UP)
    else BigDecimal.ZERO
    val ratio = percent.toFloat().coerceIn(0f, 100f) / 100f
    ElevatedCard(modifier = modifier.heightIn(min = 140.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp)
                .padding(12.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = ratio,
                    strokeWidth = 6.dp,
                    color = color,
                    trackColor = color.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "${percent.stripTrailingZeros().toPlainString()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "${grams.stripTrailingZeros().toPlainString()} g",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
@Composable
private fun CholesterolCard(value: BigDecimal) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Cholesterol", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("${value.stripTrailingZeros().toPlainString()} mg", style = MaterialTheme.typography.titleMedium)
        }
    }
}
@Composable
private fun SemiCircleProgress(
    progress: Float,
    size: Dp,
    thickness: Dp,
    color: Color,
    trackColor: Color
) {
    val animated = animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700),
        label = "semicircle-progress"
    ).value
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = thickness.toPx()
            val canvasSize = this.size
            val diameter = min(canvasSize.width, canvasSize.height * 2f)
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(
                (canvasSize.width - diameter) / 2f,
                0f
            )
            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )
            drawArc(
                color = color,
                startAngle = 180f,
                sweepAngle = 180f * animated,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )
        }
    }
}
@Composable
private fun MacroSemiCircle(
    title: String,
    grams: BigDecimal,
    percent: BigDecimal,
    color: Color,
    modifier: Modifier = Modifier,
    unit: String = "g"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SemiCircleProgress(
            progress = percent.toFloat().coerceIn(0f, 100f) / 100f,
            size = 70.dp,
            thickness = 7.dp,
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
        Text(
            "${grams.stripTrailingZeros().toPlainString()} $unit",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "${percent.stripTrailingZeros().toPlainString()}%",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
private fun MacroTable(
    calories: String,
    protein: String,
    carbs: String,
    fat: String,
    sugar: String,
    fiber: String,
    cholesterol: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Calories: $calories kcal")
        Text("Protein: $protein g")
        Text("Carbs: $carbs g")
        Text("Fat: $fat g")
        Text("Sugar: $sugar g")
        Text("Fiber: $fiber g")
        Text("Cholesterol: $cholesterol mg")
    }
}
@Composable
private fun CustomFoodInputSheet(
    input: CustomFoodInput,
    onInputChange: (CustomFoodInput) -> Unit,
    onAdd: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        Modifier
            .padding(16.dp)
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add custom food", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = input.name,
            onValueChange = { onInputChange(input.copy(name = it)) },
            label = { Text("Food name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = input.grams,
            onValueChange = { onInputChange(input.copy(grams = it.filter { c -> c.isDigit() || c == '.' })) },
            label = { Text("Weight, g") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = input.calories,
            onValueChange = { onInputChange(input.copy(calories = it.filter { c -> c.isDigit() || c == '.' })) },
            label = { Text("Calories, kcal") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input.protein,
                onValueChange = { onInputChange(input.copy(protein = it.filter { c -> c.isDigit() || c == '.' })) },
                label = { Text("Protein, g") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = input.fat,
                onValueChange = { onInputChange(input.copy(fat = it.filter { c -> c.isDigit() || c == '.' })) },
                label = { Text("Fat, g") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = input.carbohydrates,
            onValueChange = { onInputChange(input.copy(carbohydrates = it.filter { c -> c.isDigit() || c == '.' })) },
            label = { Text("Carbohydrates, g") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input.sugar,
                onValueChange = { onInputChange(input.copy(sugar = it.filter { c -> c.isDigit() || c == '.' })) },
                label = { Text("Sugar, g") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = input.fiber,
                onValueChange = { onInputChange(input.copy(fiber = it.filter { c -> c.isDigit() || c == '.' })) },
                label = { Text("Fiber, g") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = input.cholesterol,
            onValueChange = { onInputChange(input.copy(cholesterol = it.filter { c -> c.isDigit() || c == '.' })) },
            label = { Text("Cholesterol, mg") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
            Button(onClick = onAdd, modifier = Modifier.weight(1f)) { Text("Add") }
        }
        Spacer(Modifier.height(12.dp))
    }
}