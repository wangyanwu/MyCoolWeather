package com.mycoolweather.app.util;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {
	private static ProgressDialog mProgressDialog;

	/**
	 * ��ʾProgressDialog
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
/**
 * �ر�ProgressDialog
 */
public static void dismissProgressDialog(){
	if(mProgressDialog!=null){
		mProgressDialog.dismiss();
		mProgressDialog=null;
	}
}
}
