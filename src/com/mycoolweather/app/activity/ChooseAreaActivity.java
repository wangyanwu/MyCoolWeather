package com.mycoolweather.app.activity;

import java.util.ArrayList;
import java.util.List;























import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mycoolweather.app.R;
import com.mycoolweather.app.db.MyCoolWeatherDB;
import com.mycoolweather.app.model.City;
import com.mycoolweather.app.model.County;
import com.mycoolweather.app.model.Province;
import com.mycoolweather.app.util.HttpCallbackListener;
import com.mycoolweather.app.util.HttpUtil;
import com.mycoolweather.app.util.LogUtil;
import com.mycoolweather.app.util.MyApplication;
import com.mycoolweather.app.util.ProgressDialogUtils;
import com.mycoolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity{
public static final int LEVEL_PROVINCE=0;
public static final int LEVEL_CITY=1;
public static final int LEVEL_COUNTY=2;

private ProgressDialog progressDialog;
private TextView titleText;
private ListView listView;
private ArrayAdapter<String> adapter;
private MyCoolWeatherDB myCoolWeatherDB;
private List<String> dataList=new ArrayList<String>();
/**
* ʡ�б�
*/
private List<Province> provinceList;

/**
 * ���б�
 */
private List<City> cityList;
/**
 * ���б�
 */
private List<County> countyList;

/**
 * ѡ�е�ʡ��
 */
private Province selectProvince;
/**
 * ѡ�еĳ���
 */
private City selectCity;
/*
 * ��ǰѡ�еļ���
 */
private int currentLevel;

private boolean isFromWeatherActivity;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	initWeather();
	setContentView(R.layout.choose_area);
	initView();
	
}
private void initWeather() {
	// TODO Auto-generated method stub
	isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
	SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
	LogUtil.d("city_selected", prefs.getBoolean("city_selected", false)+"");
	// �Ѿ�ѡ���˳����Ҳ��Ǵ�WeatherActivity��ת�������Ż�ֱ����ת��WeatherActivity
	if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity){
		Intent intent=new Intent(this,WeatherActivity.class);
		LogUtil.d("city_selected_activity", "1");
		startActivity(intent);
		LogUtil.d("city_selected_activity", "2");
		finish();
		LogUtil.d("city_selected_activity", "3");
		return;
	}
}
private void initView() {
	// TODO Auto-generated method stub
	titleText=(TextView) findViewById(R.id.title_text);
	listView=(ListView) findViewById(R.id.list_view);
	adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
	listView.setAdapter(adapter);
	myCoolWeatherDB=MyCoolWeatherDB.getInstance(this);
	listView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtil.d("currentLevel", currentLevel+"");
			// TODO Auto-generated method stub
			if(currentLevel==LEVEL_PROVINCE){
				selectProvince=provinceList.get(position);
				queryCites();
			}else if(currentLevel==LEVEL_CITY){
				selectCity=cityList.get(position);
				queryCounties();
			}else if(currentLevel==LEVEL_COUNTY){
				String countyCode=countyList.get(position).getCountyCode();
				LogUtil.d("countyCode", countyCode);
				Intent intent=new Intent(ChooseAreaActivity.this, WeatherActivity.class);
				intent.putExtra("county_code", countyCode);
				startActivity(intent);
				finish();
				
			}
			
		}
	});
	queryProvinces();//����ʡ������
}



/*
 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в鵽��ȥ��������ѯ
 */
private void queryProvinces() {
	// TODO Auto-generated method stub
	provinceList=myCoolWeatherDB.loadProvince();
	if(provinceList.size()>0){
		dataList.clear();
		for(Province province:provinceList){
			dataList.add(province.getProvinceName());
		}
		adapter.notifyDataSetChanged();
		listView.setSelection(0);
		titleText.setText("�й�");
		currentLevel=LEVEL_PROVINCE;
		
	}else{
	queryFromServer(null,"province");
	}
	
}
/**
* ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
*/
protected void queryCites() {
	// TODO Auto-generated method stub
	cityList=myCoolWeatherDB.loadCities(selectProvince.getId());
	if(cityList.size()>0){
		dataList.clear();
		for(City city:cityList){
			dataList.add(city.getCityName());
		}
		adapter.notifyDataSetChanged();
		listView.setSelection(0);
		titleText.setText(selectProvince.getProvinceName());
		currentLevel=LEVEL_CITY;
		
	}else{
	queryFromServer(selectProvince.getProvinceCode(),"city");
	}
}
/**
* ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
*/
protected void queryCounties() {
	// TODO Auto-generated method stub
	countyList=myCoolWeatherDB.loadCounties(selectCity.getId());
	if(countyList.size()>0){
		dataList.clear();
		for(County county:countyList){
			dataList.add(county.getCountyName());
		}
		adapter.notifyDataSetChanged();
		listView.setSelection(0);
		titleText.setText(selectCity.getCityName());
		currentLevel=LEVEL_COUNTY;
	}else{
		queryFromServer(selectCity.getCityCode(), "county");
	}
}
private void queryFromServer(final String code,final String type) {
	// TODO Auto-generated method stub
	String address=null;
	if(!TextUtils.isEmpty(code)){
		address = "http://www.weather.com.cn/data/list3/city" + code +
				".xml";
	}else{
		address=address ="http://www.weather.com.cn/data/list3/city.xml";
	}
	LogUtil.d("address", address);
//	showProgressDialog();
	ProgressDialogUtils.showProgressDialog(ChooseAreaActivity.this, "���������У��͹����Եȣ�");
	HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			// TODO Auto-generated method stub
			boolean result=false;
			if("province".equals(type)){
				result=Utility.handleProvinceResponse(myCoolWeatherDB, response);
			}else if("city".equals(type)){
				result=Utility.handleCitiesResponse(myCoolWeatherDB, response, selectProvince.getId());
			}else if("county".equals(type)){
				result=Utility.handleCountiesResponse(myCoolWeatherDB, response, selectCity.getId());
			}
			if(result){
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
//					closeProgressDialog();
						ProgressDialogUtils.dismissProgressDialog();
						if("province".equals(type)){
							queryProvinces();
						}else if("city".equals(type)){
							queryCites();
						}else if("county".equals(type)){
							queryCounties();
						}
					}
				});
			}
		}
		
		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			// ͨ��runOnUiThread()�����ص����̴߳����߼�
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					closeProgressDialog();
					ProgressDialogUtils.dismissProgressDialog();
					Toast.makeText(MyApplication.getContext(),
					"����ʧ��", Toast.LENGTH_SHORT).show();
				}

				
			});
		}
	});
}

/**
* ��ʾ���ȶԻ���
*/
private void showProgressDialog() {
	// TODO Auto-generated method stub
	if(progressDialog==null){
		progressDialog=new ProgressDialog(this);
		progressDialog.setMessage("���������У�");
		progressDialog.setCanceledOnTouchOutside(false);
		
	}
	progressDialog.show();
}
private void closeProgressDialog() {
	// TODO Auto-generated method stub
	if(progressDialog!=null)
	{
		progressDialog.dismiss();
	}
}
/**
* ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳���
*/
@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
	if(currentLevel==LEVEL_COUNTY){
		queryCites();
	}else if(currentLevel==LEVEL_CITY){
		queryProvinces();
	}else 
		{
		if(isFromWeatherActivity){
		Intent intent=new Intent(this, WeatherActivity.class);
		startActivity(intent);
	}
		
		finish();
		}
}

}