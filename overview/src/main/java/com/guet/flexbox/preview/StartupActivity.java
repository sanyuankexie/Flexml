package com.guet.flexbox.preview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.facebook.soloader.SoLoader;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;


public class StartupActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DoraemonKit.install(getApplication());
        SoLoader.init(this, false);
        ActivityCompat.requestPermissions(
                this,
                getDefinedPermissions(),
                REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("需要授予权限才能正常使用哦！")
                        .setCancelable(false)
                        .setNegativeButton("确认", (dialog1, which) -> finish())
                        .show();
                return;
            }
        }
        startQRCodeActivity();
    }

    private void startQRCodeActivity() {
        Intent intent = new Intent(this, CaptureActivity.class);
        /*ZxingConfig是配置类
         *可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(false);//是否扫描条形码 默认为true
        config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
        config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
        config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            // 扫描二维码/条码回传
            if (requestCode == REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    String content = data.getStringExtra(Constant.CODED_CONTENT);
                    Toasty.success(this, "扫码成功", Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(this, OverviewActivity.class);
                    intent.putExtra("url", content);
                    startActivity(intent);
                } else {
                    Toasty.error(this, "解析二维码失败", Toast.LENGTH_LONG)
                            .show();
                    startQRCodeActivity();
                }
            }
        }
        finish();
    }

    public String[] getDefinedPermissions() {
        try {
            PackageInfo packageInfo = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(),
                            PackageManager.GET_PERMISSIONS);
            ArrayList<String> list = new ArrayList<>(Arrays.asList(packageInfo.requestedPermissions));
            list.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);
            return list.toArray(new String[0]);
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }
}
