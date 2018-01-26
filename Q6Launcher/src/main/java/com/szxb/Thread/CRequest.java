package com.szxb.Thread;

import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.zhoukai.modemtooltest.ModemToolTest;
import com.szxb.http.CallServer;
import com.szxb.http.HttpListener;
import com.szxb.http.JsonRequest;
import com.szxb.sp.CommonSharedPreferences;
import com.szxb.sp.FetchAppConfig;
import com.szxb.util.AppUtil;
import com.szxb.util.FTP;
import com.szxb.util.MainLooper;
import com.szxb.view.MyApplication;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：Tangren on 2018-01-16
 * 包名：com.szxb.Thread
 * 邮箱：996489865@qq.com
 * TODO:检查更新状态
 */

public class CRequest {

    private volatile static CRequest instance = null;
    private JsonRequest request;

    private CRequest() {
        String url = "http://139.199.158.253/bipeqt/interaction/devUpdate";
        request = new JsonRequest(url, RequestMethod.POST);
    }

    public static CRequest getInstance() {
        if (instance == null) {
            synchronized (CRequest.class) {
                if (instance == null) {
                    instance = new CRequest();
                }
            }
        }
        return instance;
    }

    public void check() {

        Map<String, Object> map = new HashMap<>();
        map.put("mch_id", FetchAppConfig.mchId());
        map.put("pos_no", ModemToolTest.getItem(7));
        map.put("version_1", AppUtil.getVersionName(MyApplication.getInstance()));
        map.put("version_2", FetchAppConfig.busAppVersion());
        map.put("last_time", FetchAppConfig.lastTime());
        map.put("charge_type", FetchAppConfig.chargeType());
        map.put("launher_name", "launcher");
        map.put("app_name", FetchAppConfig.packages());
        request.add(map);
        CallServer.getHttpclient().add(0, request, new HttpListener<JSONObject>() {
            @Override
            public void success(int what, Response<JSONObject> response) {
                try {
                    Log.d("CRequest",
                            "success(CRequest.java:73)" + response.get().toString());
                    JSONObject object = response.get();
                    String rescode = object.getString("rescode");
                    if (TextUtils.equals(rescode, "0000")) {
                        String update_mode = object.getString("update_mode");
                        String update_type = object.getString("update_type");

                        if (TextUtils.equals(update_mode, "0")
                                || TextUtils.equals(update_type, "0")) {
                            //不做更新
                            return;
                        }
                        String current_version = object.getString("current_version");
                        String update_effect = object.getString("update_effect");
                        String file_name = object.getString("file_name");
                        if (TextUtils.equals(update_mode, "1")) {
                            //全量更新
                            if (TextUtils.equals(update_type, "1")) {
                                //更新launcher
                                if (AppUtil.version(AppUtil.getVersionName(MyApplication.getInstance()), current_version)) {
                                    //如果当前版本号小于服务器版本号更新
                                    downAllPack(file_name + ".apk", update_effect, "com.szxb.view");
                                    CommonSharedPreferences.put("update_type", 1);
                                }
                            } else if (TextUtils.equals(update_type, "2")) {
                                //更新车载程序
                                if (AppUtil.version(FetchAppConfig.busAppVersion(), current_version)) {
                                    //如果当前版本号小于服务器版本号更新
//                                    {"update_effect":"0","current_version":"1.0.1","result":"success","mch_id":"10000052","update_mode":"0","update_type":"2","rescode":"0000","file_name":"czbus_1.0.1"}
                                    Log.d("CRequest",
                                            "success(CRequest.java:101)如果当前版本号小于服务器版本号更新");
                                    downAllPack(file_name + ".apk", update_effect, FetchAppConfig.packages());
                                    CommonSharedPreferences.put("update_type", 2);
                                }

                            } else {

                            }
                        } else if (TextUtils.equals(update_mode, "2")) {
                            //增量更新

                        } else if (TextUtils.equals(update_mode, "3")) {
                            //卸载

                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("CRequest",
                            "success(CRequest.java:122)" + e.toString());
                }
            }

            @Override
            public void fail(int what, String e) {

            }
        });
    }


    private void downAllPack(final String apkName, final String update_effect, final String packages) {

        Log.d("CRequest",
                "downAllPack(CRequest.java:137)开机下载");
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean download = download(apkName);
                if (download) {
                    control(apkName, update_effect, packages);
                } else {
                    Log.d("CRequest",
                            "run(CRequest.java:169)下载失败");
                }
            }
        }).start();

    }

    private void control(String apkName, String update_effect, String packages) {
        String path = Environment.getExternalStorageDirectory() + "/" + apkName;
        Log.d("CRequest",
                "downAddPack(CRequest.java:183)下载完成:" + path);
        if (TextUtils.equals(update_effect, "0")) {
            MainLooper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyApplication.getInstance(), "[Launcher]更新完成,重启机器后生效", Toast.LENGTH_SHORT).show();
                }
            });
            //下载完成
            CommonSharedPreferences.put("download_success", true);
            //保存路径
            CommonSharedPreferences.put("apk_path", path);
            //需要更新
            CommonSharedPreferences.put("update", true);
        } else {
            //立即生效
            MainLooper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyApplication.getInstance(), "[Launcher]更新完成,立即生效", Toast.LENGTH_SHORT).show();
                }
            });

            try {

                MyApplication.getInstance().getmService().apkInstall(path);
                MyApplication.getInstance().getApkUtil().startOtherApp(packages);
                CommonSharedPreferences.put("apk_path", "0");
                CommonSharedPreferences.put("update", false);
                Log.d("CRequest",
                        "control(CRequest.java:189)");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean download(String apkName) {
//        return new FTP()
//                .builder("112.74.102.125")
//                .setPort(2121)
//                .setLogin("Administrator", "@@#Dzw2016szxb")
//                .setFileName(apkName)
//                .setPath(Environment.getExternalStorageDirectory() + "/")
//                .setFTPPath("czbusftp/" + apkName)
//                .setRetry(3)
//                .download();

        return new FTP()
                .builder("139.199.158.253")
                .setPort(21)
                .setLogin("ftpuser", "QW!@123qwe")
                .setFileName(apkName)
                .setPath(Environment.getExternalStorageDirectory() + "/")
                .setFTPPath(apkName)
                .setRetry(3)
                .download();
    }
}
