package com.inspiration.inspirationrewards.JSON;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

public class PostDetailsAsyncTask extends AsyncTask<String, Void, String> {

	private final String TAG = PostDetailsAsyncTask.this.getClass()
			.getSimpleName();

	private final StatusNotifier notifier;
	 ProgressDialog pDialog;
	public PostDetailsAsyncTask(StatusNotifier notify) {
		this.notifier = notify;
	}
	 @Override
     protected void onPreExecute(){
        pDialog = new ProgressDialog((Context)notifier);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
     }
	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		String body = params[1];

		JSONParser jParser = new JSONParser();
		jParser.initialize((Context) notifier);
		String resultDetails;
		try {
			resultDetails = jParser.sendDetailsDetails(url, body,
					(Context) notifier);
			if (resultDetails != null) {
				resultDetails = resultDetails.trim();
				if(resultDetails.length() == 0) {
//					resultDetails = null;
				}
				return resultDetails;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("SendDetailsAsync","JSONException ==>> "+e.getLocalizedMessage());
		} catch (org.apache.http.ParseException e) {
			e.printStackTrace();
			Log.e("SendDetailsAsync","org.apache.http.ParseException ==>> "+e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("SendDetailsAsync","IOException ==>> "+e.getLocalizedMessage());
		} catch (InvalidResponseCode e) {
			e.printStackTrace();
			Log.e("SendDetailsAsync","InvalidResponseCode ==>> "+e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	protected void onPostExecute(String resultDetails) {
		 pDialog.dismiss();
		Log.e("SendDetailsAsync","SendDetailsAsync ==>> "+resultDetails);
		if (resultDetails != null) {
			notifier.OnSuccess(resultDetails);
		} else {
			notifier.OnError();
		}
	}
}