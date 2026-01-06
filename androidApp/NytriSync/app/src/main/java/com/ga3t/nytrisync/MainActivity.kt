package com.ga3t.nytrisync
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.ga3t.nytrisync.data.model.MealType
import com.ga3t.nytrisync.ui.details.*
import com.ga3t.nytrisync.ui.home.HomeScreen
import com.ga3t.nytrisync.ui.home.HomeViewModel
import com.ga3t.nytrisync.ui.meal.MealBuilderScreen
import com.ga3t.nytrisync.ui.notifications.NotificationsScreen
import com.ga3t.nytrisync.ui.profile.ProfileScreen
import com.ga3t.nytrisync.ui.screens.LoginScreen
import com.ga3t.nytrisync.ui.screens.RegistrationScreen
import com.ga3t.nytrisync.ui.scan.BarcodeScannerScreen
import com.ga3t.nytrisync.ui.start.AppStartScreen
import com.ga3t.nytrisync.ui.theme.AppTheme
import com.ga3t.nytrisync.utils.SessionEvents
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    SessionEvents.logout.collectLatest {
                        navController.navigate("login") {
                            popUpTo("app_start") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= 33) {
                    val launcher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {}
                    LaunchedEffect(Unit) {
                        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = "app_start"
                ) {
                    composable("app_start") {
                        AppStartScreen(
                            goLogin = {
                                navController.navigate("login") {
                                    popUpTo("app_start") { inclusive = true }
                                }
                            },
                            goApp = {
                                navController.navigate("details_check") {
                                    popUpTo("app_start") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegistration = { navController.navigate("registration") },
                            onLoggedIn = {
                                navController.navigate("details_check") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("registration") {
                        RegistrationScreen(
                            onBackToLogin = { navController.navigate("login") },
                            onAutoLoginSuccess = {
                                navController.navigate("details_check") {
                                    popUpTo("registration") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("details_check") {
                        DetailsGateScreen(
                            onExists = {
                                navController.navigate("home") {
                                    popUpTo("details_check") { inclusive = true }
                                }
                            },
                            onNotExists = {
                                navController.navigate("onboarding") {
                                    popUpTo("details_check") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") { backStackEntry ->
                        val vm: HomeViewModel = viewModel(
                            viewModelStoreOwner = backStackEntry,
                            factory = HomeViewModel.factory()
                        )
                        val refreshTs by backStackEntry.savedStateHandle
                            .getStateFlow("home_refresh", 0L)
                            .collectAsState()
                        LaunchedEffect(refreshTs) {
                            if (refreshTs != 0L) {
                                vm.refresh()
                                backStackEntry.savedStateHandle.remove<Long>("home_refresh")
                            }
                        }
                        HomeScreen(
                            onRequireOnboarding = {
                                navController.navigate("onboarding") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onProfileClick = {
                                navController.navigate("profile")
                            },
                            onNotificationsClick = { navController.navigate("notifications") },
                            onAddMealClick = { mealType ->
                                val today = java.time.LocalDate.now().toString()
                                navController.navigate("meal_builder/${mealType.name}/$today")
                            },
                            onChartClick = { navController.navigate("stats") },
                            onCalendarClick = { navController.navigate("calendar") }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(onBack = { navController.popBackStack() })
                    }
                    composable("notifications") {
                        NotificationsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("meal_builder/{mealType}/{date}") { backStackEntry ->
                        val mealTypeStr = backStackEntry.arguments?.getString("mealType") ?: "BREAKFAST"
                        val date = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
                        val type = runCatching { MealType.valueOf(mealTypeStr) }.getOrElse { MealType.BREAKFAST }
                        val scanned by backStackEntry.savedStateHandle
                            .getStateFlow("scanned_barcode", "")
                            .collectAsState()
                        MealBuilderScreen(
                            mealType = type,
                            onBack = { navController.popBackStack() },
                            onSaved = {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("home_refresh", System.currentTimeMillis())
                                navController.popBackStack()
                            },
                            onScanClick = { navController.navigate("barcode_scan") },
                            scannedBarcode = scanned,
                            clearScannedBarcode = { backStackEntry.savedStateHandle["scanned_barcode"] = "" },
                            date = date
                        )
                    }
                    composable("stats") {
                        com.ga3t.nytrisync.ui.stats.StatsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("barcode_scan") {
                        BarcodeScannerScreen(
                            onDetected = { code ->
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("scanned_barcode", code)
                                navController.popBackStack()
                            },
                            onClose = { navController.popBackStack() }
                        )
                    }
                    navigation(
                        startDestination = "ud_weight",
                        route = "onboarding"
                    ) {
                        composable("ud_weight") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            WeightScreen(
                                title = "Your weight",
                                subtitle = "Adjust your current weight (kg).",
                                valueKg = vm.ui.currentWeight.toFloat(),
                                onValueChange = { vm.setCurrentWeight(it) },
                                onNext = { navController.navigate("ud_height") }
                            )
                        }
                        composable("ud_height") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            HeightScreen(
                                valueCm = vm.ui.height.toFloat(),
                                onValueChange = { vm.setHeight(it) },
                                onNext = { navController.navigate("ud_birth") }
                            )
                        }
                        composable("ud_birth") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            BirthDateScreen(
                                year = vm.ui.birthYear,
                                month = vm.ui.birthMonth,
                                day = vm.ui.birthDay,
                                onChange = { y, m, d -> vm.setBirth(y, m, d) },
                                onNext = { navController.navigate("ud_sex") }
                            )
                        }
                        composable("ud_sex") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            SexScreen(
                                selected = vm.ui.sex,
                                onSelect = { vm.setSex(it) },
                                onNext = { navController.navigate("ud_goal") }
                            )
                        }
                        composable("ud_goal") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            GoalScreen(
                                selected = vm.ui.goal,
                                onSelect = { vm.setGoal(it) },
                                onNext = { navController.navigate("ud_activity") }
                            )
                        }
                        composable("ud_activity") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            ActivityScreen(
                                label = vm.activityLabel(),
                                onPrevLevel = { vm.setActivityIndex(vm.ui.activityIndex - 1) },
                                onNextLevel = { vm.setActivityIndex(vm.ui.activityIndex + 1) },
                                onNext = { navController.navigate("ud_wanted") }
                            )
                        }
                        composable("ud_wanted") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            WantedWeightScreen(
                                currentKg = vm.ui.currentWeight.toFloat(),
                                wantedKg = vm.ui.wantedWeight.toFloat(),
                                showWarning = vm.ui.showGoalMismatchWarning,
                                onWantedChange = { vm.setWantedWeight(it) },
                                onApplySuggestedGoal = { vm.applySuggestedGoal() },
                                onNext = {
                                    vm.submit()
                                    navController.navigate("ud_result")
                                }
                            )
                        }
                        composable("calendar") { backStackEntry ->
                            val vm: com.ga3t.nytrisync.ui.calendar.CalendarViewModel = viewModel(
                                viewModelStoreOwner = backStackEntry,
                                factory = com.ga3t.nytrisync.ui.calendar.CalendarViewModel.factory()
                            )
                            val refreshTs by backStackEntry.savedStateHandle
                                .getStateFlow("calendar_refresh", 0L)
                                .collectAsState()
                            LaunchedEffect(refreshTs) {
                                if (refreshTs != 0L) {
                                    vm.loadData()
                                    backStackEntry.savedStateHandle.remove<Long>("calendar_refresh")
                                }
                            }
                            com.ga3t.nytrisync.ui.calendar.CalendarScreen(
                                onBack = { navController.popBackStack() },
                                onDayClick = { dateStr ->
                                    if (dateStr == LocalDate.now().toString()) {
                                        navController.popBackStack()
                                    } else {
                                        navController.navigate("day_detail/$dateStr")
                                    }
                                }
                            )
                        }
                        composable("day_detail/{date}") { backStackEntry ->
                            val date = backStackEntry.arguments?.getString("date") ?: ""
                            val refreshFromMeal by backStackEntry.savedStateHandle
                                .getStateFlow("home_refresh", 0L)
                                .collectAsState()
                            LaunchedEffect(refreshFromMeal) {
                                if (refreshFromMeal != 0L) {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("calendar_refresh", System.currentTimeMillis())
                                }
                            }
                            com.ga3t.nytrisync.ui.calendar.DayDetailScreen(
                                date = date,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onAddMealClick = { mealType ->
                                    navController.navigate("meal_builder/${mealType.name}/$date")
                                }
                            )
                        }
                        composable("ud_result") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("onboarding")
                            }
                            val vm: OnboardingViewModel =
                                viewModel(parentEntry, factory = OnboardingViewModel.factory())
                            when {
                                vm.ui.isLoading -> {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                                vm.ui.result != null -> {
                                    ResultScreen(
                                        result = vm.ui.result!!,
                                        onNext = {
                                            navController.navigate("home") {
                                                popUpTo("onboarding") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                vm.ui.error != null -> {
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("Error: ${vm.ui.error}", color = MaterialTheme.colorScheme.error)
                                        Spacer(Modifier.height(12.dp))
                                        Button(onClick = { vm.submit() }) { Text("Retry") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}