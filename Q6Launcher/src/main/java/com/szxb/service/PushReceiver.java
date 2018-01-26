package com.szxb.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.szxb.presenter.pinteface.BasePresenter;
import com.szxb.view.MyApplication;

/**
 * 作者：Tangren on 2018-01-25
 * 包名：com.szxb.service
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class PushReceiver extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BasePresenter basePresenter = MyApplication.getInstance().getBasePresenter();
        MyApplication.getInstance().setBp(basePresenter);

    }
}
