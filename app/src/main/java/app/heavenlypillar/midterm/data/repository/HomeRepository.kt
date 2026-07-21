package app.heavenlypillar.midterm.data.repository

import app.heavenlypillar.midterm.data.model.AccountBalance
import app.heavenlypillar.midterm.data.model.ExpectedIncome
import app.heavenlypillar.midterm.data.model.HomeDashboardData
import app.heavenlypillar.midterm.data.model.UpcomingBill
import app.heavenlypillar.midterm.data.model.PlannedExpense
import kotlinx.coroutines.delay

class HomeRepository {

    suspend fun fetchDashboardData(): HomeDashboardData {
        delay(1200)

        return HomeDashboardData(
            balance = AccountBalance(
                mainBalanceGEL = "10.90 ₾",
                eurBalance = "1.90 €",
                usdBalance = "0.00 $"
            ),
            upcomingBills = listOf(
                UpcomingBill("1", "40.90 ₾", "⚡ 10.90 ₾  |  🚰 1.90 ₾  |  📶 0.00 ₾", "house"),
                UpcomingBill("2", "140.90 ₾", "🔹 10.90 ₾  |  💳 1.90 ₾", "card"),
                UpcomingBill("3", "140.90 ₾", "🔹 10.90 ₾  |  🏦 1.90 ₾", "bank"),
                UpcomingBill("4", "25.00 ₾", "📱 25.00 ₾", "phone")
            ),
            plannedExpenses = emptyList(),
            expectedIncomes = emptyList()
        )
    }
}