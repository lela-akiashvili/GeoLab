package app.heavenlypillar.midterm.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.heavenlypillar.midterm.core.designsystem.theme.BankCardCream

@Composable
fun BankingCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null, // ✨ Added optional click handling
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = BankCardCream),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (onClick != null) 4.dp else 2.dp // Gives interactive cards extra depth pop
        )
    ) {
        Column(
            modifier = Modifier
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(16.dp)
        ) {
            content()
        }
    }
}