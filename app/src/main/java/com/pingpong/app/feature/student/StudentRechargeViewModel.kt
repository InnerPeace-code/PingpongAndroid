package com.pingpong.app.feature.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.PaymentRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.student.PaymentHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StudentRechargeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentRechargeUiState())
    val uiState: StateFlow<StudentRechargeUiState> = _uiState

    private var studentId: Long? = null

    init {
        viewModelScope.launch {
            loadSession()
        }
    }

    private suspend fun loadSession() {
        val session = runCatching { sessionRepository.currentSession() }
            .onFailure { throwable ->
                _uiState.update { it.copy(message = throwable.message ?: "Unable to load account") }
            }
            .getOrNull() ?: return
        studentId = session.userId
        refreshBalance()
        refreshTransactions()
    }

    fun refreshBalance() {
        val sid = studentId ?: return
        _uiState.update { it.copy(balanceState = UiState.Loading) }
        viewModelScope.launch {
            val result = paymentRepository.getBalance(sid)
            result
                .onSuccess { balance ->
                    _uiState.update { it.copy(balanceState = UiState.Success(balance)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(balanceState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun refreshTransactions() {
        val sid = studentId ?: return
        val page = _uiState.value.page
        val size = _uiState.value.size
        val status = _uiState.value.statusFilter
        val method = _uiState.value.methodFilter
        _uiState.update { it.copy(transactionsState = UiState.Loading) }
        viewModelScope.launch {
            val result = paymentRepository.getPaymentRecords(sid, page, size, status, method)
            result
                .onSuccess { history ->
                    _uiState.update { it.copy(transactionsState = UiState.Success(history)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(transactionsState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun selectAmount(amount: Double) {
        _uiState.update { it.copy(selectedAmount = amount, customAmount = "") }
    }

    fun updateCustomAmount(amount: String) {
        _uiState.update { state ->
            val value = amount.toDoubleOrNull()
            state.copy(
                customAmount = amount,
                selectedAmount = value
            )
        }
    }

    fun updatePaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun changeStatusFilter(status: String?) {
        _uiState.update { it.copy(statusFilter = status, page = 1) }
        refreshTransactions()
    }

    fun changeMethodFilter(method: String?) {
        _uiState.update { it.copy(methodFilter = method, page = 1) }
        refreshTransactions()
    }

    fun changePage(page: Int) {
        _uiState.update { it.copy(page = page) }
        refreshTransactions()
    }

    fun changePageSize(size: Int) {
        _uiState.update { it.copy(size = size, page = 1) }
        refreshTransactions()
    }

    fun createPayment() {
        val sid = studentId ?: return
        val amount = _uiState.value.selectedAmount
        val method = _uiState.value.paymentMethod
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(message = "Please select a valid amount") }
            return
        }
        _uiState.update { it.copy(isCreatingPayment = true) }
        viewModelScope.launch {
            val result = paymentRepository.createPayment(sid, amount, method)
            result
                .onSuccess { summary ->
                    _uiState.update {
                        it.copy(
                            isCreatingPayment = false,
                            paymentDialog = PaymentDialogState.Pending(summary.recordId, summary.qrCodeUrl)
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isCreatingPayment = false, message = throwable.message ?: "Failed to create payment") }
                }
        }
    }

    fun confirmPayment() {
        val recordId = (_uiState.value.paymentDialog as? PaymentDialogState.Pending)?.recordId ?: return
        viewModelScope.launch {
            val result = paymentRepository.confirmPayment(recordId)
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            paymentDialog = PaymentDialogState.Hidden,
                            message = "Payment confirmed"
                        )
                    }
                    refreshBalance()
                    refreshTransactions()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(message = throwable.message ?: "Failed to confirm payment") }
                }
        }
    }

    fun cancelPayment() {
        val recordId = (_uiState.value.paymentDialog as? PaymentDialogState.Pending)?.recordId ?: return
        viewModelScope.launch {
            val result = paymentRepository.cancelPayment(recordId)
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            paymentDialog = PaymentDialogState.Hidden,
                            message = "Payment cancelled"
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(message = throwable.message ?: "Failed to cancel payment") }
                }
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(paymentDialog = PaymentDialogState.Hidden) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class StudentRechargeUiState(
    val balanceState: UiState<Double> = UiState.Loading,
    val transactionsState: UiState<PaymentHistory> = UiState.Loading,
    val selectedAmount: Double? = null,
    val customAmount: String = "",
    val paymentMethod: String = "WECHAT",
    val page: Int = 1,
    val size: Int = 10,
    val statusFilter: String? = null,
    val methodFilter: String? = null,
    val isCreatingPayment: Boolean = false,
    val paymentDialog: PaymentDialogState = PaymentDialogState.Hidden,
    val message: String? = null
)

sealed interface PaymentDialogState {
    object Hidden : PaymentDialogState
    data class Pending(val recordId: Long, val qrCodeUrl: String?) : PaymentDialogState
}
