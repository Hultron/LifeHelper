package com.hultron.lifehelper.uitils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Picasso封装
 */

public class PicassoUtils {
    //默认加载图片
    public static void loadImageView(Context context, String url, ImageView imageView) {
        Picasso.with(context)
                .load(url)
                .into(imageView);
    }

    //默认加载图片（指定大小）
    public static void loadImageViewSize(Context context, String url, int width, int height,
                                         ImageView imageView) {
        Picasso.with(context)
                .load(url)
                .resize(width, height)
                .centerCrop()
                .into(imageView);
    }

    //加载图片有默认图片
    public static void loadImageViewHolder(Context context, String url, int loadImage,
                                           int errImage, ImageView imageView) {
        Picasso.with(context)
                .load(url)
                .placeholder(loadImage)
                .error(errImage)
                .into(imageView);
    }

    //裁剪图片
    public static void loadImageViewCrop(Context context, String url, ImageView
            imageView) {
        Picasso.with(context)
                .load(url)
                .transform(new CropSquareTransformation())
                .into(imageView);
    }

    // 按比例裁剪，矩形
    public static class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);//正方形
            if (result != source) {
                //回收
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "square()";
        }
    }
}
