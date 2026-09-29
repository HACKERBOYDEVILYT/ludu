package com.example.rsludo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.PlayerColor
import com.example.ui.theme.RsGold
import com.example.ui.theme.RsGoldDark
import com.example.ui.theme.RsGoldLight

@Composable
fun DiceView(
    value: Int,
    isRolling: Boolean,
    canRoll: Boolean,
    playerColor: PlayerColor = PlayerColor.RED,
    size: Dp = 56.dp,
    onRoll: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dice_roll")
    val rollAngle by if (isRolling) {
        infiniteTransition.animateFloat(
            initialValue = -18f,
            targetValue = 18f,
            animationSpec = infiniteRepeatable(
                animation = tween(60, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dice_shake"
        )
    } else {
        rememberInfiniteTransition(label = "static").animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static"
        )
    }

    val pulseScale by if (canRoll && !isRolling) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "can_roll_pulse"
        )
    } else {
        rememberInfiniteTransition(label = "static").animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static"
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .scale(pulseScale)
                .rotate(rollAngle)
                .shadow(
                    elevation = if (canRoll) 10.dp else 4.dp,
                    shape = RoundedCornerShape(size * 0.24f),
                    spotColor = if (canRoll) playerColor.primary else Color.Black
                )
                .clip(RoundedCornerShape(size * 0.24f))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFEDE9F2)
                        )
                    )
                )
                .border(
                    width = if (canRoll) 2.5.dp else 1.dp,
                    color = if (canRoll) playerColor.primary else Color(0xFFCCCCCC),
                    shape = RoundedCornerShape(size * 0.24f)
                )
                .clickable(
                    enabled = canRoll && !isRolling,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRoll
                )
                .padding(size * 0.16f)
        ) {
            DicePips(value = value, dotColor = playerColor.dark)
        }
    }
}

@Composable
private fun DicePips(value: Int, dotColor: Color) {
    // 3x3 grid for realistic dice dots
    val pips = when (value) {
        1 -> listOf(Pair(1, 1))
        2 -> listOf(Pair(0, 0), Pair(2, 2))
        3 -> listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
        4 -> listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        5 -> listOf(Pair(0, 0), Pair(0, 2), Pair(1, 1), Pair(2, 0), Pair(2, 2))
        6 -> listOf(Pair(0, 0), Pair(0, 2), Pair(1, 0), Pair(1, 2), Pair(2, 0), Pair(2, 2))
        else -> listOf(Pair(1, 1))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        for (r in 0..2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (c in 0..2) {
                    val hasPip = pips.contains(Pair(r, c))
                    if (hasPip) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                                .border(0.5.dp, Color.Black.copy(alpha = 0.2f), CircleShape)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(7.dp))
                    }
                }
            }
        }
    }
}
