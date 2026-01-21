package com.example.todolistquiz.presentation.sign_in

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val SkyBlue = Color(0xFFB3E5FC)
private val SkyBlueDark = Color(0xFF81D4FA)
private val PeachSoft = Color(0xFFFFE0D6)
private val BackgroundSoft = Color(0xFFF9FBFD)

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(state.signInError) {
        state.signInError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SkyBlue.copy(alpha = 0.6f),
                        BackgroundSoft,
                        PeachSoft.copy(alpha = 0.7f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(900)) +
                    slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(900, easing = FastOutSlowInEasing)
                    )
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {


                    Text(
                        text = "TodoList 🌤",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlueDark
                    )

                    Text(
                        text = "Atur harimu dengan lebih rapi dan tenang",
                        fontSize = 14.sp,
                        color = Color(0xFF607D8B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Menuliskan rasa adalah cara paling pelan\nuntuk membuat hati lebih baik.",
                        fontSize = 13.sp,
                        color = Color(0xFF90A4AE),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBlueDark,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Sign in with Google",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))


                    Text(
                        text = "Mulai dari hal kecil, rasakan perubahan 💕",
                        fontSize = 12.sp,
                        color = Color(0xFF8D6E63)
                    )
                }
            }
        }
    }
}
