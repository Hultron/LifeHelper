package com.hultron.lifehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.NewsData;

import java.util.List;

/**
 * 时事新闻适配器
 */

public class NewsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<NewsData> mNewsDataList;
    private NewsData mNewsData;

    public NewsAdapter(Context context, List<NewsData> newsDataList) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        mNewsDataList = newsDataList;
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.news_item, parent, false);
            holder.mTxtTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.mTxtTime = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mNewsData = mNewsDataList.get(position);
        holder.mTxtTitle.setText(mNewsData.getTitle());
        holder.mTxtTime.setText(mNewsData.getCtime());

        return convertView;
    }

    private class ViewHolder {
        private TextView mTxtTitle;
        private TextView mTxtTime;
    }
}
