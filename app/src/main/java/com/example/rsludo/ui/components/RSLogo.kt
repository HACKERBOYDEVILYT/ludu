package com.example.rsludo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun RSLogo(
    size: Dp = 64.dp,
    showSubtitle: Boolean = true,
    animatedGlow: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rs_glow")
    val glowAlpha by if (animatedGlow) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
    } else {
        rememberInfiniteTransition(label = "static").animateFloat(
            initialValue = 0.8f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static_alpha"
        )
    }

    val goldGradient = Brush.linearGradient(
        colors = listOf(RsGoldLight, RsGold, RsGoldDark)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 12.dp * glowAlpha,
                    shape = RoundedCornerShape(size * 0.28f),
                    spotColor = RsGold,
                    ambientColor = RsGold
                )
                .clip(RoundedCornerShape(size * 0.28f))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E1A47),
                            Color(0xFF140B22)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = goldGradient,
                    shape = RoundedCornerShape(size * 0.28f)
                )
        ) {
            // Subtle luxury inner glow rim
            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .clip(RoundedCornerShape(size * 0.22f))
                    .border(
                        width = 1.dp,
                        color = RsGoldLight.copy(alpha = 0.4f * glowAlpha),
                        shape = RoundedCornerShape(size * 0.22f)
                    )
            )

            // RS Monogram
            Text(
                text = "RS",
                fontSize = (size.value * 0.48f).sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                color = RsGold,
                letterSpacing = 1.5.sp
            )
        }

        if (showSubtitle) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "RS LUDO",
                    fontSize = (size.value * 0.28f).coerceAtLeast(14f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    color = RsGoldLight
                )
            }
        }
    }
}
