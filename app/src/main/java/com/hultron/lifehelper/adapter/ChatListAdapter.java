package com.hultron.lifehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.ChatListData;
import java.util.List;

/**
 * 对话Adapter
 */

public class ChatListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ChatListData> mChatList;
    private ChatListData data;


    //左边的type
    public static final int VALUE_LEFT_TEXT = 1;
    //右边的type
    public static final int VALUE_RIGHT_TEXT = 2;

    public ChatListAdapter(Context context, List<ChatListData> list) {
        mContext = context;
        mChatList = list;
        //获取系统服务
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mChatList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeftViewHolder leftViewHolder = null;
        RightViewHolder rightViewHolder = null;
        //获取当前要显示的type，根据这个type来区分数据的加载
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case VALUE_LEFT_TEXT:
                    leftViewHolder = new LeftViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.left_item, null);
                    leftViewHolder.leftText = (TextView) convertView.findViewById(R.id.left_text);
                    convertView.setTag(leftViewHolder);
                    break;
                case VALUE_RIGHT_TEXT:
                    rightViewHolder = new RightViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.right_item, null);
                    rightViewHolder.rightText = (TextView) convertView.findViewById(R.id
                            .right_text);
                    convertView.setTag(rightViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case VALUE_LEFT_TEXT:
                    leftViewHolder = (LeftViewHolder) convertView.getTag();
                    break;
                case VALUE_RIGHT_TEXT:
                    rightViewHolder = (RightViewHolder) convertView.getTag();
                    break;
            }
        }

        //赋值
        data = mChatList.get(position);
        switch (type) {
            case VALUE_LEFT_TEXT:
                leftViewHolder.leftText.setText(data.getText());
                break;
            case VALUE_RIGHT_TEXT:
                rightViewHolder.rightText.setText(data.getText());
                break;
        }
        return convertView;
    }

    //根据数据源的position返回要显示的item
    @Override
    public int getItemViewType(int position) {
        ChatListData data = mChatList.get(position);
        int type = data.getType();
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;//mList.size()+1;
    }

    //左侧文本
    class LeftViewHolder {
        private TextView leftText;
    }

    //右侧文本
    class RightViewHolder {
        private TextView rightText;
    }
}
