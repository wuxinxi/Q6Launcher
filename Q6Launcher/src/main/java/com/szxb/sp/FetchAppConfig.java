package com.szxb.sp;


/**
 * TODO:获取全局的SP数据
 */

public class FetchAppConfig {


    public static String ftpIp() {
        return (String) CommonSharedPreferences.get("ftpIp", "123.207.244.244");
    }


    public static Integer ftpPort() {
        return (Integer) CommonSharedPreferences.get("ftpPort", 21);
    }

    //文件路径
    public static String ftpPath() {
        return (String) CommonSharedPreferences.get("ftpPath", "czbustemp/");
    }

    // 用户名
    public static String ftpUser() {
        return (String) CommonSharedPreferences.get("ftpUser", "ftpuser");
    }

    //密码
    public static String ftpPassWord() {
        return (String) CommonSharedPreferences.get("ftpPassWord", "QW!@123qwe");
    }

    //商户号
    public static String mchId() {
        return (String) CommonSharedPreferences.get("mch_id", "0");
    }

    //launcher版本
    public static String launcherVersion() {
        return (String) CommonSharedPreferences.get("launcher_ver", "1.0");
    }

    //车载程序版本
    public static String busAppVersion() {
        return (String) CommonSharedPreferences.get("bus_app_ver", "1.0");
    }

    //上次更新时间
    public static String lastTime() {
        return (String) CommonSharedPreferences.get("last_time", "0");
    }

    //计费类型
    public static String chargeType() {
        return (String) CommonSharedPreferences.get("charge_type", "0");
    }

    //文件路径
    public static String filePathName() {
        return (String) CommonSharedPreferences.get("file_path_name", "0");
    }

    //旧apk路径
    public static String oldApkPath() {
        return (String) CommonSharedPreferences.get("old_apk_path", "0");
    }


    //车载包名
    public static String packages() {
        return (String) CommonSharedPreferences.get("pack_ages", "0");
    }


    //是否需要更新
    public static boolean update() {
        return (boolean) CommonSharedPreferences.get("update", false);
    }

    //是否下载完成
    public static boolean download() {
        return (boolean) CommonSharedPreferences.get("download_success", false);
    }

    //apk路径
    public static String apkpkPath() {
        return (String) CommonSharedPreferences.get("apk_path", "0");
    }

    //apk路径 launcher
    public static String apkpkPathLauncher() {
        return (String) CommonSharedPreferences.get("apk_path_launcher", "0");
    }

    //apk路径
    public static int updateType() {
        return (int) CommonSharedPreferences.get("update_type", 0);
    }


    //参数信息是否已收到
    public static boolean exit() {
        return (boolean) CommonSharedPreferences.get("exit", false);
    }


}