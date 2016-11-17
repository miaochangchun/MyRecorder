package com.example.sinovoice.http;

/**
 * Created by miaochangchun on 2016/11/17.
 */
public interface OnHttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
