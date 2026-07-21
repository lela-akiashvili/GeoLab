package app.heavenlypillar.midterm.ui.personaldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.midterm.data.model.*
import app.heavenlypillar.midterm.data.repository.GlobalBankRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PersonalDetailsUiState>(PersonalDetailsUiState.Loading)
    val uiState: StateFlow<PersonalDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPersonalDetails()
    }

    private fun loadPersonalDetails() {
        viewModelScope.launch {
            _uiState.value = PersonalDetailsUiState.Loading

            GlobalBankRepository.syncWithYnab()

            val tbcBal = "%,.2f ₾".format(GlobalBankRepository.getBalanceFor("TBC Bank"))
            val bogBal = "%,.2f ₾".format(GlobalBankRepository.getBalanceFor("Bank of Georgia"))
            val credoBal = "%,.2f ₾".format(GlobalBankRepository.getBalanceFor("Credo Bank"))

            val mockData = PersonalDetailsData(
                savedCards = listOf(
                    SavedCard("1", "TBC Bank", "**** **** **** 4512", "tbc", tbcBal),
                    SavedCard("2", "Bank of Georgia", "**** **** **** 9921", "bog", bogBal),
                    SavedCard("3", "Credo Bank", "**** **** **** 1024", "credo", credoBal)
                ),
                pensionStat = "1,450.50 ₾",
                investmentTotal = "8,200.00 ₾",
                investments = listOf(
                    InvestmentItem("i1", "S&P 500 (VOO)", "2.5 აქცია", "3,200.00 ₾"),
                    InvestmentItem("i2", "Apple (AAPL)", "5.0 აქცია", "2,850.00 ₾")
                )
            )

            _uiState.value = PersonalDetailsUiState.Success(mockData)
        }
    }
}