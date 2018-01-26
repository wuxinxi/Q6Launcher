package com.szxb.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * 作者: Tangren on 2017/8/7
 * 包名：com.szxb.utils
 * 邮箱：996489865@qq.com
 * TODO:判断是否是DEBUG模式
 */

public class AppUtil {

    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug == null ? false : isDebug;
    }

    public static void syncISDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0";
        }
    }


    /**
     * @param v1 软件当前版本
     * @param v2 服务器端版本
     * @return .true服务器版本大于当前版本
     */
    public static boolean version(String v1, String v2) {
        String v11 = v1.replace(".", "");
        Log.d("AppUtil",
            "version(AppUtil.java:49)软件当前版本:"+v11);
        String v22 = v2.replace(".", "");
        Log.d("AppUtil",
                "version(AppUtil.java:54)服务器端版本:"+v22);
        return Integer.valueOf(v11) < Integer.valueOf(v22);
    }

}
