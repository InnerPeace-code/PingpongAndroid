package com.pingpong.app.feature.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.model.coach.CoachTransaction
import kotlinx.coroutines.delay

@Composable
fun CoachAccountRoute(
    viewModel: CoachAccountViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    CoachAccountScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onWithdraw = viewModel::submitWithdraw
    )
}

@Composable
private fun CoachAccountScreen(
    state: CoachAccountUiState,
    onRefresh: (page: Int, size: Int, type: String?) -> Unit,
    onWithdraw: (Double, String, String, String) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var bankAccount by rememberSaveable { mutableStateOf("") }
    var bankName by rememberSaveable { mutableStateOf("") }
    var accountHolder by rememberSaveable { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    val filterOptions = listOf(null to "All", "COURSE_INCOME" to "Income", "WITHDRAW" to "Withdraw")
    val totalPages = if (state.size > 0) {
        (state.total + state.size - 1) / state.size
    } else {
        1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Account Balance", style = MaterialTheme.typography.titleLarge)
                Text(text = "${state.balance}", style = MaterialTheme.typography.displaySmall)
                state.message?.let { Text(text = it, color = MaterialTheme.colorScheme.secondary) }
                state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    filterOptions.forEach { (value, label) ->
                        val selected = state.filterType == value
                        if (selected) {
                            FilledTonalButton(onClick = { onRefresh(1, state.size, value) }) {
                                Text(text = label)
                            }
                        } else {
                            OutlinedButton(onClick = { onRefresh(1, state.size, value) }) {
                                Text(text = label)
                            }
                        }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Withdraw", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bankAccount,
                    onValueChange = { bankAccount = it },
                    label = { Text("Bank account") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = accountHolder,
                    onValueChange = { accountHolder = it },
                    label = { Text("Account holder") },
                    modifier = Modifier.fillMaxWidth()
                )
                formError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                Button(onClick = {
                    val parsed = amount.toDoubleOrNull()
                    if (parsed == null || parsed <= 0) {
                        formError = "Enter a valid amount"
                    } else if (bankAccount.isBlank() || bankName.isBlank() || accountHolder.isBlank()) {
                        formError = "Fill all fields"
                    } else {
                        formError = null
                        onWithdraw(parsed, bankAccount.trim(), bankName.trim(), accountHolder.trim())
                    }
                }) {
                    Text(text = "Submit withdraw")
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Transactions", style = MaterialTheme.typography.titleMedium)
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    OutlinedButton(onClick = { onRefresh(state.page, state.size, state.filterType) }) {
                        Text(text = "Refresh")
                    }
                }
            }
            if (state.transactions.isEmpty() && !state.isLoading) {
                Text(text = "No transaction records")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.transactions, key = { it.id }) { transaction ->
                        CoachTransactionCard(transaction = transaction)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = { onRefresh((state.page - 1).coerceAtLeast(1), state.size, state.filterType) }, enabled = state.page > 1) {
                        Text(text = "Previous")
                    }
                    Text(text = "Page ${state.page} / $totalPages")
                    OutlinedButton(
                        onClick = { onRefresh((state.page + 1).coerceAtMost(totalPages), state.size, state.filterType) },
                        enabled = state.page < totalPages
                    ) {
                        Text(text = "Next")
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachTransactionCard(transaction: CoachTransaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Amount: ${transaction.amount}", style = MaterialTheme.typography.titleMedium)
            transaction.type?.let { Text(text = "Type: $it") }
            transaction.status?.let { Text(text = "Status: $it") }
            transaction.description?.let { Text(text = it) }
            transaction.createdAt?.let { Text(text = "Created at: $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}
