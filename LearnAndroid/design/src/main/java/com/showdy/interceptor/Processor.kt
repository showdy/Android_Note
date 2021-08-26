package com.showdy.interceptor

import java.util.*

object Processor {

    private val interceptors = mutableListOf<Interceptor>()

    fun  addInterceptor(interceptor: Interceptor){
        interceptors.add(interceptor)
    }

    fun addInterceptors(interceptors:List<Interceptor>){
        Processor.interceptors.addAll(interceptors)
    }

    fun getInterceptors():List<Interceptor>{
        return Collections.unmodifiableList(interceptors)
    }

    fun newCall(request: Request): Call<Response> {
        return RealCall(this,request)
    }
}