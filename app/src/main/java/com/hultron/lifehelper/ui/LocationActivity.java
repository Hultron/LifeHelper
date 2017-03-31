package com.hultron.lifehelper.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.hultron.lifehelper.R;
import com.hultron.lifehelper.application.BaseApplication;
import com.hultron.lifehelper.uitils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * LocationActivity
 * 定位
 */
public class LocationActivity extends BaseActivity {

    private static final int LOC_PER_CODE = 11;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    //定位
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private void initView() {

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        //声明LocationClient类
        mLocationClient = new LocationClient(BaseApplication.getContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        initLocation();

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, LOC_PER_CODE);
        } else {
            requestLocation();
        }



    }

    //开始定位
    private void requestLocation() {
        mLocationClient.start();
        L.e("开始定位");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOC_PER_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span = 0;
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，
        // 结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，
        // 设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);
    }

    //定位的回调
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            //获取定位结果
            StringBuilder locResult = new StringBuilder(256);

            locResult.append("time : ");
            locResult.append(location.getTime());    //获取定位时间

            locResult.append("\nerror code : ");
            locResult.append(location.getLocType());    //获取类型类型

            locResult.append("\nlatitude : ");
            locResult.append(location.getLatitude());    //获取纬度信息

            locResult.append("\nlontitude : ");
            locResult.append(location.getLongitude());    //获取经度信息

            locResult.append("\nradius : ");
            locResult.append(location.getRadius());    //获取定位精准度

            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                // GPS定位结果
                locResult.append("\nspeed : ");
                locResult.append(location.getSpeed());    // 单位：公里每小时

                locResult.append("\nsatellite : ");
                locResult.append(location.getSatelliteNumber());    //获取卫星数

                locResult.append("\nheight : ");
                locResult.append(location.getAltitude());    //获取海拔高度信息，单位米

                locResult.append("\ndirection : ");
                locResult.append(location.getDirection());    //获取方向信息，单位度

                locResult.append("\naddr : ");
                locResult.append(location.getAddrStr());    //获取地址信息

                locResult.append("\ndescribe : ");
                locResult.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                // 网络定位结果
                locResult.append("\naddr : ");
                locResult.append(location.getAddrStr());    //获取地址信息

                locResult.append("\noperationers : ");
                locResult.append(location.getOperators());    //获取运营商信息

                locResult.append("\ndescribe : ");
                locResult.append("网络定位成功");

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                locResult.append("\ndescribe : ");
                locResult.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                locResult.append("\ndescribe : ");
                locResult.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到" +
                        "loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                locResult.append("\ndescribe : ");
                locResult.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                locResult.append("\ndescribe : ");
                locResult.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，" +
                        "处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }

            locResult.append("\nlocationdescribe : ");
            locResult.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                locResult.append("\npoilist size = : ");
                locResult.append(list.size());
                for (Poi p : list) {
                    locResult.append("\npoi = : ");
                    locResult.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            //定位结果
            L.i(locResult.toString());
            //结束定位
            L.e("结束定位");
            //mLocationClient.stop();
            //移动到我的位置
            //设置缩放，确保我在屏幕内
            MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(18);
            mBaiduMap.setMapStatus(update);
            //开始移动
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate updatell = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.setMapStatus(updatell);

            /*添加覆盖物*/
            //定义Marker坐标点
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_location);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);


        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
