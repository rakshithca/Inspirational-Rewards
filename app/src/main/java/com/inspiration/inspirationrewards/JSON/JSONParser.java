package com.inspiration.inspirationrewards.JSON;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.inspiration.inspirationrewards.activity.EditProfileActivity;
import com.inspiration.inspirationrewards.utils.APIs;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class JSONParser {

    private final static String TAG = "JSONParser";
    private static boolean initializeInstance = false;

    private static Context mContext;

    public static void initialize(Context context) {
        if (!initializeInstance) {
            initializeInstance = true;
            mContext = context;
            Resources resources = context.getResources();
        }
    }

    public String sendDetailsDetails(String url, String body,
                                     Context context) throws IOException, JSONException,
            InvalidResponseCode {
        Log.e(TAG, "sendDetailsDetails: "+url );
        Log.e(TAG, "sendDetailsDetails: "+body );
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("Content-Type", "application/json");
        hashMap.put("Accept", "application/json");
        HTTPPoster httpPoster = new HTTPPoster();
        try {
            ResponseHolder responseHolder = httpPoster.doPost(url, hashMap, body);
            int responseCode = responseHolder.getResponseCode();
           /* if (responseCode == 401) {
                return null;
            }*/
            String response = responseHolder.getResponseStr();
            Log.e(TAG, "sendDetailsDetails: "+response );
            return response;
        } catch (Exception e) {
            System.out.println("Error Response is ::" + e.getLocalizedMessage());
        }
        return null;
    }

    public String updateDetails(String url, String body,
                                Context context) throws IOException, JSONException,
            InvalidResponseCode {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("Content-Type", "application/json");
        HTTPPoster httpPoster = new HTTPPoster();
        try {
            ResponseHolder responseHolder = httpPoster.doPut(url, hashMap, body);
            if (responseHolder != null) {
                int responseCode = responseHolder.getResponseCode();
                String response = responseHolder.getResponseStr();
                if (responseCode == 401) {
                    return null;
                } else if (responseCode == 200) {
                    return response;
                } else {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null) {
//                        Log.e("Registration ", "Response" + jsonObject.toString());
                        if (jsonObject.has("errordetails")) {
                            return "" + jsonObject.getJSONObject("errordetails").getString("message");
                        }
                    }
                }
                System.out.println("Response is ::" + response);
                return response;
            }
        } catch (Exception e) {
            Log.i("Inspiration", "post logs error  %% " + e.getLocalizedMessage());

        }
        return null;
    }
}