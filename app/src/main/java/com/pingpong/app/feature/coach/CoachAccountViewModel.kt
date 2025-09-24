package com.pingpong.app.feature.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.CoachRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.coach.CoachTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CoachAccountViewModel @Inject constructor(
    private val coachRepository: CoachRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachAccountUiState())
    val uiState: StateFlow<CoachAccountUiState> = _uiState

    private var coachId: Long? = null

    init {
        viewModelScope.launch {
            loadSessionAndData()
        }
    }

    private suspend fun loadSessionAndData() {
        runCatching { sessionRepository.currentSession() }
            .onSuccess { session ->
                coachId = session.userId
                refresh()
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(error = throwable.message ?: "Unable to load session") }
            }
    }

    fun refresh(page: Int = _uiState.value.page, size: Int = _uiState.value.size, type: String? = _uiState.value.filterType) {
        val id = coachId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, page = page, size = size, filterType = type) }
            val result = coachRepository.getAccountSnapshot(id, page, size, type)
            result
                .onSuccess { snapshot ->
                    val transactions = snapshot.transactions
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            balance = snapshot.balance,
                            transactions = transactions.records,
                            total = transactions.total,
                            page = transactions.page,
                            size = transactions.size
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load account info") }
                }
        }
    }

    fun submitWithdraw(amount: Double, bankAccount: String, bankName: String, accountHolder: String) {
        val id = coachId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = coachRepository.submitWithdraw(id, amount, bankAccount, bankName, accountHolder)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Withdraw submitted") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, actionState = UiState.Idle) }
    }
}

data class CoachAccountUiState(
    val balance: Double = 0.0,
    val transactions: List<CoachTransaction> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val size: Int = 10,
    val filterType: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
