package com.hultron.lifehelper.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.uitils.UtilTools;

import java.util.ArrayList;
import java.util.List;

/**
 * AboutSoftwareActivity
 * 关于软件
 */
public class AboutSoftwareActivity extends BaseActivity {

    private ListView mSoftwareInfo;
    private List<String> mStringList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutsoftware);
        initView();
    }

    private void initView() {
        mSoftwareInfo = (ListView) findViewById(R.id.software_info);
        mStringList.add("应用名：" + getString(R.string.app_name));
        mStringList.add("版本号：" + UtilTools.getVersion(this));
        mStringList.add("开发者博客：http://blog.csdn.net/hultron");

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStringList);
        //设置适配器
        mSoftwareInfo.setAdapter(mAdapter);
    }


}
