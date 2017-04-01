package com.hultron.lifehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.NewsData;
import com.hultron.lifehelper.entity.PicassoUtils;


import java.util.List;

/**
 * 时事新闻适配器
 */

public class NewsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<NewsData> mNewsDataList;
    private NewsData mNewsData;
    private int width, height;
    private WindowManager wm;

    public NewsAdapter(Context context, List<NewsData> newsDataList) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        mNewsDataList = newsDataList;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }

    @Override
    public int getCount() {
        return mNewsDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsDataList.get(position);
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
            convertView = mLayoutInflater.inflate(R.layout.news_item, parent, false);
            holder.mImgNewsImage = (ImageView) convertView.findViewById(R.id.iv_news_image);
            holder.mTxtTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.mTxtSrc = (TextView) convertView.findViewById(R.id.tv_src);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mNewsData = mNewsDataList.get(position);
        holder.mTxtTitle.setText(mNewsData.getTitle());
        holder.mTxtSrc.setText(mNewsData.getSrc());
        //加载图片
        PicassoUtils.loadImageViewSize(mContext, mNewsData.getImgUrl(), width / 3, 200, holder
                .mImgNewsImage);
        return convertView;
    }

    private class ViewHolder {
        private ImageView mImgNewsImage;
        private TextView mTxtTitle;
        private TextView mTxtSrc;
    }
}
