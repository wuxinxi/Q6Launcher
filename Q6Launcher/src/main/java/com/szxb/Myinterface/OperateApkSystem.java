package com.szxb.Myinterface;

/**
 * Created by 斩断三千烦恼丝 on 2017/7/13.
 */

public interface OperateApkSystem {
    boolean installApk(String installpath);

    boolean uninstallApk(String unintallBypackage);

    boolean updateSystem(String installpath);

    boolean apklist();
}
