package com.example.sinovoice;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sinovoice.hcicloudsdk.android.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;
import com.sinovoice.hcicloudsdk.recorder.RecorderEvent;

/**
 * Created by miaochangchun on 2016/10/25.
 */
public class HciCloudAsrHelper {
    private static final String TAG = HciCloudAsrHelper.class.getSimpleName();
    private static HciCloudAsrHelper mHciCloudAsrHelper = null;
    private ASRRecorder mASRRecorder;
    private Handler myHander;
    private String voicePath;   //音频文件保存路径

    public void setMyHander(Handler myHander) {
        this.myHander = myHander;
    }

    public Handler getMyHander() {
        return myHander;
    }

    private HciCloudAsrHelper() {
    }

    public static HciCloudAsrHelper getInstance(){
        if (mHciCloudAsrHelper == null) {
            return  new HciCloudAsrHelper();
        }
        return mHciCloudAsrHelper;
    }

    /**
     * 录音机初始化
     * @param context   上下文
     * @param initCapkeys   初始化的capkey，可以同时更新多个，中间以分号间隔
     */
    public void initAsrRecorder(Context context, String initCapkeys) {
        mASRRecorder = new ASRRecorder();
        String strConfig = getAsrInitParam(context, initCapkeys);
        mASRRecorder.init(strConfig, new ASRRecorderCallback());
    }

    /**
     * 获取初始化时的参数配置
     * @param context   上下文
     * @param initCapkeys   初始化的capkey，可以同时更新多个，中间以分号间隔
     * @return
     */
    private String getAsrInitParam(Context context, String initCapkeys) {
        AsrInitParam asrInitParam = new AsrInitParam();
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, initCapkeys);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, "android_so");
        String dataPath = context.getFilesDir().getAbsolutePath().replace("files", "lib");
        //云端功能可以不设置dataPath
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        return asrInitParam.getStringConfig();
    }

    /**
     * 开启语音识别功能，只有录音机状态为Idle的时候，才可以使用录音机
     * @param capkey    识别时的capkey，只能配置一个，如使用多个需要进行切换
     */
    public void startAsrRecorder(String capkey) {
        if (mASRRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_IDLE) {
            String strConfig = getAsrConfigParam(capkey);
            mASRRecorder.start(strConfig, null);
        }
    }

    /**
     * 结束录音并开始识别，只有正在录音的时候，才可以停止录音并识别
     */
    public void stopAsrRecorder(){
        if (mASRRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_RECORDING) {
            mASRRecorder.stopAndRecog();
        }
    }

    /**
     * 获取asr识别时的配置参数
     * @param capkey    识别时的capkey，只能配置一个，如使用多个需要进行切换
     * @return
     */
    private String getAsrConfigParam(String capkey) {
        AsrConfig asrConfig = new AsrConfig();
        asrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
        //录音的压缩格式，建议使用speex进行压缩
        asrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE, "speex");
        asrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
        asrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, "yes");
        asrConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_ADD_PUNC, "yes");
        return asrConfig.getStringConfig();
    }

    /**
     * 录音机release接口
     */
    public void releaseAsrRecorder() {
        if (mASRRecorder != null) {
            mASRRecorder.release();
        }
    }

    /**
     * ASR录音机回调类
     */
    private class ASRRecorderCallback implements ASRRecorderListener{
        String result = "";
        @Override
        public void onRecorderEventStateChange(RecorderEvent recorderEvent) {
            String state = "状态为：初始状态";
            if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECORD) {
                state = "状态为：开始录音";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECOGNIZE) {
                state = "状态为：开始识别";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_NO_VOICE_INPUT) {
                state = "状态为：无音频输入";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_HAVING_VOICE) {
                state = "状态为：录音中";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_END_RECORD) {
                state = "状态为：录音结束";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_COMPLETE) {
                state = "状态为：识别结束";
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_VOICE_BUFFER_FULL) {
                state = "状态为：缓冲满";
            }

            //把录音机状态传递到Activity上
            Message message = new Message();
            message.arg1 = 3;
            Bundle bundle = new Bundle();
            bundle.putString("state", state);
            message.setData(bundle);
            myHander.sendMessage(message);
        }

        @Override
        public void onRecorderEventRecogFinsh(RecorderEvent recorderEvent, AsrRecogResult asrRecogResult) {
            if (asrRecogResult != null) {
                if (asrRecogResult.getRecogItemList().size() > 0) {
                    //识别结果
                    result = asrRecogResult.getRecogItemList().get(0).getRecogResult();
                    //置信度
                    int score = asrRecogResult.getRecogItemList().get(0).getScore();

                    //把识别结果传递到Activity上
                    Message message = new Message();
                    message.arg1 = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("result", "识别结果：" + result + "\t" + "置信度：" + score);
                    message.setData(bundle);
                    myHander.sendMessage(message);
                }
            }
        }

        @Override
        public void onRecorderEventRecogProcess(RecorderEvent recorderEvent, AsrRecogResult asrRecogResult) {

        }

        @Override
        public void onRecorderEventError(RecorderEvent recorderEvent, int i) {
            String error = "错误码为：" + i;

//            //把错误信息传递到Activity上
//            Message message = new Message();
//            message.arg1 = 2;
//            Bundle bundle = new Bundle();
//            bundle.putString("error", error);
//            message.setData(bundle);
//            myHander.sendMessage(message);
        }

        @Override
        public void onRecorderRecording(byte[] bytes, int i) {
//            File file = new File(voicePath);
//            if (!file.exists()) {
//                file.getParentFile().mkdirs();
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                FileOutputStream outputStream = new FileOutputStream(file);
//                outputStream.write(bytes);
//                outputStream.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}