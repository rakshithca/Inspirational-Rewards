package com.inspiration.inspirationrewards.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkCheck {
	
	private static final String TAG = NetworkCheck.class.getSimpleName();

	public static boolean isInternetAvailable(Context context) {
		if(context == null){
			return false;
		}
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null) {
			Log.d(TAG, "no internet connection");
			return false;
		} else {
			if (info.isConnected()) {
				Log.d(TAG, "internet connection available...");
				return true;
			} else {
				Log.d(TAG, "internet connection");
				return true;
			}
		}
	}
	
}
