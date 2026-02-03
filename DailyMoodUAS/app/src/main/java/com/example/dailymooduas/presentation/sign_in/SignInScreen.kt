package com.example.dailymooduas.presentation.sign_in

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

// Color Palette untuk Daily Mood
private val SoftPink = Color(0xFFFFC1CC)
private val SoftPinkDark = Color(0xFFFF9FB0)
private val LavenderSoft = Color(0xFFE6D5F5)
private val PeachSoft = Color(0xFFFFE0D6)
private val BackgroundSoft = Color(0xFFFFFAFB)

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
                        SoftPink.copy(alpha = 0.4f),
                        LavenderSoft.copy(alpha = 0.3f),
                        BackgroundSoft,
                        PeachSoft.copy(alpha = 0.5f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000)) +
                    slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(1000, easing = FastOutSlowInEasing)
                    ) +
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(1000, easing = FastOutSlowInEasing)
                    )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.97f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Emoji Header
                    Text(
                        text = "💭",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // App Title
                    Text(
                        text = "Daily Mood",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftPinkDark,
                        letterSpacing = 1.sp
                    )

                    // Subtitle
                    Text(
                        text = "Catat perasaanmu, pahami dirimu",
                        fontSize = 15.sp,
                        color = Color(0xFF8D6E63),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quote
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = LavenderSoft.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "\"Setiap perasaan memiliki arti.\nSetiap hari adalah kesempatan baru\nuntuk memahami diri lebih dalam.\"",
                            fontSize = 13.sp,
                            color = Color(0xFF5D4037),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign In Button
                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftPinkDark,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🌸",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Masuk dengan Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Text
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mulai perjalanan mengenal dirimu hari ini ",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4AE)
                        )
                        Text(
                            text = "💕",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}