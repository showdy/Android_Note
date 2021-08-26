package com.showdy.learnandroid

import android.app.Application
import android.content.Context
import com.showdy.learnandroid.android9.AndroidPCompat

/**
 * Created by <b>Showdy</b> on 2020/10/30 22:05
 *
 */
class CompatApp:Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        AndroidPCompat.closeAndroidPDialog()
    }
}