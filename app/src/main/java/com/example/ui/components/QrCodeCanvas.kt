package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
/ Draws a vector QR code matrix on Canvas using a deterministic hash pattern of [content]
/ with authentic corner finder patterns (top-left, top-right, bottom-left).
*/
@Composable
fun QrCodeCanvas(
    content: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    qrColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = Color.White
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            val gridSize = 21 // 21x21 QR Version 1 Matrix
            val moduleWidth = this.size.width / gridSize
            val moduleHeight = this.size.height / gridSize

            // 1. Draw Background
            drawRect(color = backgroundColor)

            // Hash content for deterministic matrix pattern
            val hash = abs(content.hashCode())

            // 2. Build 21x21 matrix
            for (row in 0 until gridSize) {
                for (col in 0 until gridSize) {
                    val isCornerFinder = isFinderPattern(row, col, gridSize)
                    val isTimingPattern = isTiming(row, col)

                    val isBlack = when {
                        isCornerFinder -> drawFinderPixel(row, col, gridSize)
                        isTimingPattern -> (row + col) % 2 == 0
                        else -> {
                            // Seed pseudo-random matrix based on hash and coordinates
                            val seed = (row * 31 + col * 17 + hash)
                            (seed % 3 == 0 || seed % 7 == 0 || (row + col) % 5 == 0)
                        }
                    }

                    if (isBlack) {
                        drawRoundRect(
                            color = qrColor,
                            topLeft = Offset(col * moduleWidth, row * moduleHeight),
                            size = Size(moduleWidth * 0.92f, moduleHeight * 0.92f),
                            cornerRadius = CornerRadius(moduleWidth * 0.2f, moduleHeight * 0.2f)
                        )
                    }
                }
            }
        }
    }
}

private fun isFinderPattern(row: Int, col: Int, gridSize: Int): Boolean {
    // Top-left (0..6, 0..6)
    if (row < 7 && col < 7) return true
    // Top-right (0..6, gridSize-7..gridSize-1)
    if (row < 7 && col >= gridSize - 7) return true
    // Bottom-left (gridSize-7..gridSize-1, 0..6)
    if (row >= gridSize - 7 && col < 7) return true
    return false
}

private fun drawFinderPixel(row: Int, col: Int, gridSize: Int): Boolean {
    val r = when {
        row < 7 && col < 7 -> Pair(row, col)
        row < 7 && col >= gridSize - 7 -> Pair(row, col - (gridSize - 7))
        else -> Pair(row - (gridSize - 7), col)
    }
    val localRow = r.first
    val localCol = r.second

    // 7x7 outer square
    if (localRow == 0 || localRow == 6 || localCol == 0 || localCol == 6) return true
    // 5x5 inner white gap
    if (localRow == 1 || localRow == 5 || localCol == 1 || localCol == 5) return false
    // 3x3 inner black square
    return true
}

private fun isTiming(row: Int, col: Int): Boolean {
    return (row == 6 && col in 7..13) || (col == 6 && row in 7..13)
}
