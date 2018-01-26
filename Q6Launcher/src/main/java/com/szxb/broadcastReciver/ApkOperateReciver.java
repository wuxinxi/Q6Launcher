package com.szxb.broadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.szxb.model.PakageMod;
import com.szxb.util.MyUtils;
import com.szxb.view.MyApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by 斩断三千烦恼丝 on 2017/11/1.
 */

public class ApkOperateReciver extends BroadcastReceiver {
    private MyApplication mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp= (MyApplication) context.getApplicationContext();
     //   PackageManager manager = context.getPackageManager();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
            MyUtils.Logd("ApkOperateReciver packganme:"+packageName);
            NoticeUpdateUi();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
            NoticeUpdateUi();;
        }
      /*  if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "更新成功" + packageName, Toast.LENGTH_LONG).show();
            NoticeUpdateUi();
        }*/
    }

    //通知刷新ui
    private void NoticeUpdateUi() {
        List<PakageMod> ars = mp.getApkUtil().getApkAll();
        EventBus.getDefault().postSticky(ars);
    }
}
