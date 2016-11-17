package com.example.sinovoice.presenter;

import android.os.Handler;
import android.util.Log;

import com.example.sinovoice.http.HttpUtil;
import com.example.sinovoice.http.OnHttpCallbackListener;
import com.example.sinovoice.view.ITranslateView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miaochangchun on 2016/11/17.
 */
public class TranslationHelper {
    private static final String TAG = TranslationHelper.class.getSimpleName();
    private ITranslateView translateView;
    private HttpUtil httpUtil;
    private Handler myHandler = new Handler();

    public TranslationHelper(ITranslateView translateView){
        this.translateView = translateView;
        this.httpUtil = new HttpUtil();
    }

    /**
     * 汉语翻译为维语
     * @param text
     */
    public void chineseTrans(String text) {
        translateView.showLoading();
        String url = "http://218.241.146.70:8080/NiuTransServer/translation?from=zh&to=uy";
        httpUtil.sendHttpRequest(url, text, new OnHttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String result = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            result = jsonObject.getString("tgt_text");
                            Log.d(TAG, "result = " + result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        translateView.toMainActivity(result);
                        translateView.hideLoading();
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        translateView.showFailedError(e);
                        translateView.hideLoading();
                    }
                });
            }
        });
    }

    /**
     * 维语翻译为汉语
     * @param text
     */
    public void uyghurTrans(String text) {
        translateView.showLoading();
        String url = "http://218.241.146.70:8080/NiuTransServer/translation?from=uy&to=zh";
        httpUtil.sendHttpRequest(url, text, new OnHttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String result = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            result = jsonObject.getString("tgt_text");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        translateView.toMainActivity(response);
                        translateView.hideLoading();
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        translateView.showFailedError(e);
                        translateView.hideLoading();
                    }
                });
            }
        });
    }
}
