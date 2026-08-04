package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserEntity

@Composable
fun WalletScreen(
    userProfile: UserEntity?,
    transactions: List<TransactionEntity>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onDeposit: (amount: Double, method: String) -> Unit,
    onWithdraw: (amount: Double, method: String, payoutAccount: String, callback: (Boolean) -> Unit) -> Unit
) {
    var showDepositModal by remember { mutableStateOf(false) }
    var showWithdrawModal by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    val filterOptions = listOf("ALL", "DEPOSIT", "WITHDRAWAL", "TASK_REWARD", "REFERRAL_BONUS")

    val filteredTransactions = when (selectedFilter) {
        "ALL" -> transactions
        else -> transactions.filter { it.type == selectedFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Wallet Balance Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "BaizPay Digital Wallet",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "USD $" + String.format("%.2f", userProfile?.walletBalance ?: 0.0),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showDepositModal = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("wallet_deposit_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Deposit", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showWithdrawModal = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("wallet_withdraw_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(Icons.Default.ArrowUpward, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Withdraw", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (statusMessage.isNotBlank()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = statusMessage,
                        color = Color(0xFF10B981),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Transaction Audit Log Filter
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction History & Audit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FilterList, null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Filter", fontSize = 12.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { filter ->
                        val isSel = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSel) Color(0xFF2563EB) else MaterialTheme.colorScheme.surface)
                                .clickable { onFilterChange(filter) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter.replace("_", " "),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        items(filteredTransactions) { txn ->
            TransactionRowItem(txn)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Deposit Modal
    if (showDepositModal) {
        var depAmount by remember { mutableStateOf("25.00") }
        var depMethod by remember { mutableStateOf("Visa Card") }

        AlertDialog(
            onDismissRequest = { showDepositModal = false },
            title = { Text("Deposit Funds to BaizPay Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = depAmount,
                        onValueChange = { depAmount = it },
                        label = { Text("Deposit Amount (USD $)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deposit_amount_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Payment Method:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    listOf("Visa Card", "Mastercard", "PayPal", "Crypto USDT").forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { depMethod = method }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Payment,
                                null,
                                tint = if (depMethod == method) Color(0xFF2563EB) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                method,
                                fontWeight = if (depMethod == method) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = depAmount.toDoubleOrNull() ?: 10.0
                        onDeposit(amt, depMethod)
                        showDepositModal = false
                        statusMessage = "Deposit of $$amt via $depMethod processed successfully!"
                    },
                    modifier = Modifier.testTag("confirm_deposit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Confirm Deposit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Withdrawal Modal
    if (showWithdrawModal) {
        var wdAmount by remember { mutableStateOf("20.00") }
        var wdMethod by remember { mutableStateOf("PayPal") }
        var payoutAccount by remember { mutableStateOf(userProfile?.email ?: "baizid2537@gmail.com") }

        AlertDialog(
            onDismissRequest = { showWithdrawModal = false },
            title = { Text("Withdraw Funds (Min $10 USD)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = wdAmount,
                        onValueChange = { wdAmount = it },
                        label = { Text("Withdrawal Amount (USD $)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = payoutAccount,
                        onValueChange = { payoutAccount = it },
                        label = { Text("Payout Email / Wallet Address") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payout_account_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Payout Gateway:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    listOf("PayPal", "Bank Wire Transfer", "Crypto USDT", "Visa / Mastercard Payout").forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { wdMethod = method }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                null,
                                tint = if (wdMethod == method) Color(0xFF10B981) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                method,
                                fontWeight = if (wdMethod == method) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = wdAmount.toDoubleOrNull() ?: 10.0
                        onWithdraw(amt, wdMethod, payoutAccount) { success ->
                            showWithdrawModal = false
                            statusMessage = if (success) {
                                "Withdrawal request of $$amt submitted for approval!"
                            } else {
                                "Insufficient available balance for withdrawal."
                            }
                        }
                    },
                    modifier = Modifier.testTag("confirm_withdraw_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Submit Withdrawal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
