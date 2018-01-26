package com.szxb.util;


import com.szxb.model.ConfigModel;
import com.szxb.model.minterface.IConfig;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.szxb.util.StringUtil.Dowload_Process;
import static com.szxb.util.StringUtil.FTP_CONNECT_FAIL;
import static com.szxb.util.StringUtil.FTP_CONNECT_SUCCESSS;
import static com.szxb.util.StringUtil.FTP_DOWN_FAIL;
import static com.szxb.util.StringUtil.FTP_DOWN_SUCCESS;
import static com.szxb.util.StringUtil.FTP_FILE_NOTEXISTS;
import static com.szxb.util.StringUtil.Ftp_CloseConnection_Failed;
import static com.szxb.util.StringUtil.Ftp_CloseConnection_Success;
import static com.szxb.util.StringUtil.ftpHost;
import static com.szxb.util.StringUtil.ftpPort;
import static com.szxb.util.StringUtil.ftpPwd;
import static com.szxb.util.StringUtil.ftpUName;
import static com.szxb.util.StringUtil.local_DELETEFILE_SUCCESS;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/1.
 */

public class MyFtpUtils {

    private FTPClient ftpClient;
    private IConfig config;


    public MyFtpUtils() {
        ftpClient = null;
        ftpClient = new FTPClient();
        config = ConfigModel.getInstance();
    }

    //Ftp登陆连接
    public void openConnect() throws IOException {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.disconnect();
            System.out.println(" ftp 连接已存在");
            return;
        }
        ftpClient.setDataTimeout(20000);//设置连接超时时间
        ftpClient.setControlEncoding("utf-8");
        ftpClient.connect(config.getConfig(ftpHost), Integer.parseInt(config.getConfig(ftpPort)));
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (ftpClient.login(config.getConfig(ftpUName), config.getConfig(ftpPwd))) {
                System.out.println("ftp连接成功");
                FTPClientConfig config = new FTPClientConfig(ftpClient
                        .getSystemType().split(" ")[0]);
                config.setServerLanguageCode("zh");
                ftpClient.configure(config);
                // 使用被动模式设为默认
                ftpClient.enterLocalPassiveMode();
                // 二进制文件支持
                ftpClient
                        .setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
            }
        }

    }

    //Ftp断开连接
    private void closeConnect() throws IOException {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    private String TAG = "TAG";

    /**
     * @param serverPath 服务器存放文件地址
     * @param localPath  本地存放文件地址
     * @param gfd        回调接口
     * @throws Exception 异常抛出
     */

    public void downLoadSingeFile(String serverPath, String localPath, getFtpDownLoad gfd) throws Exception {
        MyUtils.Loge("serverPath:"+serverPath);
        try {
            openConnect();
            gfd.getInformation(FTP_CONNECT_SUCCESSS);
        } catch (IOException e) {
            gfd.getInformation(FTP_CONNECT_FAIL);
            e.printStackTrace();
        }


        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            gfd.getInformation(FTP_FILE_NOTEXISTS + serverPath);
            return;
        }

        File lcfile = new File(localPath);
        if (!lcfile.exists()) {
            lcfile.mkdirs();
        }

        localPath = localPath + File.separator + files[0].getName();
        gfd.getInformation("localPath:" + localPath);
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
        gfd.getInformation("serverFileSize:" + serverSize);
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            gfd.getInformation("localFileSize:" + localSize);
            if (localSize >= serverSize) {
                File file = new File(localPath);
                file.delete();
                localSize = 0;
                gfd.getInformation(local_DELETEFILE_SUCCESS);
            }
        }
        //    gfd.getInformation("CurrentlocalPath:" + localPath);
        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = 0;
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        byte[] b = new byte[1024 * 10];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 1 == 0) {
                    gfd.getProcess(Dowload_Process + process);
                }
            }
        }
        out.flush();
        out.close();
        input.close();
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            gfd.getInformation(FTP_DOWN_SUCCESS);
        } else {
            gfd.getInformation(FTP_DOWN_FAIL);
        }

        try {
            closeConnect();
            gfd.getInformation(Ftp_CloseConnection_Success);
        } catch (IOException e) {
            gfd.getInformation(Ftp_CloseConnection_Failed);
            e.printStackTrace();
        }
    }

    public interface getFtpDownLoad {
        void getInformation(String result);

        void getProcess(String process);
    }
}
