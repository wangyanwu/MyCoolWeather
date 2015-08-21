package com.mycoolweather.app.util;

import android.text.TextUtils;
import android.util.Log;

import com.mycoolweather.app.db.MyCoolWeatherDB;
import com.mycoolweather.app.model.City;
import com.mycoolweather.app.model.County;
import com.mycoolweather.app.model.Province;

public class Utility {
	/**
	* 解析和处理服务器返回的省级数据
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
					Log.d("province", provinceInfo[1]);
					province.setProvinceName(provinceInfo[1]);
					myCoolWeatherDB.saveProvince(province);
				}
			}
			return true;
		}
		
				return false;
		
	}
	/**
	* 解析和处理服务器返回的市级数据
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
	* 解析和处理服务器返回的县级数据
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
					// 将解析出来的数据存储到County表
					myCoolWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
				return false;
		
	}
}
