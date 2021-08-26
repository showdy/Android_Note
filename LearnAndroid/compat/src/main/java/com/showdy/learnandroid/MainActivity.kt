package com.showdy.learnandroid

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.showdy.learnandroid.android6.PermissionActivity
import com.showdy.learnandroid.android7.FileProviderActivity
import com.showdy.learnandroid.android8.NotificationActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.addItemDecoration(object : DividerItemDecoration(this, HORIZONTAL) {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.set(0, 0, 0, 2)
            }
        })
        recycleView.adapter = DemoAdapter(this, demoList)
    }


    class DemoAdapter(val context: Activity, val list: Array<Pair<String, Class<out Activity>>>) :
        RecyclerView.Adapter<DemoViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_compat, parent, false)
            return DemoViewHolder(view)
        }

        override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
            val pair = list[position]
            holder.item.findViewById<AppCompatButton>(R.id.item).text = pair.first
            holder.item.setOnClickListener {
                Log.d("tag", "onBindViewHolder: click")
                context.startActivity(Intent(context, pair.second))
            }
        }

        override fun getItemCount(): Int {
            Log.d("tag", "getItemCount: ${list.size}")
            return list.size
        }
    }

    class DemoViewHolder(val item: View) : RecyclerView.ViewHolder(item)

    val demoList = arrayOf<Pair<String, Class<out Activity>>>(
        "Android6,Permission" to PermissionActivity::class.java,
        "Android7,FileProvider" to FileProviderActivity::class.java,
        "Android8,Notification" to NotificationActivity::class.java
    )
}