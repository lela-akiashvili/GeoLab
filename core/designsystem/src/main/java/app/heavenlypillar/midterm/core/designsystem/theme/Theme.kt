package app.heavenlypillar.midterm.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BankingColorScheme = lightColorScheme(
    primary = BankTextDark,
    background = BankBackgroundGreen,
    surface = BankCardCream,
    onBackground = BankTextDark,
    onSurface = BankTextDark,
    error = BankDeleteRed
)

@Composable
fun BankingAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BankingColorScheme,
        content = content
    )
}