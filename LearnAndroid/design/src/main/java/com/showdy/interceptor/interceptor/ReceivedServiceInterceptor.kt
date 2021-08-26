package com.showdy.interceptor.interceptor

import com.showdy.interceptor.Interceptor
import com.showdy.interceptor.Response

/**
 * 领取服务，具体业务拦截器
 */
class ReceivedServiceInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        return chain.request().scanResult.startsWith("#https://")
    }

    override fun handle(chain: Interceptor.Chain): Response {
        //具体的业务逻辑
        return Response(true,"Received Service Success")
    }
}