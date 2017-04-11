package com.hultron.lifehelper.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.adapter.CourierAdapter;
import com.hultron.lifehelper.entity.CourierData;
import com.hultron.lifehelper.uitils.StaticClass;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 快递查询
 */

public class CourierActivity extends BaseActivity implements View.OnClickListener {

    private EditText mCourierCom, mCourierNo;
    private ListView mCourierList;
    Button mGetCourier;
    private List<CourierData> mCourierDataList = new ArrayList<>();
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);

        initView();
    }

    private void initView() {
        mCourierCom = (EditText) findViewById(R.id.courier_com);
        mCourierCom.setOnClickListener(this);
        mCourierNo = (EditText) findViewById(R.id.courier_no);
        mCourierNo.setOnClickListener(this);
        mGetCourier = (Button) findViewById(R.id.get_courier);
        mGetCourier.setOnClickListener(this);
        mCourierList = (ListView) findViewById(R.id.courier_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.courier_com:
                break;
            case R.id.courier_no:
                break;
            case R.id.get_courier:
                //1.获取输入框的内容
                String company = mCourierCom.getText().toString().trim();
                String number = mCourierNo.getText().toString().trim();
                //拼接url
                String url = "http://v.juhe.cn/exp/index?" +
                        "key=" + StaticClass.COURIER_KEY +
                        "&com=" + company +
                        "&no=" + number;

                //2.判断是否为空
                if (!TextUtils.isEmpty(company)&&!TextUtils.isEmpty(number)) {
                    //3.拿到数据去请求查询结果(json)
                    RxVolley.get(url, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            //解析json
                            parsingJson(t);
                        }
                    });
                }
                //4.listView适配器
                //6.实体类
                //7.设置数据/显示效果

                break;
            case R.id.courier_list:
                break;
        }
    }

    private void parsingJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            JSONObject jsonResult = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonResult.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                CourierData data = new CourierData();
                data.setRemark(json.getString("remark"));
                data.setZone(json.getString("zone"));
                data.setDatetime(json.getString("datetime"));
                mCourierDataList.add(data);
            }
            //倒序
            Collections.reverse(mCourierDataList);
            CourierAdapter adapter = new CourierAdapter(this, mCourierDataList);
            mCourierList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
