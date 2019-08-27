package com.inspiration.inspirationrewards.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.model.LeaderBoardModel;
import com.inspiration.inspirationrewards.model.RewardModel;

import java.util.ArrayList;

public class RewardsPoints_Adapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    ArrayList<RewardModel> rewardModels;

    public RewardsPoints_Adapter(Context context, ArrayList<RewardModel> rewardModels) {
        this.context = context;
        this.rewardModels = rewardModels;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
//        return leaderBoardModels.size();
        return rewardModels.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.card_view_reward_users, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pos = position;

        RewardModel rewardModel = rewardModels.get(holder.pos);
        holder.dateTV.setText(rewardModel.getDate());
        holder.nameTV.setText(rewardModel.getName());
        holder.rewardPointsTV.setText(rewardModel.getValue());
        holder.commentsTV.setText(rewardModel.getNotes());
        return convertView;
    }

    public static class ViewHolder {
        public TextView dateTV, nameTV, rewardPointsTV, commentsTV;
        int pos;

        public ViewHolder(View view) {
            dateTV = (TextView) view.findViewById(R.id.dateTV);
            nameTV = (TextView) view.findViewById(R.id.nameTV);
            rewardPointsTV = (TextView) view.findViewById(R.id.rewardPointsTV);
            commentsTV = (TextView) view.findViewById(R.id.commentsTV);
        }
    }
}
