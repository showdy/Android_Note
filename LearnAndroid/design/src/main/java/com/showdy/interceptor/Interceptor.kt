package com.showdy.interceptor

interface Interceptor {
    //是否需要拦截
    fun intercept(chain: Chain):Boolean

    //处理请求返回的结果，解析结果
    fun handle(chain: Chain): Response


    interface Chain{
        //处理请求
        fun request(): Request

        //处理请求返回的解析结果
        fun proceed(request: Request): Response
    }
}