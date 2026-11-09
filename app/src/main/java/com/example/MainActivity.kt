package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.VoltaviaDatabase
import com.example.data.model.AppMode
import com.example.data.repository.VoltaviaRepository
import com.example.ui.components.AdaptiveScaffold
import com.example.ui.navigation.Screen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlansScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.QuoteFormScreen
import com.example.ui.screens.QuoteSuccessScreen
import com.example.ui.screens.ServiceDetailScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.screens.SupportCreateScreen
import com.example.ui.screens.SupportDetailScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.theme.VoltaviaTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val db = VoltaviaDatabase.getInstance(applicationContext)
        val repo = VoltaviaRepository(db.voltaviaDao())
        MainViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VoltaviaTheme {
                VoltaviaAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VoltaviaAppRoot(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val isTopLevelRoute = currentRoute in listOf(
        Screen.Home.route,
        Screen.Services.route,
        Screen.Plans.route,
        Screen.Projects.route,
        Screen.Support.route,
        Screen.Profile.route
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { scaffoldPadding ->
        if (isTopLevelRoute) {
            AdaptiveScaffold(
                currentRoute = currentRoute,
                onNavigate = { targetRoute ->
                    if (targetRoute != currentRoute) {
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            ) {
                AppNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    modifier = Modifier.padding(scaffoldPadding)
                )
            }
        } else {
            // Secondary screens (forms, details) take full screen with own AppTopBar
            AppNavHost(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.padding(scaffoldPadding)
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val toggleMode = {
        val curr = viewModel.appMode.value
        val next = if (curr == AppMode.OFFLINE_DEMO) AppMode.PRODUCTION_CLOUD else AppMode.OFFLINE_DEMO
        viewModel.setAppMode(next)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onRequestQuote = { svcId ->
                    if (svcId != null) {
                        viewModel.prefillQuoteForService(svcId)
                    }
                    navController.navigate(Screen.QuoteForm.route)
                },
                onGetSupport = {
                    navController.navigate(Screen.Support.route)
                },
                onViewPlans = {
                    navController.navigate(Screen.Plans.route)
                },
                onViewServices = {
                    navController.navigate(Screen.Services.route)
                },
                onSelectService = { svcId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(svcId))
                },
                onViewProjects = {
                    navController.navigate(Screen.Projects.route)
                },
                onSwitchMode = toggleMode
            )
        }

        composable(Screen.Services.route) {
            ServicesScreen(
                viewModel = viewModel,
                onSelectService = { svcId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(svcId))
                },
                onSwitchMode = toggleMode
            )
        }

        composable(
            route = Screen.ServiceDetail.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "svc_software"
            BackHandler { navController.popBackStack() }
            ServiceDetailScreen(
                serviceId = serviceId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onRequestQuote = {
                    navController.navigate(Screen.QuoteForm.route)
                }
            )
        }

        composable(Screen.Plans.route) {
            PlansScreen(
                viewModel = viewModel,
                onSelectPlanForQuote = { _, _ ->
                    navController.navigate(Screen.QuoteForm.route)
                },
                onSwitchMode = toggleMode
            )
        }

        composable(Screen.QuoteForm.route) {
            BackHandler { navController.popBackStack() }
            QuoteFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onQuoteSubmitted = {
                    navController.navigate(Screen.QuoteSuccess.route) {
                        popUpTo(Screen.QuoteForm.route) { inclusive = true }
                    }
                },
                onSwitchMode = toggleMode
            )
        }

        composable(Screen.QuoteSuccess.route) {
            BackHandler { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }
            QuoteSuccessScreen(
                viewModel = viewModel,
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onViewServices = {
                    navController.navigate(Screen.Services.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.Projects.route) {
            ProjectsScreen(
                viewModel = viewModel,
                onSelectProject = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onSwitchMode = toggleMode
            )
        }

        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: "proj_sample_01"
            BackHandler { navController.popBackStack() }
            ProjectDetailScreen(
                projectId = projectId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Support.route) {
            SupportScreen(
                viewModel = viewModel,
                onCreateTicket = { navController.navigate(Screen.SupportCreate.route) },
                onSelectTicket = { ticketId ->
                    navController.navigate(Screen.SupportDetail.createRoute(ticketId))
                },
                onSwitchMode = toggleMode
            )
        }

        composable(Screen.SupportCreate.route) {
            BackHandler { navController.popBackStack() }
            SupportCreateScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onTicketCreated = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SupportDetail.route,
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: "tkt_sample_01"
            BackHandler { navController.popBackStack() }
            SupportDetailScreen(
                ticketId = ticketId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onSwitchMode = toggleMode
            )
        }
    }
}
