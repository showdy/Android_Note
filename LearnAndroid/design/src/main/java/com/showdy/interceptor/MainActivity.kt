package com.showdy.interceptor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.showdy.interceptor.interceptor.ReceivedServiceInterceptor

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onResume() {
        super.onResume()
        //添加自定义的拦截器
        Processor.addInterceptor(ReceivedServiceInterceptor())
        //执行
        Processor.newCall(Request("#https://abds"))
            .execute()
    }
}