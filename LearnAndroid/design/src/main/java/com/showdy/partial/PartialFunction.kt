package com.showdy.partial

/**
 * 偏函数实现责任链模式
 */
class PartialFunction<in P1, out R>(
    private val matcher: (P1) -> Boolean,
    private val handler: (P1) -> R
) : (P1) -> R {
    override fun invoke(p1: P1): R {
        if (matcher(p1)) {
            return handler(p1)
        } else {
            throw IllegalArgumentException("Value:${p1} isn‘t supported by this function")
        }
    }

    fun isMatch(p1: P1) = matcher(p1)
}








