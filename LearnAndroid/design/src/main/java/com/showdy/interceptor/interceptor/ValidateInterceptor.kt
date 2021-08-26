package com.showdy.interceptor.interceptor

import com.showdy.interceptor.Interceptor
import com.showdy.interceptor.Response

class ValidateInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        return true
    }

    override fun handle(chain: Interceptor.Chain): Response {
        val request = chain.request()
        //简单校验
        if (!request.scanResult.startsWith("#")){
            return Response(false,"Validate failure")
        }
        return chain.proceed(request)
    }

}