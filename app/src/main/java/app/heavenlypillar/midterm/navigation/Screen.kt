package app.heavenlypillar.midterm.navigation

sealed interface Screen {
    data object Home : Screen
    data object PersonalDetails : Screen
    data class BankDetails(val bankName: String) : Screen
}