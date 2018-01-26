package com.szxb.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.szxb.Thread.RequestTime;
import com.szxb.adapter.MyAdapter;
import com.szxb.model.PakageMod;
import com.szxb.presenter.pinteface.BasePresenter;
import com.szxb.sp.CommonSharedPreferences;
import com.szxb.sp.FetchAppConfig;
import com.szxb.util.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;


public class MainActivity extends Activity {


    @BindView(R.id.show_app)
    GridView showApp;
    @BindView(R.id.show_sn)
    TextView showSn;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.app_version)
    TextView appVersion;

    private MyAdapter adapter;
    private BasePresenter bp;
    private MyApplication mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getFullScreen();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        mp = (MyApplication) getApplication();
        EventBus.getDefault().register(this);
        showSn.setText(mp.getSN());
        bp = MyApplication.getInstance().getBp();
        appVersion.setText(mp.getApkUtil().getVersionName(getPackageName()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity",
                        "run(MainActivity.java:77)" + FetchAppConfig.update());
                check();
            }
        }, 7000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!MyApplication.getInstance().isUpdateTime()) {
                    RequestTime time = new RequestTime();
                    time.request();
                }
            }
        }, 7000);
    }

    private void check() {
        try {
            if (FetchAppConfig.update()) {
                //需要更新
                Log.d("MainActivity",
                        "check(MainActivity.java:77)马上更新");
                Log.d("MainActivity",
                        "check(MainActivity.java:99)" + FetchAppConfig.updateType());
                if (FetchAppConfig.updateType() == 1) {
                    //更新launcher
                    Log.d("MainActivity",
                            "check(MainActivity.java:82)更新launcher");
                    String path = FetchAppConfig.apkpkPathLauncher();
                    if (!TextUtils.equals(path, "0")) {
                        MyApplication.getInstance().getmService().apkInstall(path);
                        MyApplication.getInstance().getApkUtil().startOtherApp(this.getPackageName());
                        CommonSharedPreferences.put("update", false);
                    }

                } else if (FetchAppConfig.updateType() == 2) {
                    //更新车载项目
                    Log.d("MainActivity",
                            "check(MainActivity.java:94)更新车载项目");
                    String path = FetchAppConfig.apkpkPath();
                    String packages = FetchAppConfig.packages();
                    if (!TextUtils.equals(path, "0") && !TextUtils.equals(packages, "0")) {
                        MyApplication.getInstance().getmService().apkInstall(path);
                        MyApplication.getInstance().getApkUtil().startOtherApp(packages);
                        CommonSharedPreferences.put("update", false);
                    }

                } else if (FetchAppConfig.updateType() == 3) {
                    Log.d("MainActivity",
                            "check(MainActivity.java:125)推送更新");
                    String path = FetchAppConfig.apkpkPath();
                    String packages = FetchAppConfig.packages();
                    Log.d("MainActivity",
                            "check(MainActivity.java:129)" + path);
                    Log.d("MainActivity",
                            "check(MainActivity.java:129)" + packages);
                    if (!TextUtils.equals(path, "0") && !TextUtils.equals(packages, "0")) {
                        MyApplication.getInstance().getmService().apkInstall(path);
                        Log.d("MainActivity",
                                "check(MainActivity.java:131)启动：" + packages);
                        MyApplication.getInstance().getApkUtil().startOtherApp(packages);
                        CommonSharedPreferences.put("update", false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActivity",
                    "check(MainActivity.java:109)" + e.toString());
            CommonSharedPreferences.put("apk_path", "0");
            CommonSharedPreferences.put("update", false);
        }

    }

    // 全屏透明状态栏和导航栏
    @SuppressLint("NewApi")
    private MainActivity getFullScreen() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        return this;
    }


    //刷新显示图标名字的Listview
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateListview(List<PakageMod> ar) {
        if (adapter == null) {
            adapter = new MyAdapter(MainActivity.this, ar);
            showApp.setAdapter(adapter);
        } else {
            adapter.updateData(ar);
        }
    }

    //收到配置修改的通知，重置mqtt连接的地址
    @Subscribe
    public void getNotification(Boolean flag) {
        MyUtils.Logd("收到回调修改连接配置");
        if (flag)
            bp.connectOrReConnect();
    }

    //收到mqtt连接通知，重置连接状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ResetMqttConnect(String msg) {
        if (msg.equals("true")) {

            img.setImageResource(R.mipmap.link);
        } else {
            img.setImageResource(R.mipmap.unlink);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("if come into ondestory");
        EventBus.getDefault().unregister(this);

    }

    //预留点击跳转apk的事件
    @OnItemClick(R.id.show_app)
    void OnItemClick(View v) {
        TextView tv = (TextView) v.findViewById(R.id.mytvs);
        mp.getApkUtil().startOtherApp(tv.getTag().toString());
    }

    //长按事件
    @OnItemLongClick(R.id.show_app)
    boolean onLongClick(View view, int position, long id) {

        return true;
    }

}
