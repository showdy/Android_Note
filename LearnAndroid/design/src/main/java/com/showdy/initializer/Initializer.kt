package com.showdy.initializer

import android.app.Application

/**
 *  Created by showdy on 2021/7/12 17:42
 */
interface Initializer {

    val context: Application
    //前置初始化 : 初始化各端的唯一表示
    fun onPreInit():InitScope

    /**
     * 自身初始化逻辑
     * @return InitScope
     */
    fun onInit():InitScope

    /**
     * 后置初始化逻辑：初始化第三方
     * @return InitScope
     */
    fun onPostInit():InitScope
}