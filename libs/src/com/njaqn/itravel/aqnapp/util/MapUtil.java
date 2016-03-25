package com.njaqn.itravel.aqnapp.util;

import android.app.Activity;
import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.njaqn.itravel.aqnapp.service.AmService;
import com.njaqn.itravel.aqnapp.service.AmServiceImpl;
import com.njaqn.itravel.aqnapp.service.bean.AQNPointer;

public class MapUtil extends Activity
{
	private Context ctx;
	private LocationClient cli;
	private BaiduMap map;
	private boolean isFristLoc =true;
	
	public MapUtil(Context ctx)
	{
		this.ctx = ctx;
	}
	
	public  boolean initMap()
	{
		SDKInitializer.initialize(ctx); 
		AmService am = new AmServiceImpl();  
		am.getAroundSpotByCurrLocation(getCurrGPSPointer(), 4);
		return true;
	}
	
	public boolean setCurrLocation(MapView view)
	{
		map = view.getMap();
		map.setMyLocationEnabled(true); //������λͼ��
		cli = new LocationClient(ctx);//ʵ����LocationClient��     
		cli.registerLocationListener( new MyLocationListener() );    
		this.setLocationOption();
		cli.start();
		map.setMapType(BaiduMap.MAP_TYPE_NORMAL); //���õ�ͼ����
		
		return true;
	}
	
	public class MyLocationListener implements BDLocationListener 
	{
		@Override
		public void onReceiveLocation(BDLocation location) 
		{
			if (location == null)
		            return ;
			 MyLocationData locData=new MyLocationData.Builder().accuracy(location.getRadius())
					 .direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			 //���ö�λ����
			 map.setMyLocationData(locData);
			 
			 if(isFristLoc)
			 {
				 isFristLoc=false;
				 LatLng l1=new LatLng(location.getLatitude(),location.getLongitude());
				 //���õ�ͼ���ĵ㼰���ż���
				 MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(l1,16);
				 map.animateMapStatus(u);
			 }
		}
	}
	
	//���ö�λ����
	private void setLocationOption()
	{
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		//���ö�λģʽ
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setCoorType("bd0911");
		option.setScanSpan(5000);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		cli.setLocOption(option);
	}
	
	public static AQNPointer getCurrGPSPointer()
	{
		return null;
	}
	
    public void destroy() 
	{ 
    	cli.stop();
    	map.setMyLocationEnabled(false);
    }  
}
