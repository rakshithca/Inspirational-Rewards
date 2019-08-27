package com.inspiration.inspirationrewards.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.inspiration.inspirationrewards.JSON.PostDetailsAsyncTask;
import com.inspiration.inspirationrewards.JSON.StatusNotifier;
import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.adapters.LeaderBoard_Adapter;
import com.inspiration.inspirationrewards.model.LeaderBoardModel;
import com.inspiration.inspirationrewards.utils.APIs;
import com.inspiration.inspirationrewards.utils.StoredData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class LeaderBoardActivity extends Activity implements StatusNotifier {

    LeaderBoard_Adapter leaderBoard_adapter;
    ArrayList<LeaderBoardModel> leaderBoardModels = new ArrayList<>();
    ArrayList<String> userData = new ArrayList<>();
    ListView leaderBoardLV;
    public static String userDataString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        leaderBoardLV = (ListView) findViewById(R.id.leaderBoardLV);
        leaderBoard_adapter = new LeaderBoard_Adapter(LeaderBoardActivity.this, leaderBoardModels);
        leaderBoardLV.setAdapter(leaderBoard_adapter);
        leaderBoardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LeaderBoardActivity.this, AwardedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                userDataString = userData.get(position);
                startActivity(intent);
            }
        });
        ((ImageView) findViewById(R.id.backIconIMG)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        allProfilesData();
    }

    private void allProfilesData() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject loginResult = new JSONObject(StoredData.getString(LeaderBoardActivity.this, "loginResult"));
            jsonObject.put("studentId", loginResult.getString("studentId"));
            jsonObject.put("username", loginResult.getString("username"));
            jsonObject.put("password", loginResult.getString("password"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostDetailsAsyncTask asynctask = new PostDetailsAsyncTask(
                LeaderBoardActivity.this);
        asynctask.execute(APIs.BASE_URL + APIs.ALL_PROFILES, jsonObject.toString());
    }

    @Override
    public void OnSuccess(String response) {

        if (response != null) {
            userData.clear();
            leaderBoardModels.clear();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject userJsonObject = jsonArray.getJSONObject(i);
                    userData.add(userJsonObject.toString());
                    int totalRewardPoints = 0;
                    if (!userJsonObject.getString("rewards").equalsIgnoreCase("null")) {
                        if (userJsonObject.getJSONArray("rewards") != null) {
                            JSONArray rewardsJsonArray = userJsonObject.getJSONArray("rewards");
                            for (int j = 0; j < rewardsJsonArray.length(); j++) {
                                totalRewardPoints = totalRewardPoints + Integer.parseInt(rewardsJsonArray.getJSONObject(j).getString("value"));
                            }
                        }
                    }
                    leaderBoardModels.add(new LeaderBoardModel(userJsonObject.getString("studentId"), userJsonObject.getString("firstName"),
                            userJsonObject.getString("lastName"), userJsonObject.getString("username"), userJsonObject.getString("department"),
                            userJsonObject.getString("story"), userJsonObject.getString("position"), userJsonObject.getString("pointsToAward"),
                            userJsonObject.getString("admin"), userJsonObject.getString("imageBytes"), userJsonObject.getString("location"),
                            totalRewardPoints+""));
                }
                //Collections.sort(leaderBoardModels);
                leaderBoard_adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnError() {

    }
}
