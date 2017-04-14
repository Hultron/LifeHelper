package com.hultron.lifehelper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.adapter.NewsAdapter;
import com.hultron.lifehelper.entity.NewsData;
import com.hultron.lifehelper.ui.NewsContentActivity;
import com.hultron.lifehelper.uitils.LogUtil;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NewsFragment extends Fragment {

    private ListView mNewsList;
    private List<NewsData> mNewsDataList = new ArrayList<>();
    //新闻标题容器
    private List<String> mListTitle = new ArrayList<>();
    //新闻地址容器
    private List<String> mListUrl = new ArrayList<>();

    //下拉刷新
    private SwipeRefreshLayout swipeRefreshNews;

    private NewsAdapter mNewsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        findView(view);

        return view;
    }

    //初始化View
    private void findView(View view) {
        mNewsList = (ListView) view.findViewById(R.id.list_news);
        swipeRefreshNews = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_news);
        swipeRefreshNews.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });

        requestNews();
        mNewsAdapter = new NewsAdapter(getActivity(), mNewsDataList);
        mNewsList.setAdapter(mNewsAdapter);
        mNewsAdapter.notifyDataSetChanged();

        //为listview设置点击事件
        mNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NewsContentActivity.class);
                //Intent两种传值方式bundle和intent
                intent.putExtra("title", mListTitle.get(position));
                intent.putExtra("url", mListUrl.get(position));
                startActivity(intent);
            }
        });
    }

    //解析接口
    private void requestNews() {
        String newsUrl = "https://api.tianapi" +
                ".com/world/?key=f518734caa0bcf19f8ee1b4c4ede2b65&num=40";
        RxVolley.get(newsUrl, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                LogUtil.e(t);
                parseJson(t);
            }
        });
    }

    private void refreshNews() {
        requestNews();
        mNewsAdapter.notifyDataSetChanged();
        swipeRefreshNews.setRefreshing(false);
        Toast.makeText(getContext(), "获取最近新闻成功！", Toast.LENGTH_SHORT).show();
    }

    private void parseJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            JSONArray jsonResult = jsonObject.getJSONArray("newslist");
            for (int i = 0; i < jsonResult.length(); i++) {
                JSONObject json = (JSONObject) jsonResult.get(i);
                NewsData data = new NewsData();
                //title
                String title = json.getString("title");
                data.setTitle(title);
                mListTitle.add(title);//将标题存入容器
                //url
                String url = json.getString("url");
                data.setUrl(url);
                mListUrl.add(url);//将地址存入容器
                //time
                String ctime = json.getString("ctime");
                data.setCtime(ctime);
                mNewsDataList.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
