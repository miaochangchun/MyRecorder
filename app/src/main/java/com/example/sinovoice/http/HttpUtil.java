package com.example.sinovoice.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by miaochangchun on 2016/11/17.
 */
public class HttpUtil implements IHttpUtil{
    @Override
    public void sendHttpRequest(final String url, final String text, final OnHttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new HttpClient();
                PostMethod method = new UTF8PostMethod(url);
                method.setParameter("src_text", text);
                try {
                    int statusCode = client.executeMethod(method);
                    if (statusCode == 200) {
                        if (listener != null) {
                            String str = method.getResponseBodyAsString();
                            listener.onFinish(str);
                        }
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
}
