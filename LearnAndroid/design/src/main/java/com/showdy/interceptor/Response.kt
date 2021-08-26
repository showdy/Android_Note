package com.showdy.interceptor

data class Response(
        val success:Boolean ,// 扫描结果解析后的响应
        val description:String //解析结果的描述
){
    init {
        println(this.toString())
    }
}