package com.hultron.lifehelper.uitils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class UtilTools {

    public static void setFont(Context context, TextView textView) {
        //设置字体
        Typeface fontType = Typeface.createFromAsset(context.getAssets(), "fonts/FONT.TTF");
        textView.setTypeface(fontType);
    }

    //保存图片到ShareUtils
    public static void putImageToShare(Context context, ImageView imageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        //第一步：将Bitmap压缩成字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        //第二步：利用base64将字节数组输出流转换成String
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageString = new String(Base64.encode(byteArray, Base64.DEFAULT));
        //第三步：将String保存到ShareUtils
        ShareUtil.putString(context, "image_title", imageString);
    }

    //从ShareUtils获取图片
    public static void getImageFromShare(Context context, ImageView imageView) {
        //1.拿到String
        String imgString = ShareUtil.getString(context, "image_title", "");
        if (!imgString.equals("")) {
            //2.利用Base64将字符串转化为字节数组
            byte[] b = Base64.decode(imgString, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
            //3.生成Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
            imageView.setImageBitmap(bitmap);
        }
    }

    //获取版本号
    public static String getVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "未知";
        }
    }
}
