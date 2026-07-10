package app.heavenlypillar.geolab

import android.widget.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*

@Composable
fun ProductDetailsScreen() {
    val context = LocalContext.current

    var selectedSize by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp, 30.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.left_arrow),
                contentDescription = "Go Back",
                Modifier.size(40.dp)
            )
            Text(
                text = "Details", fontSize = 25.sp, fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = R.drawable.notification_bell),
                contentDescription = "Notifications",
                Modifier.size(30.dp)
            )
        }
        Box(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.product_image),
                contentDescription = "Product Image",
                Modifier.fillMaxWidth()
            )
            Icon(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = "Add to Favourites",
                Modifier
                    .align(Alignment.TopEnd)
                    .size(45.dp)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(10.dp)
            )
        }
        Text(
            text = "Regular Fit Slogan", fontSize = 25.sp, fontWeight = FontWeight.Bold
        )
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.start_rating),
                contentDescription = "Rating Star",
                modifier = Modifier.size(25.dp),
                tint = Color(0xFFFFB300)
            )
            Text(
                text = "4.5/5", textDecoration = TextDecoration.Underline
            )
            Text(
                text = "(45 Reviews)", color = Color.Gray
            )
        }
        Text(
            text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since 1966, when designers at Letraset and James Mosley, the librarian at St Bride Printing Library in London, took a 1914 Cicero translation and scrambled it to make",
            color = Color.Gray
        )
        Text(
            text = "Choose Size", fontSize = 20.sp, fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SizeButton(
                text = "S",
                isSelected = selectedSize == "S",
                onClick = { selectedSize = if (selectedSize == "S") null else "S" }
            )
            SizeButton(
                text = "M",
                isSelected = selectedSize == "M",
                onClick = { selectedSize = if (selectedSize == "M") null else "M" }
            )
            SizeButton(
                text = "L",
                isSelected = selectedSize == "L",
                onClick = { selectedSize = if (selectedSize == "L") null else "L" }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Price", fontSize = 15.sp, color = Color.Gray
                )
                Text(
                    text = "$1,190", fontSize = 25.sp, fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    if (selectedSize == null) {
                        Toast.makeText(context, "Please Choose a Size.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Size $selectedSize Added to Cart!", Toast
                            .LENGTH_SHORT).show()
                    }
                },
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cart),
                    contentDescription = "Add to Cart",
                    Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add to Cart")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProductDetailsScreenPreview() {
    ProductDetailsScreen()
}