package com.szxb.presenter.pinteface;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/19.
 */

public interface BasePresenter {

    //解析推送过来的消息
    void analysisMsg(String msg);

    //mqtt的连接与重置连接地址
    void connectOrReConnect();

    //接到一条消息结果处理完毕,移除最先收到的消息
    void RemovetTopMsg();

    //通知刷新MainActivtiy的ui
    void UpdateUi();
}
