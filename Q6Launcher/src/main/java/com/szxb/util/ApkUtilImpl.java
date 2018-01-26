package com.szxb.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;

import com.szxb.Myinterface.IApkUtil;
import com.szxb.model.PakageMod;
import com.szxb.view.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/15.
 */

public class ApkUtilImpl implements IApkUtil {

    private MyApplication mp;

    public ApkUtilImpl(MyApplication mp) {
        this.mp = mp;
    }

    @Override
    public boolean apkInstall(String path) {
        try {
            String ret = mp.getmService().apkInstall(path);
            if (ret != null && ret.contains("Success")) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void uninstallApk(String packages) {
        try {
            String ret = mp.getmService().apkUninstall(packages);
            if (!ret.contains("Success")) {
                MyUtils.Logd("卸载失败:" + packages + "\r\n" + "ret:" + ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersionName(String packageName) {
        String code = "";
        try {
            PackageInfo pi = mp.getPackageManager().getPackageInfo(packageName, 0);
            code = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }


    /**
     * 获取apk包的信息：版本号，名称，图标等
     *
     * @param absPath apk包的绝对路径
     * @param context
     */
    public static Map apkInfo(String absPath, Context context) {
        Map m = new HashMap();
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
        /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
            String apkName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
            String packageName = appInfo.packageName; // 得到包名
            String version = pkgInfo.versionName; // 得到版本信息
            Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
            m.put("packageName", packageName);
            m.put("version", version);
            //     m.put("appname", apkName);

        }
        return m;
    }


    @Override
    public List<PakageMod> getApkAll() {
        return loadApps();
    }

    @Override
    public void startOtherApp(String packagename) {
        Intent intent = null;
        try {
            PackageManager pm = mp.getPackageManager();
            intent = pm.getLaunchIntentForPackage(packagename);
            mp.startActivity(intent);
        } catch (Exception e) {
            MyUtils.Loge("startExection:" + e.getMessage() + " 此应用无入口Activity");
        }
    }


    //获取当前第三方app信息
    public List<PakageMod> loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pc = mp.getPackageManager();
        List<PackageInfo> appList = getThirdPartyApp();
        List<PakageMod> mApps = new ArrayList<PakageMod>();
        for (int i = 0; i < appList.size(); i++) {
            PackageInfo pinfo = appList.get(i);
            PakageMod shareItem = new PakageMod();
            // 设置图片
            shareItem.icon = pc.getApplicationIcon(pinfo.applicationInfo);
            // 设置应用程序名字
            shareItem.appName = pc.getApplicationLabel(
                    pinfo.applicationInfo).toString();
            // 设置应用程序的包名
            shareItem.pakageName = pinfo.applicationInfo.packageName;

            mApps.add(shareItem);

        }
        return mApps;
    }

    /**
     * 获取所有已经安装第三方应用的信息集合
     * （包含一个默认的系统设置的信息）
     */
    private List<PackageInfo> getThirdPartyApp() {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = mp.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            String packageName = pak.applicationInfo.packageName;
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加己安装的第三方应用程序
                if (!packageName.equals("com.szxb.view") && !packageName.equals("com.lilei.tool.tool"))
                    apps.add(pak);
            } else if (packageName.equals("com.android.settings")) {
                apps.add(pak);
            }
        }
        return apps;
    }

    @Override
    public Map getNotInsatllInformation(String path, int flag) {
        Map m = new HashMap();
        PackageManager pm = mp.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
        /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = path;
            appInfo.publicSourceDir = path;
            switch (flag) {
                case 0:
                    String packageName = appInfo.packageName; // 得到包名
                    m.put("packageName", packageName);
                    break;
            }
        }
        return m;
    }

    @Override
    public List<Map> getThirdPartyInformation(int flag) {
        List<Map> apps = new ArrayList();
        PackageManager pManager = mp.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            Map map = new HashMap();
            PackageInfo pak = packlist.get(i);
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {

                String packageName = pak.applicationInfo.packageName;
                if (!packageName.equals("com.szxb.view") && !packageName.equals("com.lilei.tool.tool")) {
                    Drawable icon = pManager.getApplicationIcon(pak.applicationInfo);
                    String appName = pManager.getApplicationLabel(pak.applicationInfo).toString();
                    switch (flag) {
                        case 0:
                            map.put("packageName", packageName);
                            break;
                        case 1:
                            map.put("icon", icon);
                            break;
                        case 2:
                            map.put("appName", appName);
                            break;
                        case 3:
                            map.put("packageName", packageName);
                            map.put("icon", icon);
                            map.put("appName", appName);
                            break;
                    }
                    apps.add(map);
                }
            }
        }
        return apps;
    }

}
