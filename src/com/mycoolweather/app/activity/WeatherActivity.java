package com.mycoolweather.app.activity;


import com.mycoolweather.app.R;
import com.mycoolweather.app.service.AutoUpdateService;
import com.mycoolweather.app.util.HttpCallbackListener;
import com.mycoolweather.app.util.HttpUtil;
import com.mycoolweather.app.util.LogUtil;
import com.mycoolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class WeatherActivity extends Activity implements OnClickListener{

	private static final String APPKEY="15051";
	private static final String SIGN="29f9ef6e577c1ea2d37933a4804cc99a";
	private RelativeLayout weatherInfoLayout;
	/*
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	* ������ʾ����ʱ��
	*/
	private TextView publishText;
	/**
	* ������ʾ����������Ϣ
	*/
	private TextView weatherDespText;
	/**
	* ������ʾ����1
	*/
	private TextView temp1Text;
	/**
	* ������ʾ����2
	*/
	private TextView temp2Text;
	/**
	* ������ʾ��ǰ����
	*/
	private TextView currentDateText;
	/**
	 * �л����а�ť
	 */
	private Button switchCity;
	/*
	 * ����������ť
	 */
	private Button refreshWeather;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		LogUtil.d("weatheractivity", "weatheractivity");
		initView();
	}


	private void initView() {
		// TODO Auto-generated method stub
		LogUtil.d("weatheractivity", "weatheractivity1");
		weatherInfoLayout=(RelativeLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		
		switchCity=(Button) findViewById(R.id.switch_city);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
		
		
		LogUtil.d("weatheractivity", "weatheractivity2");
		String countyCode=getIntent().getStringExtra("county_code");
		LogUtil.d("WeahtercountyCode", countyCode+"");
		if(!TextUtils.isEmpty(countyCode)){
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����....");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
			
		}else{
			//û���ؼ����ž�ֱ����ʾ��������
		showWeather();
			
		}
	}

	/**
	* ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
	*/
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", "")+"��");
		temp2Text.setText(prefs.getString("temp2", "")+"��");
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText(prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent=new Intent(this, AutoUpdateService.class);
		startService(intent);
	}


	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" +
				countyCode + ".xml";
		LogUtil.d("address_countyCode", address);
		queryFromServer(address,"countyCode");
		
	}
	private void queryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				LogUtil.d("type", type);
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						// �ӷ��������ص������н�������������
						String[] array=response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];
							LogUtil.d("weatherCode", weatherCode);
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					// ������������ص�������Ϣ
					LogUtil.d("type_weatherCode", type);
					Utility.handleWeatherResponseByAPI(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("ͬ��ʧ�ܣ�");
					}
				});
			}
		});
	}


	/**
	* ��ѯ������������Ӧ��������
	*/
	private void queryWeatherInfo(String weatherCode){
		/*String address = "http://www.weather.com.cn/data/cityinfo/" +
				weatherCode + ".html";*/
		StringBuilder addressBuilder=new StringBuilder();
		addressBuilder.append("http://api.k780.com:88/?app=weather.future&weaid=");
		addressBuilder.append(weatherCode);
		addressBuilder.append("&&appkey=");
		addressBuilder.append(APPKEY);
		addressBuilder.append("&sign=");
		addressBuilder.append(SIGN);
		addressBuilder.append("&format=json");
		
		String address=addressBuilder.toString();
		LogUtil.d("address_weatherCode", address);
		queryFromServer(address,"weatherCode");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent=new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("���ڸ���...");
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			LogUtil.d("refreshweather", weatherCode);
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;

		default:
			break;
		}
	}

	
	
}
