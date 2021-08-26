package com.showdy.interceptor

class RealInterceptorChain(
    private val request: Request,
    private val interceptors: MutableList<Interceptor>,
    private val index: Int
) : Interceptor.Chain {
    override fun request(): Request {
        return request
    }

    override fun proceed(request: Request): Response {
        if (index >= interceptors.size) {
            println("unable to proceed...")
            return Response(false,"Found interceptor failure");
        }
        val next = RealInterceptorChain(request,interceptors,index+1)
        val interceptor = interceptors[index]
        return if (interceptor.intercept(next)){
            interceptor.handle(next)
        }else{
            next.proceed(request)
        }
    }
}