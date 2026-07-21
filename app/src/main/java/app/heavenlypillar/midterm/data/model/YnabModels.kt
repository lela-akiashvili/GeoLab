package app.heavenlypillar.midterm.data.model

import com.google.gson.annotations.SerializedName

data class YnabResponse(
    val data: YnabData
)

data class YnabData(
    val transactions: List<YnabTransaction>
)

data class YnabTransaction(
    val id: String,
    val date: String,
    val amount: Long, // YNAB stores money in milliunits (e.g. 30000 = $30.00)
    val payee_name: String?,
    val memo: String?,
    val cleared: String,
    val account_name: String?,
    val account_id: String
)

data class YnabScheduledResponse(
    val data: YnabScheduledData
)

data class YnabScheduledData(
    val scheduled_transactions: List<YnabScheduledTransaction>
)

data class YnabScheduledTransaction(
    val id: String,
    val date_next: String,
    val amount: Long,
    val payee_name: String?,
    val memo: String?,
    val account_name: String?
)

data class YnabSaveTransactionWrapper(
    val transaction: YnabSaveTransaction
)

data class YnabSaveTransaction(
    val account_id: String,
    val date: String,
    val amount: Long,
    val payee_name: String?,
    val memo: String?,
    val cleared: String = "cleared"
)

data class YnabSaveScheduledWrapper(
    val scheduled_transaction: YnabSaveScheduled
)

data class YnabSaveScheduled(
    val account_id: String,
    val date: String,
    val amount: Long,
    val payee_name: String?,
    val memo: String?,
    val frequency: String = "never"
)