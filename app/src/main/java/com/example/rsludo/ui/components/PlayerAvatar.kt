package com.example.rsludo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rsludo.model.PlayerColor
import com.example.ui.theme.RsGoldLight
import java.io.File

@Composable
fun PlayerAvatar(
    color: PlayerColor,
    avatarUri: String? = null,
    isAi: Boolean = false,
    size: Dp = 32.dp,
    isActive: Boolean = false,
    borderWidth: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_active")
    val pulseScale by if (isActive) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
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

    val glowColor = if (isActive) RsGoldLight else color.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size * pulseScale)
            .shadow(
                elevation = if (isActive) 8.dp else 3.dp,
                shape = CircleShape,
                spotColor = glowColor,
                ambientColor = glowColor
            )
            .border(
                width = borderWidth,
                brush = Brush.sweepGradient(
                    listOf(
                        color.light,
                        color.primary,
                        color.dark,
                        color.light
                    )
                ),
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(color.baseBg)
    ) {
        val hasLocalPhoto = avatarUri != null && File(avatarUri).exists()

        if (hasLocalPhoto) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(avatarUri!!))
                    .crossfade(true)
                    .build(),
                contentDescription = "Player photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            // Default stylized avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(color.light, color.dark)
                        )
                    )
            ) {
                Icon(
                    imageVector = if (isAi) Icons.Filled.SmartToy else Icons.Filled.Person,
                    contentDescription = if (isAi) "AI Player" else "Player Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
        }

        // Glossy highlight reflection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
