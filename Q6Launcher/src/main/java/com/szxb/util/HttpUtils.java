package com.szxb.util;

import android.util.Log;
import android.widget.Toast;

import com.szxb.http.CallServer;
import com.szxb.http.HttpListener;
import com.szxb.http.JsonRequest;
import com.szxb.view.MyApplication;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by 斩断三千烦恼丝 on 2017/8/9.
 */

public class HttpUtils {
    private static class LazyHoler {
        static final HttpUtils instance = new HttpUtils();
    }

    public static HttpUtils getInstance() {
        return LazyHoler.instance;
    }

    private RequestQueue q;

    public void SendPost(Map<String, Object> map, String url) {
        JsonRequest request = new JsonRequest(url);
        request.add(map);
        CallServer.getHttpclient().add(0, request, new HttpListener<JSONObject>() {
            @Override
            public void success(int what, Response<JSONObject> response) {
                JSONObject js = null;
                try {
                    js = new JSONObject(response.get().toString());
                    if (js.get("result").toString().equals("success") && js.get("rescode").toString().equals("0000")) {
                        MainLooper.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyApplication.getInstance(), "发送状态成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("HttpUtils",
                                "success(HttpUtils.java:46)发送状态成功");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("HttpUtils",
                            "success(HttpUtils.java:51)" + e.toString());
                }

            }

            @Override
            public void fail(int what, String e) {
                Log.d("HttpUtils",
                        "fail(HttpUtils.java:59)" + e.toString());
            }
        });

    }


   /* public void httpPost(String url, String denvo, final String appname, String version, final String status, final GetResultInterface get) {
        if (q == null) {
            q = NoHttp.newRequestQueue();
        }
        Request<String> re = NoHttp.createStringRequest(url, RequestMethod.POST);
        re.add("devno", denvo);
        re.add("appname", appname);
        re.add("version", version);
        re.add("status", status);
        q.add(0, re, new SimpleResponseListener<String>() {

            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject js = new JSONObject(response.get().toString());
                    Log.e("TAG", "onSucceed: " + response.get());
                    if (js.getString("result").equals("0000") && js.getString("rescode").equals("success")) {
                        get.backResult("success");
                    } else {
                        get.backResult("failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                Log.e("TAG", "onFailed: " + response.get());
                get.backResult("failed");
            }

        });

    }*/

    public void disconnect() {
        if (q != null) {
            q.cancelAll();
        }
    }


    //ping百度来判断当前是否真的有网
    public final boolean ping() {

        String result = null;

        try {

            String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~

            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);//ping3次


            // 读取ping的内容，可不加。

            InputStream input = p.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(input));

            StringBuffer stringBuffer = new StringBuffer();

            String content = "";

            while ((content = in.readLine()) != null) {

                stringBuffer.append(content);

            }

            Log.i("TTT", "result content : " + stringBuffer.toString());


            // PING的状态

            int status = p.waitFor();

            if (status == 0) {

                result = "successful~";

                return true;

            } else {

                result = "failed~ cannot reach the IP address";

            }

        } catch (IOException e) {

            result = "failed~ IOException";

        } catch (InterruptedException e) {

            result = "failed~ InterruptedException";

        } finally {

            Log.i("TTT", "result = " + result);

        }

        return false;

    }

}
