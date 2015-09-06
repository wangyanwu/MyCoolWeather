package com.mycoolweather.app.util;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {
	private static ProgressDialog mProgressDialog;

	/**
	 * œ‘ æProgressDialog
	 * @param context
	 * @param message
	 */
	
public static void showProgressDialog(Context context,String message){
	if(mProgressDialog==null){
		mProgressDialog=ProgressDialog.show(context, "", message);
	}else{
		mProgressDialog.show();
	}
}
private ProgressDialogUtils() {
		throw new AssertionError();
		// TODO Auto-generated constructor stub
	}
/**
 * πÿ±’ProgressDialog
 */
public static void dismissProgressDialog(){
	if(mProgressDialog!=null){
		mProgressDialog.dismiss();
		mProgressDialog=null;
	}
}
}
