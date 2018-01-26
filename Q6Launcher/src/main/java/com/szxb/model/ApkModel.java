package com.szxb.model;

import com.szxb.model.minterface.IApk;

import java.util.Map;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/8.
 */

public class ApkModel implements IApk {


    private Map map;

    private ApkModel() {
    }

    public static ApkModel getInstance() {
        return ExampleInstance.instance;
    }

    @Override
    public Map getApkStatus() {
        return this.map;
    }

    @Override
    public void setApkStatus(Map map) {
        this.map =null;
        this.map = map;
    }

    @Override
    public String getApkStatus(String str) {
        return map.get(str).toString();
    }

    static class ExampleInstance {
        static final ApkModel instance = new ApkModel();
    }

/*    public ApkModel(String appname, String version, String status, String sendFlag) {
        this.appname = appname;
        this.version = version;
        this.status = status;
        this.sendFlag = sendFlag;
    }*/



}
