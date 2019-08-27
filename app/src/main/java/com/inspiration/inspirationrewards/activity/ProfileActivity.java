package com.inspiration.inspirationrewards.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inspiration.inspirationrewards.JSON.PostDetailsAsyncTask;
import com.inspiration.inspirationrewards.JSON.StatusNotifier;
import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.adapters.RewardsPoints_Adapter;
import com.inspiration.inspirationrewards.model.LeaderBoardModel;
import com.inspiration.inspirationrewards.adapters.LeaderBoard_Adapter;
import com.inspiration.inspirationrewards.model.RewardModel;
import com.inspiration.inspirationrewards.utils.APIs;
import com.inspiration.inspirationrewards.utils.StoredData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends Activity implements StatusNotifier {

    RewardsPoints_Adapter rewardsPoints_adapter;
    ArrayList<RewardModel> rewardModels = new ArrayList<>();
    ListView rewardsLV;
    ImageView iv_profile_image;
    TextView fullnameTV, userNameTV, addressTV, pointsAwardedTV, departmentTV, positionTV, pointsToAwardedTV, yourstoryET, rewardsHistoryTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ((ImageView) findViewById(R.id.backIconIMG)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        ((ImageView) findViewById(R.id.leaderBoardIMG)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LeaderBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        ((ImageView) findViewById(R.id.editIMG)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        rewardsLV = (ListView) findViewById(R.id.rewardsLV);
        rewardsPoints_adapter = new RewardsPoints_Adapter(ProfileActivity.this, rewardModels);
        rewardsLV.setAdapter(rewardsPoints_adapter);

        // Add a header to the ListView
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.profilepage_header, rewardsLV, false);

        initViews(header);
        if (getIntent().getExtras().getBoolean("defaultLogin")) {
            profileData();
        } else {
            assignValuesToViews();
        }
        rewardsLV.addHeaderView(header);
    }

    private void assignValuesToViews() {
        try {
            if (StoredData.getString(ProfileActivity.this, "loginResult") != null) {

                JSONObject jsonObject = new JSONObject(StoredData.getString(ProfileActivity.this, "loginResult"));
                fullnameTV.setText(jsonObject.getString("firstName") + " " + jsonObject.getString("lastName"));
                userNameTV.setText(jsonObject.getString("username"));
                addressTV.setText(jsonObject.getString("location"));
                pointsToAwardedTV.setText(jsonObject.getString("pointsToAward"));
                departmentTV.setText(jsonObject.getString("department"));
                positionTV.setText(jsonObject.getString("position"));
                yourstoryET.setText(jsonObject.getString("story"));

                byte[] decodedString = Base64.decode(jsonObject.getString("imageBytes"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                iv_profile_image.setImageBitmap(decodedByte);

                if (!jsonObject.getString("rewards").equalsIgnoreCase("null")) {
                    if (jsonObject.getJSONArray("rewards") != null) {
                        rewardModels.clear();
                        int rewardPoints = 0;
                        JSONArray jsonArray = jsonObject.getJSONArray("rewards");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject rewardsJsonObject = jsonArray.getJSONObject(i);
                            rewardModels.add(new RewardModel(rewardsJsonObject.getString("studentId"), rewardsJsonObject.getString("name"),
                                    rewardsJsonObject.getString("username"), rewardsJsonObject.getString("date"),
                                    rewardsJsonObject.getString("notes"), rewardsJsonObject.getString("value")));
                            rewardPoints = rewardPoints + Integer.parseInt(rewardsJsonObject.getString("value"));
                        }
                        pointsAwardedTV.setText(rewardPoints + "");
                        rewardsHistoryTV.setText("Reward History(" + rewardModels.size() + ")");
                        rewardsPoints_adapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews(ViewGroup header) {
        iv_profile_image = ((ImageView) header.findViewById(R.id.iv_profile_image));
        fullnameTV = (TextView) header.findViewById(R.id.fullnameTV);
        userNameTV = (TextView) header.findViewById(R.id.userNameTV);
        addressTV = (TextView) header.findViewById(R.id.addressTV);
        pointsAwardedTV = (TextView) header.findViewById(R.id.pointsAwardedTV);
        departmentTV = (TextView) header.findViewById(R.id.departmentTV);
        positionTV = (TextView) header.findViewById(R.id.positionTV);
        pointsToAwardedTV = (TextView) header.findViewById(R.id.pointsToAwardedTV);
        yourstoryET = (TextView) header.findViewById(R.id.yourstoryET);
        rewardsHistoryTV = (TextView) header.findViewById(R.id.rewardsHistoryTV);
    }

    private void profileData() {
        JSONObject jsonObject = new JSONObject();
        try {
//A20424771
            JSONObject loginResult = new JSONObject(StoredData.getString(ProfileActivity.this, "loginResult"));
            jsonObject.put("studentId", loginResult.getString("studentId"));
            jsonObject.put("username", loginResult.getString("username"));
            jsonObject.put("password", loginResult.getString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostDetailsAsyncTask asynctask = new PostDetailsAsyncTask(
                ProfileActivity.this);
        asynctask.execute(APIs.BASE_URL + APIs.LOGIN, jsonObject.toString());
    }

    @Override
    public void OnSuccess(String response) {
        StoredData.saveString(ProfileActivity.this, "loginResult", response);
        assignValuesToViews();
    }

    @Override
    public void OnError() {
        LayoutInflater inflater = getLayoutInflater();
        View custom_toast = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_root));
        TextView tv = custom_toast.findViewById(R.id.toast_text);
        tv.setText("Login Failed");
        Toast toast = new Toast(getApplicationContext());
        toast.setView(custom_toast);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        //Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
