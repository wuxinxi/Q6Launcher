package com.szxb.view;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.zhoukai.modemtooltest.ModemToolTest;
import com.example.zhoukai.modemtooltest.NvConstants;
import com.lilei.tool.tool.IToolInterface;
import com.szxb.Myinterface.IApkUtil;
import com.szxb.presenter.BasePresenterImpl;
import com.szxb.presenter.pinteface.BasePresenter;
import com.szxb.service.PushReceiver;
import com.szxb.util.ApkUtilImpl;
import com.szxb.util.MyUtils;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by 斩断三千烦恼丝 on 2017/9/8.
 */

public class MyApplication extends Application {

    //****线程管理*****
    private ExecutorService ex = Executors.newFixedThreadPool(3);
    //当前机器的SN号
    private String snCode;

    private IApkUtil iApkUtil;

    private BasePresenter bp;

    private boolean updateTime = false;

    private boolean send = false;

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(boolean updateTime) {
        this.updateTime = updateTime;
    }

    //  private static MyApplication mp;

    @Override
    public void onCreate() {
        super.onCreate();
        //mp = this;
        instance = this;
        init(getApplicationContext());
        initService();

        Intent pushReceiver = new Intent(this, PushReceiver.class);
        startService(pushReceiver);

//        LoopCheckTask.getInstance().startLoop();

    }

    private volatile static MyApplication instance = null;

    public static MyApplication getInstance() {
        return instance;
    }


    public BasePresenter getBasePresenter() {
        if (bp == null) {
//            bp = new BasePresenterImpl(MyApplication.this);
            bp = BasePresenterImpl.getInstance(MyApplication.this);
        }
        bp.UpdateUi();
        return bp;
    }

    public BasePresenter getBp() {
        return bp;
    }

    public void setBp(BasePresenter bp) {
        this.bp = bp;
    }

    /*  //获取MyApplication对象的单例
    public static MyApplication getInstance() {
        return mp;
    }*/

    private void init(Context context) {
        //初始化NoHttp请求
        InitializationConfig config = InitializationConfig.newBuilder(context)
                .retry(5)
                .build();
        NoHttp.initialize(config);
        Logger.setDebug(true);
        snCode = ModemToolTest.getItem(NvConstants.REQUEST_GET_SN);
        iApkUtil = new ApkUtilImpl(this);
    }


    //获取机器的SN号
    public String getSN() {
        return snCode;
    }

    //返回线程池对象
    public ExecutorService getEx() {
        return ex;
    }


    //返回操作apk,system的更新卸载操作类
    public IApkUtil getApkUtil() {

        return iApkUtil;
    }

    //连接服务
    private void initService() {
        Intent i = new Intent();
        i.setAction("com.lypeer.aidl");
        i.setPackage("com.lilei.tool.tool");
        boolean ret = bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    //断开服务
    private void releaseService() {
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }

    //服务操作
    private IToolInterface mService;

    public IToolInterface getmService() {

        return mService;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            MyUtils.Logd("mservice is null");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub

            mService = IToolInterface.Stub.asInterface(service);
            MyUtils.Logd("mservice is serviceconneceted:" + mService);

        }
    };


    //程序终止的时候执行
    @Override
    public void onTerminate() {
        super.onTerminate();
        MyUtils.Loge("onTerminate:" + "程序退出");
    }
}
