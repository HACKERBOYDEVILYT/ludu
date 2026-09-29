package com.example.rsludo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rsludo.model.PlayerColor
import com.example.rsludo.model.Token
import com.example.ui.theme.RsGold

@Composable
fun TokenView(
    token: Token,
    avatarUri: String? = null,
    isAi: Boolean = false,
    size: Dp = 22.dp,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSelectable = token.isSelectable

    val infiniteTransition = rememberInfiniteTransition(label = "token_selectable")
    val pulseScale by if (isSelectable) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(450, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        rememberInfiniteTransition(label = "static").animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static"
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size * 1.35f)
            .scale(pulseScale)
            .clickable(
                enabled = isSelectable,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        if (isSelectable) {
            // Animated golden glow ring around token
            Box(
                modifier = Modifier
                    .size(size * 1.3f)
                    .border(2.dp, RsGold, CircleShape)
            )
        }

        PlayerAvatar(
            color = token.color,
            avatarUri = avatarUri,
            isAi = isAi,
            size = size,
            isActive = isSelectable,
            borderWidth = if (isSelectable) 2.5.dp else 1.5.dp
        )
    }
}
