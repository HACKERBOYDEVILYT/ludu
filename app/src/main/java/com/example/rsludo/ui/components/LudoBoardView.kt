package com.example.rsludo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    onPlayerBaseClick: (Player) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cellSize = boardSize / 15f
    val tokenSize = (cellSize.value * 0.90f).dp

    // Active player breathing pulse for their home base
    val infiniteTransition = rememberInfiniteTransition(label = "board_active_base")
    val activeBaseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "active_glow"
    )

    // Outer 3D Mahogany Frame with Gold Filigree Bezel
    Box(
        modifier = modifier
            .size(boardSize)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF3E1D0E), // Rich polished dark mahogany
                        Color(0xFF240E04),
                        Color(0xFF4A2412)
                    )
                )
            )
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    listOf(RsGoldLight, Color(0xFFD4AF37), Color(0xFF7A5C1E), RsGoldLight)
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(4.dp)
    ) {
        // Inner Game Field (Canvas Rendering)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFAF7FC))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellPx = size.width / 15f

                // Track Floor base
                drawRect(color = Color(0xFFF7F5FA))

                // 4 Home Bases (Velvet Color fills)
                drawRect(
                    color = LudoRed,
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
                )
                drawRect(
                    color = LudoGreen,
                    topLeft = Offset(cellPx * 9, 0f),
                    size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
                )
                drawRect(
                    color = LudoBlue,
                    topLeft = Offset(0f, cellPx * 9),
                    size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
                )
                drawRect(
                    color = LudoYellow,
                    topLeft = Offset(cellPx * 9, cellPx * 9),
                    size = androidx.compose.ui.geometry.Size(cellPx * 6, cellPx * 6)
                )

                // Inner Ivory Pillows inside bases
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cellPx * 0.75f, cellPx * 0.75f),
                    size = androidx.compose.ui.geometry.Size(cellPx * 4.5f, cellPx * 4.5f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f)
                )
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cellPx * 9.75f, cellPx * 0.75f),
                    size = androidx.compose.ui.geometry.Size(cellPx * 4.5f, cellPx * 4.5f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f)
                )
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cellPx * 0.75f, cellPx * 9.75f),
                    size = androidx.compose.ui.geometry.Size(cellPx * 4.5f, cellPx * 4.5f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f)
                )
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cellPx * 9.75f, cellPx * 9.75f),
                    size = androidx.compose.ui.geometry.Size(cellPx * 4.5f, cellPx * 4.5f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f)
                )

                // 4 Home Stretch Corridors
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

                // 4 Starting Blocks
                drawRect(color = LudoRed, topLeft = Offset(1 * cellPx, 6 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))
                drawRect(color = LudoGreen, topLeft = Offset(8 * cellPx, 1 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))
                drawRect(color = LudoYellow, topLeft = Offset(13 * cellPx, 8 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))
                drawRect(color = LudoBlue, topLeft = Offset(6 * cellPx, 13 * cellPx), size = androidx.compose.ui.geometry.Size(cellPx, cellPx))

                // Center Home Triangles (rows 6..8, cols 6..8)
                val cLeft = 6 * cellPx
                val cTop = 6 * cellPx
                val cRight = 9 * cellPx
                val cBottom = 9 * cellPx
                val midX = 7.5f * cellPx
                val midY = 7.5f * cellPx

                // Red triangle (Left)
                drawPath(Path().apply {
                    moveTo(cLeft, cTop)
                    lineTo(midX, midY)
                    lineTo(cLeft, cBottom)
                    close()
                }, LudoRed)

                // Green triangle (Top)
                drawPath(Path().apply {
                    moveTo(cLeft, cTop)
                    lineTo(cRight, cTop)
                    lineTo(midX, midY)
                    close()
                }, LudoGreen)

                // Yellow triangle (Right)
                drawPath(Path().apply {
                    moveTo(cRight, cTop)
                    lineTo(cRight, cBottom)
                    lineTo(midX, midY)
                    close()
                }, LudoYellow)

                // Blue triangle (Bottom)
                drawPath(Path().apply {
                    moveTo(cLeft, cBottom)
                    lineTo(midX, midY)
                    lineTo(cRight, cBottom)
                    close()
                }, LudoBlue)

                // Track Cell borders (Beveled Wood/Brass Gridlines)
                val gridColor = Color(0xFFC7BED6)
                for (i in 0..15) {
                    drawLine(gridColor, Offset(6 * cellPx, i * cellPx), Offset(9 * cellPx, i * cellPx), 1.5f)
                    drawLine(gridColor, Offset(0f, i * cellPx), Offset(6 * cellPx, i * cellPx), 1.5f)
                    drawLine(gridColor, Offset(9 * cellPx, i * cellPx), Offset(15 * cellPx, i * cellPx), 1.5f)

                    drawLine(gridColor, Offset(i * cellPx, 6 * cellPx), Offset(i * cellPx, 9 * cellPx), 1.5f)
                    drawLine(gridColor, Offset(i * cellPx, 0f), Offset(i * cellPx, 6 * cellPx), 1.5f)
                    drawLine(gridColor, Offset(i * cellPx, 9 * cellPx), Offset(i * cellPx, 15 * cellPx), 1.5f)
                }
            }

            // 3D Star Medallions on 8 Safe Cells
            SafeStarsOverlay(cellSize = cellSize)

            // Base Token Slots & Player Base Avatars
            HomeBaseAvatarsAndSlots(
                gameState = gameState,
                cellSize = cellSize,
                activeGlow = activeBaseGlow,
                onPlayerBaseClick = onPlayerBaseClick
            )

            // Center Royal 3D Crown Victory Pedestal
            CenterRoyalPedestal(cellSize = cellSize)

            // Active Interactive 3D Tokens Layer
            TokensOverlay(
                gameState = gameState,
                cellSize = cellSize,
                tokenSize = tokenSize,
                onTokenClick = onTokenClick
            )
        }
    }
}

@Composable
private fun SafeStarsOverlay(cellSize: Dp) {
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
            // 3D Brass Medallion behind star
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(cellSize * 0.76f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFF176), Color(0xFFFFB300), Color(0xFFC78100))
                        )
                    )
                    .border(1.dp, Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Safe Star",
                    tint = Color(0xFF5D4037),
                    modifier = Modifier.size(cellSize * 0.52f)
                )
            }
        }
    }
}

@Composable
private fun CenterRoyalPedestal(cellSize: Dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(x = cellSize * 6.6f, y = cellSize * 6.6f)
            .size(cellSize * 1.8f)
            .shadow(10.dp, CircleShape, spotColor = RsGold)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        RsGoldLight,
                        RsGold,
                        Color(0xFF996515),
                        Color(0xFF4A3205)
                    )
                )
            )
            .border(2.5.dp, Color.White, CircleShape)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "👑", fontSize = (cellSize.value * 0.55f).sp)
            Text(
                text = "RS",
                fontSize = (cellSize.value * 0.38f).sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF251201)
            )
        }
    }
}

@Composable
private fun HomeBaseAvatarsAndSlots(
    gameState: GameState,
    cellSize: Dp,
    activeGlow: Float,
    onPlayerBaseClick: (Player) -> Unit = {}
) {
    val playersByColor = gameState.players.associateBy { it.color }

    val baseConfigs = listOf(
        Triple(PlayerColor.RED, 0.75f, 0.75f),
        Triple(PlayerColor.GREEN, 9.75f, 0.75f),
        Triple(PlayerColor.BLUE, 0.75f, 9.75f),
        Triple(PlayerColor.YELLOW, 9.75f, 9.75f)
    )

    for ((color, leftCol, topRow) in baseConfigs) {
        val player = playersByColor[color]
        val isActive = gameState.currentPlayer?.color == color

        Box(
            modifier = Modifier
                .offset(x = cellSize * leftCol, y = cellSize * topRow)
                .size(cellSize * 4.5f)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isActive) (2.5.dp * activeGlow) else 1.5.dp,
                    color = if (isActive) RsGold else color.light,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    if (player != null) onPlayerBaseClick(player)
                }
                .padding(4.dp)
        ) {
            // Player label and small photo avatar in center of base
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
                        size = cellSize * 1.35f,
                        isActive = isActive
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

            // 4 circular token slots with brass rings
            val slotPositions = listOf(
                Pair(cellSize * 0.25f, cellSize * 0.25f),
                Pair(cellSize * 2.35f, cellSize * 0.25f),
                Pair(cellSize * 0.25f, cellSize * 2.35f),
                Pair(cellSize * 2.35f, cellSize * 2.35f)
            )

            for ((sx, sy) in slotPositions) {
                Box(
                    modifier = Modifier
                        .offset(x = sx, y = sy)
                        .size(cellSize * 1.5f)
                        .clip(CircleShape)
                        .background(color.light.copy(alpha = 0.25f))
                        .border(
                            1.5.dp,
                            Brush.linearGradient(listOf(RsGoldLight, color.primary)),
                            CircleShape
                        )
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
    val allTokens = gameState.players.flatMap { it.tokens }

    for (token in allTokens) {
        val player = playersByColor[token.color] ?: continue
        val isSelectable = gameState.legalTokens.contains(Pair(token.playerId, token.id))
        val tokenWithSelectable = token.copy(isSelectable = isSelectable)

        val (row, col) = LudoBoardCoordinates.getCoordinatesForStep(token.color, token.step, token.id)

        // Micro-offsets for tokens sharing the exact same cell on the track
        val (stackOffsetX, stackOffsetY) = if (token.step != -1) {
            val cellTokens = allTokens.filter {
                it.step != -1 && LudoBoardCoordinates.getCoordinatesForStep(it.color, it.step, it.id) == Pair(row, col)
            }
            val indexInCell = cellTokens.indexOf(token)
            if (cellTokens.size > 1) {
                when (indexInCell % 4) {
                    0 -> Pair((-cellSize * 0.14f), (-cellSize * 0.14f))
                    1 -> Pair((cellSize * 0.14f), (cellSize * 0.14f))
                    2 -> Pair((cellSize * 0.14f), (-cellSize * 0.14f))
                    else -> Pair((-cellSize * 0.14f), (cellSize * 0.14f))
                }
            } else Pair(0.dp, 0.dp)
        } else Pair(0.dp, 0.dp)

        val targetX = cellSize * col + (cellSize - tokenSize) / 2 + stackOffsetX
        val targetY = cellSize * row + (cellSize - tokenSize) / 2 + stackOffsetY

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
