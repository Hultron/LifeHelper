package com.hultron.lifehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

<<<<<<< HEAD:app/src/main/java/com/hultron/lifehelper/adapter/GirlAdapter.java
import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.GirlData;
import com.hultron.lifehelper.entity.PicassoUtils;
=======
import com.hultron.lifehelper.entity.GirlData;
import com.hultron.lifehelper.R;
>>>>>>> secondweather:app/src/main/java/com/hultron/lifehelper/adapter/GirlAdapter.java

import java.util.List;


/**
 * 妹子Adapter
 */

public class GirlAdapter extends BaseAdapter {

    private Context mContext;
    private List<GirlData> mGirlDataList;
    private LayoutInflater mInflater;
    private GirlData mData;
    private WindowManager wm;
    //屏幕宽度
    private int width;

    public GirlAdapter(Context context, List<GirlData> list) {
        mContext = context;
        mGirlDataList = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
    }

    @Override
    public int getCount() {
        return mGirlDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGirlDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.left_item, parent, false);
            holder.girlImage = (ImageView) convertView.findViewById(R.id.girl_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mData = mGirlDataList.get(position);
        //解析图片
        String url = mData.getImgUrl();
        //PicassoUtils.loadImageViewSize(mContext, url, width/2, 250, holder.girlImage);
        return convertView;
    }

    private class ViewHolder {
        private ImageView girlImage;
    }
}
