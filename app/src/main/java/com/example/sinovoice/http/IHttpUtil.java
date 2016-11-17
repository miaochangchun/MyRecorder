package com.example.sinovoice.http;

/**
 * Created by miaochangchun on 2016/11/17.
 */
public interface IHttpUtil {
    void sendHttpRequest(String url, String text, OnHttpCallbackListener listener);
}
