package com.showdy.initializer

/**
 *  Created by showdy on 2021/7/12 17:48
 */
class AppInitializer private constructor(initializer: Initializer) : Initializer by initializer {

    companion object {

        private var sInstance: AppInitializer? = null

        @JvmStatic
        fun setup(initializer: Initializer) {
            sInstance = AppInitializer(initializer)
        }

        @JvmStatic
        fun initAll() {
            sInstance?.init(Init.ALL)
        }

        @JvmStatic
        fun initSelf() {
            sInstance?.init(Init.SELF)
        }

        @JvmStatic
        fun initSupplier() {
            sInstance?.init(Init.SUPPLIER)
        }
    }

    private fun init(init: Init) {
        val isMainProcess: Boolean = true
        listOf(
            //1. 前置初始化
            onPreInit(),
            //2. 后面初始化逻辑可以用到的一些基础业务初始化
            onInitBasicBiz(),
            //3. 各自特有业务初始化
            onInit(),
            //4. 公共业务初始化
            onInitCommonBiz(),
            //5. 后置初始化
            onPostInit()
        ).forEach {
            if (init.needInitSelf()) {
                it.initSelf(isMainProcess)
            }

            if (init.needInitSupplier()) {
                it.initSupplier(isMainProcess)
            }
        }
    }

    private fun onInitCommonBiz(): InitScope = generateInitScope {

        initSelf {
            //初始化ActivityManger
            //初始化AppRestarter
        }

        initSupplier {
            //初始化极光sdk
            //初始化云信sdk
        }

    }

    private fun onInitBasicBiz(): InitScope = generateInitScope {
        initSelf {
            //初始化context
            //初始化logger
            //初始化 db
        }

        initSupplier {
            //初始化 Bugly 日记reporter
        }
    }

}