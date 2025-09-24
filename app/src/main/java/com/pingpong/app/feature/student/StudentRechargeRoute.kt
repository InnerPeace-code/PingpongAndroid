package com.pingpong.app.feature.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DividerItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.student.PaymentHistory
import com.pingpong.app.core.model.student.PaymentRecord

@Composable
fun StudentRechargeRoute(
    viewModel: StudentRechargeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    StudentRechargeScreen(
        state = state,
        onSelectAmount = viewModel::selectAmount,
        onCustomAmountChange = viewModel::updateCustomAmount,
        onPaymentMethodChange = viewModel::updatePaymentMethod,
        onCreatePayment = viewModel::createPayment,
        onConfirmPayment = viewModel::confirmPayment,
        onCancelPayment = viewModel::cancelPayment,
        onDismissDialog = viewModel::dismissDialog,
        onRefreshBalance = viewModel::refreshBalance,
        onRefreshTransactions = viewModel::refreshTransactions,
        onStatusFilterChange = viewModel::changeStatusFilter,
        onMethodFilterChange = viewModel::changeMethodFilter,
        onPageChange = viewModel::changePage,
        onPageSizeChange = viewModel::changePageSize,
        onMessageConsumed = viewModel::clearMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentRechargeScreen(
    state: StudentRechargeUiState,
    onSelectAmount: (Double) -> Unit,
    onCustomAmountChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onCreatePayment: () -> Unit,
    onConfirmPayment: () -> Unit,
    onCancelPayment: () -> Unit,
    onDismissDialog: () -> Unit,
    onRefreshBalance: () -> Unit,
    onRefreshTransactions: () -> Unit,
    onStatusFilterChange: (String?) -> Unit,
    onMethodFilterChange: (String?) -> Unit,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    onMessageConsumed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageConsumed()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Account", style = MaterialTheme.typography.headlineSmall)

            BalanceCard(state.balanceState, onRefreshBalance)

            AmountSelectionSection(
                selectedAmount = state.selectedAmount,
                customAmount = state.customAmount,
                onSelectAmount = onSelectAmount,
                onCustomAmountChange = onCustomAmountChange
            )

            PaymentMethodSection(state.paymentMethod, onPaymentMethodChange)

            Button(
                onClick = onCreatePayment,
                enabled = !state.isCreatingPayment
            ) {
                if (state.isCreatingPayment) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                }
                Text("Create payment")
            }

            DividerSection()

            TransactionsSection(
                state = state.transactionsState,
                page = state.page,
                size = state.size,
                statusFilter = state.statusFilter,
                methodFilter = state.methodFilter,
                onStatusFilterChange = onStatusFilterChange,
                onMethodFilterChange = onMethodFilterChange,
                onPageChange = onPageChange,
                onPageSizeChange = onPageSizeChange,
                onRefresh = onRefreshTransactions
            )
        }
    }

    when (val dialog = state.paymentDialog) {
        is PaymentDialogState.Pending -> PaymentDialog(
            qrCodeUrl = dialog.qrCodeUrl,
            onConfirm = onConfirmPayment,
            onCancel = onCancelPayment,
            onDismiss = onDismissDialog
        )
        PaymentDialogState.Hidden -> Unit
    }
}

@Composable
private fun BalanceCard(balanceState: UiState<Double>, onRefresh: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Current balance", style = MaterialTheme.typography.titleMedium)
            when (balanceState) {
                UiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                is UiState.Error -> {
                    Text(balanceState.message ?: "Unable to load balance", color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onRefresh) { Text("Retry") }
                }
                is UiState.Success -> {
                    Text("гд %.2f".format(balanceState.data), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onRefresh) { Text("Refresh") }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun AmountSelectionSection(
    selectedAmount: Double?,
    customAmount: String,
    onSelectAmount: (Double) -> Unit,
    onCustomAmountChange: (String) -> Unit
) {
    val presetAmounts = listOf(100.0, 200.0, 300.0, 500.0)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Recharge amount", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presetAmounts.forEach { amount ->
                val selected = selectedAmount != null && kotlin.math.abs(selectedAmount - amount) < 0.01
                OutlinedButton(onClick = { onSelectAmount(amount) }) {
                    Text("гд %.0f".format(amount), color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        OutlinedTextField(
            value = customAmount,
            onValueChange = onCustomAmountChange,
            label = { Text("Custom amount") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PaymentMethodSection(currentMethod: String, onChange: (String) -> Unit) {
    val methods = listOf("WECHAT" to "WeChat Pay", "ALIPAY" to "Alipay")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Payment method", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            methods.forEach { (value, label) ->
                val selected = currentMethod == value
                OutlinedButton(onClick = { onChange(value) }) {
                    Text(label, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun DividerSection() {
    Divider()
}

@Composable
private fun TransactionsSection(
    state: UiState<PaymentHistory>,
    page: Int,
    size: Int,
    statusFilter: String?,
    methodFilter: String?,
    onStatusFilterChange: (String?) -> Unit,
    onMethodFilterChange: (String?) -> Unit,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Transactions", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onRefresh) { Text("Refresh") }
        }
        FilterRow(statusFilter, methodFilter, onStatusFilterChange, onMethodFilterChange)
        when (state) {
            UiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is UiState.Error -> Text(state.message ?: "Failed to load records", color = MaterialTheme.colorScheme.error)
            is UiState.Success -> TransactionList(state.data, page, size, onPageChange, onPageSizeChange)
            else -> Unit
        }
    }
}

@Composable
private fun FilterRow(
    statusFilter: String?,
    methodFilter: String?,
    onStatusFilterChange: (String?) -> Unit,
    onMethodFilterChange: (String?) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        DropdownSelector(
            label = "Status",
            options = listOf(null to "All", "PENDING" to "Pending", "SUCCESS" to "Success", "FAILED" to "Failed", "REFUNDED" to "Refunded"),
            selectedValue = statusFilter,
            onValueChange = onStatusFilterChange
        )
        DropdownSelector(
            label = "Method",
            options = listOf(null to "All", "WECHAT" to "WeChat", "ALIPAY" to "Alipay", "OFFLINE" to "Offline", "ACCOUNT" to "Account"),
            selectedValue = methodFilter,
            onValueChange = onMethodFilterChange
        )
    }
}

@Composable
private fun DropdownSelector(
    label: String,
    options: List<Pair<String?, String>>,
    selectedValue: String?,
    onValueChange: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card {
        Column(modifier = Modifier.padding(8.dp)) {
            TextButton(onClick = { expanded = true }) {
                val selectedLabel = options.firstOrNull { it.first == selectedValue }?.second ?: label
                Text(selectedLabel)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (value, display) ->
                    DropdownMenuItem(
                        text = { Text(display) },
                        onClick = {
                            expanded = false
                            onValueChange(value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionList(
    history: PaymentHistory,
    page: Int,
    size: Int,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(history.records, key = { it.id }) { record ->
                PaymentRecordRow(record)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { if (page > 1) onPageChange(page - 1) }) { Text("Prev") }
            Text("Page $page")
            TextButton(onClick = { if ((page * size) < history.total) onPageChange(page + 1) }) { Text("Next") }
        }
    }
}

@Composable
private fun PaymentRecordRow(record: PaymentRecord) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("#${record.id} бд гд %.2f".format(record.amount), fontWeight = FontWeight.SemiBold)
            record.status?.let { Text("Status: $it") }
            record.method?.let { Text("Method: $it") }
            record.createdAt?.let { Text("Created: $it") }
            record.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun PaymentDialog(
    qrCodeUrl: String?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Scan to pay") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (qrCodeUrl.isNullOrBlank()) {
                    Text("Awaiting QR code from server")
                } else {
                    AsyncImage(model = qrCodeUrl, contentDescription = "Payment QR code")
                }
                Text("After completing the payment, confirm below")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("I have paid") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}

