package com.mycoolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.mycoolweather.app.db.MyCoolWeatherDB;
import com.mycoolweather.app.model.City;
import com.mycoolweather.app.model.County;
import com.mycoolweather.app.model.Province;

public class Utility {
	/**
	* �����ʹ�����������ص�ʡ������
	*/
	public synchronized static boolean handleProvinceResponse(//
			MyCoolWeatherDB myCoolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for(String p:allProvinces){
					String[] provinceInfo=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(provinceInfo[0]);
					LogUtil.d("province", provinceInfo[1]);
					province.setProvinceName(provinceInfo[1]);
					myCoolWeatherDB.saveProvince(province);
				}
			}
			return true;
		}
		
				return false;
		
	}
	/**
	* �����ʹ�����������ص��м�����
	*/ 
	public static boolean handleCitiesResponse(MyCoolWeatherDB myCoolWeatherDB,
			String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities!=null&&allCities.length>0){
				for(String c:allCities){
					String[] cityInfo=c.split("\\|");
					City city=new City();
					city.setCityCode(cityInfo[0]);
					city.setCityName(cityInfo[1]);
					city.setProvinceId(provinceId);
					myCoolWeatherDB.saveCity(city);
				}
			}
			return true;
		}
				return false;
		
	}
	
	/**
	* �����ʹ�����������ص��ؼ�����
	*/
	public static boolean handleCountiesResponse(MyCoolWeatherDB myCoolWeatherDB,
			String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if(allCounties!=null&&allCounties.length>0){
				for(String c:allCounties){
					String[] countyInfo=c.split("\\|");
					County county=new County();
					county.setCountyCode(countyInfo[0]);
					county.setCountyName(countyInfo[1]);
					county.setCityId(cityId);
					// ���������������ݴ洢��County��
					myCoolWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
				return false;
		
	}
	/*
	 * �������������ص�Json���ݣ��������������ݴ��ڱ��ء�
	 */
	public static void handleWeatherResponse(Context context,String response){
		LogUtil.d("handleWeatherResponse","handleWeatherResponse");
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			LogUtil.d("cityName", cityName);
			LogUtil.d("temp",temp1+"~"+temp2 );
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	* �����������ص�����������Ϣ�洢��SharedPreferences�ļ��С�
	*/
	
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.//
				getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
		
		
		
	}
}
