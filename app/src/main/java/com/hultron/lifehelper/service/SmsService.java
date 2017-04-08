package com.hultron.lifehelper.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.uitils.LogUtil;
import com.hultron.lifehelper.uitils.StaticClass;
import com.hultron.lifehelper.view.DispatchLinearLayout;

/**
 * 短信监听
 */

public class SmsService extends Service {

    private static final String SYSTEM_DIALOGS_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOGS_HOME_KEY = "homekey";

    private SmsReceiver mSmsReceiver;
    //发件人号码
    private String smsPhone;
    //短信内容
    private String smsContent;
    //窗口管理器
    private WindowManager wm;
    //布局参数
    private WindowManager.LayoutParams layoutparams;
    //View
    private DispatchLinearLayout mView;

    private TextView mTvPhone;
    private TextView mTvContent;
    private Button mSendSms;

    //Home键监听广播
    private HomeWatchReceiver mHomeWatchReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        LogUtil.i("init service");
        /*
        * 动态注册广播
        * */
        mSmsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //添加Action
        intentFilter.addAction(StaticClass.SMS_ACTION);
        //设置优先级，谷歌优先级最大值为1000，这里我设置为int的最大值
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册
        registerReceiver(mSmsReceiver, intentFilter);

        mHomeWatchReceiver = new HomeWatchReceiver();
        IntentFilter intentFilter1 = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeWatchReceiver, intentFilter1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("stop service");
        //注销广播
        unregisterReceiver(mSmsReceiver);
        unregisterReceiver(mHomeWatchReceiver);
    }

    //短信广播
    public class SmsReceiver extends BroadcastReceiver implements View.OnClickListener {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (StaticClass.SMS_ACTION.equals(action)) {
                LogUtil.i("来短信了");
                //获取短信内容，返回的是一个Object数组
                Object[] objs = (Object[]) intent.getExtras().get("pdus");
                //遍历数组得到相关数据
                for (Object obj : objs) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                    //发件人
                    smsPhone = sms.getOriginatingAddress();
                    smsContent = sms.getMessageBody();
                    LogUtil.i("短信内容：" + smsPhone + smsContent);
                    showWindow();
                }
            }
        }

        //窗口提示
        private void showWindow() {
            //获取系统服务
            wm = (WindowManager) getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE);

            //获取布局参数
            layoutparams = new WindowManager.LayoutParams();
            //定义宽高
            layoutparams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutparams.height = WindowManager.LayoutParams.MATCH_PARENT;
            //定义标记
            layoutparams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            //定义格式
            layoutparams.format = PixelFormat.TRANSLUCENT;
            //定义类型,之前这行代码漏掉了，耽误了一天，以后写代码一定要仔细，慎重，不能图快，不然
            //会付出更多的时间成本
            layoutparams.type = WindowManager.LayoutParams.TYPE_PHONE;
            //加载布局
            mView = (DispatchLinearLayout) View.inflate(getApplicationContext(),
                    R.layout.sms_item, null);
            //mView控件初始化
            mTvPhone = (TextView) mView.findViewById(R.id.tv_phone);
            mTvContent = (TextView) mView.findViewById(R.id.tv_content);
            mSendSms = (Button) mView.findViewById(R.id.btn_send_sms);
            mSendSms.setOnClickListener(this);

            //控件绑定数据
            mTvPhone.setText(String.format("发件人： %s", smsPhone));
            mTvContent.setText(smsContent);

            //添加View到窗口
            wm.addView(mView, layoutparams);

            mView.setDispatchKeyEventListener(mDispatchKeyEventListener);
        }

        private DispatchLinearLayout.DispatchKeyEventListener mDispatchKeyEventListener
                = new DispatchLinearLayout.DispatchKeyEventListener() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                //判断是否是按返回键
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    LogUtil.i("按下back");
                    if (mView.getParent() != null) {
                        wm.removeView(mView);
                    } else {
                        return true;
                    }
                }
                return false;
            }
        };

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_send_sms:
                    sendSms();
                    //关闭窗口
                    if (mView.getParent() != null) {
                        wm.removeView(mView);
                    }
                    break;
            }
        }
    }

    //回复短信
    private void sendSms() {
        Uri uri = Uri.parse("smsto:" + smsPhone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        //设置启动模式
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }


    //监听Home键的广播
    class HomeWatchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOGS_REASON_KEY);
                if (reason.equals(SYSTEM_DIALOGS_HOME_KEY)) {
                    LogUtil.i("点击Home");
                    if (mView.getParent() != null) {
                        wm.removeView(mView);
                    }
                }
            }
        }
    }
}
