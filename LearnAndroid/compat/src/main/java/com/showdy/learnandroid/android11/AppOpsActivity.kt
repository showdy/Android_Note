package com.showdy.learnandroid.android11

import android.Manifest
import android.app.AppOpsManager
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.showdy.learnandroid.R
import kotlinx.android.synthetic.main.activity_app_op_manager.*
import java.util.concurrent.Executors

/**
 * Created by <b>Showdy</b> on 2020/10/30 17:07
 *
 * 为了让应用及其依赖项访问用户私密数据的过程更加透明，Android 11 引入了数据访问审核功能。
 *
 * 哪些范畴属于用户私密数据呢？其实就是危险权限的调用，所以这个功能就是提供了可以监听危险权限调用的监听。
 * 主要涉及到的方法是AppOpsManager.OnOpNotedCallback
 *
 */
class AppOpsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AppOpsActivity"
    }

    //创建归因
    private val attributeContext = createAttributionContext("location_coarse")

    private val executors = Executors.newFixedThreadPool(1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_app_op_manager)

        val appOpsCallback = object : AppOpsManager.OnOpNotedCallback() {

            private fun logPrivateDataAccess(
                opCode: String,
                attributionTag: String,
                trace: String
            ) {
                Log.i(
                    TAG, "Private data accessed. " +
                            "Operation: $opCode\n " +
                            "Attribution Tag:$attributionTag\nStack Trace:\n$trace"
                )
            }

            override fun onNoted(op: SyncNotedAppOp) {
                op.attributionTag?.let {
                    logPrivateDataAccess(op.op, it, Throwable().stackTrace.toString())
                }

            }

            override fun onSelfNoted(op: SyncNotedAppOp) {
                op.attributionTag?.let {
                    logPrivateDataAccess(op.op, it, Throwable().stackTrace.toString())
                }
            }

            override fun onAsyncNoted(asyncOp: AsyncNotedAppOp) {
                asyncOp.attributionTag?.let {
                    logPrivateDataAccess(asyncOp.op, it, asyncOp.message)
                }
            }
        }

        //开启私密数据的监听
        val appOpsManager =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                attributeContext.getSystemService(AppOpsManager::class.java) as AppOpsManager
            } else {
                attributeContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            }
        appOpsManager.setOnOpNotedCallback(executors, appOpsCallback)

        location.setOnClickListener {
            getLocation()
        }


    }

    private fun getLocation() {
        val locationManager =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                attributeContext.getSystemService(LocationManager::class.java) as LocationManager
            } else {
                attributeContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            }

        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            return
        }
        val location: Location? =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            Toast.makeText(this, "${location.latitude}", Toast.LENGTH_SHORT).show()
        }
    }
}