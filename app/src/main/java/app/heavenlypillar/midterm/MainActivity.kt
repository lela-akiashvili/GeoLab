package app.heavenlypillar.midterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.heavenlypillar.midterm.core.designsystem.theme.BankingAppTheme
import app.heavenlypillar.midterm.navigation.Screen
import app.heavenlypillar.midterm.ui.bankdetails.BankDetailsScreen
import app.heavenlypillar.midterm.ui.home.HomeScreen
import app.heavenlypillar.midterm.ui.personaldetails.PersonalDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BankingAppTheme {
                val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
                BackHandler(enabled = backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavDisplay(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding),
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() }, // Handles back swipe
                        entryProvider = entryProvider {
                            entry<Screen.Home> {
                                HomeScreen(backStack = backStack)
                            }
                            entry<Screen.PersonalDetails> {
                                PersonalDetailsScreen(backStack = backStack)
                            }
                            entry<Screen.BankDetails> { key ->
                                BankDetailsScreen(bankName = key.bankName, backStack = backStack)
                            }
                        }
                    )
                }
            }
        }
    }
}