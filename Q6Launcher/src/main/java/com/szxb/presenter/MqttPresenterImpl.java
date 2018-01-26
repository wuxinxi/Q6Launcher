package com.szxb.presenter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.zhoukai.modemtooltest.ModemToolTest;
import com.example.zhoukai.modemtooltest.NvConstants;
import com.szxb.model.ConfigModel;
import com.szxb.presenter.pinteface.BasePresenter;
import com.szxb.presenter.pinteface.MqttPresenter;
import com.szxb.sp.CommonSharedPreferences;
import com.szxb.util.FTP;
import com.szxb.util.HttpUtils;
import com.szxb.util.MainLooper;
import com.szxb.util.MyUtils;
import com.szxb.util.SdCardUtils;
import com.szxb.util.StringUtil;
import com.szxb.view.MyApplication;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.szxb.util.StringUtil.AddressForUploadApkVersion;
import static com.szxb.util.StringUtil.Apk_InstallSuccess;
import static com.szxb.util.StringUtil.Apk_InstallTAG;
import static com.szxb.util.StringUtil.Apk_UninstallTAG;
import static com.szxb.util.StringUtil.GetMackey;
import static com.szxb.util.StringUtil.Local_Conifg;
import static com.szxb.util.StringUtil.MyConfig;
import static com.szxb.util.StringUtil.mqttUri;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/8.
 */

public class MqttPresenterImpl implements MqttCallbackExtended, MqttPresenter {

    private BasePresenter mview;
    //  private IConfig config;
    private MyApplication mp;


    public MqttPresenterImpl(BasePresenter mview, MyApplication mp) {
        this.mview = mview;
        this.mp = mp;
        init();
    }


    private void init() {
        String str = SdCardUtils.ReadFromSdCard(Local_Conifg + File.separator + MyConfig);
        if (!str.equals("")) {
            String serverUrI = ConfigModel.getInstance().getConfig(mqttUri);
            initConnect(serverUrI);
        } else {
            //读取默认的
            String uri = "tcp://112.74.102.125:1883";
            initConnect(uri);
        }
    }

    //**********初始化MQTT连接参数************
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    final String subscriptionTopic = "Topic";//订阅的名字
    private volatile String serverUri = ""; //mqtt请求的服务器地址

    //MQTT客户端连接初始化
    public void initConnect(String serverUri) {
        Log.d("MqttPresenterImpl",
            "initConnect(MqttPresenterImpl.java:90)tcp id:"+serverUri);
        if (serverUri.equals("") || serverUri.equals(this.serverUri)) {
            MyUtils.Loge("serverUri can not be as empty string  or same adress");
            return;
        }
        this.serverUri = serverUri;
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mqttAndroidClient.close();
            MyUtils.Logd("mqtt close");
        }
        mqttAndroidClient = null;
        mqttConnectOptions = null;
        mqttAndroidClient = new MqttAndroidClient(mp, serverUri,
                mp.getSN());

        mqttAndroidClient.setCallback(this);

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        connectMqtt(mqttConnectOptions);

    }

    private void connectMqtt(MqttConnectOptions mqttConnectOptions) {
        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MyUtils.Loge("come into success");
                    postLinkStatus(true);
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    postLinkStatus(false);
                    //    mview.setMsg("Failed to connect to: " + serverUri);
                }
            });

        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }


    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MyUtils.Loge("订阅成功");
                    postLinkStatus(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    MyUtils.Loge("订阅失败");
                    //   postLinkStatus(false);
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        //判断法是否重连
        if (reconnect) {
            subscribeToTopic();
        } else {
            postLinkStatus(true);
            //mqtt连接成功，表示有网，就可以将当前的版本号发送过去
            try {
                String versionName = mp.getPackageManager()
                        .getPackageInfo(mp.getPackageName(), 0).versionName;
                Map m = new HashMap();
                m.put("devno", mp.getSN());
                m.put("appname", "com.szxb.view");
                m.put("version", versionName);
                m.put("status", Apk_InstallSuccess);
//                HttpUtils.getInstance().SendPost(m, AddressForUploadApkVersion);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        MyUtils.Loge("connect is lost ！ cause =  " + cause.getMessage());
        postLinkStatus(false);
        //main.addToHistory("The Connection was lost.");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    //接收到后台推送的消息
    @Override
    public void messageArrived(String topic, final MqttMessage message) throws Exception {

        MyApplication.getInstance().getEx().submit(new Runnable() {
            @Override
            public void run() {
                try {

                    String result = new String(message.getPayload());
                    Log.d("MqttPresenterImpl",
                            "run(MqttPresenterImpl.java:210)" + result);
                    String item = ModemToolTest.getItem(NvConstants.REQUEST_GET_SN);
                    JSONObject object = new JSONObject(result);
                    if (check(item, object)) {
                        String flag = object.getString("flag");
                        switch (flag) {
                            case Apk_InstallTAG:
                                String apkName = object.getString("apkname") + ".apk";
                                String version = object.getString("version");
                                String packages = object.getString("pages");
//                                if (checkDownFinish(apkName)) {
//                                    Log.d("MqttPresenterImpl",
//                                            "run(MqttPresenterImpl.java:220)已经下载完成");
//                                    return;
//                                }
                                String setPath = "/storage/emulated/0/";
                                boolean download = new FTP()
                                        .builder("139.199.158.253")
                                        .setPort(21)
                                        .setLogin("ftpuser", "QW!@123qwe")
                                        .setFileName(apkName)
                                        .setPath(setPath)
                                        .setFTPPath(apkName)
                                        .setRetry(3)
                                        .download();
                                if (download) {
                                    String path = setPath + apkName;
                                    Log.d("MqttPresenterImpl",
                                            "run(MqttPresenterImpl.java:239)" + setPath);
                                    CommonSharedPreferences.put("update", true);
                                    CommonSharedPreferences.put("apk_path", path);
                                    godoSth(apkName, version, 0, packages);
                                    Log.d("MqttPresenterImpl",
                                            "run(MqttPresenterImpl.java:242)重启更新");
                                } else {

                                    Log.d("MqttPresenterImpl",
                                            "run(MqttPresenterImpl.java:230)下载失败");
                                }
                                break;
                            case Apk_UninstallTAG:
                                Log.d("MqttPresenterImpl",
                                        "run(MqttPresenterImpl.java:240)执行卸载");
                                String apkpackage = object.getString("apkpackage");
                                MyApplication.getInstance().getmService().apkUninstall(apkpackage);
                                break;
                            case StringUtil.PATCH:
                                //分差包
                                String patchName = object.getString("apkname") + ".patch";
//                                boolean download2 = new FTP()
//                                        .builder("139.199.158.253")
//                                        .setPort(21)
//                                        .setLogin("ftpuser", "QW!@123qwe")
//                                        .setFileName(patchName)
//                                        .setPath(Environment.getExternalStorageDirectory() + "/")
//                                        .setFTPPath("eqt/" + patchName)
//                                        .setRetry(3)
//                                        .download();
//
//                                if (download2) {
//                                    String oldApkPath = Environment.getExternalStorageDirectory() + "";
//                                    boolean make = Patch.make(FetchAppConfig.oldApkPath(), oldApkPath, oldApkPath + File.separator + patchName);
//                                    if (make) {
//                                        Log.d("MqttPresenterImpl",
//                                                "run(MqttPresenterImpl.java:274)apk生成成功");
//                                    } else {
//                                        Log.d("MqttPresenterImpl",
//                                                "run(MqttPresenterImpl.java:277)apk生成失败");
//                                    }
//                                    Log.d("MqttPresenterImpl",
//                                            "run(MqttPresenterImpl.java:269)下载成功");
//                                } else {
//                                    Log.d("MqttPresenterImpl",
//                                            "run(MqttPresenterImpl.java:272)下载失败");
//                                }

                                break;
                            case GetMackey:

                                break;
                        }
                    }
                } catch (JSONException | RemoteException e) {
                    e.printStackTrace();
                    Log.d("MqttPresenterImpl",
                            "run(MqttPresenterImpl.java:254)" + e.toString());
                }
            }
        });

    }

    /**
     * 通知服务器
     *
     * @param apkName .
     * @param version .
     */
    private void godoSth(String apkName, String version, int status, String packages) {
        String path = "/storage/emulated/0/" + apkName;
        Log.d("MqttPresenterImpl",
                "godoSth(MqttPresenterImpl.java:312)" + path);
        CommonSharedPreferences.put("download_success", true);
        CommonSharedPreferences.put("apk_path", path);
        CommonSharedPreferences.put("update", true);
        CommonSharedPreferences.put("update_type", 3);
        CommonSharedPreferences.put("pack_ages", packages);
        Map<String, Object> map = new HashMap<>();
        map.put("appname", apkName);
        map.put("version", version);
        map.put("status", status);
        map.put("devno", mp.getSN());


        Intent intent = new Intent();
        intent.setAction("com.szxb.launcher.receiverbus");
        intent.putExtra("result", "success");
        MyApplication.getInstance().sendBroadcast(intent);

        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getInstance(), "下载成功,重启生效", Toast.LENGTH_SHORT).show();
            }
        });
        if (status == 0)
            HttpUtils.getInstance().SendPost(map, AddressForUploadApkVersion);
    }

    //连接或者重置连接
    @Override
    public void connectOrReConnect() {
        initConnect(ConfigModel.getInstance().getConfig(mqttUri));
    }


    //通知ui更新状态
    private void postLinkStatus(Boolean bl) {

        EventBus.getDefault().postSticky(bl + "");
    }


    //判断推送过来denvo数组是否包含此机器的SN号
    private boolean check(String devcode, JSONObject js) {
        try {
            JSONArray arary = js.getJSONArray("devno");
            for (int i = 0; i < arary.length(); i++) {
                JSONObject object = arary.getJSONObject(i);
                String dev = object.getString("dev");
                Log.d("MqttPresenterImpl",
                        "check(MqttPresenterImpl.java:294)" + dev);
                if (dev.equals(devcode)) {
                    return true;
                }
            }
            return false;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("MqttPresenterImpl",
                    "check(MqttPresenterImpl.java:302)" + e.toString());
        }
        return false;
    }


    /**
     * 检查此apk是否下载完成
     *
     * @param name
     * @return
     */
    private boolean checkDownFinish(String name) {
        String path = Environment.getExternalStorageDirectory() + File.separator + name;
        File file = new File(path);
        return file.exists();
    }


}
