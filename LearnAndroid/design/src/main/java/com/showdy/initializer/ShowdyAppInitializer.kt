package com.showdy.initializer

import android.app.Application

/**
 *  Created by showdy on 2021/7/12 18:01
 *
 *  具体业务app的初始化器，具有自身的独特性
 */
class ShowdyAppInitializer(override val context: Application):Initializer {
    override fun onPreInit(): InitScope  = generateInitScope {
        initSelf {
            //初始化自身的图标，名称，通知渠道
        }
    }

    override fun onInit(): InitScope  = generateInitScope {
        initSelf {
            //初始化自身封装的app升级sdk，http配置参数
        }
    }

    override fun onPostInit(): InitScope = generateInitScope {
        initSupplier {
            //云信sdk
            //极光推送sdk
        }
    }
}