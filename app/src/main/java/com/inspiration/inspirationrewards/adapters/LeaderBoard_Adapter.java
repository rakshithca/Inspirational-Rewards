package com.inspiration.inspirationrewards.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.model.LeaderBoardModel;

import java.util.ArrayList;

public class LeaderBoard_Adapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    ArrayList<LeaderBoardModel> leaderBoardModels;

    public LeaderBoard_Adapter(Context context, ArrayList<LeaderBoardModel> leaderBoardModels) {
        this.context = context;
        this.leaderBoardModels = leaderBoardModels;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return leaderBoardModels.size();
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
            convertView = inflater.inflate(R.layout.card_view_users, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pos = position;

        LeaderBoardModel leaderBoardModel = leaderBoardModels.get(holder.pos);
        holder.nameTV.setText(leaderBoardModel.getFirstName()+", "+leaderBoardModel.getLastName());
        holder.postionDepartmentTV.setText(leaderBoardModel.getDepartment()+", "+leaderBoardModel.getPosition());
        holder.rewardPointsTV.setText(leaderBoardModel.getRewards());

        byte[] decodedString = Base64.decode(leaderBoardModel.getImageBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.userIMG.setImageBitmap(decodedByte);

        return convertView;
    }

    public static class ViewHolder {
        public TextView nameTV, rewardPointsTV, postionDepartmentTV;
        private ImageView userIMG;
        int pos;

        public ViewHolder(View view) {
            userIMG = (ImageView) view.findViewById(R.id.userIMG);
            nameTV = (TextView) view.findViewById(R.id.nameTV);
            rewardPointsTV = (TextView) view.findViewById(R.id.rewardPointsTV);
            postionDepartmentTV = (TextView) view.findViewById(R.id.postionDepartmentTV);
        }
    }
}
