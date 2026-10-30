package com.example.chessoverlay

import android.graphics.Canvas
import android.graphics.Path
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun ArrowView.showMove(text: String): Boolean {
    val m = text.lowercase().filter { it.isLetterOrDigit() }
    if (!Regex("^[a-h][1-8][a-h][1-8]").containsMatchIn(m)) return false
    move = m.take(4)
    return true
}

fun ArrowView.drawGrid(c: Canvas) {
    val cell = boardSize / 8
    c.drawRect(boardX, boardY, boardX + boardSize, boardY + boardSize, frameP)
    for (i in 1..7) {
        c.drawLine(boardX + i * cell, boardY, boardX + i * cell, boardY + boardSize, gridP)
        c.drawLine(boardX, boardY + i * cell, boardX + boardSize, boardY + i * cell, gridP)
    }
}

fun ArrowView.drawMove(c: Canvas) {
    val m = move ?: return
    val cell = boardSize / 8
    val a = center(m[0] - 'a', m[1] - '1')
    val b = center(m[2] - 'a', m[3] - '1')
    for (p in listOf(a, b)) {
        c.drawRect(p.x - cell / 2, p.y - cell / 2, p.x + cell / 2, p.y + cell / 2, squareP)
    }
    val ang = atan2(b.y - a.y, b.x - a.x)
    val head = cell * 0.45f
    arrowP.strokeWidth = cell * 0.18f
    c.drawLine(a.x, a.y, b.x - cos(ang) * head * 0.6f, b.y - sin(ang) * head * 0.6f, arrowP)
    val tip = Path().apply {
        moveTo(b.x, b.y)
        lineTo(b.x - head * cos(ang - 0.45f), b.y - head * sin(ang - 0.45f))
        lineTo(b.x - head * cos(ang + 0.45f), b.y - head * sin(ang + 0.45f))
        close()
    }
    c.drawPath(tip, arrowP)
}
