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
import com.google.firebase.database.*

class MyService : Service() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference.child("Dara")

    private var newsTitle = ""
    private var newsContext = ""


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun dataFetch(news: (todayTitle: String, todayNewsContext: String) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val title = snapshot.child("tilte").value.toString()
                val context = snapshot.child("Context").value.toString()

                newsTitle = title
                newsContext = context

                news(newsTitle, newsContext)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun notification() {
        dataFetch { todayTitle, todayNewsContext ->
            val notificationChannelID = "Permanent Notification"
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
                        .setContentTitle("Weather")
                        .setContentText("Title: $todayTitle")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_MAX).setStyle(
                            NotificationCompat.BigTextStyle().bigText(
                                "$todayTitle \n" +
                                        todayNewsContext
                            )
                        )


                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent =
                    PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                notificationBuilder.setContentIntent(pendingIntent)


                val notification = notificationBuilder.build()
                startForeground(1, notification)
            } else {
                Toast.makeText(applicationContext, "Notification not available", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }
}