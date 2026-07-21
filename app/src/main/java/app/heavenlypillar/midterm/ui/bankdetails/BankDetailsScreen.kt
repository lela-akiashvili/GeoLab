package app.heavenlypillar.midterm.ui.bankdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.heavenlypillar.midterm.navigation.Screen
import app.heavenlypillar.midterm.core.designsystem.components.BankingCard
import app.heavenlypillar.midterm.core.designsystem.theme.BankTextDark
import app.heavenlypillar.midterm.core.designsystem.theme.BankTextMuted
import app.heavenlypillar.midterm.ui.bankdetails.BankDetailsViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankDetailsScreen(
    bankName: String,
    backStack: SnapshotStateList<Screen>,
    modifier: Modifier = Modifier,
    viewModel: BankDetailsViewModel = viewModel()
) {

    val transactions by viewModel.transactions.collectAsState()
    val mainBalance by viewModel.mainBalance.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // If the user actually selected a file (uri is not null), trigger the upload logic
        if (uri != null) {
            viewModel.uploadStatement(bankName)
        }
    }

    LaunchedEffect(bankName) {
        viewModel.loadBankData(bankName)
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(bankName, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                windowInsets = WindowInsets(0.dp),
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BankingCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("მიმდინარე ბალანსი", color = BankTextMuted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("%,.2f ₾".format(mainBalance), color = BankTextDark, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { pdfPickerLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("მუშავდება...", color = Color.White)
                } else {
                    Text("ამონაწერის ატვირთვა (PDF)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("ბოლო ტრანზაქციები", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)

            BankingCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions) { transaction ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(transaction.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BankTextDark)
                                Text(transaction.date, fontSize = 14.sp, color = BankTextMuted)
                            }

                            val sign = if (transaction.isPositive) "+" else "-"
                            Text(
                                text = "$sign ${transaction.amount} ₾",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (transaction.isPositive) Color(0xFF2E7D32) else BankTextDark
                            )
                        }
                    }
                }
            }
        }
    }
}