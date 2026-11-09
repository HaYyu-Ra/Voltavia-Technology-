package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Services : Screen("services")
    object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }
    object Plans : Screen("plans")
    object QuoteForm : Screen("quote_form")
    object QuoteSuccess : Screen("quote_success")
    object Projects : Screen("projects")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
    object Support : Screen("support")
    object SupportCreate : Screen("support_create")
    object SupportDetail : Screen("support_detail/{ticketId}") {
        fun createRoute(ticketId: String) = "support_detail/$ticketId"
    }
    object Profile : Screen("profile")
}
