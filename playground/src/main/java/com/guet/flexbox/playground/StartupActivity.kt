package com.guet.flexbox.playground

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.guet.flexbox.playground.model.AppLoader

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = application
        AppLoader.init(ctx) {
            ActivityCompat.requestPermissions(
                    this,
                    definedPermissions,
                    REQUEST_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("需要授予权限才能正常使用哦！")
                        .setCancelable(false)
                        .setNegativeButton("确认") { _: DialogInterface?, _: Int -> finish() }
                        .show()
                return
            }
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private val definedPermissions: Array<String>
        get() = try {
            val packageInfo = this.packageManager
                    .getPackageInfo(this.packageName,
                            PackageManager.GET_PERMISSIONS)
            val list = mutableListOf(*packageInfo.requestedPermissions)
            list.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
            list.toTypedArray()
        } catch (e: PackageManager.NameNotFoundException) {
            throw AssertionError(e)
        }

    companion object {
        private const val REQUEST_CODE = 8080
    }
}