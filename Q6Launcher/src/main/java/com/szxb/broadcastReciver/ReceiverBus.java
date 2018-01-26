package com.szxb.broadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * 作者：Tangren on 2018-01-17
 * 包名：com.szxb.broadcastReciver
 * 邮箱：996489865@qq.com
 * TODO:接收车载bus信息
 */

public class ReceiverBus extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (TextUtils.equals(intent.getAction(), "com.szxb.receiverbus")) {

                String str = intent.getStringExtra("info");
                Log.d("ReceiverBus",
                        "onReceive(ReceiverBus.java:20)接收得到的信息;" + str);

//                JSONObject object = new JSONObject(str);
//                String mch_id = object.getString("mch_id");
//                String type = object.getString("charge_type");
//                String packages = object.getString("packages");
//                String version = object.getString("version");
//
//                CommonSharedPreferences.put("mch_id", mch_id);
//                CommonSharedPreferences.put("charge_type", type);
//                CommonSharedPreferences.put("bus_app_ver", version);
//                CommonSharedPreferences.put("pack_ages", packages);
//                CommonSharedPreferences.put("exit", true);

//                MyApplication.getInstance().setSend(true);


            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ReceiverBus",
                    "onReceive(ReceiverBus.java:42)" + e.toString());
        }
    }
}
