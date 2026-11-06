package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    var loadingPhrase by remember { mutableStateOf("Harmonizing mind...") }

    LaunchedEffect(Unit) {
        // Step 1: Smooth breathing scale & fade-in of ArrowVeda lotus logo
        alpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        scale.animateTo(1f, tween(750, easing = FastOutSlowInEasing))

        // Step 2: Fade in title & subtitle
        textAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        subtitleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))

        delay(600)
        loadingPhrase = "Loading 10,000 Zen Mazes..."
        delay(650)
        loadingPhrase = "Entering tranquility..."
        delay(450)

        onSplashFinished()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Allow instant skip on user tap
                onSplashFinished()
            },
        color = Color(0xFFF9EED9) // Authentic warm beige from user artwork
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                // Official ArrowVeda Lotus Squircle Logo Asset
                Box(
                    modifier = Modifier
                        .scale(scale.value)
                        .alpha(alpha.value)
                        .size(190.dp)
                        .shadow(24.dp, RoundedCornerShape(44.dp), ambientColor = Color(0x33C08A3E), spotColor = Color(0x448B5A2B))
                        .clip(RoundedCornerShape(44.dp))
                        .background(Color(0xFFFFF9EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowveda_icon_fg),
                        contentDescription = "ArrowVeda Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Brand Typography: "ArrowVeda"
                Row(
                    modifier = Modifier.alpha(textAlpha.value),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Arrow",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2B3441), // Charcoal black from user artwork
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Veda",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF986B49), // Warm rich sandalwood brown from user artwork
                        letterSpacing = (-0.5).sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle: "Untangle your mind"
                Text(
                    text = "Untangle your mind",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF5D4A39), // Muted dark umber
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.alpha(subtitleAlpha.value)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Subtle Zen Loading Indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(subtitleAlpha.value)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF00A3FF),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = loadingPhrase,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8C7A6B)
                    )
                }
            }

            // Footer
            Text(
                text = "10,000 Levels • 100% Offline • Calming ASMR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA09080),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}
