package com.appster.pocket;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appster.models.PocketHistoryModel;
import com.appster.R;
import com.pack.utility.SetDateTime;

import java.util.ArrayList;

/**
 * Created by User on 11/4/2015.
 */
public class AdapterBeansHistory extends ArrayAdapter<PocketHistoryModel> {

    private ArrayList<PocketHistoryModel> arrItem;
    private int resource;
    private Context context;

    public AdapterBeansHistory(Context context, int resource, ArrayList<PocketHistoryModel> arrItem) {
        super(context, resource);

        this.arrItem = arrItem;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        if (arrItem != null) {
            return arrItem.size();
        }
        return 0;
    }

    @Override
    public PocketHistoryModel getItem(int position) {

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

        ViewHolder viewHolder = null;
        PocketHistoryModel item = arrItem.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.txt_message = (TextView) convertView.findViewById(R.id.txt_message);
            viewHolder.txt_date_time = (TextView) convertView.findViewById(R.id.txt_date_time);
            viewHolder.txt_In = (TextView) convertView.findViewById(R.id.txt_In);
            viewHolder.txt_Total = (TextView) convertView.findViewById(R.id.txt_Total);
            viewHolder.txt_date_time = (TextView) convertView.findViewById(R.id.txt_date_time);
            viewHolder.txt_Remark = (TextView) convertView.findViewById(R.id.txt_Remark);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#E7E7E8"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#FAFAFB"));
        }

        viewHolder.txt_date_time.setText(SetDateTime.convertTimePocket(item.getCreated()));

        viewHolder.txt_message.setText(item.getMessage());

        if (item.getAmount() > 0) {
            if (item.getTypeTransaction() == 0) {
                viewHolder.txt_In.setText("-" + item.getAmount() + "");
            } else {
                viewHolder.txt_In.setText(item.getAmount() + "");
            }
        } else {
            viewHolder.txt_In.setText("-" + "");
        }

        viewHolder.txt_Total.setText(item.getTotal() + "");
        viewHolder.txt_Remark.setText(item.getRemark());

        return convertView;
    }

    private class ViewHolder {

        private TextView txt_message;
        private TextView txt_date_time;
        private TextView txt_In;
        private TextView txt_Total;
        private TextView txt_Remark;

    }
}
