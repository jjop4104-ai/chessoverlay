package com.example.chessoverlay

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class OverlayPanel(
    private val ctx: Context,
    private val onDrag: (Int, Int) -> Unit,
    private val onShow: (String) -> Unit,
    private val onClear: () -> Unit,
    private val onCalib: () -> Unit,
    private val onFlip: () -> Unit,
    private val onQuit: () -> Unit,
    private val onNeedFocus: () -> Unit,
    private val onReleaseFocus: (View) -> Unit
) {
    private fun dp(v: Int) = (v * ctx.resources.displayMetrics.density).toInt()

    fun build(): LinearLayout {
        val root = LinearLayout(ctx).apply {
            setBackgroundColor(Color.argb(220, 30, 30, 30))
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        val handle = TextView(ctx).apply {
            text = "≡"; textSize = 22f; setTextColor(Color.WHITE)
            setPadding(dp(8), 0, dp(8), 0)
            var lx = 0f; var ly = 0f
            setOnTouchListener { _, e ->
                if (e.action == MotionEvent.ACTION_MOVE) {
                    onDrag((e.rawX - lx).toInt(), (e.rawY - ly).toInt())
                }
                lx = e.rawX; ly = e.rawY
                true
            }
        }
        val input = EditText(ctx).apply {
            hint = "e2e4"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY)
            textSize = 16f; setSingleLine(true); minWidth = dp(80)
            setOnTouchListener { v, e ->
                if (e.action == MotionEvent.ACTION_DOWN) onNeedFocus()
                v.performClick()
                false
            }
        }
        fun btn(label: String, action: () -> Unit) = Button(ctx).apply {
            text = label; minWidth = dp(44); minimumWidth = dp(44)
            setPadding(0, 0, 0, 0)
            setOnClickListener { action() }
        }
        listOf(
            handle, input,
            btn("▶") { onShow(input.text.toString()); onReleaseFocus(input) },
            btn("✕") { input.setText(""); onClear(); onReleaseFocus(input) },
            btn("⌖") { onCalib() },
            btn("⇅") { onFlip() },
            btn("⏻") { onQuit() }
        ).forEach { root.addView(it) }
        return root
    }
}
