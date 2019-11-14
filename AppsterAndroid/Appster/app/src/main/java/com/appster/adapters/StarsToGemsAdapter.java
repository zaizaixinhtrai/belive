package com.appster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appster.models.StarsToGemsModel;
import com.appster.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by User on 8/22/2016.
 */

public class StarsToGemsAdapter extends ArrayAdapter<StarsToGemsModel> {
    final private Context context;
    final private ArrayList<StarsToGemsModel> listComent;
    private StarsToGemsListener starsToGemsListener;

    public void setStarsToGemsListener(StarsToGemsListener starsToGemsListener) {
        this.starsToGemsListener = starsToGemsListener;
    }

    public StarsToGemsAdapter(Context context, int resource, ArrayList<StarsToGemsModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.listComent = objects;
    }

    @Override
    public StarsToGemsModel getItem(int position) {
        if (listComent != null) {
            return listComent.get(position);
        }

        return null;
    }

    @Override
    public int getCount() {

        if (listComent != null) {
            return listComent.size();
        }

        return 0;
    }

    class ViewHolder {
        TextView gold;
        TextView bean;
        LinearLayout startChange;
        TextView paddingBottom;
        TextView paddingTop;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final StarsToGemsModel model = listComent.get(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stars_to_gems_adapter_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bean = (TextView) convertView.findViewById(R.id.txt_Bean);
            viewHolder.gold = (TextView) convertView.findViewById(R.id.txt_gold);
            viewHolder.startChange = (LinearLayout) convertView.findViewById(R.id.start_change);
            viewHolder.paddingBottom = (TextView) convertView.findViewById(R.id.paddingBottom);
            viewHolder.paddingTop = (TextView) convertView.findViewById(R.id.paddingTop);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bean.setText(String.format(Locale.US,"%,d",model.getBean()));
        viewHolder.gold.setText(String.format(Locale.US,"%,d",model.getGold()));
        viewHolder.startChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (starsToGemsListener != null) {
                    starsToGemsListener.onClickViewExchange(position);
                }
            }
        });

        if (position == 0) {
            viewHolder.paddingTop.setVisibility(View.VISIBLE);
        } else {
            viewHolder.paddingTop.setVisibility(View.GONE);
        }

        if (position == getCount() - 1) {
            viewHolder.paddingBottom.setVisibility(View.VISIBLE);
        } else {
            viewHolder.paddingBottom.setVisibility(View.GONE);
        }

        return convertView;
    }

    public interface StarsToGemsListener {
        void onClickViewExchange(int position);
    }

}

