package com.njaqn.itravel.aqnapp.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.njaqn.itravel.aqnapp.service.AmService;
import com.njaqn.itravel.aqnapp.service.AmServiceImpl;
import com.njaqn.itravel.aqnapp.service.bean.AQNPointer;
import com.njaqn.itravel.aqnapp.service.bean.JSpotBean;
import com.njaqn.itravel.aqnapp.service.bean.JingDianBean;
import com.njaqn.itravel.aqnapp.AppInfo;
import com.njaqn.itravel.aqnapp.R;

public class MapUtil extends Activity
{
    private Context ctx;
    private LocationClient cli;
    private BaiduMap map;
    private boolean isFristLoc = true;
    private String locationAddress = null;
    private LatLng locationLatLng = null;
    private PlayAuditData data;
    private AppInfo app;
    private Button btnLocation;
    private VoiceUtil vUtil;

    private ButtonClickListener btnClick;
    private AmService aService = new AmServiceImpl();
    
    public MapUtil(Context ctx, PlayAuditData data, AppInfo app, VoiceUtil vUtil)
    {
	this.data = data;
	this.ctx = ctx;
	this.app = app;
	this.vUtil = vUtil;
	SDKInitializer.initialize(ctx);
    }

    public void setBtnLocation(Button btnLocation)
    {
	this.btnLocation = btnLocation;
    }

    public void setMapMarker(int iconResource, LatLng point, Bundle info)
    {
	BitmapDescriptor bitmap = BitmapDescriptorFactory
		.fromResource(iconResource);
	OverlayOptions option = new MarkerOptions().position(point)
		.icon(bitmap).zIndex(0).period(25).extraInfo(info);
	map.addOverlay(option);
    }

    public void setPopMarker(LatLng point, Bundle info)
    {
	// ����InfoWindowչʾ��view
	LayoutInflater inflater = (LayoutInflater) ctx
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View v = inflater.inflate(R.layout.am001_map_popview, null);

	ImageButton button = (ImageButton) v.findViewById(R.id.btnPlayAudio);
	ImageButton enter = (ImageButton) v.findViewById(R.id.btnEnter); 
	if(btnClick != null)
	{
	    btnClick = null;
	}
	btnClick = new ButtonClickListener(info);
	enter.setOnClickListener(btnClick);
	button.setOnClickListener(btnClick);
	TextView textView = (TextView) v.findViewById(R.id.txtIntro);
	TextView txtName = (TextView) v.findViewById(R.id.txtName);
	txtName.setText(info.getString("name"));
	textView.setText(info.getString("intro"));

	// ����InfoWindow , ���� view�� �������꣬ y ��ƫ����
	InfoWindow mInfoWindow = new InfoWindow(v, point, -27);
	map.showInfoWindow(mInfoWindow);
    }
    
    private final class ButtonClickListener implements OnClickListener
    {

	private Bundle info; 
	public ButtonClickListener(Bundle info)
	{
	    this.info = info;
	}

	@Override
    	public void onClick(View v)
	{
	    switch (v.getId())
	    {
	    	case R.id.btnPlayAudio:
	    	    if (vUtil.getPlayMode() == 1 || vUtil.getPlayMode() == 2)
		    {
			vUtil.playStop();
		    }
		    vUtil.playAudio(info.getString("intro"));
		    break;

	    	case R.id.btnEnter:
	    	    
	    	    if(info.getString("type").equals("spot"))
	    	    {
	    		map.clear();
	    		try
			{
	    		    setJingDianPointer(info.getInt("id"));
			}
			catch (Exception e)
			{
			    e.printStackTrace();
			}
	    		
	    	    }
	    	   
		break;
	    }
	    

	}
    }
	

    // ��Ӹ�����ĵ�
    public void setCurrLocationMarker()
    {
	BitmapDescriptor bd1 = BitmapDescriptorFactory
		.fromResource(R.drawable.m01_point_red);
	// BitmapDescriptor bd2 = BitmapDescriptorFactory
	// .fromResource(R.drawable.m01_point_white);
	// ArrayList<BitmapDescriptor> giflist = new
	// ArrayList<BitmapDescriptor>();
	// giflist.add(bd1);
	// giflist.add(bd2);
	OverlayOptions ol = new MarkerOptions().position(locationLatLng)
		.icon(bd1).zIndex(0).period(25);
	map.addOverlay(ol);
    }

    public void setMarkerText(LatLng pointer, String text)
    {
	// ��������Option���������ڵ�ͼ���������
	OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00)
		.fontSize(24).fontColor(0xFFFF00FF).text(text)
		.position(pointer);
	map.addOverlay(textOption);
    }

    public boolean setCurrLocation(MapView view)
    {
	map = view.getMap();
	map.setMyLocationEnabled(true); // ������λͼ��
	cli = new LocationClient(ctx);// ʵ����LocationClient��
	cli.registerLocationListener(new MyLocationListener());
	this.setLocationOption();
	map.setMapType(BaiduMap.MAP_TYPE_NORMAL); // ���õ�ͼ����
	cli.start();
	return true;
    }

    public class MyLocationListener implements BDLocationListener
    {
	@Override
	public void onReceiveLocation(BDLocation location)
	{
	    if (location == null)
		return;

	    MyLocationData locData = new MyLocationData.Builder()
		    .accuracy(location.getRadius()).direction(100)
		    .latitude(location.getLatitude())
		    .longitude(location.getLongitude()).build();
	    String city = location.getCity();
	    String province = location.getProvince();
	    app.setCity(city.substring(0, city.length() - 1));
	    app.setLongitude(location.getLongitude());
	    app.setLatitude(location.getLatitude());
	    btnLocation.setText(app.getCity());

	    // ���ö�λ����
	    map.setMyLocationData(locData);
	    // ����Ĭ�ϵĶ�λͼ����ʾ
	    map.setMyLocationEnabled(true);

	    if (location.getLocType() == BDLocation.TypeNetWorkLocation)
		locationAddress = location.getAddrStr();
	    // �ж��û��Ƿ��ھ�����
	    if (isFristLoc)
	    {
		isFristLoc = false;
		locationLatLng = new LatLng(location.getLatitude(),
			location.getLongitude());
		data.setLocationInfo(province, city, locationAddress,
			locationLatLng);
		// setCurrLocationMarker(); // ������˸��ͼ��
		// setPopMarker(locationLatLng, locationAddress);
		// LatLng l1 = new
		// LatLng(location.getLatitude()+0.01,location.getLongitude()+0.01);
		// setMapMarker(R.drawable.ic_launcher,l1);
		MapStatusUpdate su = MapStatusUpdateFactory.newLatLngZoom(
			locationLatLng, 16);
		// ���õ�ͼ���ĵ㼰���ż���
		map.animateMapStatus(su);

		// ���õ�ͼ��־��������¼�
		map.setOnMarkerClickListener(new MarkerListener());

		//
		map.setOnMapClickListener(new MapClickListener());
		// �����û�����λ�ö�̬ѡȡ�������߾���
		setMark(location);
	    }
	}
    }

    private final class MapClickListener implements OnMapClickListener
    {

	@Override
	public void onMapClick(LatLng arg0)
	{
	    map.hideInfoWindow();

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0)
	{
	    // TODO Auto-generated method stub
	    return false;
	}

    }

    private final class MarkerListener implements OnMarkerClickListener
    {

	@Override
	public boolean onMarkerClick(Marker arg0)
	{
	    Bundle bundle = arg0.getExtraInfo();
	    String type = bundle.getString("type");
	    if (type.equals("spot"))
	    {
		setPopMarker(arg0.getPosition(), bundle);
	    }
	    else if(type.equals("jingDian"))
	    {
		setPopMarker(arg0.getPosition(),bundle);
	    }

	    return true;
	}

    }

    // ���ö�λ����
    private void setLocationOption()
    {
	LocationClientOption option = new LocationClientOption();
	option.setOpenGps(true);

	// ���ö�λģʽ
	option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
	option.setCoorType("bd09ll");
	option.setAddrType("all");
	option.setScanSpan(5000);
	option.setIsNeedAddress(true);
	option.setNeedDeviceDirect(true);
	cli.setLocOption(option);
    }

    public void judgeUserLocation(BDLocation location)
    {
	AmService as = new AmServiceImpl();
	// as.judgeSpot

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

    private void setMark(BDLocation location)
    {
	int currentCityId = app.getCityId();
	List<JSpotBean> spots = new ArrayList<JSpotBean>();
	JSONObject locationSpot = aService.judgeLocation(location.getLongitude(), location.getLatitude());
	try
	{
	    if (locationSpot.getInt("distance") < 100)
	    {
		// ˵���û��ھ�������ʾ�þ����ڵľ���
		vUtil.playAudio("���������ڵľ�����"+locationSpot.getString("name"));
		setJingDianPointer(locationSpot.getInt("ID"));
	    }
	  //������ʾ��ǰ�������о���
	  else
	  {	
	      vUtil.playAudio("�����ڲ����κξ���");
	      spots = aService.getSpotLocationByCityId(currentCityId);
	      if (spots != null)
	      {
		  for (JSpotBean i : spots)
		  {
		      LatLng latlng = new LatLng(Double.parseDouble(i.getLatitude()), Double.parseDouble(i
	  			                  .getLongitude()));
	  	    Bundle bundle = new Bundle();
	 	    bundle.putString("type", "spot");
	 	    bundle.putString("name", i.getName());
	 	    bundle.putInt("id", i.getId());
	 	    bundle.putString("intro", i.getIntro());
	 	    setMapMarker(R.drawable.am001_map_spot, latlng, bundle);
		  }
	    }
	 }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    private void setJingDianPointer(int ID) throws JSONException
    {
	List<JingDianBean> jingDians = aService
		.getAllJingDianBySpotId(ID);
	if (jingDians != null)
	{
	    for (JingDianBean i : jingDians)
	    {
		LatLng latlng = new LatLng(i.getLatitude(), i.getLongitude());
		Bundle bundle = new Bundle();
		bundle.putString("type", "jingDian");
		bundle.putString("intro", i.getIntro());
		bundle.putString("name",i.getName() );
		bundle.putInt("id", i.getId());
		setMapMarker(R.drawable.am001_map_spot, latlng, bundle);
	    }
	}
    }

}
