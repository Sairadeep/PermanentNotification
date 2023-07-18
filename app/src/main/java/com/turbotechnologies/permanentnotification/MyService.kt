package com.turbotechnologies.permanentnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyService : Service() {

    private val notificationChannelID = "Permanent Notification"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            // Create a notification channel
            val channel = NotificationChannel(
                notificationChannelID,
                "notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            Toast.makeText(applicationContext, "Service Started", Toast.LENGTH_SHORT).show()

            val notificationManager: NotificationManager by lazy {
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            notificationManager.createNotificationChannel(channel)

            val notificationBuilder =
                NotificationCompat.Builder(this, notificationChannelID)
                    .setContentTitle("App is running")
                    .setContentText("Display notification even if app is closed")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(NotificationCompat.PRIORITY_MAX)

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
            notificationBuilder.setContentIntent(pendingIntent)


            val notification = notificationBuilder.build()
           startForeground(1, notification)
        } else {
            Toast.makeText(applicationContext, "Notification not available", Toast.LENGTH_SHORT)
                .show()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

//    override fun onTaskRemoved(rootIntent: Intent?) {
//        super.onTaskRemoved(rootIntent)
//        // Restart the service
//        val intent = Intent(this, MyService::class.java)
//        startService(intent)
//    }
}