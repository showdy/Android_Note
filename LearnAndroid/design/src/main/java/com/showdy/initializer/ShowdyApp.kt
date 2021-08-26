package com.showdy.initializer

import android.app.Application

/**
 *  Created by showdy on 2021/7/12 18:06
 */
class ShowdyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //是否已同意隐私协议
        val agreement = true

        AppInitializer.setup(ShowdyAppInitializer(this))

        if (agreement) {
            AppInitializer.initAll()
        } else {
            AppInitializer.initSelf()
        }
    }
}