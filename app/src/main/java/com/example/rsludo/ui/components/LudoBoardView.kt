package com.example.rsludo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.game.LudoBoardCoordinates
import com.example.rsludo.model.GameState
import com.example.rsludo.model.Player
import com.example.rsludo.model.PlayerColor
import com.example.rsludo.model.Token
import com.example.ui.theme.*

@Composable
fun LudoBoardView(
    gameState: GameState,
    boardSize: Dp = 360.dp,
    onTokenClick: (Token) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cellSize = boardSize / 15f
    val tokenSize = (cellSize.value * 0.88f).dp

    Box(
        modifier = modifier
            .size(boardSize)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF9F7FB))
            .border(3.5.dp, Brush.linearGradient(listOf(RsGoldDark, RsGold, RsGoldDark)), RoundedCornerShape(18.dp))
    ) {
        // 1. Draw Board Grid, Colors, Safe Stars, and Center Triangles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellPx = size.width / 15f

            // Background of whole board
            drawRect(color = Color(0xFFFBF9FD))

            // Bases (6x6 each)
            drawRect(
                color = LudoRed.copy(alpha = 0.95f),
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
            )
            drawRect(
                color = LudoGreen.copy(alpha = 0.95f),
                topLeft = Offset(cellPx * 9, 0f),
                size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
            )
            drawRect(
                color = LudoBlue.copy(alpha = 0.95f),
                topLeft = Offset(0f, cellPx * 9),
                size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
            )
            drawRect(
                color = LudoYellow.copy(alpha = 0.95f),
                topLeft = Offset(cellPx * 9, cellPx * 9),
                size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
            )

            // Inner white boxes inside bases
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cellPx * 0.8f, cellPx * 0.8f),
                size = androidx.compose.ui.geometry.Size(cellPx * 4.4f, cellPx * 4.4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cellPx * 9.8f, cellPx * 0.8f),
                size = androidx.compose.ui.geometry.Size(cellPx * 4.4f, cellPx * 4.4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cellPx * 0.8f, cellPx * 9.8f),
                size = androidx.compose.ui.geometry.Size(cellPx * 4.4f, cellPx * 4.4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cellPx * 9.8f, cellPx * 9.8f),
                size = androidx.compose.ui.geometry.Size(cellPx * 4.4f, cellPx * 4.4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // Home corridors (5 cells each)
            // Red corridor (row 7, cols 1..5)
            for (c in 1..5) {
                drawRect(
                    color = LudoRed,
                    topLeft = Offset(c * cellPx, 7 * cellPx),
                    size = androidx.compose.ui.geometry.Size(cellPx, cellPx)
                )
            }
            // Green corridor (col 7, rows 1..5)
            for (r in 1..5) {
                drawRect(
                    color = LudoGreen,
                    topLeft = Offset(7 * cellPx, r * cellPx),
                    size = androidx.compose.ui.geometry.Size(cellPx, cellPx)
                )
            }
            // Yellow corridor (row 7, cols 9..13)
            for (c in 9..13) {
                drawRect(
                    color = LudoYellow,
                    topLeft = Offset(c * cellPx, 7 * cellPx),
                    size = androidx.compose.ui.geometry.Size(cellPx, cellPx)
                )
            }
            // Blue corridor (col 7, rows 9..13)
            for (r in 9..13) {
                drawRect(
                    color = LudoBlue,
                    topLeft = Offset(7 * cellPx, r * cellPx),
                    size = androidx.compose.ui.geometry.Size(cellPx, cellPx)
                )
            }

            // Start cells
            // Red start: row 6, col 1
            drawRect(color = LudoRed, topLeft = Offset(1 * cellPx, 6 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))
            // Green start: row 1, col 8
            drawRect(color = LudoGreen, topLeft = Offset(8 * cellPx, 1 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))
            // Yellow start: row 8, col 13
            drawRect(color = LudoYellow, topLeft = Offset(13 * cellPx, 8 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))
            // Blue start: row 13, col 6
            drawRect(color = LudoBlue, topLeft = Offset(6 * cellPx, 13 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))

            // Center Home Triangle (rows 6..8, cols 6..8)
            val centerLeft = 6 * cellPx
            val centerTop = 6 * cellPx
            val centerRight = 9 * cellPx
            val centerBottom = 9 * cellPx
            val midX = 7.5f * cellPx
            val midY = 7.5f * cellPx

            // Red triangle (Left)
            val redPath = Path().apply {
                moveTo(centerLeft, centerTop)
                lineTo(midX, midY)
                lineTo(centerLeft, centerBottom)
                close()
            }
            drawPath(redPath, LudoRed)

            // Green triangle (Top)
            val greenPath = Path().apply {
                moveTo(centerLeft, centerTop)
                lineTo(centerRight, centerTop)
                lineTo(midX, midY)
                close()
            }
            drawPath(greenPath, LudoGreen)

            // Yellow triangle (Right)
            val yellowPath = Path().apply {
                moveTo(centerRight, centerTop)
                lineTo(centerRight, centerBottom)
                lineTo(midX, midY)
                close()
            }
            drawPath(yellowPath, LudoYellow)

            // Blue triangle (Bottom)
            val bluePath = Path().apply {
                moveTo(centerLeft, centerBottom)
                lineTo(midX, midY)
                lineTo(centerRight, centerBottom)
                close()
            }
            drawPath(bluePath, LudoBlue)

            // Track Cell grid borders (horizontal and vertical arms)
            val gridColor = Color(0xFFD6CFE0)
            for (i in 0..15) {
                // Horizontal lines across arm sections
                drawLine(gridColor, Offset(6 * cellPx, i * cellPx), Offset(9 * cellPx, i * cellPx), 1.2f)
                drawLine(gridColor, Offset(0f, i * cellPx), Offset(6 * cellPx, i * cellPx), 1.2f)
                drawLine(gridColor, Offset(9 * cellPx, i * cellPx), Offset(15 * cellPx, i * cellPx), 1.2f)

                // Vertical lines across arm sections
                drawLine(gridColor, Offset(i * cellPx, 6 * cellPx), Offset(i * cellPx, 9 * cellPx), 1.2f)
                drawLine(gridColor, Offset(i * cellPx, 0f), Offset(i * cellPx, 6 * cellPx), 1.2f)
                drawLine(gridColor, Offset(i * cellPx, 9 * cellPx), Offset(i * cellPx, 15 * cellPx), 1.2f)
            }
        }

        // 2. Safe Stars decoration overlay
        SafeStarsOverlay(cellSize = cellSize)

        // 3. Base circular token spots & player base avatars
        HomeBaseAvatarsAndSlots(gameState = gameState, cellSize = cellSize)

        // 4. Center RS emblem inside the home triangle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(x = cellSize * 6.75f, y = cellSize * 6.75f)
                .size(cellSize * 1.5f)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(RsGoldLight, RsGoldDark)))
                .border(1.5.dp, Color.White, CircleShape)
        ) {
            Text(
                text = "RS",
                fontSize = (cellSize.value * 0.52f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E0E32)
            )
        }

        // 5. Render active tokens on the board
        TokensOverlay(
            gameState = gameState,
            cellSize = cellSize,
            tokenSize = tokenSize,
            onTokenClick = onTokenClick
        )
    }
}

@Composable
private fun SafeStarsOverlay(cellSize: Dp) {
    // 8 Safe Cells
    val safePositions = listOf(
        Pair(6, 1),  // Red Start
        Pair(2, 6),  // Safe Star 1
        Pair(1, 8),  // Green Start
        Pair(6, 12), // Safe Star 2
        Pair(8, 13), // Yellow Start
        Pair(12, 8), // Safe Star 3
        Pair(13, 6), // Blue Start
        Pair(8, 2)   // Safe Star 4
    )

    for (pos in safePositions) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(x = cellSize * pos.second, y = cellSize * pos.first)
                .size(cellSize)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Safe Cell",
                tint = StarGold,
                modifier = Modifier.size(cellSize * 0.65f)
            )
        }
    }
}

@Composable
private fun HomeBaseAvatarsAndSlots(gameState: GameState, cellSize: Dp) {
    val playersByColor = gameState.players.associateBy { it.color }

    val baseConfigs = listOf(
        Triple(PlayerColor.RED, 0.8f, 0.8f),
        Triple(PlayerColor.GREEN, 9.8f, 0.8f),
        Triple(PlayerColor.BLUE, 0.8f, 9.8f),
        Triple(PlayerColor.YELLOW, 9.8f, 9.8f)
    )

    for ((color, leftCol, topRow) in baseConfigs) {
        val player = playersByColor[color]

        Box(
            modifier = Modifier
                .offset(x = cellSize * leftCol, y = cellSize * topRow)
                .size(cellSize * 4.4f)
                .padding(4.dp)
        ) {
            // Player label and small avatar in center of base
            if (player != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    PlayerAvatar(
                        color = color,
                        avatarUri = player.avatarUri,
                        isAi = player.isAi,
                        size = cellSize * 1.3f,
                        isActive = gameState.currentPlayer?.color == color
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = player.name.take(9),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = color.dark
                    )
                }
            }

            // 4 circular token slots
            val slotPositions = listOf(
                Pair(cellSize * 0.2f, cellSize * 0.2f),
                Pair(cellSize * 2.3f, cellSize * 0.2f),
                Pair(cellSize * 0.2f, cellSize * 2.3f),
                Pair(cellSize * 2.3f, cellSize * 2.3f)
            )

            for ((sx, sy) in slotPositions) {
                Box(
                    modifier = Modifier
                        .offset(x = sx, y = sy)
                        .size(cellSize * 1.5f)
                        .clip(CircleShape)
                        .background(color.light.copy(alpha = 0.25f))
                        .border(1.2.dp, color.primary, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun TokensOverlay(
    gameState: GameState,
    cellSize: Dp,
    tokenSize: Dp,
    onTokenClick: (Token) -> Unit
) {
    val playersByColor = gameState.players.associateBy { it.color }

    // Group tokens by their board coordinate to prevent overlapping
    val allTokens = gameState.players.flatMap { it.tokens }

    for (token in allTokens) {
        val player = playersByColor[token.color] ?: continue
        val isSelectable = gameState.legalTokens.contains(Pair(token.playerId, token.id))
        val tokenWithSelectable = token.copy(isSelectable = isSelectable)

        val (row, col) = LudoBoardCoordinates.getCoordinatesForStep(token.color, token.step, token.id)

        // Calculate smooth position
        val targetX = cellSize * col + (cellSize - tokenSize) / 2
        val targetY = cellSize * row + (cellSize - tokenSize) / 2

        val animX by animateDpAsState(
            targetValue = targetX,
            animationSpec = tween(140, easing = LinearOutSlowInEasing),
            label = "tokenX_${token.playerId}_${token.id}"
        )
        val animY by animateDpAsState(
            targetValue = targetY,
            animationSpec = tween(140, easing = LinearOutSlowInEasing),
            label = "tokenY_${token.playerId}_${token.id}"
        )

        TokenView(
            token = tokenWithSelectable,
            avatarUri = player.avatarUri,
            isAi = player.isAi,
            size = tokenSize,
            onClick = { onTokenClick(tokenWithSelectable) },
            modifier = Modifier.offset(x = animX, y = animY)
        )
    }
}
