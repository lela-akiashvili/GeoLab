package app.heavenlypillar.midterm.data.model

data class TransactionModel(
    val title: String,
    val date: String,
    val amount: String,
    val isPositive: Boolean
)