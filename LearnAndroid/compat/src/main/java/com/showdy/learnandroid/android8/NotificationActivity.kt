package com.showdy.learnandroid.android8

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.showdy.learnandroid.R
import kotlinx.android.synthetic.main.activity_notification.*

/**
 * Created by <b>Showdy</b> on 2020/10/30 23:17
 *
 */
class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)


        notification.setOnClickListener {
            val intent = Intent(this, NotificationService::class.java)
            val pendingIntent = PendingIntent.getService(
                this, 0x100, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            sendNotification(
                pendingIntent = pendingIntent,
                iconResId = R.mipmap.ic_launcher,
                contentText = "It's time to record your health status",
                contentTitle = "Time to Clock",
                cancelable = true
            )
            sound()
            vibrate()
        }
    }
}