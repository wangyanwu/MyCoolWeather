package com.mycoolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
private  HttpUtil() {
		throw new AssertionError();
		// TODO Auto-generated constructor stub
	}

public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			try {
				URL url=new URL(address);
				connection=(HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setReadTimeout(8000);
				connection.setReadTimeout(8000);
				InputStream in=connection.getInputStream();
				BufferedReader reader=new BufferedReader(new InputStreamReader(in));
				StringBuilder response=new StringBuilder();
				String line=null;
				while((line=reader.readLine())!=null){
					response.append(line);
				}
				if(listener!=null){
					//回调onFinish()方法
					listener.onFinish(response.toString());
				}
			} catch (Exception e) {
				// TODO: handle exception
				//回调onError()方法。
				if(listener!=null)
				{
					listener.onError(e);
				}
			}
		}
	}).start();;
}
}
