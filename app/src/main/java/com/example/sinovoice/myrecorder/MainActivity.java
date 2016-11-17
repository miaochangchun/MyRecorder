package com.example.sinovoice.myrecorder;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sinovoice.HciCloudAsrHelper;
import com.example.sinovoice.HciCloudSysHelper;
import com.example.sinovoice.presenter.TranslationHelper;
import com.example.sinovoice.view.ITranslateView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, ITranslateView{

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText result;
    private Button cnBtn;
    private Button uyBtn;
    private HciCloudSysHelper mHciCloudSysHelper;
    private HciCloudAsrHelper mHciCloudAsrHelper;
    private TextView state;
    private String initCapkeys = "asr.cloud.freetalk;asr.cloud.freetalk.uyghur";
    private ProgressBar pBar;
    private TranslationHelper translationHelper = new TranslationHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (EditText) findViewById(R.id.result);
        cnBtn = (Button) findViewById(R.id.cnRecorder);
        uyBtn = (Button) findViewById(R.id.uyRecorder);
        state = (TextView) findViewById(R.id.stateView);

        pBar = (ProgressBar) findViewById(R.id.pBar);

        cnBtn.setOnTouchListener(this);
        uyBtn.setOnTouchListener(this);

        mHciCloudSysHelper = HciCloudSysHelper.getInstance();
        mHciCloudAsrHelper = HciCloudAsrHelper.getInstance();
        mHciCloudSysHelper.init(this);
        mHciCloudAsrHelper.initAsrRecorder(this, initCapkeys);
        mHciCloudAsrHelper.setMyHander(new MyHander());
    }

    @Override
    protected void onDestroy() {
        mHciCloudAsrHelper.releaseAsrRecorder();
        mHciCloudSysHelper.release();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.cnRecorder:
                function(event, "asr.cloud.freetalk");
                break;
            case R.id.uyRecorder:
                function(event, "asr.cloud.freetalk.uyghur");
                break;
            default:
                break;
        }
        //返回结果为true，不会响应click事件
        return true;
    }

    /**
     * 对按钮事件进行判断并做出对应的响应
     * @param event
     * @param capkey
     */
    private void function(MotionEvent event, String capkey) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "MotionEvent.ACTION_DOWN");
                mHciCloudAsrHelper.startAsrRecorder(capkey);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "MotionEvent.ACTION_UP");
                mHciCloudAsrHelper.stopAsrRecorder();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "MotionEvent.ACTION_CANCEL");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "MotionEvent.ACTION_MOVE");
            default:
                break;
        }
    }

    @Override
    public void showLoading() {
        pBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        pBar.setVisibility(View.GONE);
    }

    @Override
    public void toMainActivity(String response) {

    }

    @Override
    public void showFailedError(Exception e) {

    }

    private class MyHander extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1){
                case 1:
                    Bundle bundle = msg.getData();
                    String resultString = bundle.getString("result");
                    result.setText(resultString);
                    break;
                case 3:
                    Bundle bundle2 = msg.getData();
                    String stateResult = bundle2.getString("state");
                    state.setText(stateResult);
                    break;
                default:
                    break;
            }
        }
    }
}
