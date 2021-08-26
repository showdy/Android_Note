package com.showdy.interceptor

/**
 * 执行扫描结果解析任务
 */
interface Call<T> {

    fun execute():T

    fun cancel()

    fun isCancelled():Boolean
}