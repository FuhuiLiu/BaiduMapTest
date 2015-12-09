package com.example.baidumaptest;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	private final String TAG = "BaiduMapTest";
	//百度地图控件 
	private MapView mapView;
	//百度地图
	private BaiduMap baiduMap;
	//位置管理器
	private LocationManager locationManager;
	//位置提供者
	private String provider;
	private boolean isFirstLocate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化百度地址SDK
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		mapView = (MapView)findViewById(R.id.map_view);
		//获取百度地图对象
		baiduMap = mapView.getMap();
		//允许设置自身位置
		baiduMap.setMyLocationEnabled(true);
		//获取位置管理器
		locationManager = (LocationManager)
				getSystemService(Context.LOCATION_SERVICE);
		//获取所有的可用位置提供器
		List<String> providerList = locationManager.getProviders(true);
		if(providerList.contains(LocationManager.GPS_PROVIDER))
		{
			provider = LocationManager.GPS_PROVIDER;
		}
		else if(providerList.contains(LocationManager.NETWORK_PROVIDER))
		{
			provider = LocationManager.NETWORK_PROVIDER;
		} 
		else
		{
			//当没有可用的位置提供者时，给出提示
			Log.i(TAG, "No location provider to use");
			return ;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if(location != null)
		{
			navigateTo(location);
		}
		locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
	}
	
	private void navigateTo(Location location)
	{
		if(isFirstLocate)
		{
			//准备数据来定位到指定的经纬度
			LatLng ll = new LatLng(location.getLatitude(), 
					               location.getLongitude());
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(update);
			//设置缩放比
			update = MapStatusUpdateFactory.zoomTo(16f);
			baiduMap.animateMapStatus(update);
			isFirstLocate = false;
		}
		MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
		locationBuilder.latitude(location.getLatitude());
		locationBuilder.longitude(location.getLongitude());
		MyLocationData locationData = locationBuilder.build();
		baiduMap.setMyLocationData(locationData);
	}

	LocationListener locationListener = new LocationListener()
	{

		@Override
		public void onLocationChanged(Location location) {
			//更新当前设置的位置信息
			if(location != null)			{
				navigateTo(location);
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}};
	
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		baiduMap.setMyLocationEnabled(false);
		if(locationManager != null){
			//关闭程序时将监听器移除
			locationManager.removeUpdates(locationListener);
		}
	}
}
