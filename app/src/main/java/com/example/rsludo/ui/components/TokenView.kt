package com.example.rsludo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.PlayerColor
import com.example.rsludo.model.Token
import com.example.ui.theme.*

@Composable
fun TokenView(
    token: Token,
    avatarUri: String? = null,
    isAi: Boolean = false,
    size: Dp = 24.dp,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSelectable = token.isSelectable

    val infiniteTransition = rememberInfiniteTransition(label = "token_3d_anim")

    // Vertical bounce jump animation when selectable
    val bounceY by if (isSelectable) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -7f,
            animationSpec = infiniteRepeatable(
                animation = tween(380, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bounce"
        )
    } else {
        rememberInfiniteTransition(label = "static_b").animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static_b"
        )
    }

    val pulseScale by if (isSelectable) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(420, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
    } else {
        rememberInfiniteTransition(label = "static_p").animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static_p"
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size * 1.4f)
            .clickable(
                enabled = isSelectable,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Dynamic bottom ground shadow (expands when piece lands, fades when jumps)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 2.dp)
                .size(width = size * 0.9f, height = size * 0.35f)
                .clip(CircleShape)
                .background(
                    Color.Black.copy(
                        alpha = if (isSelectable) 0.35f + (bounceY / 20f) else 0.45f
                    )
                )
        )

        // The 3D Piece Container with vertical bounce
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(y = bounceY.dp)
                .scale(pulseScale)
        ) {
            // Glowing aura ring when selectable
            if (isSelectable) {
                Box(
                    modifier = Modifier
                        .size(size * 1.35f)
                        .clip(CircleShape)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(RsGoldLight, RsGold, token.color.light, RsGoldLight)
                            ),
                            shape = CircleShape
                        )
                )
            }

            // 3D Brass / Gold Beveled Rim
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size * 1.15f)
                    .shadow(
                        elevation = if (isSelectable) 10.dp else 4.dp,
                        shape = CircleShape,
                        spotColor = if (isSelectable) RsGold else Color.Black
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                RsGoldLight,
                                Color(0xFFD4AF37),
                                Color(0xFF8C6D1F),
                                RsGoldLight
                            )
                        )
                    )
                    .border(1.2.dp, Color(0xFFFFF3B0), CircleShape)
                    .padding(2.dp)
            ) {
                // Inner Player Piece Core (shows photo or stylized avatar)
                PlayerAvatar(
                    color = token.color,
                    avatarUri = avatarUri,
                    isAi = isAi,
                    size = size * 0.95f,
                    isActive = isSelectable,
                    borderWidth = 1.5.dp
                )

                // Domed Lens Reflection (glossy specular curve)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .fillMaxHeight(0.42f)
                        .align(Alignment.TopCenter)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Crown Badge if token is in home stretch (steps 51..55)
            if (token.isInHomeCorridor || token.isFinished) {
                Text(
                    text = "👑",
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-8).dp)
                )
            }
        }
    }
}
