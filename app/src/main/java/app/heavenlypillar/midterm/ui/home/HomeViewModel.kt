package app.heavenlypillar.midterm.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.midterm.data.model.HomeDashboardData
import app.heavenlypillar.midterm.data.repository.GlobalBankRepository
import app.heavenlypillar.midterm.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: HomeDashboardData) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {
    private val repository = HomeRepository()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private fun parseAmountToDouble(amountString: String): Double {
        val cleanString = amountString.replace("[^\\d.]".toRegex(), "")
        return cleanString.toDoubleOrNull() ?: 0.0
    }

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                GlobalBankRepository.syncWithYnab()

                val currentBalance = GlobalBankRepository.totalBalance.value
                val liveTotalBalanceString = "%,.2f ₾".format(currentBalance)
                val liveBills = GlobalBankRepository.getLiveUpcomingBills()

                val liveExpenses = GlobalBankRepository.getLivePlannedExpenses()
                val liveIncomes = GlobalBankRepository.getLiveExpectedIncomes()

                val data = repository.fetchDashboardData()

                val mergedBills = if (liveBills.isNotEmpty()) {
                    liveBills + data.upcomingBills.filter { it.iconType == "card" || it.iconType == "bank" }
                } else {
                    data.upcomingBills
                }

                val totalBills = mergedBills.sumOf { parseAmountToDouble(it.title) }
                val totalExpenses = liveExpenses.sumOf { parseAmountToDouble(it.amount) }
                val totalIncome = liveIncomes.sumOf { parseAmountToDouble(it.amount) }

                val projectedBalance = currentBalance + totalIncome - totalExpenses - totalBills

                val updatedData = data.copy(
                    balance = data.balance.copy(mainBalanceGEL = liveTotalBalanceString),
                    upcomingBills = mergedBills,
                    plannedExpenses = liveExpenses,
                    expectedIncomes = liveIncomes,
                    projectedBalance = projectedBalance
                )

                _uiState.value = HomeUiState.Success(updatedData)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("ინფორმაციის ჩატვირთვა ვერ მოხერხდა")
            }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            GlobalBankRepository.deletePlannedTransaction(id)
            loadDashboard()
        }
    }

    fun saveItem(id: String?, title: String, amount: String, date: String, bankName: String, isIncome: Boolean) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            GlobalBankRepository.savePlannedTransaction(
                transactionId = id,
                title = title,
                amountStr = amount,
                georgianDate = date,
                bankName = bankName,
                isIncome = isIncome
            )
            loadDashboard()
        }
    }
}