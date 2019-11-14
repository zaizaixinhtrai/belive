package com.appster.refill;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appster.AppsterApplication;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.R;
import com.appster.iap_manager.InAppBillHelper;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.CountryCode;

public class RefillClassAdapter extends BaseAdapter {

    private Activity activity;
    private List<RefillListItem> arrObjects;


    public RefillClassAdapter(Activity activity, List<RefillListItem> objects) {
        this.activity = activity;
        this.arrObjects = objects;
    }

    @Override
    public int getCount() {

        if (arrObjects != null) {
            return arrObjects.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (arrObjects != null) {
            return arrObjects.get(position);
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
        final RefillListItem item = arrObjects.get(position);

        if (convertView == null) {
            LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mLayoutInflater.inflate(
                    R.layout.grid_view_topup, parent, false);

            holder = new AdapterHolder();
            holder.rlItemPurchased = (RelativeLayout) convertView.findViewById(R.id.item_purchased);
            holder.coin_image = (ImageView) convertView.findViewById(R.id.coin_image);
            holder.usd_price = (TextView) convertView.findViewById(R.id.usd_price);
            holder.percentage = (TextView) convertView.findViewById(R.id.percentage);
            holder.txt_usd = (TextView) convertView.findViewById(R.id.txt_usd);
            holder.name_icon = (TextView) convertView.findViewById(R.id.name_icon);

            convertView.setTag(holder);

        } else {
            holder = (AdapterHolder) convertView.getTag();
        }

        ImageLoaderUtil.displayMediaImage(activity, item.getImage(), holder.coin_image);
        holder.percentage.setText("+ " + item.getPercentage() + " " + activity.getString(R.string.refill_percen));
        holder.txt_usd.setText(String.format(activity.getString(R.string.refill_usd), item.getPrice_usd()));
        if (AppsterApplication.mAppPreferences.getUserCountryCode().equals(CountryCode.CHINA)) {
            holder.txt_usd.setText(String.format(activity.getString(R.string.refill_cny), item.getPrice_cny()));
        }
        holder.usd_price.setText(item.getBean() + "");
        holder.name_icon.setText(item.getName());

        holder.rlItemPurchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                    ((BaseToolBarActivity) activity).goingLoginScreen();
                } else {

                    if (((BaseActivity) activity).isMaintenance() || ((BaseActivity) activity).preventMultiClicks()) {
                        return;
                    }

                    try {
                            InAppBillHelper.purchase(activity, item.getIos_store_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return convertView;
    }


    private class AdapterHolder {
        private RelativeLayout rlItemPurchased;
        private ImageView coin_image;
        private TextView usd_price;
        private TextView percentage;
        private TextView txt_usd;
        private TextView name_icon;
    }
}
