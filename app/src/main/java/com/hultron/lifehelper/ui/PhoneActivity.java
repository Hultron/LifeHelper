package com.hultron.lifehelper.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.uitils.LogUtil;
import com.hultron.lifehelper.uitils.StaticClass;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 归属地查询
 */

public class PhoneActivity extends BaseActivity implements View.OnClickListener {

    //输入框
    private EditText number;
    //公司logo
    private ImageView mCompany;
    //结果
    private TextView result;
    //按钮
    private Button zero, one, two, three, four, five, six, seven, eight, nine, delete, query;
    //标记位
    private boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        initView();
    }

    private void initView() {
        number = (EditText) findViewById(R.id.ph_number);
        mCompany = (ImageView) findViewById(R.id.company);
        result = (TextView) findViewById(R.id.result);
        zero = (Button) findViewById(R.id.zero);
        zero.setOnClickListener(this);
        one = (Button) findViewById(R.id.one);
        one.setOnClickListener(this);
        two = (Button) findViewById(R.id.two);
        two.setOnClickListener(this);
        three = (Button) findViewById(R.id.three);
        three.setOnClickListener(this);
        four = (Button) findViewById(R.id.four);
        four.setOnClickListener(this);
        five = (Button) findViewById(R.id.five);
        five.setOnClickListener(this);
        six = (Button) findViewById(R.id.six);
        six.setOnClickListener(this);
        seven = (Button) findViewById(R.id.seven);
        seven.setOnClickListener(this);
        eight = (Button) findViewById(R.id.eight);
        eight.setOnClickListener(this);
        nine = (Button) findViewById(R.id.nine);
        nine.setOnClickListener(this);
        delete = (Button) findViewById(R.id.del);
        delete.setOnClickListener(this);
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                number.setText("");
                return false;
            }
        });
        query = (Button) findViewById(R.id.query);
        query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /*
        * 逻辑：
        * 1.获取输入框的内容
        * 2.判断是否为空
        * 3.网络亲贵
        * 4.解析json
        * 5.结果显示
        *
        * ----
        * 键盘逻辑
        * */
        if (v == null) {
            return;
        }
        //获取输入框的内容
        String content = number.getText().toString();
        switch (v.getId()) {
            case R.id.zero:
            case R.id.one:
            case R.id.two:
            case R.id.three:
            case R.id.four:
            case R.id.five:
            case R.id.six:
            case R.id.seven:
            case R.id.eight:
            case R.id.nine:
                if (flag == true) {
                    content = "";
                    number.setText("");
                    flag = false;
                }
                number.setText(content + ((Button) v).getText());
                //移动光标
                if (content.length() >= 11) {
                    Toast.makeText(this, "输入号码超出11位，请删除多余内容",
                            Toast.LENGTH_SHORT).show();
                    number.setEnabled(false);
                } else {
                    number.setSelection(content.length() + 1);
                }
                break;
            case R.id.del:
                if (!TextUtils.isEmpty(content) && content.length() > 0) {
                    number.setEnabled(true);
                    //每次结尾减1
                    number.setText(content.substring(0, content.length() - 1));
                    number.setSelection(content.length() - 1);
                }
                break;
            case R.id.query:
                if (!TextUtils.isEmpty(content)) {
                    getPhone(content);
                }
                break;
        }

    }

    //获取归属地
    private void getPhone(String content) {
        String url = "http://apis.juhe.cn/mobile/get?phone=" + content + "&key=" +
                StaticClass.PHONE_KEY;
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                LogUtil.e(t);
                parseJson(t);
            }
        });
    }

    /*
    * {"resultcode":"200",
    * "reason":"Return Successd!",
    * "result":{
    * "province":"浙江",
    * "city":"杭州",
    * "areacode":"0571", "zip":"310000",
    * "mCompany":"中国移动",
    * "card":"移动动感地带卡"
}
}
    * */
    private void parseJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            JSONObject jsonResult = jsonObject.getJSONObject("result");
            String province = jsonResult.getString("province");
            String city = jsonResult.getString("city");
            String areacode = jsonResult.getString("areacode");
            String zip = jsonResult.getString("zip");
            String company = jsonResult.getString("company");
            String card = jsonResult.getString("card");

            result.setText("归属地：" + province + city + "\n" +
                    "区号：" + areacode + "\n" +
                    "邮编：" + zip + "\n" +
                    "运营商：" + company + "\n" +
                    "类型：" + card);

            //图片显示
            switch (company) {
                case "移动":
                    mCompany.setBackgroundResource(R.drawable.china_mobile);
                    break;
                case "联通":
                    mCompany.setBackgroundResource(R.drawable.china_unicom);
                    break;
                case "电信":
                    mCompany.setBackgroundResource(R.drawable.china_telecom);
                    break;
            }
            flag = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
