package com.showdy.learnandroid.android7

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX
import com.showdy.learnandroid.R
import kotlinx.android.synthetic.main.activity_file_provider.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <b>Showdy</b> on 2020/10/30 20:31
 *
 * 拍照，安装apk：在官方7.0的以上的系统中，尝试传递 file://URI可能会触发FileUriExposedException。
 * 故而转用 content://URI访问文件
 *
 * 使用content://替代file://，主要需要FileProvider的支持，而因为FileProvider是ContentProvider的子类，
 * 所以需要在AndroidManifest.xml中注册；而又因为需要对真实的filepath进行映射，所以需要编写一个xml文档，
 * 用于描述可使用的文件夹目录，以及通过name去映射该文件夹目录。
 * 对于权限，有两种方式：
 * 方式一为Intent.addFlags，该方式主要用于针对intent.setData，setDataAndType以及setClipData相关方式传递uri的。
 * 方式二为grantUriPermission来进行授权
 * 相比来说方式二较为麻烦，因为需要指定目标应用包名，很多时候并不清楚，所以需要通过PackageManager进行查找到所有匹配的应用，全部进行授权。不过更为稳妥~
 * 方式一较为简单，对于intent.setData，setDataAndType正常使用即可，但是对于setClipData，由于5.0前后Intent#migrateExtraStreamToClipData
 *
 * @see https://blog.csdn.net/lmj623565791/article/details/72859156
 */
class FileProviderActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_TAKE_PHOTO = 0x100
    }

    private var currentPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_provider)

        open_camera.setOnClickListener(this::takePhotoNoCompress)
        installApk.setOnClickListener(this::installApk)
    }


    private fun takePhotoNoCompress(view: View) {
        PermissionX.init(this)
            .permissions(
                arrayListOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            )
            .request { allGranted, _, _ ->
                if (allGranted) {
                   takePhotoWithPermission()
                }else{
                    Toast.makeText(this,"No permission",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun takePhotoWithPermission() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            val filename = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
                .format(Date()) + ".png"
            val file = File(Environment.getExternalStorageDirectory(), filename)
            currentPhotoPath = file.absolutePath
            val fileUri = FileProvider7.getUriForFile(this, file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO)
        }
    }


    private fun installApk(view: View) {
        PermissionX.init(this)
            .permissions(
                arrayListOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            .request { allGranted, _, _ ->
                if (allGranted) {
                    installApkWithPermission()
                }else{
                    Toast.makeText(this,"No permission",Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun installApkWithPermission() {
        val file = File(Environment.getExternalStorageDirectory(), "app-debug.apk")
        if (!file.exists()) Toast.makeText(this, "APK not exist", Toast.LENGTH_SHORT).show()
        intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        FileProvider7.setIntentDataAndType(
            this, intent,
            "application/vnd.android.package-archive", file, true
        )
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG", "onActivityResult: ")
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath))
        }
    }
}