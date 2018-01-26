package com.szxb.Thread;

import android.util.Xml;

import com.szxb.Myinterface.BackResult;
import com.szxb.util.Fileflow_Utils;
import com.szxb.util.MyUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadFromSdCard implements Runnable {
    private String path;
    private BackResult result;

    public ReadFromSdCard(String path, BackResult result) {
        this.path = path;
        this.result = result;
        if (!Fileflow_Utils.ifExists(path)) {
            result.getResulte(null);
        }
    }


    //读取SD卡里面的xml的配置参数
    @Override
    public void run() {

        //有序Map
        Map<String, String> map = new HashMap<String, String>();
        String key = null;
        String values = null;
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "utf-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("key")) {
                            eventType = parser.next();
                            key = parser.getText();

                        } else if (parser.getName().equals("value")) {
                            eventType = parser.next();
                            values = parser.getText();
                            map.put(key, values);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
            inputStream.close();
            result.getResulte(map);
        } catch (IOException e) {
            MyUtils.Logd("ReadFromSdCard:" + e.getMessage());
        } catch (XmlPullParserException e) {
            MyUtils.Logd("XmlPullParserException:" + e.getMessage());

        }

    }


}