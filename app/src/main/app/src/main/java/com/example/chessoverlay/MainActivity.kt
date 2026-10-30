package com.example.chessoverlay

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val info = TextView(this).apply {
            text = "Оверлей для шахмат.\n\n" +
                "1. Разрешите «Поверх других приложений».\n" +
                "2. Нажмите «Запустить».\n" +
                "3. Откройте шахматное приложение.\n" +
                "4. Кнопка ⌖ — подгоните рамку под доску " +
                "(палец двигает, щипок меняет размер).\n" +
                "5. Введите ход, например e2e4, и нажмите ▶."
            textSize = 16f
        }
        val start = Button(this).apply {
            text = "Запустить оверлей"
            setOnClickListener { startOverlay() }
        }
        val stop = Button(this).apply {
            text = "Остановить"
            setOnClickListener { stopService(Intent(this@MainActivity, OverlayService::class.java)) }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 96, 48, 48)
            addView(info)
            addView(start)
            addView(stop)
        })

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    private fun startOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            )
            return
        }
        startForegroundService(Intent(this, OverlayService::class.java))
    }
}
