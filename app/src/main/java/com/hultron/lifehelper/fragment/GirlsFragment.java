package com.hultron.lifehelper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.adapter.GirlAdapter;
import com.hultron.lifehelper.application.BaseApplication;
import com.hultron.lifehelper.entity.GirlData;
import com.hultron.lifehelper.uitils.L;
import com.hultron.lifehelper.uitils.PicassoUtils;
import com.hultron.lifehelper.uitils.StaticClass;
import com.hultron.lifehelper.view.CustomDialog;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


public class GirlsFragment extends Fragment {
    private GridView mGridViewGirl;
    private List<GirlData> mGirlDataList = new ArrayList<>();
    GirlAdapter mGirlAdapter;

    /*
    * 1.监听点击事件
    * 2.提示框
    * 3.加载图片
    * 4.PhotoView
    * */

    //提示框
    private CustomDialog mCustomDialog;
    //预览图片
    private ImageView mForseeImage;
    //图片地址数据
    private List<String> mUrlList = new ArrayList<>();
    //photoview
    private PhotoViewAttacher mAttacher;
    //下拉刷新
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_girls, container, false);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_girls);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGirls();
            }
        });
        findView(view);
        return view;
    }

    //初始化View
    private void findView(View view) {
        mGridViewGirl = (GridView) view.findViewById(R.id.grid_girl);

        //解析
        RxVolley.get(StaticClass.GANK_GIRL_URL, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                L.i(t);
                parseJson(t);
                mGirlAdapter = new GirlAdapter(BaseApplication.getContext(), mGirlDataList);
                mGridViewGirl.setAdapter(mGirlAdapter);//设置适配器
            }
        });

        mCustomDialog = new CustomDialog(getActivity(),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                R.layout.dialog_girl, R.style.Theme_dialog, Gravity.CENTER);
        mCustomDialog.setCancelable(true);
        mForseeImage = (ImageView) mCustomDialog.findViewById(R.id.dialog_im);

        //监听点击事件
        mGridViewGirl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //加载图片
                PicassoUtils.loadImageView(getActivity(), mUrlList.get(position), mForseeImage);
                //缩放
                mAttacher = new PhotoViewAttacher(mForseeImage);
                //刷新
                mAttacher.update();
                mCustomDialog.show();

            }
        });

    }

    private void parseJson(String t) {
        if (mGirlDataList.size() != 0) {
            mGirlDataList.clear();
            mUrlList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(t);
            JSONArray result = jsonObject.getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                GirlData girl = new GirlData();
                JSONObject json = (JSONObject) result.get(i);
                String url = json.getString("url");
                if (url == null) {
                    continue;
                }
                girl.setImgUrl(url);
                mUrlList.add(url);
                mGirlDataList.add(girl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //刷新美女图片
    private int i = 1;
    private void refreshGirls() {
        i = i + 1;
        if (i == 10) {
            i = 1;
        }
        String gankUrl = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/50/" + i;
        RxVolley.get(gankUrl, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                L.i(t);
                Toast.makeText(getContext(), "刷新成功！老司机，保重身体啊！", Toast.LENGTH_SHORT).show();
                parseJson(t);
                mGirlAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
