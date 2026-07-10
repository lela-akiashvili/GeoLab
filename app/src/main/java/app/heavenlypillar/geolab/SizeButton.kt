package app.heavenlypillar.geolab

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

@Composable
fun SizeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val sizeSelectedColor = Color.Black
    val buttonShape = RoundedCornerShape(12.dp)

    Button(
        onClick = onClick,
        modifier = Modifier
            .size(45.dp)
            .then(
                if (!isSelected) Modifier.border(2.dp, Color.LightGray, buttonShape)
                else Modifier
            ),
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) sizeSelectedColor else Color.White,
            contentColor = if (isSelected) Color.White else sizeSelectedColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}