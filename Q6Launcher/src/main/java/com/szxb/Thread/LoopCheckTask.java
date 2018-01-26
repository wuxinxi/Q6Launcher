package com.szxb.Thread;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.szxb.sp.FetchAppConfig;
import com.szxb.view.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 作者：Tangren on 2018-01-16
 * 包名：com.szxb.Thread
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class LoopCheckTask {

    private volatile static LoopCheckTask instance = null;
    private ScheduledThreadPoolExecutor service;
    private String url = "";

    private LoopCheckTask() {
        service = new ScheduledThreadPoolExecutor(1);
    }

    public static LoopCheckTask getInstance() {
        if (instance == null) {
            synchronized (LoopCheckTask.class) {
                if (instance == null) {
                    instance = new LoopCheckTask();
                }
            }
        }
        return instance;
    }


    public void startLoop() {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //无网络、晚上11:30停止查询
                if (filter()) {
                    return;
                }
                if (FetchAppConfig.update()) {
                    //如果存在需要更新的,则停止循环
                    return;
                }

                if (!MyApplication.getInstance().isSend()) {
                    return;
                }


                if (FetchAppConfig.exit()) {
                    CRequest.getInstance().check();
                }
            }
        }, 1, 180, TimeUnit.MINUTES);
    }


    private boolean filter() {
        return !checkNetStatus() || checkTime();
    }


    /**
     * 是否有网络
     *
     * @return boolean
     */
    private boolean checkNetStatus() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        return current != null && current.isAvailable();
    }

    private SimpleDateFormat format = new SimpleDateFormat("HHmmss");

    private boolean checkTime() {
        String format = this.format.format(new Date());
        long time = Long.valueOf(format);
        return time > 233000;
    }

}
