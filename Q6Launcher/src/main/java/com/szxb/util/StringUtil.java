package com.szxb.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by 斩断三千烦恼丝 on 2017/7/17.
 */

public final class StringUtil {


    public static final String slash = File.separator;
    //Log的Tag
    public static String TAGS = "MyTag";


    //-------------------FTP上传下载相关状态-----------------
    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    public static final String local_DELETEFILE_SUCCESS = "本地文件删除成功";
    public static final String local_DELETEFILE_FAIL = "本地文件删除失败";

    public static final String Dowload_Process = "下载进度";
    public static final String Ftp_CloseConnection_Success = "ftp连接关闭成功";
    public static final String Ftp_CloseConnection_Failed = "ftp连接关闭异常";

    //-----------------后端向前台推送的指令----------------------------
    public static final String Apk_InstallTAG = "1";
    public static final String Apk_UninstallTAG = "2";
    public static final String System_Update = "3";
    public static final String Parm_Send = "4";
    public static final String PATCH = "5";
    public static final String GetMackey = "6";

    public static final String NoNeedToupdate = "无需更新此机器操作";

    //本地Sd卡默认目录
    public final static String Sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();

    //本地默认存放下载的apk的地址
    public static String localSaveApkPath = Sdpath + "/download/apk";

    //本地默认存放下载的system的地址
    public static final String localSaveSystemPath = "/download/system";

    //本地默认存放待下载的apk名字的txt文件地址
    public static final String apktodownLoad = "apktodownLoad.txt";
    public static final String Local_apk = Sdpath + "/apktodownLoad";
    /**
     * 本地存放config配置文件的地址
     */

    public static final String MyConfig = "myconfig.txt";
    public final static String Local_Conifg = Sdpath + "/myconfig";

    //外置SD卡存放activemq等配置参数的xml文件地址
    public final static String Config_xml = "/myconfig/myconfig.xml";
    public final static String Operate_xml = "/myconfig/operate.xml";
    public static final String CombinationOperation_xml = "/combinationoperation.xml";

    /**
     * config的xml文件对应的参数
     */
    //---------------------- MQTT相关配置名称------------------------
    public static final String mqttUri = "mqttUri";

    //------------------ftp相关配置--------------------------
    public static final String[] FtpConfig = new String[]{"ftpHost", "ftpPort", "ftpUName", "ftpPwd"};
    public static final String ftpHost = "ftpHost";
    public static final String ftpPort = "ftpPort";
    public static final String ftpUName = "ftpUName";
    public static final String ftpPwd = "ftpPwd";

    //wifi相关配置
    public static final String wifiName = "wifiName";
    public static final String wifiPwd = "wifiPwd";

    //需要监控的apk的包名
    public static final String apkPackage = "apkPackage";

    //开机更新或者立即更新(0 开机更新， 1 立即更新)
    public static final String ifBootUpdate = "ifBootUpdate";

    //是否卸载所有第三方apk
    public static final String ifUninstallAllThirdApk = "ifUninstallAllThirdApk";


    //-----------javaweb后台的请求相关------------
    //修改apk更新安装状态的参数
    public static final String AddressForUploadApkVersion = "http://139.199.158.253/bipeqt/interaction/updateRes";
    public static final String Apk_downloadSuccess = "0";  //下载成功
    public static final String Apk_InstallSuccess = "1";    //安装成功
    public static final String Apk_InstallFailed = "2";     //安装失败
    public static final String Apk_downloadFailed = "3";    //下载失败或者还未下载
    public static final String Post_StateFailed = "Post_StateFailed"; //发送apk下载，更新的状态到后台失败
    public static final String Post_StateSuccess = "Post_StateSuccess";//发送apk下载，更新的状态到后台成功

    /*---------SharedPreferences的键值对管理-------*/
    public static final String putEntityToShare_Success = "0";// 存放实体类对象成功
    public static final String putEntityToShare_Failed = "1";// 存放实体类对象失败

}
