package com.inspiration.inspirationrewards.JSON;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HTTPPoster {

	private final String TAG = "HTTPPoster";

	public ResponseHolder doPostasBytes(String url,
                                        HashMap<String, String> hashMap) throws ClientProtocolException,
            IOException, JSONException, InvalidResponseCode {

		Log.d(TAG, "url doPost : " + url);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/json;charset=utf-8");
		request.setHeader("Authorization", hashMap.get("Authorization"));
		JSONObject jsonObject = new JSONObject();
		Set<String> set = hashMap.keySet();
		Iterator<String> iterator = set.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object object = hashMap.get(key);

			if (object instanceof JSONObject) {
				if (key.trim().length() == 0) {
					jsonObject = (JSONObject) object;
				} else {
					JSONObject valueObject = (JSONObject) object;
					jsonObject.put(key, valueObject);
				}
			} else if (object instanceof String) {
				String value = (String) object;
				jsonObject.put(key, value);
			} else if (object instanceof Integer) {
				Integer integer = (Integer) object;
				jsonObject.put(key, integer);
			} else if (object instanceof byte[]) {

			} else if (object instanceof Boolean) {
				boolean val = (Boolean) object;
				jsonObject.put(key, val);
			}

		}
		StringEntity s = new StringEntity(jsonObject.toString());
		s.setContentEncoding("UTF-8");
		request.setEntity(s);
		HttpResponse response = httpclient.execute(request);
		Log.d(TAG, "Status code of post: "
				+ response.getStatusLine().getStatusCode());
		ResponseHolder holder = new ResponseHolder();
		holder.setResponseCode(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200) {
			// throw new
			// InvalidResponseCode(response.getStatusLine().getStatusCode());
			return holder;
		}

		String string = "";
		InputStream is = response.getEntity().getContent();
		if (is != null) {

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			byte[] bytes = buffer.toByteArray();
			is.close();

			string = new String(bytes, "UTF-8");
			holder.setResponseStr(string);
		}
		return holder;
	}

	public ResponseHolder doPost(String url, HashMap<String, String> hashMap, String body)
			throws ClientProtocolException, IOException, JSONException,
            InvalidResponseCode {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", hashMap.get("Authorization"));
		request.setEntity((new StringEntity(body, "UTF8")));

		HttpResponse response = httpclient.execute(request);
		ResponseHolder holder = new ResponseHolder();
		holder.setResponseCode(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 201) {

			String string = EntityUtils.toString(response.getEntity());
			holder.setResponseStr(string);
			return holder;
		}

			if (response.getStatusLine().getStatusCode() != 200) {
			// throw new
			// InvalidResponseCode(response.getStatusLine().getStatusCode());
			return holder;
		}

		String string = EntityUtils.toString(response.getEntity());
		holder.setResponseStr(string);
		return holder;
	}

	public ResponseHolder doPut(String url, HashMap<String, String> hashMap, String body)
			throws JSONException, ClientProtocolException, IOException,
            InvalidResponseCode {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPut request = new HttpPut(url);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", hashMap.get("Authorization"));
		request.setEntity((new StringEntity(body, "UTF8")));
		HttpResponse response = httpclient.execute(request);
		Log.d(TAG, "Status code of put body: "
				+ response.getStatusLine().getStatusCode());

		ResponseHolder holder = new ResponseHolder();
		holder.setResponseCode(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200) {
			// throw new
			// InvalidResponseCode(response.getStatusLine().getStatusCode());
			return holder;
		}

		String string = EntityUtils.toString(response.getEntity());
		holder.setResponseStr(string);
		// Log.d(TAG, "Response : " + string);

		return holder;
	}

	private class MyDelete extends HttpPost {
		public MyDelete(String url) {
			super(url);
		}

		@Override
		public String getMethod() {
			return "DELETE";
		}

	}

	public ResponseHolder doDelete(String url, HashMap<String, String> hashMap, String body)
			throws ClientProtocolException, IOException, JSONException {

		Log.d(TAG, "url doDelete : " + url+" body "+body +"   token  "+hashMap.get("Authorization"));

		HttpClient httpclient = new DefaultHttpClient();
		// HttpDelete request = new HttpDelete(url);
		MyDelete request = new MyDelete(url);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", hashMap.get("Authorization"));
		request.setEntity((new StringEntity(body, "UTF8")));
		JSONObject jsonObject = new JSONObject();
		Set<String> set = hashMap.keySet();
		Iterator<String> iterator = set.iterator();

		JSONArray valueArray = null;
		while (iterator.hasNext()) {
			String key = iterator.next();

			Object object = hashMap.get(key);

			if (object instanceof JSONArray) {
				valueArray = (JSONArray) object;
			} else if (object instanceof JSONObject) {
				JSONObject valueObject = (JSONObject) object;
				jsonObject.put(key, valueObject);
			} else if (object instanceof String) {
				String value = (String) object;
				jsonObject.put(key, value);
			}
		}

		StringEntity s = null;

		if (valueArray == null) {
			s = new StringEntity(jsonObject.toString());
			// Log.d(TAG, "Request : " + jsonObject);
		} else {
			s = new StringEntity(valueArray.toString());
			// Log.d(TAG, "Request : " + valueArray);
		}
		s.setContentEncoding("UTF-8");
		request.setEntity(s);
		HttpResponse response = httpclient.execute(request);
		Log.d(TAG, "Status code of delete: "
				+ response.getStatusLine().getStatusCode());
		ResponseHolder holder = new ResponseHolder();
		holder.setResponseCode(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200) {
			// throw new
			// InvalidResponseCode(response.getStatusLine().getStatusCode());
			return holder;
		}
		String string = EntityUtils.toString(response.getEntity());
		holder.setResponseStr(string);
		return holder;
	}
}