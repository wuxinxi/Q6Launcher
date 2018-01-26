package com.szxb.Thread;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.szxb.http.CallServer;
import com.szxb.http.HttpListener;
import com.szxb.http.JsonRequest;
import com.szxb.view.MyApplication;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者：Tangren on 2018-01-22
 * 包名：com.szxb.Thread
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class RequestTime {

    private String url = "http://139.199.158.253/bipeqt/interaction/getStandardTime";

    public void request() {
        JsonRequest request = new JsonRequest(url, RequestMethod.POST);
        request.setRetryCount(5);
        CallServer.getHttpclient().add(0, request, new HttpListener<JSONObject>() {
            @Override
            public void success(int what, Response<JSONObject> response) {
                try {
                    JSONObject object = response.get();
                    String rescode = object.getString("rescode");
                    if (TextUtils.equals("0000", rescode)) {
                        String time = object.getString("date");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = format.parse(time);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.get(Calendar.YEAR);
                        calendar.get(Calendar.MONTH);
                        calendar.get(Calendar.DATE);
                        calendar.get(Calendar.HOUR);
                        calendar.get(Calendar.MINUTE);
                        calendar.get(Calendar.SECOND);

                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int min = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);

                        Log.d("RequestTime",
                                "success(RequestTime.java:60)" + year + "-" + month + "-" + day + "-" + hour + "-" + min + "-" + second);

                        MyApplication.getInstance().setUpdateTime(true);
                        MyApplication.getInstance().getmService().setDateTime(year, month, day, hour, min);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Log.d("RequestTime",
                            "success(RequestTime.java:71)" + e.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d("RequestTime",
                            "success(RequestTime.java:74)" + e.toString());
                }
            }

            @Override
            public void fail(int what, String e) {
                Log.d("RequestTime",
                        "fail(RequestTime.java:81)" + e.toString());
            }
        });
    }
}
