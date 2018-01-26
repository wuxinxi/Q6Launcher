package com.szxb.http;


import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;


public class HttpResponseListener<T> implements OnResponseListener<T> {

    private Request<T> request;

    private HttpListener<T> httpListener;

    /**
     * @param request  请求对象
     * @param callback 回调对象
     */
    public HttpResponseListener(final Request<T> request, HttpListener<T> callback) {
        this.httpListener = callback;
        this.httpListener = callback;
    }

    public void onFinish(int arg0) {

    }

    public void onStart(int arg0) {
    }


    public void onSucceed(int what, Response<T> response) {
        int responseCode = response.getHeaders().getResponseCode();
        System.out.println("responseCode:" + responseCode + "");
        if (responseCode > 400) {
            if (responseCode == 405) {
                httpListener.fail(what, "服务器暂不支持该类型");
            }
        }

        if (httpListener != null) {
            httpListener.success(what, response);
        }
    }

    @Override
    public void onFailed(int what, Response<T> response) {
        if (httpListener != null)
            httpListener.fail(what, response.getException().getMessage());

    }


}
