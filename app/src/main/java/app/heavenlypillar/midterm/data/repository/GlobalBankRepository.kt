package app.heavenlypillar.midterm.data.repository

import app.heavenlypillar.midterm.data.api.*
import app.heavenlypillar.midterm.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.*
import kotlin.random.*

object GlobalBankRepository {

    private const val API_TOKEN = "Bearer cGTtOlgJyaUzNUlJnIFZimyykzhGmKsmabg3-TLLVsg"
    private const val BUDGET_ID = "67bb66cd-f44d-4127-8e61-2fbc0eac59ac"
    private var rawYnabTransactions: List<YnabTransaction> = emptyList()
    private var rawScheduledTransactions: List<YnabScheduledTransaction> = emptyList()
    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance = _totalBalance.asStateFlow()
    private val _currentBankTransactions = MutableStateFlow<List<TransactionModel>>(emptyList())
    val currentBankTransactions = _currentBankTransactions.asStateFlow()
    private val _currentBankBalance = MutableStateFlow(0.0)
    val currentBankBalance = _currentBankBalance.asStateFlow()

    suspend fun syncWithYnab() {
        try {
            val response = YnabApiService.instance.getTransactions(API_TOKEN, BUDGET_ID)
            rawYnabTransactions = response.data.transactions

            val scheduledResponse =
                    YnabApiService.instance.getScheduledTransactions(API_TOKEN, BUDGET_ID)
            rawScheduledTransactions = scheduledResponse.data.scheduled_transactions

            // Calculate the total for the Home Screen
            _totalBalance.value = rawYnabTransactions.sumOf { it.amount / 1000.0 }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Called when you click a specific card
    fun selectBank(bankName: String) {
        val bankTxs = rawYnabTransactions.filter {
            it.account_name.equals(bankName, ignoreCase = true)
        }

        _currentBankBalance.value = bankTxs.sumOf { it.amount / 1000.0 }

        _currentBankTransactions.value = bankTxs.map { ynabTx ->
            val convertedAmount = ynabTx.amount / 1000.0
            TransactionModel(
                title = ynabTx.payee_name ?: "ტრანზაქცია",
                date = ynabTx.date,
                amount = "%.2f".format(Math.abs(convertedAmount)),
                isPositive = convertedAmount >= 0
            )
        }.reversed()
    }

    fun getBalanceFor(bankName: String): Double {
        return rawYnabTransactions.filter { it.account_name.equals(bankName, ignoreCase = true) }
            .sumOf { it.amount / 1000.0 }
    }

    fun getLiveUpcomingBills(): List<app.heavenlypillar.midterm.data.model.UpcomingBill> {
        val liveBills = mutableListOf<app.heavenlypillar.midterm.data.model.UpcomingBill>()

        val electricityTx = rawScheduledTransactions.find {
            it.memo?.contains(
                "Electricity", ignoreCase = true
            ) == true
        }
        val waterTx = rawScheduledTransactions.find {
            it.memo?.contains(
                "water", ignoreCase = true
            ) == true
        }
        val internetTx = rawScheduledTransactions.find {
            it.memo?.contains(
                "Internet", ignoreCase = true
            ) == true
        }
        val phoneTx = rawScheduledTransactions.find {
            it.memo?.contains(
                "phone", ignoreCase = true
            ) == true
        }

        val electricityAmount = Math.abs((electricityTx?.amount ?: 0) / 1000.0)
        val waterAmount = Math.abs((waterTx?.amount ?: 0) / 1000.0)
        val internetAmount = Math.abs((internetTx?.amount ?: 0) / 1000.0)
        val phoneAmount = Math.abs((phoneTx?.amount ?: 0) / 1000.0)

        val totalUtilities = electricityAmount + waterAmount + internetAmount

        if (totalUtilities > 0) {
            liveBills.add(
                app.heavenlypillar.midterm.data.model.UpcomingBill(
                    id = "live_home_utils",
                    title = "%.2f ₾".format(totalUtilities),
                    subtitleDetails = "⚡ %.2f ₾  |  🚰 %.2f ₾  |  📶 %.2f ₾".format(
                        electricityAmount, waterAmount, internetAmount
                    ),
                    iconType = "house"
                )
            )
        }

        if (phoneAmount > 0) {
            liveBills.add(
                app.heavenlypillar.midterm.data.model.UpcomingBill(
                    id = "live_phone_bill",
                    title = "%.2f ₾".format(phoneAmount),
                    subtitleDetails = "📱 %.2f ₾".format(phoneAmount),
                    iconType = "phone"
                )
            )
        }

        return liveBills
    }

    fun getLivePlannedExpenses(): List<app.heavenlypillar.midterm.data.model.PlannedExpense> {
        return rawScheduledTransactions.filter { ynabTx ->
            (ynabTx.memo?.contains("Planned", ignoreCase = true) == true) && ynabTx.amount < 0
        }.map { ynabTx ->
            val gelAmount = Math.abs(ynabTx.amount / 1000.0)
            app.heavenlypillar.midterm.data.model.PlannedExpense(
                id = ynabTx.id,
                title = ynabTx.payee_name ?: "დაგეგმილი ხარჯი",
                amount = "%.2f ₾".format(gelAmount),
                date = isoToGeorgian(ynabTx.date_next) // Use date_next for scheduled items
            )
        }
    }

    fun getLiveExpectedIncomes(): List<app.heavenlypillar.midterm.data.model.ExpectedIncome> {
        return rawScheduledTransactions.filter { ynabTx ->
            (ynabTx.memo?.contains("Planned", ignoreCase = true) == true) && ynabTx.amount > 0
        }.map { ynabTx ->
            val gelAmount = Math.abs(ynabTx.amount / 1000.0)
            app.heavenlypillar.midterm.data.model.ExpectedIncome(
                id = ynabTx.id,
                title = ynabTx.payee_name ?: "მოსალოდნელი შემოსავალი",
                amount = "%.2f ₾".format(gelAmount),
                date = isoToGeorgian(ynabTx.date_next) // Use date_next for scheduled items
            )
        }
    }

    suspend fun savePlannedTransaction(
        transactionId: String?,
        title: String,
        amountStr: String,
        georgianDate: String,
        bankName: String,
        isIncome: Boolean
    ) {
        val accountId = getAccountIdForBank(bankName) ?: return

        val cleanAmount = amountStr.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val finalAmount = if (isIncome) cleanAmount else -cleanAmount
        val milliunits = (finalAmount * 1000).toLong()
        val isoDate = georgianToIso(georgianDate)

        val tx = YnabSaveScheduled(
            account_id = accountId,
            date = isoDate,
            amount = milliunits,
            payee_name = title,
            memo = "Planned in App",
            frequency = "never"
        )

        try {
            val response = if (transactionId == null) {
                YnabApiService.instance.createScheduledTransaction(
                    API_TOKEN, BUDGET_ID, YnabSaveScheduledWrapper(tx)
                )
            } else {
                YnabApiService.instance.deleteScheduledTransaction(
                    API_TOKEN, BUDGET_ID, transactionId
                )
                YnabApiService.instance.createScheduledTransaction(
                    API_TOKEN, BUDGET_ID, YnabSaveScheduledWrapper(tx)
                )
            }

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                println("🚨 YNAB API ERROR: $errorBody") //added those so i could see error in logcat
            } else {
                println("✅ YNAB API SUCCESS!")
            }

            syncWithYnab()
        } catch (e: Exception) {
            println("🚨 GENERAL ERROR: ${e.message}")
            e.printStackTrace()//added those so i could see error in logcat
        }
    }

    suspend fun deletePlannedTransaction(transactionId: String) {
        try {
            YnabApiService.instance.deleteScheduledTransaction(API_TOKEN, BUDGET_ID, transactionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun processPdfUpload(bankName: String) {
        delay(1000) // Small delay for the loading spinner

        val accountId = rawYnabTransactions.find {
            it.account_name.equals(bankName, ignoreCase = true)
        }?.account_id ?: return

        val possiblePayees =
                listOf("Wolt", "Glovo", "Carrefour", "Fresco", "Amazon", "Netflix", "Salary Bonus")
        val randomPayee = possiblePayees.random()

        val randomAmount = Random.nextDouble(5.0, 150.0)
        val isIncome = Random.nextBoolean()

        val finalAmount = if (isIncome) randomAmount else -randomAmount
        val milliunits = (finalAmount * 1000).toLong()

        val today = LocalDate.now().toString()

        val newTx = YnabSaveTransaction(
            account_id = accountId,
            date = today,
            amount = milliunits,
            payee_name = randomPayee,
            memo = "PDF Upload Auto-Parsed"
        )

        try {
            YnabApiService.instance.createTransaction(
                token = API_TOKEN, budgetId = BUDGET_ID, payload = YnabSaveTransactionWrapper(newTx)
            )

            syncWithYnab()
            selectBank(bankName)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Handles Creating (POST) and Editing (PUT) based on whether an ID exists
    private fun getAccountIdForBank(bankName: String): String? {
        val exactMatch = rawYnabTransactions.find {
            it.account_name.equals(bankName, ignoreCase = true)
        }?.account_id
        if (exactMatch != null) return exactMatch

        val firstWord = bankName.split(" ").firstOrNull() ?: bankName
        val partialMatch = rawYnabTransactions.find {
            it.account_name?.contains(firstWord, ignoreCase = true) == true
        }?.account_id
        if (partialMatch != null) return partialMatch

        //use any valid account_id found in the user's budget
        return rawYnabTransactions.firstOrNull { !it.account_id.isNullOrEmpty() }?.account_id
    }

    // Converts "25 ივლისი" -> "2026-07-25"
    private fun georgianToIso(georgianDate: String): String {
        val parts = georgianDate.split(" ")
        if (parts.size < 2) return LocalDate.now().toString()
        val day = parts[0].padStart(2, '0')
        val monthNum = when (parts[1]) {
            "იანვარი" -> "01"
            "თებერვალი" -> "02"
            "მარტი" -> "03"
            "აპრილი" -> "04"
            "მაისი" -> "05"
            "ივნისი" -> "06"
            "ივლისი" -> "07"
            "აგვისტო" -> "08"
            "სექტემბერი" -> "09"
            "ოქტომბერი" -> "10"
            "ნოემბერი" -> "11"
            else -> "12"
        }
        return "2026-$monthNum-$day"
    }

    // "2026-07-25" -> "25 ივლისი"
    fun isoToGeorgian(isoDate: String): String {
        val parts = isoDate.split("-")
        if (parts.size < 3) return isoDate
        val day = parts[2].toInt().toString() // Removes leading zero
        val monthStr = when (parts[1]) {
            "01" -> "იანვარი"
            "02" -> "თებერვალი"
            "03" -> "მარტი"
            "04" -> "აპრილი"
            "05" -> "მაისი"
            "06" -> "ივნისი"
            "07" -> "ივლისი"
            "08" -> "აგვისტო"
            "09" -> "სექტემბერი"
            "10" -> "ოქტომბერი"
            "11" -> "ნოემბერი"
            else -> "დეკემბერი"
        }
        return "$day $monthStr"
    }
}

