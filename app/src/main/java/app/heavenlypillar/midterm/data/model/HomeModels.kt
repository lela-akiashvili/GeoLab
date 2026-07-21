package app.heavenlypillar.midterm.data.model

// Represents the top balance card data
data class AccountBalance(
    val mainBalanceGEL: String,
    val eurBalance: String,
    val usdBalance: String
)

data class UpcomingBill(
    val id: String,
    val title: String,
    val subtitleDetails: String,
    val iconType: String
)

data class PlannedExpense(
    val id: String,
    val title: String,
    val amount: String,
    val date: String
)

data class ExpectedIncome(
    val id: String,
    val title: String,
    val amount: String,
    val date: String

)

data class HomeDashboardData(
    val balance: AccountBalance,
    val upcomingBills: List<UpcomingBill>,
    val plannedExpenses: List<PlannedExpense>,
    val expectedIncomes: List<ExpectedIncome>,
    val projectedBalance: Double = 0.0
)