package com.appster.trending;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appster.models.LeaderBoardModel;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.utility.ImageLoaderUtil;

import java.util.ArrayList;

/**
 * Created by User on 10/6/2015.
 */
public class AdapterTrending extends BaseAdapter {

    private ArrayList<LeaderBoardModel> arrItem;
    private Activity activity;

    public AdapterTrending(ArrayList<LeaderBoardModel> arrItem, Activity activity) {
        this.arrItem = arrItem;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        if (arrItem != null) {
            return arrItem.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (arrItem != null) {
            return arrItem.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AdapterHolder holder;
        final LeaderBoardModel item = arrItem.get(position);

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(activity);
            convertView = inflater.inflate(
                    R.layout.trending_listview_row, parent, false);
            holder = new AdapterHolder(item);
            holder.imv_user = (ImageView) convertView.findViewById(R.id.imv_user);
            holder.txt_order = (TextView) convertView.findViewById(R.id.txt_order);
            holder.txt_gift_receipt = (TextView) convertView.findViewById(R.id.txt_gift_receipt);
            holder.txt_follower = (TextView) convertView.findViewById(R.id.txt_follower);
            holder.txt_gift_send = (TextView) convertView.findViewById(R.id.txt_gift_send);
            holder.txt_follower2 = (TextView) convertView.findViewById(R.id.txt_follower2);
            holder.txt_username = (TextView) convertView.findViewById(R.id.txt_username);
            holder.imv_user_top = (ImageView) convertView.findViewById(R.id.imv_user_top);
            holder.view_hide = (View) convertView.findViewById(R.id.view_hide);
            holder.ll_top = (LinearLayout) convertView.findViewById(R.id.ll_top);

            convertView.setTag(holder);

        } else {
            holder = (AdapterHolder) convertView.getTag();
        }

        // Set background
        if (position == 0) {

            holder.ll_top.setBackgroundResource(R.drawable.trending_listview_top_list);
            holder.txt_order.setTextColor(Color.parseColor("#F7D66A"));
            holder.imv_user_top.setVisibility(View.VISIBLE);
            holder.view_hide.setVisibility(View.VISIBLE);

        } else {

            holder.txt_order.setTextColor(Color.parseColor("#000000"));
            holder.ll_top.setBackgroundResource(R.drawable.trending_listview_non_top);
            holder.imv_user_top.setVisibility(View.GONE);
            holder.view_hide.setVisibility(View.GONE);
        }

        // Set user image
        ImageLoaderUtil.displayUserImage(activity, item.getProfilePic(),
                holder.imv_user);

        // Set user name
        holder.txt_username.setText(item.getDisplay_name());
        holder.txt_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseToolBarActivity) activity).startActivityProfile(item.getId(), item.getDisplay_name());
            }
        });

        // Set order
        holder.txt_order.setText(position + 1 + "");

        // Gift receipt
        holder.txt_gift_receipt.setText(item.getGift_received_count() + "");

        // Set follower
        holder.txt_follower.setText(item.getFollowers_count() + "");
        holder.txt_follower2.setText(item.getFollowers_count() + "");

        // Set Gift send
        holder.txt_gift_send.setText(item.getGift_sent_count() + "");

        // View user profile
        holder.imv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseToolBarActivity) activity).startActivityProfile(item.getId(), item.getDisplay_name());
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseToolBarActivity) activity).startActivityProfile(item.getId(), item.getDisplay_name());
            }
        });

        return convertView;
    }

    private class AdapterHolder {
        private ImageView imv_user;
        private TextView txt_order;
        private TextView txt_username;
        private TextView txt_gift_receipt;
        private TextView txt_follower;
        private TextView txt_gift_send;
        private TextView txt_follower2;
        private ImageView imv_user_top;
        private View view_hide;
        private LinearLayout ll_top;

        private LeaderBoardModel item;

        public AdapterHolder(LeaderBoardModel item) {
            this.item = item;
        }

    }
}
