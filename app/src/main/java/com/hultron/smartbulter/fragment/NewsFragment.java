package com.hultron.smartbulter.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hultron.smartbulter.R;
import com.hultron.smartbulter.adapter.NewsAdapter;
import com.hultron.smartbulter.entity.NewsData;
import com.hultron.smartbulter.ui.NewsContentActivity;
import com.hultron.smartbulter.uitils.L;
import com.hultron.smartbulter.uitils.StaticClass;
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

        //解析接口
        String newsUrl = "http://api.avatardata" + ".cn/ActNews/Query?key=" +
                StaticClass.LATELY_NEWS_KEY + "&keyword=美国";
        RxVolley.get(newsUrl, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                L.e(t);
                parseJson(t);
            }
        });

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

    private void parseJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            JSONArray jsonResult = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonResult.length(); i++) {
                JSONObject json = (JSONObject) jsonResult.get(i);
                NewsData data = new NewsData();
                if (json.getString("img").equals("")) {
                    continue;
                }
                data.setImgUrl(json.getString("img"));
                String title = json.getString("title");
                data.setTitle(title);
                mListTitle.add(title);//将标题存入容器
                data.setSrc(json.getString("src"));
                String newsUrl = json.getString("url");
                data.setNewsUrl(newsUrl);
                mListUrl.add(newsUrl);//将地址存入容器
                mNewsDataList.add(data);
            }
            NewsAdapter adapter = new NewsAdapter(getActivity(), mNewsDataList);
            mNewsList.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
