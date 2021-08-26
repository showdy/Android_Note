package com.showdy.initializer

/**
 *  Created by showdy on 2021/7/13 11:49
 *
 *  初始化策略
 */
enum class Init(val flag: Int) {

    ALL(0x11), SELF(0x01), SUPPLIER(0x10);

    /**
     * 是否需要初始化自身
     * @return Boolean
     */
    fun needInitSelf() = this.flag and SELF.flag == SELF.flag

    /**
     * 是否需要初始化第三方
     * @return Boolean
     */
    fun needInitSupplier() = this.flag and SUPPLIER.flag == SUPPLIER.flag
}