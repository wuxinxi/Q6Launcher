package com.szxb.broadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.szxb.Thread.RequestTime;
import com.szxb.model.ConfigModel;
import com.szxb.model.minterface.IConfig;
import com.szxb.util.MyUtils;
import com.szxb.util.WifiAdmin;
import com.szxb.view.MyApplication;

import static com.szxb.util.StringUtil.wifiName;
import static com.szxb.util.StringUtil.wifiPwd;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/12.
 */

public class NetWorkReciver extends BroadcastReceiver {
    private MyApplication mp;
    private WifiAdmin wifiAdmin;
    private IConfig mconfig;

    @Override
    public void onReceive(Context context, Intent intent) {

        // 监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            if (mp == null) {
                mp = (MyApplication) context.getApplicationContext();
            }
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.e("TAG", "wifiState:" + wifiState);
            switch (wifiState) {
                //wifi已经关闭
                case WifiManager.WIFI_STATE_DISABLED:
                    MyUtils.Print("wifi has been closed");
                    break;
                //wifi正在关闭
                case WifiManager.WIFI_STATE_DISABLING:
                    MyUtils.Print("wifi不应该被关闭");
                    break;
                //wifi已经开启
                case WifiManager.WIFI_STATE_ENABLED:
                    if (wifiAdmin == null) {
                        wifiAdmin = new WifiAdmin(context);
                        mconfig = ConfigModel.getInstance();
                    }
                    String ssid = mconfig.getConfig(wifiName);
                    if (!ssid.equals("")) {
                        String pwd = mconfig.getConfig(wifiPwd);
                        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, pwd, 3));
                    }
                    break;
                //wifi正在开启
                case WifiManager.WIFI_STATE_ENABLING:
                    MyUtils.Print("wifi is opening");
                    break;
            }
        }

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            if (MyUtils.getNetWorkState(context)) {
                if (!MyApplication.getInstance().isUpdateTime()) {
                    RequestTime time = new RequestTime();
                    time.request();
                }
                Toast.makeText(context, "网络连接成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "网络连接已断开", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
