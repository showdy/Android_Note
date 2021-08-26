package com.showdy.interceptor

import com.showdy.interceptor.interceptor.ValidateInterceptor
import java.util.concurrent.atomic.AtomicBoolean

class RealCall(
    private val client: Processor,
    private val request: Request
): Call<Response> {
    private val isCanceled = AtomicBoolean(false)

    override fun execute(): Response {
        val interceptors = mutableListOf<Interceptor>()
        //有效性校驗
        interceptors.add(ValidateInterceptor())
        //自定义的拦截器
        interceptors.addAll(client.getInterceptors())
        //真正执行逻辑的起点
        return RealInterceptorChain(request,interceptors,0).proceed(request)
    }

    override fun cancel() {
        isCanceled.compareAndSet(false,true)
    }

    override fun isCancelled(): Boolean {
        return isCanceled.get()
    }
}