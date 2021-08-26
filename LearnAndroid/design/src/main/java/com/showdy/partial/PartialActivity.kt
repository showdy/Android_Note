package com.showdy.partial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.showdy.interceptor.R

/**
 *  Created by showdy on 2021/4/26 17:53
 */
class PartialActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PartialFunctionExecutor.add(groupLeader)
        PartialFunctionExecutor.add(college)
        PartialFunctionExecutor.add(president)
        PartialFunctionExecutor.execute<ApplyEvent,Unit>(ApplyEvent(600, "hold a debat match"))
    }
}