package com.example.alzheimerapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MatchSelection : Screen("match_selection")
    object Recognition : Screen("recognition")
    object Match : Screen("match")
    object Differences : Screen("differences")
    object ImageManager : Screen("images")
}
