package com.szxb.broadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.szxb.Myinterface.BackResult;
import com.szxb.Thread.ReadFromSdCard;
import com.szxb.model.ConfigModel;
import com.szxb.model.minterface.IConfig;
import com.szxb.util.ApkUtilImpl;
import com.szxb.util.Fileflow_Utils;
import com.szxb.util.HttpUtils;
import com.szxb.util.MyUtils;
import com.szxb.util.SdCardUtils;
import com.szxb.view.MyApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.szxb.util.StringUtil.AddressForUploadApkVersion;
import static com.szxb.util.StringUtil.Apk_InstallFailed;
import static com.szxb.util.StringUtil.Apk_InstallSuccess;
import static com.szxb.util.StringUtil.Config_xml;
import static com.szxb.util.StringUtil.Local_Conifg;
import static com.szxb.util.StringUtil.MyConfig;
import static com.szxb.util.StringUtil.Operate_xml;

/**
 * Created by 斩断三千烦恼丝 on 2017/8/3.
 */

public class SdCardReciver extends BroadcastReceiver {

    private MyApplication mp;
    //  private String path;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("SdCardReciver",
                "onReceive(SdCardReciver.java:46)" + action);
        if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            MyUtils.Logd("SD卡已被拔出");
        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            //获取外置SD卡的路径
            //  path = intent.getData().getPath();
            final String path = intent.getData().getPath();
            //MyUtils.Loge("sd卡的路径为" + path);
            //     getInstance();
            mp = (MyApplication) context.getApplicationContext();
            //   ReadConfigOperate();

            if (Fileflow_Utils.ifExists(path + "/update.zip")) {
                MyUtils.Loge("come into here:  " + path + "/update.zip");
                try {
                    mp.getmService().updateSystem("/sdcard/update.zip");
                } catch (Exception e) {
                    MyUtils.Logd("updatesystem: " + e.getMessage());
                }
            } else if (Fileflow_Utils.getAllFileName(path + "/apks/").size() > 0) {
                mp.getEx().execute(new Runnable() {
                    @Override
                    public void run() {

                        if (Fileflow_Utils.ifExitsUninstall(path + "/apks/uninstall")) {
                            unInstallAllAPk();
                        }
                        updateApkFromSdCard(path + "/apks/", context);

                        //    mp.getApkUtil().installApk(path + "/apks/");
                    }
                });
            } else if (Fileflow_Utils.ifExists(path + Operate_xml)) {
                MyUtils.Loge("卸载测试");
                mp.getEx().execute(new ReadFromSdCard(path + Operate_xml, new BackResult() {
                    @Override
                    public void getResulte(Map map) {
                        boolean flag = Boolean.parseBoolean(map.get("ifUninstallAllThirdApk").toString());
                        if (flag) {
                            unInstallAllAPk();
                        }
                    }
                }));
            } else if (Fileflow_Utils.ifExists(path + Config_xml)) {
                //执行读取SD卡里面的xml配置文件的线程
                mp.getEx().execute(new ReadFromSdCard(path + Config_xml, new BackResult() {
                    @Override
                    public void getResulte(Map map) {
                        doOther(map);
                    }
                }));
            }
        }

    }

 /*   private void getInstance() {
        if (mp == null) {
            mp = MyApplication.getInstance();
        }
    }*/

    //读取配置文件，选择当前要进行的组合操作
/*    private void ReadConfigOperate() {
        mp.getEx().execute(new ReadFromSdCard(path + CombinationOperation_xml, new BackResult() {
            @Override
            public void getResulte(Map map) {
                if (map != null) {
                    String flag = map.get("operate").toString();
                    switch (flag) {
                        case "1":
                            unInstallAllAPk();
                            updateApkFromSdCard(path + "/apks/", MyApplication.getInstance());
                            break;
                        case "2":
                            break;

                        case "3":
                            break;
                    }

                }
            }
        }));
    }*/

    /**
     * 从sd卡更新ap
     */
    private void updateApkFromSdCard(String path, Context context) {
        List ar = Fileflow_Utils.getAllFileName(path);
        for (int i = 0; i < ar.size(); i++) {
            String apkName = ar.get(i).toString();
            MyUtils.Loge("apkName:" + apkName);
            boolean flag = mp.getApkUtil().apkInstall(path + apkName);
            String status = Apk_InstallFailed;
            if (flag)
                status = Apk_InstallSuccess;
            Map maps = ApkUtilImpl.apkInfo(path + apkName, context);
            String version = maps.get("version").toString();
            String packageName = maps.get("packageName").toString();
            Map<String, Object> map = new HashMap<>();
            map.put("appname", ar.get(i).toString());
            map.put("version", version);
            map.put("status", status);
            map.put("devno", mp.getSN());

            MyUtils.Logd("packgename:" + packageName + "\r\n" + "status:" + status);
            if (status.equals(Apk_InstallSuccess)) {
                //跳转安装apk
                mp.getApkUtil().startOtherApp(packageName);
            }
            HttpUtils.getInstance().SendPost(map, AddressForUploadApkVersion);
        }
    }


    /**
     * 2017年10月19日19:26:03
     */
    //卸载所有第三方apk
    private void unInstallAllAPk() {
        List<Map> ar = mp.getApkUtil().getThirdPartyInformation(0);
        for (int i = 0; i < ar.size(); i++) {
            Map m = ar.get(i);
            String packageName = m.get("packageName").toString();
            MyUtils.Loge("in here packageName:" + packageName);
            mp.getApkUtil().uninstallApk(packageName);
        }
    }


    //将读到的配置文件内容写入本地以及javabean
    private IConfig config;

    private void doOther(Map map) {
        SdCardUtils.writeToSdCard(map.toString(), Local_Conifg, MyConfig);
        config = ConfigModel.getInstance();
        config.setConfig(map);
        OpenWifi();
        EventBus.getDefault().postSticky(true);
    }


    //收到开机广播后打开wifi
    private void OpenWifi() {
        // 取得WifiManager对象
        WifiManager mWifiManager = (WifiManager) mp.getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }


}
