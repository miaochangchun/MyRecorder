package com.example.sinovoice.view;

/**
 * Created by miaochangchun on 2016/11/17.
 */
public interface ITranslateView {
    void showLoading();
    void hideLoading();
    void toMainActivity(String response);
    void showFailedError(Exception e);
}
