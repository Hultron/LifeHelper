package com.hultron.lifehelper.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hultron.lifehelper.MainActivity;
import com.hultron.lifehelper.R;
import com.hultron.lifehelper.uitils.L;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GuideActivity";

    private ViewPager mViewPager;
    //容器
    private List<View> mViewList = new ArrayList<>();
    private View mView1, mView2, mView3;
    //小圆点
    private ImageView mPointOne, mPointTwo, mPointThree;
    //跳过
    private ImageView mBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.guide_view_pager);
        mPointOne = (ImageView) findViewById(R.id.point_one);
        mPointTwo = (ImageView) findViewById(R.id.point_two);
        mPointThree = (ImageView) findViewById(R.id.point_three);
        mBack = (ImageView) findViewById(R.id.img_back);
        mBack.setOnClickListener(this);

        //设置默认
        setPointImg(true, false, false);

        mView1 = View.inflate(this, R.layout.page_item_one, null);
        mView2 = View.inflate(this, R.layout.page_item_two, null);
        mView3 = View.inflate(this, R.layout.page_item_three, null);

        mView3.findViewById(R.id.btn_start).setOnClickListener(this);

        mViewList.add(mView1);
        mViewList.add(mView2);
        mViewList.add(mView3);

        //设置适配器
        mViewPager.setAdapter(new GuideAdapter());

        //监听 ViewPager 的滑动
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                L.i("position: " + position);
                switch (position) {
                    case 0:
                        setPointImg(true, false, false);
                        mBack.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        setPointImg(false, true, false);
                        mBack.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        setPointImg(false, false, true);
                        mBack.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
            case R.id.img_back:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }


    //设置小圆点的选中效果
    private void setPointImg(boolean isChecked1, boolean isChecked2, boolean isChecked3) {
        if (isChecked1) {
            mPointOne.setBackgroundResource(R.drawable.point_on);
        } else {
            mPointOne.setBackgroundResource(R.drawable.point_off);
        }

        if (isChecked2) {
            mPointTwo.setBackgroundResource(R.drawable.point_on);
        } else {
            mPointTwo.setBackgroundResource(R.drawable.point_off);
        }

        if (isChecked3) {
            mPointThree.setBackgroundResource(R.drawable.point_on);
        } else {
            mPointThree.setBackgroundResource(R.drawable.point_off);
        }
    }
}
