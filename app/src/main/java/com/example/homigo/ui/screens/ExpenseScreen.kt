package com.example.homigo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.homigo.data.model.Expense
import com.example.homigo.data.model.ExpenseSummary
import com.example.homigo.data.model.Profile
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch

@Composable
fun ExpenseScreen() {
    val coroutineScope = rememberCoroutineScope()
    var activeTab by remember { mutableStateOf(0) } // 0 for Dashboard, 1 for Splitter history
    var showAddDialog by remember { mutableStateOf(false) }

    var summary by remember { mutableStateOf<ExpenseSummary?>(null) }
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var roommates by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadData() {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                summary = HomigoRepository.getExpenseSummary()
                expenses = HomigoRepository.getExpenses()
                roommates = HomigoRepository.getChatList()
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: "Failed to load expenses"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        floatingActionButton = {
            if (activeTab == 1) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Text("+ Add Bill", modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = activeTab) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Dashboard", fontWeight = FontWeight.Bold) })
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Split Expenses", fontWeight = FontWeight.Bold) })
            }

            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (errorMessage != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { loadData() }) {
                            Text("Retry")
                        }
                    }
                } else {
                    if (activeTab == 0) {
                        ExpenseDashboard(
                            summary = summary,
                            onMarkAsPaid = { splitId ->
                                coroutineScope.launch {
                                    try {
                                        HomigoRepository.payExpense(splitId)
                                        loadData()
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to pay split: ${e.message}"
                                    }
                                }
                            }
                        )
                    } else {
                        ExpenseHistory(
                            expenses = expenses,
                            userId = HomigoRepository.currentUser.collectAsState().value?.id ?: 0
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddExpenseDialog(
                roommates = roommates,
                onDismiss = { showAddDialog = false },
                onAddSuccess = {
                    showAddDialog = false
                    loadData()
                }
            )
        }
    }
}

@Composable
fun ExpenseDashboard(
    summary: ExpenseSummary?,
    onMarkAsPaid: (splitId: Int) -> Unit
) {
    if (summary == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Outstanding Balances Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Owed to you", fontSize = 13.sp)
                    Text("₹${summary.totalOwed}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("You owe", fontSize = 13.sp)
                    Text("₹${summary.totalOwe}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // Circular Expense Pie Chart!
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Monthly Spending Share", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                
                val breakdown = summary.categoryBreakdown
                if (breakdown.isEmpty()) {
                    Text("No spending registered this month.", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                } else {
                    val colorsList = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        Color(0xFFEAB308), // Yellow
                        Color(0xFF10B981), // Green
                        Color(0xFF8B5CF6)  // Purple
                    )

                    val totalSpending = breakdown.sumOf { it.total }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Drawing using Compose Canvas
                        Canvas(modifier = Modifier.size(120.dp)) {
                            var currentAngle = 0f
                            breakdown.forEachIndexed { i, cat ->
                                val sweep = (cat.total / totalSpending).toFloat() * 360f
                                drawArc(
                                    color = colorsList[i % colorsList.size],
                                    startAngle = currentAngle,
                                    sweepAngle = sweep,
                                    useCenter = true,
                                    size = Size(size.width, size.height)
                                )
                                currentAngle += sweep
                            }
                        }

                        // Legends column
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            breakdown.forEachIndexed { i, cat ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(12.dp).background(colorsList[i % colorsList.size]))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${cat.category}: ₹${cat.total}", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Owed list
        if (summary.youOwe.isNotEmpty()) {
            Text("Outstanding Bills You Owe", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            summary.youOwe.forEach { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Owe ₹${item.share} to ${item.creditor_name}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Button(onClick = { onMarkAsPaid(item.split_id) }) {
                            Text("Settle", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        if (summary.youAreOwed.isNotEmpty()) {
            Text("Pending Payments Roommates Owe You", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            summary.youAreOwed.forEach { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${item.debtor_name} owes you ₹${item.share}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Badge(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                            Text("Pending Pay", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseHistory(
    expenses: List<Expense>,
    userId: Int
) {
    if (expenses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No split expense history found.", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(expenses) { exp ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(exp.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Category: ${exp.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        Text("₹${exp.amount}", fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }

                    HorizontalDivider()

                    Text(
                        text = "Paid by ${exp.creator_name} - Split into ${exp.splits.size} shares (₹${exp.my_share} each)",
                        fontSize = 12.sp
                    )

                    // splits status list
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        exp.splits.forEach { split ->
                            val isPaid = split.is_paid == 1
                            val isMe = split.user_id == userId
                            Badge(
                                containerColor = if (isPaid) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "${if (isMe) "You" else split.name}: ${if (isPaid) "Paid" else "Pending"}",
                                    color = if (isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    roommates: List<Profile>,
    onDismiss: () -> Unit,
    onAddSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    
    val categories = listOf("Rent", "Electricity", "WiFi", "Grocery", "Miscellaneous")
    var category by remember { mutableStateOf(categories.first()) }
    
    val selectedRoommates = remember { mutableStateListOf<Int>() }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Log Shared Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title (e.g. WiFi Bill)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Total Bill Amount (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Select Category", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.take(3).forEach { cat ->
                        FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat, fontSize = 11.sp) })
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat, fontSize = 11.sp) })
                    }
                }

                Text("Select Roommates to split with:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                if (roommates.isEmpty()) {
                    Text("No connected roommates to split bills with. Get accepted roommate requests first.", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                } else {
                    roommates.forEach { r ->
                        val isChecked = selectedRoommates.contains(r.user_id)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) selectedRoommates.add(r.user_id)
                                    else selectedRoommates.remove(r.user_id)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(r.name ?: "Roommate", fontSize = 14.sp)
                        }
                    }
                }

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull()
                            if (title.isBlank() || amt == null || amt <= 0 || selectedRoommates.isEmpty()) {
                                error = "Please complete all fields and pick at least 1 roommate to split."
                                return@Button
                            }
                            isSaving = true
                            coroutineScope.launch {
                                try {
                                    HomigoRepository.addExpense(title, amt, category, selectedRoommates.toList())
                                    isSaving = false
                                    onAddSuccess()
                                } catch (e: Exception) {
                                    isSaving = false
                                    error = e.message ?: "Failed to add expense"
                                }
                            }
                        },
                        enabled = !isSaving && selectedRoommates.isNotEmpty()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Split Bill")
                        }
                    }
                }
            }
        }
    }
}
