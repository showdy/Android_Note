package com.showdy.learnandroid.android8

import android.app.*
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by <b>Showdy</b> on 2020/10/17 20:23
 *
 *  通知构建类
 */

const val CHANNEL_ID = "com.showdy.channel"
const val CHANNEL_NAME = "AndroidO Channel"
const val CHANNEL_DESCRIPTION = "This is a androidO channel"

val atomicNotifyId = AtomicInteger(0x001)

fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            setShowBadge(true)
            setBypassDnd(true)
            //闪光灯
            lightColor = Color.RED
            enableLights(true)
            //锁屏显示通知
            lockscreenVisibility = Notification.VISIBILITY_SECRET
//            enableVibration(true)
//            vibrationPattern = longArrayOf(0, 200, 300, 200, 300, 200, 300)
//            setSound(
//                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
//                Notification.AUDIO_ATTRIBUTES_DEFAULT
//            )
        }
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}


fun Context.createNotification(
    pendingIntent: PendingIntent?,
    iconResId: Int,
    contentText: String,
    contentTitle: String,
    cancelable: Boolean
): Notification {
    //创建channel
    createNotificationChannel()
    //创建通知
    return NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(iconResId)
        .setContentText(contentText)
        .setContentTitle(contentTitle)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
//        .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
//        .setSound(
//            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
//            AudioManager.STREAM_ALARM
//        )
//        .setVibrate(longArrayOf(0, 200, 300, 200, 300, 200, 300))
        .setAutoCancel(cancelable)
        .build()
}

/**
 * 发送普通通知
 */
fun Context.sendNotification(
    pendingIntent: PendingIntent?,
    iconResId: Int,
    contentText: String,
    contentTitle: String,
    cancelable: Boolean
): Int {

    val notification =
        createNotification(pendingIntent, iconResId, contentText, contentTitle, cancelable)
    val notificationId = atomicNotifyId.getAndIncrement()
    with(NotificationManagerCompat.from(this)) {
        notify(notificationId, notification)
    }
    return notificationId
}

/**
 * Android O 发送前台通知
 */
fun Service.sendForegroundNotification(
    pendingIntent: PendingIntent?,
    iconResId: Int,
    contentText: String,
    contentTitle: String
): Int {
    createNotificationChannel()
    val notificationId = atomicNotifyId.getAndIncrement()
    val notification =
        createNotification(pendingIntent, iconResId, contentText, contentTitle, false)
    startForeground(notificationId, notification)
    return notificationId
}

/**
 * 兼容android8.0以上，notification设置声音震动无效
 *
 * 为了兼容手表震动和声音设置次数不生效的问题，主动去注销声音和震动
 */
fun Context.sound() {
    val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    val rt = RingtoneManager.getRingtone(this, uri)
    rt.play()
    Handler(Looper.getMainLooper()).postDelayed({ rt.stop() }, 5000)
}

fun Context.vibrate() {
    val vibrator = this.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    val vibrationPattern = longArrayOf(100, 400, 100, 400, 100, 400)
    /**
     *  -1： 震动不重复,震动一次
     *  0 : 震动重复
     *  1： 震动从数组为1下标重复震动
     *  2： 震动从数组为2下标重复震动
     */
    vibrator.vibrate(vibrationPattern, 0)
    Handler(Looper.getMainLooper()).postDelayed({ vibrator.cancel() }, 5000)
}
