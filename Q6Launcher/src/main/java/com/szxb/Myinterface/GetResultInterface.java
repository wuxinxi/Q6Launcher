package com.szxb.Myinterface;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/2.
 */

public interface GetResultInterface {
    void backResult(String appname, String version);

    //当flag为true，表示ftp上的文件不存在
    void FtpFileNotExists(boolean flag);
}
