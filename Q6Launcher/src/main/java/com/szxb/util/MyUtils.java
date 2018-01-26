package com.szxb.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;

/**
 * Created by 斩断三千烦恼丝 on 2017/8/31.
 */

public class MyUtils {

    public static void Logd(String str) {
        Log.d("TAG", str + "");
    }

    public static void Loge(String str) {
        Log.e("TAG", str + "");
    }

    public static void Print(String str) {
        System.out.println(str + "");
    }


    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }


    public static boolean getNetWorkState(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager
                .getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            return true;
           /* //获取网络类型
            int netWorkType =mNetworkInfo.getType();
            if(netWorkType==ConnectivityManager.TYPE_WIFI){
                return "当前网络是WIFI";
            }else if(netWorkType==ConnectivityManager.TYPE_MOBILE){
                return "当前网络是3G";
            }else{
                return "其它方式";
            }*/

        } else {
            return false;
        }

    }


}
