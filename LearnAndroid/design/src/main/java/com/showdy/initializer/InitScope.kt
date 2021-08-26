package com.showdy.initializer

/**
 *  Created by showdy on 2021/7/12 17:41
 */
interface InitScope {
    /**
     * 初始化自身的sdk
     * @param mainProcess Boolean
     */
    fun initSelf(mainProcess: Boolean)

    /**
     * 初始化第三方sdk
     * @param mainProcess Boolean
     */
    fun initSupplier(mainProcess: Boolean)
}