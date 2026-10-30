package com.example.chessoverlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

fun Service.startAsForeground() {
    val nm = getSystemService(NotificationManager::class.java)
    nm.createNotificationChannel(
        NotificationChannel("overlay", "Оверлей", NotificationManager.IMPORTANCE_LOW)
    )
    val n = NotificationCompat.Builder(this, "overlay")
        .setContentTitle("Chess Overlay работает")
        .setSmallIcon(android.R.drawable.ic_menu_compass)
        .build()
    val type = if (Build.VERSION.SDK_INT >= 34)
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
    ServiceCompat.startForeground(this, 1, n, type)
}
