package com.example.chessoverlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.max
import kotlin.math.min

class ArrowView(context: Context) : View(context) {
    var boardX = 80f
    var boardY = 500f
    var boardSize = 920f
    var flipped = false
    var calibrating = false
        set(v) { field = v; invalidate() }
    var move: String? = null
        set(v) { field = v; invalidate() }

    private fun paint(argb: Int, st: Paint.Style, w: Float = 2f) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = argb; style = st; strokeWidth = w }
    val arrowP = paint(Color.argb(200, 255, 140, 0), Paint.Style.FILL_AND_STROKE)
    val squareP = paint(Color.argb(90, 255, 140, 0), Paint.Style.FILL)
    val frameP = paint(Color.argb(220, 0, 200, 255), Paint.Style.STROKE, 4f)
    val gridP = paint(Color.argb(110, 0, 200, 255), Paint.Style.STROKE)

    private val scaler = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: ScaleGestureDetector): Boolean {
                val cx = boardX + boardSize / 2
                val cy = boardY + boardSize / 2
                boardSize = (boardSize * d.scaleFactor)
                    .coerceIn(200f, max(width, height).toFloat())
                boardX = cx - boardSize / 2
                boardY = cy - boardSize / 2
                invalidate()
                return true
            }
        })
    private var lx = 0f
    private var ly = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (!calibrating) return false
        scaler.onTouchEvent(e)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> { lx = e.x; ly = e.y }
            MotionEvent.ACTION_MOVE -> if (e.pointerCount == 1 && !scaler.isInProgress) {
                boardX += e.x - lx; boardY += e.y - ly
                lx = e.x; ly = e.y
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                val i = if (e.actionIndex == 0) min(1, e.pointerCount - 1) else 0
                lx = e.getX(i); ly = e.getY(i)
            }
        }
        return true
    }

    fun center(file: Int, rank: Int): PointF {
        val cell = boardSize / 8
        val col = if (flipped) 7 - file else file
        val row = if (flipped) rank else 7 - rank
        return PointF(boardX + (col + 0.5f) * cell, boardY + (row + 0.5f) * cell)
    }

    override fun onDraw(c: Canvas) {
        if (calibrating) drawGrid(c)
        drawMove(c)
    }
}
