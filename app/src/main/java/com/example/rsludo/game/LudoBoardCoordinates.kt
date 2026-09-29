package com.example.rsludo.game

import com.example.rsludo.model.PlayerColor

object LudoBoardCoordinates {

    // 52 Cells of the outer track clockwise: (row, col)
    val trackCells: List<Pair<Int, Int>> = listOf(
        Pair(6, 1),   // 0: Red Start (Safe)
        Pair(6, 2),   // 1
        Pair(6, 3),   // 2
        Pair(6, 4),   // 3
        Pair(6, 5),   // 4
        Pair(5, 6),   // 5
        Pair(4, 6),   // 6
        Pair(3, 6),   // 7
        Pair(2, 6),   // 8: Safe Star
        Pair(1, 6),   // 9
        Pair(0, 6),   // 10
        Pair(0, 7),   // 11
        Pair(0, 8),   // 12
        Pair(1, 8),   // 13: Green Start (Safe)
        Pair(2, 8),   // 14
        Pair(3, 8),   // 15
        Pair(4, 8),   // 16
        Pair(5, 8),   // 17
        Pair(6, 9),   // 18
        Pair(6, 10),  // 19
        Pair(6, 11),  // 20
        Pair(6, 12),  // 21: Safe Star
        Pair(6, 13),  // 22
        Pair(6, 14),  // 23
        Pair(7, 14),  // 24
        Pair(8, 14),  // 25
        Pair(8, 13),  // 26: Yellow Start (Safe)
        Pair(8, 12),  // 27
        Pair(8, 11),  // 28
        Pair(8, 10),  // 29
        Pair(8, 9),   // 30
        Pair(9, 8),   // 31
        Pair(10, 8),  // 32
        Pair(11, 8),  // 33
        Pair(12, 8),  // 34: Safe Star
        Pair(13, 8),  // 35
        Pair(14, 8),  // 36
        Pair(14, 7),  // 37
        Pair(14, 6),  // 38
        Pair(13, 6),  // 39: Blue Start (Safe)
        Pair(12, 6),  // 40
        Pair(11, 6),  // 41
        Pair(10, 6),  // 42
        Pair(9, 6),   // 43
        Pair(8, 5),   // 44
        Pair(8, 4),   // 45
        Pair(8, 3),   // 46
        Pair(8, 2),   // 47: Safe Star
        Pair(8, 1),   // 48
        Pair(8, 0),   // 49
        Pair(7, 0),   // 50
        Pair(6, 0)    // 51
    )

    // The 8 safe cells on the track
    val safeCellIndices = setOf(0, 8, 13, 21, 26, 34, 39, 47)

    // Home Corridors for each color: 5 cells each
    val redHomeCorridor = listOf(Pair(7, 1), Pair(7, 2), Pair(7, 3), Pair(7, 4), Pair(7, 5))
    val greenHomeCorridor = listOf(Pair(1, 7), Pair(2, 7), Pair(3, 7), Pair(4, 7), Pair(5, 7))
    val yellowHomeCorridor = listOf(Pair(7, 13), Pair(7, 12), Pair(7, 11), Pair(7, 10), Pair(7, 9))
    val blueHomeCorridor = listOf(Pair(13, 7), Pair(12, 7), Pair(11, 7), Pair(10, 7), Pair(9, 7))

    // Center victory cell
    val centerCell = Pair(7, 7)

    fun getCoordinatesForStep(color: PlayerColor, step: Int, tokenId: Int): Pair<Float, Float> {
        return when {
            step == -1 -> getBaseCoordinates(color, tokenId)
            step in 0..50 -> {
                val trackIndex = (color.startTrackIndex + step) % 52
                val cell = trackCells[trackIndex]
                Pair(cell.first.toFloat(), cell.second.toFloat())
            }
            step in 51..55 -> {
                val corridorIndex = step - 51
                val cell = when (color) {
                    PlayerColor.RED -> redHomeCorridor[corridorIndex]
                    PlayerColor.GREEN -> greenHomeCorridor[corridorIndex]
                    PlayerColor.YELLOW -> yellowHomeCorridor[corridorIndex]
                    PlayerColor.BLUE -> blueHomeCorridor[corridorIndex]
                }
                Pair(cell.first.toFloat(), cell.second.toFloat())
            }
            else -> {
                // Step 56 = Center Home
                Pair(centerCell.first.toFloat(), centerCell.second.toFloat())
            }
        }
    }

    fun getBaseCoordinates(color: PlayerColor, tokenId: Int): Pair<Float, Float> {
        val (baseRow, baseCol) = when (color) {
            PlayerColor.RED -> Pair(1.2f, 1.2f)
            PlayerColor.GREEN -> Pair(1.2f, 10.2f)
            PlayerColor.YELLOW -> Pair(10.2f, 10.2f)
            PlayerColor.BLUE -> Pair(10.2f, 1.2f)
        }
        val (dRow, dCol) = when (tokenId) {
            0 -> Pair(0.6f, 0.6f)
            1 -> Pair(0.6f, 2.6f)
            2 -> Pair(2.6f, 0.6f)
            else -> Pair(2.6f, 2.6f)
        }
        return Pair(baseRow + dRow, baseCol + dCol)
    }

    fun getGlobalTrackIndex(color: PlayerColor, step: Int): Int? {
        return if (step in 0..50) {
            (color.startTrackIndex + step) % 52
        } else {
            null
        }
    }

    fun isSafeGlobalIndex(index: Int): Boolean = index in safeCellIndices
}
