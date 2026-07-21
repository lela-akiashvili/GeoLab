package app.heavenlypillar.midterm.ui.bankdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.midterm.data.repository.GlobalBankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BankDetailsViewModel : ViewModel() {

    val transactions = GlobalBankRepository.currentBankTransactions
    val mainBalance = GlobalBankRepository.currentBankBalance

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    fun loadBankData(bankName: String) {
        GlobalBankRepository.selectBank(bankName)
    }

    fun uploadStatement(bankName: String) {
        viewModelScope.launch {
            _isUploading.value = true
            GlobalBankRepository.processPdfUpload(bankName)
            _isUploading.value = false
        }
    }
}