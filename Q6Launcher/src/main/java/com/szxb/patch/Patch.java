package com.szxb.patch;

import android.util.Log;

/**
 * 作者：Tangren on 2017-12-28
 * 包名：com.szxb.patch
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */


final public class Patch {

    static {
        try {
            System.loadLibrary("patch");
            Log.d("Patch",
                    "static initializer(Patch.java:19)加载so成功");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Patch",
                    "static initializer(Patch.java:19)加载so失败" + e.toString());
        }

    }

    public synchronized static native boolean make(String oldFilePath, String newFilePath, String patchPath);

    public synchronized static native boolean diff(String oldFilePath, String newFilePath, String patchPath);
}
