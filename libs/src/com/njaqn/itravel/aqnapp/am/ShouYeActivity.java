package com.njaqn.itravel.aqnapp.am;


import com.baidu.mapapi.map.MapView;
import com.njaqn.itravel.aqnapp.util.MapUtil;
import com.njaqn.itravel.aqnapp.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ShouYeActivity extends Activity 
{
	private MapView mMapView = null;  
	private MapUtil map = null;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		map = new MapUtil(this.getApplicationContext());
		map.initMap();
		setContentView(R.layout.activity_am_shouye);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		
		map.setCurrLocation(mMapView);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.shouye, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
	
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override  
    protected void onDestroy() 
	{ 
		map.destroy();
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
        mMapView.onDestroy(); 
        mMapView=null;
    }  
	
    @Override  
    protected void onResume()
    {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause()
    {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
      }  
  }

