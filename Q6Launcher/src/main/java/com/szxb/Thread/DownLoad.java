package com.szxb.Thread;

import android.content.Context;

import com.szxb.Myinterface.GetResultInterface;
import com.szxb.util.ApkUtilImpl;
import com.szxb.util.Fileflow_Utils;
import com.szxb.util.MyFtpUtils;
import com.szxb.util.MyUtils;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.szxb.util.StringUtil.Dowload_Process;
import static com.szxb.util.StringUtil.FTP_DOWN_SUCCESS;
import static com.szxb.util.StringUtil.FTP_FILE_NOTEXISTS;
import static com.szxb.util.StringUtil.Local_apk;
import static com.szxb.util.StringUtil.apktodownLoad;
import static com.szxb.util.StringUtil.localSaveApkPath;

public class DownLoad extends Thread {
    private MyFtpUtils ftpUtils;
    private String apkname, merNo;
    private Context con;
    private GetResultInterface gets;

    public DownLoad(Context con, String apkname, String merNo, GetResultInterface gets) {
        this.gets = gets;
        this.con = con;
        ftpUtils = new MyFtpUtils();
        this.apkname = apkname;
        this.merNo = merNo;
        MyUtils.Logd("apkname =:" + apkname);
    }

    @Override
    public void run() {
        super.run();
        MyUtils.Loge("come to download apk");
        downLoadApk();
    }


    private boolean flag = false;

    void downLoadAagin() {
        if (!flag) {
            mtimer = null;
            myTask = null;
            mtimer = new Timer();
            myTask = new MyTask();
            //30s判断一次是否有网络
            mtimer.schedule(myTask, 30 * 1000);
        }
    }

    private Timer mtimer;
    private MyTask myTask;

    class MyTask extends TimerTask {
        @Override
        public void run() {
            boolean bl = MyUtils.getNetWorkState(con);
            MyUtils.Loge("bl:" + bl);
            if (bl) {
                downLoadApk();
            }
        }
    }

    private void downLoadApk() {
        try {
            ftpUtils.downLoadSingeFile("/bus/" + merNo + "/" + apkname, localSaveApkPath, new MyFtpUtils.getFtpDownLoad() {
                @Override
                public void getInformation(String result) {
                    if (result.equals(FTP_DOWN_SUCCESS)) {
                        flag = true;
                        Map mp = ApkUtilImpl.apkInfo(localSaveApkPath + File.separator + apkname, con);
                        String version = mp.get("version").toString();

                        Fileflow_Utils.fileDelete(Local_apk + File.separator + apktodownLoad);
                        gets.backResult(apkname, version);
                    } else {
                        if (result.contains(FTP_FILE_NOTEXISTS)) {
                            gets.FtpFileNotExists(true);
                        }
                        MyUtils.Logd("getInformation: " + result);
                    }
                }

                @Override
                public void getProcess(String process) {
                    MyUtils.Logd(Dowload_Process + process + "%");
                }
            });
        } catch (Exception e) {
            MyUtils.Logd("downLoadApk execption:" + e.getMessage());
            if (!flag) {
                downLoadAagin();
            }
        }
    }

}
