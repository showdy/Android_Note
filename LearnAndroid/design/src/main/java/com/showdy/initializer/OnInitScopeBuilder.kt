package com.showdy.initializer

class OnInitScopeBuilder {
    private var initSelf: ((isMainProcess: Boolean) -> Unit)? = null
    private var initSupplier: ((isMainProcess: Boolean) -> Unit)? = null
    internal fun build() = object : InitScope {
        override fun initSelf(isMainProcess: Boolean) {
            this@OnInitScopeBuilder.initSelf?.invoke(isMainProcess)
        }

        override fun initSupplier(isMainProcess: Boolean) {
            this@OnInitScopeBuilder.initSupplier?.invoke(isMainProcess)
        }
    }

    /**
     * 初始化自己的sdk
     */
    fun initSelf(initSelf: (isMainProcess: Boolean) -> Unit) {
        this.initSelf = initSelf
    }

    /**
     * 初始化第三方供应商sdk
     */
    fun initSupplier(initSupplier: (isMainProcess: Boolean) -> Unit) {
        this.initSupplier = initSupplier
    }
}

fun generateInitScope(scope: OnInitScopeBuilder.() -> Unit) =
    OnInitScopeBuilder().also(scope).build()
