package com.szxb.broadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.szxb.util.MyUtils;
import com.szxb.view.MyApplication;

import java.io.File;

import static com.szxb.util.StringUtil.localSaveApkPath;

/**
 * Created by 斩断三千烦恼丝 on 2017/8/17.
 */

public class BootReciver extends BroadcastReceiver {

    private MyApplication mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e("TAG", "onReceive: " + "收到开机广播");

            ///   mp = (MyApplication) context.getApplicationContext();
            //  updateApk();
        }
    }

    //监听到开机广播，更新指定文件夹的apk
    private void updateApk() {
        if (mp.getEx() == null)
            MyUtils.Loge("mp.getEx() is null");
        mp.getEx().execute(new Runnable() {
            @Override
            public void run() {
                mp.getApkUtil().apkInstall(localSaveApkPath + File.separator);
                //  mp.getApkUtil().installApk(localSaveApkPath + File.separator);
            }
        });
    }


}
