package com.showdy.learnandroid.android6

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.showdy.learnandroid.R
import com.showdy.learnandroid.uitls.checkSelfPermissionCompat
import com.showdy.learnandroid.uitls.requestPermissionsCompat
import com.showdy.learnandroid.uitls.shouldShowRequestPermissionRationaleCompat
import kotlinx.android.synthetic.main.activity_permission.*

/**
 * Created by <b>Showdy</b> on 2020/10/31 16:16
 *
 *  危险权限的申请的demo演示    小米机型测试有问题
 */
class PermissionActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CAMERA = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_permission)

        button_open_camera.setOnClickListener {
            showCameraPreview()
        }
    }

    private fun showCameraPreview() {
        if (checkSelfPermissionCompat(Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            //权限已申请
            startCamera()
        } else {
            //开始申请权限
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        //权限已被拒一次，需再次告知用户，申请权限原因，再次申请一次
        //下次弹出的权限框：有个拒绝不再询问，当用户点击了，下次申请，系统直接拒绝，所以需要引导用户去设置界面打开
        /**
         * 第一次请求该权限，返回false
         * 请求过该权限并被用户拒绝，返回true。
         * 请求过该权限，但用户拒绝的时候勾选不再提醒，返回false。
         */
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.CAMERA)) {
            requestPermissionsCompat(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissionsCompat(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            Log.d("tag", "onRequestPermissionsResult: ${grantResults[0]}")
            Log.d(
                "tag",
                "onRequestPermissionsResult: ${shouldShowRequestPermissionRationaleCompat(Manifest.permission.CAMERA)}"
            )
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                startCamera()
            } else {
                if (!shouldShowRequestPermissionRationaleCompat(Manifest.permission.CAMERA)) {
                    AlertDialog.Builder(this).apply {
                        setMessage("需要前往设置中心打开应用的相机权限")
                        setCancelable(false)
                        setPositiveButton("确定") { _, _ ->
                            PermissionUtil.gotoPermission(this@PermissionActivity)
                        }
                        setPositiveButton("取消", null)
                    }.show()
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun startCamera() {
        val intent = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PERMISSION_REQUEST_CAMERA ->
                    requestCameraPermission()
            }
        }
    }
}