package app.heavenlypillar.midterm.data.model

sealed class PersonalDetailsUiState {
    object Loading : PersonalDetailsUiState()
    data class Success(val data: PersonalDetailsData) : PersonalDetailsUiState()
    data class Error(val message: String) : PersonalDetailsUiState()
}

data class PersonalDetailsData(
    val savedCards: List<SavedCard>,
    val pensionStat: String,
    val investmentTotal: String,
    val investments: List<InvestmentItem>
)

data class SavedCard(
    val id: String,
    val bankName: String,
    val lastFourDigits: String,
    val logoRes: String,
    val balance: String
)

data class InvestmentItem(
    val id: String,
    val ticker: String,
    val shares: String,
    val totalValue: String
)