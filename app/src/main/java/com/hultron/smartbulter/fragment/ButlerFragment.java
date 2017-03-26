package com.hultron.smartbulter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hultron.smartbulter.R;
import com.hultron.smartbulter.adapter.ChatListAdapter;
import com.hultron.smartbulter.entity.ChatListData;
import com.hultron.smartbulter.uitils.L;
import com.hultron.smartbulter.uitils.ShareUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ButlerFragment extends Fragment implements View.OnClickListener {
    private ListView mChatListView;

    private List<ChatListData> mList = new ArrayList<>();
    private ChatListAdapter adapter;

    private SpeechSynthesizer mTts;

    //输入框
    private EditText mEdtChat;
    //发送按钮
    private Button mBtnSend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_butler, container, false);
        findView(view);
        return view;
    }

    private void findView(View v) {

        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(getActivity(), null);
        /*
        * 语音合成
        * */
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "Jarvis");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端


        mChatListView = (ListView) v.findViewById(R.id.chat_list_view);
        mEdtChat = (EditText) v.findViewById(R.id.edt_chat);
        mBtnSend = (Button) v.findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);

        //设置适配器
        adapter = new ChatListAdapter(getActivity(), mList);
        mChatListView.setAdapter(adapter);

        addLeftItem("你好，我是贾维斯！");
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_send:
                /*
                * 逻辑：
                * 1.获取输入框的内容
                * 2.判断是否为空
                * 3.判断长度不能大于30字节
                * 4.清空输入框
                * 5.添加输入的内容到right_item
                * 6.发送给机器人请求返回内容
                * 7.拿到机器人的返回值，添加到left_item
                * */
                //1.获取输入框内容
                String mChatContent = mEdtChat.getText().toString();
                //2.判断是否为空
                if (!TextUtils.isEmpty(mChatContent)) {
                    //3.判断长度不能大于30字节
                    if (mChatContent.length() > 30) {
                        Toast.makeText(getActivity(), "输入长度超出限制", Toast.LENGTH_SHORT).show();
                    } else {
                        //4.清空输入框
                        mEdtChat.setText("");
                        //5.添加你的输入内容到right_item
                        addRightItem(mChatContent);
                        //6.发送给机器人请求返回内容
                        String mUrlChat = "http://api.qingyunke.com/api" +
                                ".php?key=free&appid=0&msg=" + mChatContent;
                        RxVolley.get(mUrlChat, new HttpCallback() {
                            @Override
                            public void onSuccess(String t) {
                                L.i(t);
                                parseJson(t);
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "输入框不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    private void parseJson(String t) {
        try {
            JSONObject jsonResult = new JSONObject(t);
            //拿到返回值
            String content = jsonResult.getString("content");
            //7.将返回值添加到leftitem
            addLeftItem(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //添加左边文本
    private void addLeftItem(String text) {
        boolean isSpeak = ShareUtils.getBoolean(getActivity(), "isSpeak", false);
        if (isSpeak) {
            startSpeaking(text);
        }
        ChatListData data = new ChatListData();
        data.setType(ChatListAdapter.VALUE_LEFT_TEXT);
        data.setText(text);
        mList.add(data);
        //通知adapter刷新
        adapter.notifyDataSetChanged();
        //滚动到底部
        mChatListView.setSelection(mChatListView.getBottom());
    }

    //添加右边文本
    private void addRightItem(String text) {
        ChatListData data = new ChatListData();
        data.setType(ChatListAdapter.VALUE_RIGHT_TEXT);
        data.setText(text);
        mList.add(data);
        //通知adapter刷新
        adapter.notifyDataSetChanged();
        //滚动到底部
        mChatListView.setSelection(mChatListView.getBottom());
    }

    //开始说话
    private void startSpeaking(String text) {
        //3.开始合成
        mTts.startSpeaking(text, mSynListener);
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，
        // info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

    };
}