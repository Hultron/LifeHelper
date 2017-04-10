package com.hultron.lifehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.CourierData;

import java.util.List;

/*
 * 快递查询适配器
 */

public class CourierAdapter extends BaseAdapter {

    private Context mContext;
    private List<CourierData> mList;
    private CourierData mData;
    //布局加载器
    private LayoutInflater mInflater;

    public CourierAdapter(Context context, List<CourierData> list) {
        mContext = context;
        mList = list;
        //获取系统服务
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //第一次加载
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.courier_item, parent, false);
            holder.mRemark = (TextView) convertView.findViewById(R.id.remark);
            holder.mZone = (TextView) convertView.findViewById(R.id.zone);
            holder.mDatetime = (TextView) convertView.findViewById(R.id.datetime);
            //设置缓存
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置数据
        mData = mList.get(position);
        holder.mRemark.setText(mData.getRemark());
        holder.mZone.setText(mData.getZone());
        holder.mDatetime.setText(mData.getDatetime());
        return convertView;
    }

    private class ViewHolder {
        private TextView mRemark;
        private TextView mZone;
        private TextView mDatetime;

    }
}
