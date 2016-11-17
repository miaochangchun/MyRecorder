package com.example.sinovoice.http;

import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by miaochangchun on 2016/11/17.
 */
public class UTF8PostMethod extends PostMethod {
    public UTF8PostMethod(String url){
        super(url);
    }

    public String getRequestCharSet(){
        return "UTF-8";
    }
}
