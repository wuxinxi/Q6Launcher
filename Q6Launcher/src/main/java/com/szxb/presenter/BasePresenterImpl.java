package com.szxb.presenter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.szxb.model.ConfigModel;
import com.szxb.model.PakageMod;
import com.szxb.model.minterface.IConfig;
import com.szxb.presenter.pinteface.BasePresenter;
import com.szxb.presenter.pinteface.MqttPresenter;
import com.szxb.util.MyUtils;
import com.szxb.util.SdCardUtils;
import com.szxb.view.MyApplication;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.szxb.util.StringUtil.Local_Conifg;
import static com.szxb.util.StringUtil.Local_apk;
import static com.szxb.util.StringUtil.MyConfig;
import static com.szxb.util.StringUtil.apktodownLoad;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/19.
 */

public class BasePresenterImpl implements BasePresenter {

    private MqttPresenter mq;
    private MyApplication mp;
    private volatile List ar;


    private volatile static BasePresenterImpl instance = null;

    private BasePresenterImpl(MyApplication mp) {
        this.mp = mp;
        ar = new ArrayList();
       /* deleteLauncher().*/
        init();
    }

    public static BasePresenterImpl getInstance(MyApplication mp) {
        if (instance == null) {
            synchronized (BasePresenterImpl.class) {
                if (instance == null) {
                    instance = new BasePresenterImpl(mp);
                }
            }
        }
        return instance;
    }


    //读取本地SD卡的文件，判断是否有apk需要更新
    private BasePresenterImpl ifNeedUpdateApk() {
        String str = SdCardUtils.ReadFromSdCard(Local_apk + File.separator + apktodownLoad);
        if (!str.equals("")) {
            MyUtils.Loge("有apk需要更新");
            analysisMsg(str);
        }
        return this;
    }

    //初始化
    private BasePresenterImpl init() {
        mp.getEx().execute(new Runnable() {
            @Override
            public void run() {
                //读取本地txt配置文件参数，判断是否可以连接ActvieMQ后台
                String str = SdCardUtils.ReadFromSdCard(Local_Conifg + File.separator + MyConfig);
                if (!str.equals("")) {
                    Map map = SdCardUtils.StringToMap(str);
                    //写入单例实体类
                    IConfig config = ConfigModel.getInstance();
                    config.setConfig(map);
                    //跳转apk
                    String st = map.get("apkPackage").toString();
                    if (!st.equals("null")) {
                        try {
                            PackageManager pm = mp.getPackageManager();
                            Intent intent = pm.getLaunchIntentForPackage(st);
                            mp.startActivity(intent);
                        } catch (Exception e) {
                            MyUtils.Loge("跳转的apk不存在");
                        }
                    }
                } else {
                    MyUtils.Loge("请插入SD卡初始化配置");
                }
                mq = new MqttPresenterImpl(BasePresenterImpl.this, mp);
                //     ifNeedUpdateApk();
            }
        });
        return this;
    }

    private volatile boolean updateFlag = true;

    @Override
    public void analysisMsg(String msg) {
        Log.d("BasePresenterImpl",
                "analysisMsg(BasePresenterImpl.java:94)" + msg);
        ar.add(msg);
        sendTo();
        // deal.sendMsg(msg);
    }

    //通知处理接收到后台推送过来的消息
    private void sendTo() {
        if (updateFlag && ar.size() > 0) {
            updateFlag = false;
            String str = ar.get(0).toString();
            WrirtoFile(str); //向本地写
        }
    }

    private void WrirtoFile(String txt) {
        File mfile = new File(Local_apk);
        if (!mfile.exists()) {
            mfile.mkdirs();
        }
        SdCardUtils.writeToSdCard(txt, Local_apk, apktodownLoad);
    }

    @Override
    public void connectOrReConnect() {
        mq.connectOrReConnect();
    }

    @Override
    public void RemovetTopMsg() {
        ar.remove(0);
        updateFlag = true;
        MyUtils.Loge("ar4:" + ar.toString() + updateFlag);
        sendTo();

    }

    @Override
    public void UpdateUi() {
        List<PakageMod> ar = mp.getApkUtil().getApkAll();
        if (ar.size() > 0) {
            EventBus.getDefault().postSticky(ar);
        }
    }

    //如果launcher文件存在，删除launcher
   /* private BasePresenterImpl deleteLauncher() {
        mp.getEx().execute(new Runnable() {
            @Override
            public void run() {
                List ar = Fileflow_Utils.getAllFileName(localSaveApkPath + File.separator);
                for (int i = 0; i < ar.size(); i++) {
                    String apkname = ar.get(i).toString();
                    Map m = ApkUtilImpl.apkInfo(localSaveApkPath + File.separator + apkname, mp);

                    String packageName = null;
                    try {
                        packageName = m.get("packageName").toString();
                    } catch (Exception e) {
                        MyUtils.Loge("packageName:" + packageName);
                        packageName = "";
                    }
                    if (packageName.contains("com.szxb.view")) {
                        Fileflow_Utils.fileDelete(localSaveApkPath + File.separator + apkname);
                        break;
                    }
                }
            }
        });
        return this;
    }*/
}
