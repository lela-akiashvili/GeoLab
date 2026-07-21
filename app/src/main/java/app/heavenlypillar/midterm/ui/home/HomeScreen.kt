package app.heavenlypillar.midterm.ui.home

import java.time.LocalDate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.heavenlypillar.midterm.core.designsystem.components.BankingCard
import app.heavenlypillar.midterm.core.designsystem.components.PlannedExpenseRow
import app.heavenlypillar.midterm.core.designsystem.theme.BankDividerLine
import app.heavenlypillar.midterm.core.designsystem.theme.BankTextDark
import app.heavenlypillar.midterm.core.designsystem.theme.BankTextMuted
import app.heavenlypillar.midterm.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    backStack: SnapshotStateList<Screen>,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("expense") } // "expense" or "income"
    var editingId by remember { mutableStateOf<String?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editAmount by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }

    fun openDialog(type: String, id: String? = null, title: String = "", amount: String = "", date: String = "") {
        dialogType = type
        editingId = id
        editTitle = title
        editAmount = amount
        editDate = date
        showDialog = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "გამარჯობა, ლელა",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                windowInsets = WindowInsets(0.dp),
                navigationIcon = {
                    IconButton(onClick = { /* Handle Profile Click */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BankTextDark
                    )
                }
                is HomeUiState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Success -> {
                    val dashboardData = state.data
                    val currentMonth = remember { getCurrentGeorgianMonth() }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "სულ ხელმისაწვდომი თანხა",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        BankingCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { backStack.add(Screen.PersonalDetails) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dashboardData.balance.mainBalanceGEL,
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BankTextDark
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        painter = painterResource(id = app.heavenlypillar.midterm.core.designsystem.R.drawable.tbc_logo),
                                        contentDescription = "TBC",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Icon(
                                        painter = painterResource(id = app.heavenlypillar.midterm.core.designsystem.R.drawable.bog_logo),
                                        contentDescription = "BOG",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Icon(
                                        painter = painterResource(id = app.heavenlypillar.midterm.core.designsystem.R.drawable.credo_logo),
                                        contentDescription = "Credo",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${dashboardData.balance.mainBalanceGEL}  |  ${dashboardData.balance.eurBalance}  |  ${dashboardData.balance.usdBalance}",
                                fontSize = 15.sp,
                                color = BankTextDark,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = BankDividerLine
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "მოახლოებული გადასახადები",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(text = currentMonth, fontSize = 14.sp, color = Color.White)
                        }

                        dashboardData.upcomingBills.forEach { bill ->
                            BankingCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val customDrawableRes = when (bill.iconType) {
                                        "house" -> app.heavenlypillar.midterm.core.designsystem.R.drawable.home_bills
                                        "card" -> app.heavenlypillar.midterm.core.designsystem.R.drawable.credit_cards
                                        "bank" -> app.heavenlypillar.midterm.core.designsystem.R
                                            .drawable.savings
                                        else -> app.heavenlypillar.midterm.core.designsystem.R.drawable.star_bookmark
                                    }

                                    Icon(
                                        painter = painterResource(id = customDrawableRes),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = bill.title,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BankTextDark
                                        )
                                        Text(
                                            text = bill.subtitleDetails,
                                            fontSize = 13.sp,
                                            color = BankTextMuted
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = BankDividerLine
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "წინასწარ დაგეგმილი ხარჯები",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            TextButton(onClick = { openDialog("expense") }) {
                                Text("+ დამატება", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        BankingCard(modifier = Modifier.fillMaxWidth()) {
                            dashboardData.plannedExpenses.forEach { expense ->
                                PlannedExpenseRow(
                                    title = expense.title,
                                    amount = expense.amount,
                                    date = expense.date,
                                    onEditClick = { openDialog("expense", expense.id, expense.title, expense.amount, expense.date) },
                                    onDeleteClick = { viewModel.deleteItem(expense.id) }
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = BankDividerLine
                            )
                            val totalSum = dashboardData.plannedExpenses.sumOf {
                                it.amount.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
                            }
                            Text(
                                text = "-%,.2f ₾".format(totalSum), // Automatically adds the decimal formatting
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BankTextDark,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = BankDividerLine
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "მოსალოდნელი შემოსავალი",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            TextButton(onClick = { openDialog("income") }) {
                                Text("+ დამატება", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        BankingCard(modifier = Modifier.fillMaxWidth()) {
                            dashboardData.expectedIncomes.forEach { income ->
                                PlannedExpenseRow(
                                    title = income.title,
                                    amount = income.amount,
                                    date = income.date,
                                    onEditClick = { openDialog("income", income.id, income.title, income.amount, income.date) },
                                    onDeleteClick = { viewModel.deleteItem(income.id) }
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = BankDividerLine
                            )
                            val totalIncome = dashboardData.expectedIncomes.sumOf {
                                it.amount.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
                            }
                            Text(
                                text = "+%,.2f ₾".format(totalIncome),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp)
                            )
                        }

                        val isSurplus = dashboardData.projectedBalance >= 0
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSurplus) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isSurplus) "თვის ბოლოს მოსალოდნელი ნაშთი" else "ყურადღება: მოსალოდნელია ოვერდრაფტი",
                                    color = if (isSurplus) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "%,.2f ₾".format(dashboardData.projectedBalance),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSurplus) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }

                        Button(
                            onClick = { backStack.add(Screen.PersonalDetails) },
                            colors = ButtonDefaults.buttonColors(containerColor = BankTextDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 32.dp)
                        ) {
                            Text("პირად დეტალებზე გადასვლა", color = Color.White)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddEditItemDialog(
                initialTitle = editTitle,
                initialAmount = editAmount,
                initialDate = editDate,
                onDismiss = { showDialog = false },
                onSave = { newTitle, newAmount, newDate, bankName ->
                    val isIncome = dialogType == "income"
                    viewModel.saveItem(
                        id = editingId,
                        title = newTitle,
                        amount = newAmount,
                        date = newDate,
                        bankName = bankName,
                        isIncome = isIncome
                    )
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemDialog(
    initialTitle: String = "",
    initialAmount: String = "",
    initialDate: String = "",
    onDismiss: () -> Unit,
    onSave: (title: String, amount: String, date: String, bankName: String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var amount by remember { mutableStateOf(initialAmount) }
    var date by remember { mutableStateOf(initialDate) }

    val bankOptions = listOf("TBC Bank", "Bank of Georgia", "Credo Bank")
    var expanded by remember { mutableStateOf(false) }
    var selectedBank by remember { mutableStateOf(bankOptions[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "ახალი ჩანაწერის დამატება" else "ჩანაწერის რედაქტირება") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedBank,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("აირჩიეთ ბარათი") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        bankOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedBank = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("დასახელება (მაგ. ქირა)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("თანხა (მაგ. 500.00)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("თარიღი (მაგ. 15 ივლისი)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalAmount = if (amount.contains("₾")) amount else "$amount ₾"
                    onSave(title, finalAmount, date, selectedBank)
                }
            ) { Text("შენახვა") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("გაუქმება") }
        }
    )
}

fun getCurrentGeorgianMonth(): String {
    return when (LocalDate.now().monthValue) {
        1 -> "იანვარი"
        2 -> "თებერვალი"
        3 -> "მარტი"
        4 -> "აპრილი"
        5 -> "მაისი"
        6 -> "ივნისი"
        7 -> "ივლისი"
        8 -> "აგვისტო"
        9 -> "სექტემბერი"
        10 -> "ოქტომბერი"
        11 -> "ნოემბერი"
        12 -> "დეკემბერი"
        else -> ""
    }
}