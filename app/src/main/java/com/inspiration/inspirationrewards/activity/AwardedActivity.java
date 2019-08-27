package com.inspiration.inspirationrewards.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inspiration.inspirationrewards.JSON.PostDetailsAsyncTask;
import com.inspiration.inspirationrewards.JSON.StatusNotifier;
import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.model.RewardModel;
import com.inspiration.inspirationrewards.utils.APIs;
import com.inspiration.inspirationrewards.utils.StoredData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AwardedActivity extends Activity implements StatusNotifier {

    ImageView iv_profile_image;
    TextView fullnameTV, userNameTV, addressTV, pointsAwardedTV, departmentTV, positionTV, pointsToAwardedTV, yourstoryET, rewardsHistoryTV, textCountTV;
    EditText commentET, rewardPointsET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awarded);
        initViews();
        assignDataToViews();

        ((ImageView) findViewById(R.id.saveIMG)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rewardPointsET.getText().toString().trim().length() > 0) {
                    if (commentET.getText().toString().trim().length() > 0) {
                        try {
                            final Dialog d = new Dialog(AwardedActivity.this);
                            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            d.setContentView(R.layout.save_changes);
                            TextView t1 = d.findViewById(R.id.custom_dialogue_text);
                            t1.setText("Add Reward Points?");
                            TextView t2 = d.findViewById(R.id.custom_dialogue_subtext);
                            JSONObject userJsonObject = new JSONObject(LeaderBoardActivity.userDataString);
                            t2.setText("Add Rewards For " + userJsonObject.getString("firstName") + " " + userJsonObject.getString("lastName") + " ?");
                            Button scancel = d.findViewById(R.id.btn_scancel);
                            Button okay = d.findViewById(R.id.btn_ok);
                            okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.dismiss();
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(((ImageView) findViewById(R.id.saveIMG)).getWindowToken(), 0);
                                    sendRewardsDataToServer();
                                }
                            });
                            scancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.dismiss();
                                    showCustomToast("Reward Points not added", Toast.LENGTH_LONG);
                                }
                            });
                            d.show();
                        } catch (JSONException je){
                            Log.d("Save Awards: ", "onClick: " + je);
                        }
                    } else {
                        showCustomToast("Please Enter Comments", Toast.LENGTH_SHORT);
                    }
                } else {
                    showCustomToast("Please Enter Reward Points", Toast.LENGTH_SHORT);
                }


            }
        });
    }

    private void showCustomToast(String msg, int length){
        LayoutInflater inflater = getLayoutInflater();
        View custom_toast = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_root));
        TextView tv = custom_toast.findViewById(R.id.toast_text);
        tv.setText(msg);
        Toast toast = new Toast(getApplicationContext());
        toast.setView(custom_toast);
        toast.setDuration(length);
        toast.show();
    }
    private void sendRewardsDataToServer() {
        JSONObject mainJsonObject = new JSONObject();
        try {
        JSONObject targetJsonObject = new JSONObject();
            JSONObject userJsonObject = new JSONObject(LeaderBoardActivity.userDataString);
            targetJsonObject.put("studentId", userJsonObject.getString("studentId"));
            targetJsonObject.put("username", userJsonObject.getString("username"));
            targetJsonObject.put("name", userJsonObject.getString("firstName") + " " + userJsonObject.getString("lastName"));
            targetJsonObject.put("date", new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
            targetJsonObject.put("notes", commentET.getText().toString());
            targetJsonObject.put("value", rewardPointsET.getText().toString());
        JSONObject jsonObject = new JSONObject();
            JSONObject loginResult = new JSONObject(StoredData.getString(AwardedActivity.this, "loginResult"));
            jsonObject.put("studentId", loginResult.getString("studentId"));
            jsonObject.put("username", loginResult.getString("username"));
            jsonObject.put("password", loginResult.getString("password"));
        mainJsonObject.put("target",targetJsonObject);
        mainJsonObject.put("source",jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PostDetailsAsyncTask asynctask = new PostDetailsAsyncTask(
                AwardedActivity.this);
        asynctask.execute(APIs.BASE_URL + APIs.REWARDS, mainJsonObject.toString());
    }

    private void assignDataToViews() {

        if (LeaderBoardActivity.userDataString != null) {
            try {
                JSONObject jsonObject = new JSONObject(LeaderBoardActivity.userDataString);
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
                        int rewardPoints = 0;
                        JSONArray jsonArray = jsonObject.getJSONArray("rewards");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject rewardsJsonObject = jsonArray.getJSONObject(i);
                            rewardPoints = rewardPoints + Integer.parseInt(rewardsJsonObject.getString("value"));
                        }
                        pointsAwardedTV.setText(rewardPoints + "");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initViews() {
        iv_profile_image = ((ImageView) findViewById(R.id.iv_profile_image));
        fullnameTV = (TextView) findViewById(R.id.fullnameTV);
        userNameTV = (TextView) findViewById(R.id.userNameTV);
        addressTV = (TextView) findViewById(R.id.addressTV);
        pointsAwardedTV = (TextView) findViewById(R.id.pointsAwardedTV);
        departmentTV = (TextView) findViewById(R.id.departmentTV);
        positionTV = (TextView) findViewById(R.id.positionTV);
        pointsToAwardedTV = (TextView) findViewById(R.id.pointsToAwardedTV);
        yourstoryET = (TextView) findViewById(R.id.yourstoryET);
        textCountTV = (TextView) findViewById(R.id.textCountTV);
        rewardsHistoryTV = (TextView) findViewById(R.id.rewardsHistoryTV);
        commentET = (EditText) findViewById(R.id.commentET);
        rewardPointsET = (EditText) findViewById(R.id.rewardPointsET);
        rewardsHistoryTV.setVisibility(View.GONE);
        commentET.addTextChangedListener(mTextEditorWatcher);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(AwardedActivity.this, LeaderBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnSuccess(String response) {
        showCustomToast("Add Reward Succeeded", Toast.LENGTH_SHORT);
        Intent intent = new Intent(AwardedActivity.this, LeaderBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void OnError() {

    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            textCountTV.setText("Comment: ("+String.valueOf(s.length())+" of 80)");
        }
        public void afterTextChanged(Editable s) {
        }
    };
}
