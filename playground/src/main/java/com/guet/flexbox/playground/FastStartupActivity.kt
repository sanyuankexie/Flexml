package com.guet.flexbox.playground

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.didichuxing.doraemonkit.DoraemonKit
import com.guet.flexbox.litho.LithoBuildTool
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import es.dmoral.toasty.Toasty

class FastStartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DoraemonKit.install(application)
        ActivityCompat.requestPermissions(
                this,
                definedPermissions,
                REQUEST_CODE1
        )
        LithoBuildTool.init(this)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE1) {
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
            startQRCodeActivity()
        }
    }

    private fun startQRCodeActivity() {
        val intent = Intent(this, CaptureActivity::class.java)
        //ZxingConfig是配置类
        val config = ZxingConfig().apply {
            isPlayBeep = true //是否播放扫描声音 默认为true
            isShake = true //是否震动  默认为true
            isDecodeBarCode = false //是否扫描条形码 默认为true
            reactColor = R.color.colorAccent //设置扫描框四个角的颜色 默认为白色
            frameLineColor = R.color.colorAccent //设置扫描框边框颜色 默认无色
            scanLineColor = R.color.colorAccent //设置扫描线的颜色 默认白色
            isFullScreenScan = false //是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        }
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
        startActivityForResult(intent, REQUEST_CODE2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null != data) {
            data.extras ?: return
            // 扫描二维码/条码回传
            if (requestCode == REQUEST_CODE2) {
                if (resultCode == Activity.RESULT_OK) {
                    val content = data.getStringExtra(Constant.CODED_CONTENT)
                    Toasty.success(this, "扫码成功", Toast.LENGTH_LONG)
                            .show()
                    val intent = Intent(this, OverviewActivity::class.java)
                    intent.putExtra("url", content)
                    startActivity(intent)
                } else {
                    Toasty.error(this, "解析二维码失败", Toast.LENGTH_LONG)
                            .show()
                    startQRCodeActivity()
                }
            }
        }
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
        private const val REQUEST_CODE1 = 8081
        private const val REQUEST_CODE2 = 8082
    }
}
