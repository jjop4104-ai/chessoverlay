package com.example.chessoverlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams as LP
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var arrow: ArrowView
    private lateinit var panel: View
    private lateinit var arrowLp: LP
    private lateinit var panelLp: LP
    override fun onBind(i: Intent?): IBinder? = null
    private fun lp(w: Int, h: Int, flags: Int) =
        LP(w, h, LP.TYPE_APPLICATION_OVERLAY, flags, PixelFormat.TRANSLUCENT)
    override fun onCreate() {
        super.onCreate()
        startAsForeground()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        arrow = ArrowView(this)
        arrowLp = lp(
            LP.MATCH_PARENT, LP.MATCH_PARENT,
            LP.FLAG_NOT_FOCUSABLE or LP.FLAG_NOT_TOUCHABLE or
                LP.FLAG_LAYOUT_IN_SCREEN or LP.FLAG_LAYOUT_NO_LIMITS
        )
        wm.addView(arrow, arrowLp)
        panel = OverlayPanel(
            this,
            onDrag = { dx, dy ->
                panelLp.x += dx; panelLp.y += dy
                wm.updateViewLayout(panel, panelLp)
            },
            onShow = { text ->
                if (!arrow.showMove(text)) {
                    Toast.makeText(this, "Формат: e2e4", Toast.LENGTH_SHORT).show()
                }
            },
            onClear = { arrow.move = null },
            onCalib = { toggleCalib() },
            onFlip = { arrow.flipped = !arrow.flipped; arrow.invalidate() },
            onQuit = { stopSelf() },
            onNeedFocus = { setFocusable(true) },
            onReleaseFocus = { v ->
                setFocusable(false)
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(v.windowToken, 0)
            }
        ).build()
        panelLp = lp(LP.WRAP_CONTENT, LP.WRAP_CONTENT, LP.FLAG_NOT_FOCUSABLE).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 20; y = 150
        }
        wm.addView(panel, panelLp)
    }
    private fun setFocusable(on: Boolean) {
        panelLp.flags = if (on) panelLp.flags and LP.FLAG_NOT_FOCUSABLE.inv()
        else panelLp.flags or LP.FLAG_NOT_FOCUSABLE
        wm.updateViewLayout(panel, panelLp)
    }
    private fun toggleCalib() {
        arrow.calibrating = !arrow.calibrating
        arrowLp.flags = if (arrow.calibrating) arrowLp.flags and LP.FLAG_NOT_TOUCHABLE.inv()
        else arrowLp.flags or LP.FLAG_NOT_TOUCHABLE
        wm.updateViewLayout(arrow, arrowLp)
    }
    override fun onDestroy() {
        runCatching { wm.removeView(arrow) }
        runCatching { wm.removeView(panel) }
        super.onDestroy()
    }
}
