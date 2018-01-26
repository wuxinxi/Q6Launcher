package com.szxb.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/5.
 * 文件操作读写类
 */

public class Fileflow_Utils {

    /**
     * 获取指定文件下所有apk的名字
     *
     * @param path 文件夹路径
     * @return 返回apk名字的集合
     */
    public static List getAllFileName(String path) {
        List ar = new ArrayList();
        File mfile = new File(path);
        if (!mfile.exists()) {
            MyUtils.Loge("the path is not exists");
        } else {
            File[] mf = mfile.listFiles();
            for (int i = 0; i < mf.length; i++) {
                String apkname = mf[i].getName();
                if (apkname.contains(".apk")) {
                    ar.add(apkname);
                }
            }
        }
        return ar;
    }

    public synchronized static boolean ifExists(String path) {
        File mfile = new File(path);
        if (mfile.exists()) {
            return true;
        }
        return false;
    }

    public synchronized static boolean ifExitsUninstall(String var) {
        File mfile = new File(var);
        if (mfile.exists()) {
            return true;
        }
        return false;
    }


    //删除指定文件
    public synchronized static void fileDelete(String path) {
        File mfile = new File(path);
        if (mfile.exists()) {
            mfile.delete();
        }
    }

}
