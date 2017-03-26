package com.hultron.smartbulter.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.hultron.smartbulter.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * QrCodeShareActivity
 * 生成二维码
 */

public class QrCodeShareActivity extends BaseActivity {

    //我的二维码
    private ImageView mQrCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrshare);

        initView();
    }

    private void initView() {

        mQrCode = (ImageView) findViewById(R.id.qr_code_container);

        //获取屏幕宽度
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        //二维码大小
        int qrCodeSize = Math.min(width, height);

        //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
        Bitmap qrCodeBitmap = EncodingUtils.createQRCode("我是智能管家", qrCodeSize, qrCodeSize,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        mQrCode.setImageBitmap(qrCodeBitmap);
    }
}
