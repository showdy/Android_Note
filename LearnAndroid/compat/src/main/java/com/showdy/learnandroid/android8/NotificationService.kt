package com.showdy.learnandroid.android8

import android.content.Intent
import android.os.Build
import androidx.lifecycle.LifecycleService
import com.showdy.learnandroid.R
import com.showdy.learnandroid.providerContext

/**
 * Created by <b>Showdy</b> on 2020/10/31 0:29
 *
 * AndroidO 在 Android 8.0 之前，创建前台服务的方式通常是先创建一个后台服务，然后将该服务推到前台。
 * Android 8.0 有一项复杂功能；系统不允许后台应用创建后台服务。 因此，Android 8.0 引入了一种全新的方法，
 * 即 Context.startForegroundService()，以在前台启动新服务。
 * 在系统创建服务后，应用有五秒的时间来调用该服务的 startForeground() 方法以显示新服务的用户可见通知。
 * 如果应用在此时间限制内未调用 startForeground()，则系统将停止服务并声明此应用为 ANR。
 */
class NotificationService : LifecycleService() {


    companion object {

        private val intent: Intent by lazy {
            Intent(
                providerContext,
                NotificationService::class.java
            )
        }

        fun startService() {
            intent.let {
                /**
                 * android 8.0后不允许app在后台时启动后台服务，必须启动前台服务，否则部分手机会报错：
                 * java.lang.IllegalStateException: Not allowed to start service Intent
                 *  <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
                 *  应用有五秒的时间来调用该服务的 startForeground()
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    providerContext.startForegroundService(it)
                } else {
                    providerContext.startService(it)
                }
            }
        }

        fun stopService() {
            providerContext.stopService(intent)
        }
    }


    override fun onCreate() {
        super.onCreate()

        //启动前台通知:应用有五秒的时间来调用该服务的 startForeground()
        sendForegroundNotification(
            pendingIntent = null,
            iconResId = R.mipmap.ic_launcher,
            contentText = "Serivce",
            contentTitle = "Foreground..."
        )
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

}