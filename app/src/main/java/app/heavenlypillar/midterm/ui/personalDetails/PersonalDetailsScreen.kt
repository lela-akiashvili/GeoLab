package app.heavenlypillar.midterm.ui.personaldetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.heavenlypillar.midterm.core.designsystem.components.BankingCard
import app.heavenlypillar.midterm.core.designsystem.theme.BankDividerLine
import app.heavenlypillar.midterm.core.designsystem.theme.BankTextDark
import app.heavenlypillar.midterm.core.designsystem.theme.BankTextMuted
import app.heavenlypillar.midterm.data.model.PersonalDetailsUiState
import app.heavenlypillar.midterm.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailsScreen(
    backStack: SnapshotStateList<Screen>,
    modifier: Modifier = Modifier,
    viewModel: PersonalDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("პირადი დეტალები", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                windowInsets = WindowInsets(0.dp),
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLast() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                is PersonalDetailsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BankTextDark)
                }
                is PersonalDetailsUiState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is PersonalDetailsUiState.Success -> {
                    val data = state.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        // saved cards
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("ჩემი ბარათები", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)

                            BankingCard(modifier = Modifier.fillMaxWidth()) {
                                data.savedCards.forEachIndexed { index, card ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { backStack.add(Screen.BankDetails(card.bankName)) }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val logoRes = when (card.logoRes) {
                                            "tbc" -> app.heavenlypillar.midterm.core.designsystem.R.drawable.tbc_logo
                                            "credo" -> app.heavenlypillar.midterm.core.designsystem.R.drawable.credo_logo
                                            else -> app.heavenlypillar.midterm.core.designsystem.R.drawable.bog_logo
                                        }

                                        Icon(
                                            painter = painterResource(id = logoRes),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(32.dp)
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = card.bankName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BankTextDark)
                                            Text(text = card.lastFourDigits, fontSize = 14.sp, color = BankTextMuted)
                                        }

                                        Text(
                                            text = card.balance,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BankTextDark
                                        )
                                    }

                                    if (index < data.savedCards.lastIndex) {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BankDividerLine)
                                    }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("საპენსიო ფონდი", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)

                            BankingCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("დაგროვილი თანხა:", fontSize = 16.sp, color = BankTextMuted)
                                    Text(data.pensionStat, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BankTextDark)
                                }
                            }
                        }

                        var isInvestmentsExpanded by remember { mutableStateOf(false) }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("ინვესტიციები", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)

                            BankingCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { isInvestmentsExpanded = !isInvestmentsExpanded }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("პორტფელის ღირებულება", fontSize = 14.sp, color = BankTextMuted)
                                        Text(data.investmentTotal, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BankTextDark)
                                    }

                                    Icon(
                                        imageVector = if (isInvestmentsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expand",
                                        tint = BankTextDark,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                AnimatedVisibility(visible = isInvestmentsExpanded) {
                                    Column(modifier = Modifier.padding(top = 16.dp)) {
                                        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp), color = BankDividerLine)

                                        data.investments.forEachIndexed { index, investment ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(investment.ticker, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BankTextDark)
                                                    Text(investment.shares, fontSize = 13.sp, color = BankTextMuted)
                                                }
                                                Text(investment.totalValue, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BankTextDark)
                                            }

                                            if (index < data.investments.lastIndex) {
                                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BankDividerLine)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}