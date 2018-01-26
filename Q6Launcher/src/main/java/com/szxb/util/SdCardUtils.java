package com.szxb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/12.
 */

public class SdCardUtils {

    /**
     * @param txt      需要写入SD的内容
     * @param path     存放目录路径
     * @param fileName 具体的txt文件路径
     */
    public synchronized static void writeToSdCard(String txt, String path, String fileName) {
        try {
            MyUtils.Logd("File path:" + path);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File file1 = new File(path + File.separator + fileName);
            if (!file1.exists()) {
                file1.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file1, false);
            outputStream.write(txt.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //向SD卡读txt文件内容
    public static String ReadFromSdCard(String path) {
        File file = null;
        FileInputStream inputStream = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return "";
            }
            inputStream = new FileInputStream(file);
            //定义缓存区
            byte[] temp = new byte[1024];
            StringBuilder buffer = new StringBuilder("");
            int len = 0;
            while ((len = inputStream.read(temp)) > 0) {
                buffer.append(new String(temp, 0, len));
            }
            inputStream.close();
            return buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //将原先是map的String转回map
    public static Map StringToMap(String str) {
        String str1 = str.replaceAll("\\{|\\}", "");
        str1 = str1.replace(" ", "");
        System.out.println("str1=" + str1);
        String[] st = str1.split(",");
        Map map = new HashMap();
        for (int i = 0; i < st.length; i++) {
            String[] sp = st[i].split("=");
            if (sp.length > 1)
                map.put(sp[0], sp[1]);
            else
                map.put(sp[0], "");
        }
        return map;
    }
}
