package com.hultron.lifehelper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

/**
 * 事件分发
 */

public class DispatchLinearLayout extends LinearLayout {

    private DispatchKeyEventListener mDispatchKeyEventListener;


    /*
    * 三个构造方法
    * */
    public DispatchLinearLayout(Context context) {
        super(context);
    }

    public DispatchLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DispatchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /*
    * 暴露一个方法给调用者用来注册接口的回调，通过接口来获得回调者对接口方法的实现
    * */
    public void setDispatchKeyEventListener(DispatchKeyEventListener dispatchKeyEventListener) {
        mDispatchKeyEventListener = dispatchKeyEventListener;
    }

    //接口
    public interface DispatchKeyEventListener {
        boolean dispatchKeyEvent(KeyEvent event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //如果不为空，说明调用了 去获取事件
        if (mDispatchKeyEventListener != null) {
            return mDispatchKeyEventListener.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }
}