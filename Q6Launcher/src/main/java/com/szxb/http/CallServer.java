package com.szxb.http;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;


public class CallServer {

    private static CallServer httpclient;

    private RequestQueue requestQueuequeue;

    private DownloadQueue downloadQueue;

    private CallServer() {
        requestQueuequeue = NoHttp.newRequestQueue();
    }

    public synchronized static CallServer getHttpclient() {
        if (httpclient == null)
            httpclient = new CallServer();
        return httpclient;
    }

    public <T> void add(int what, Request<T> request, HttpListener<T> callback) {
        requestQueuequeue.add(what, request, new HttpResponseListener<T>
                (request, callback));
    }

    public void cancelAll() {
        requestQueuequeue.cancelAll();
    }

    public void stopAll() {
        requestQueuequeue.stop();
    }

    public void calcelDown() {
        downloadQueue.cancelAll();
    }


}
