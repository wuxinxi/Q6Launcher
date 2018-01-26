package com.szxb.Myinterface;

import com.szxb.model.PakageMod;

import java.util.List;
import java.util.Map;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/15.
 */

public interface IApkUtil {


    /**
     * 注释： 获取指定目录的应用的信息
     *
     * @param path :存放应用的地址  flag 0:获取包名
     * @return
     */
    Map getNotInsatllInformation(String path, int flag);


    /**
     * 注释：获取所有已安装的第三方应用的信息
     *
     * @param flag 0 :获取应用包名 1；获取应用图标  2：获取应用名字  3：获取应用包名，图标，名字
     */
    List<Map> getThirdPartyInformation(int flag);


    /**
     * @param path 需要安装的apk的路径
     */
    boolean apkInstall(String path);


    /**
     * @param packages 根据apk包名卸载
     */
    void uninstallApk(String packages);


    /**
     * 获取已安装apk的版本号
     *
     * @param packageName apk的包名
     */
    String getVersionName(String packageName);


    /**
     * 获取所有第三方apk的包名，图标，程序的名字的集合
     */
    List<PakageMod> getApkAll();

    /**
     * 跳转其他的apk
     *
     * @param packageName :跳转apk的包名
     */
    void startOtherApp(String packageName);
}
