package com.appster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.appster.interfaces.GiftStoreListener;
import com.appster.models.GiftReceiverModel;
import com.appster.viewholder.GiftStoreHolder;

import java.util.ArrayList;

/**
 * Created by User on 11/4/2015.
 */
public class AdapterGiftStoreTemp extends ArrayAdapter<GiftReceiverModel> implements GiftStoreListener {

    private ArrayList<GiftReceiverModel> arrItem;
    private Context context;
    private int resource;

    public AdapterGiftStoreTemp(Context context, int resource, ArrayList<GiftReceiverModel> arrItem) {
        super(context, resource, arrItem);
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
    public GiftReceiverModel getItem(int position) {

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
        final GiftStoreHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            holder = new GiftStoreHolder(convertView, context);
            convertView.setTag(holder);
        } else {
            holder = (GiftStoreHolder) convertView.getTag();
        }
        holder.init(arrItem.get(position));
        return convertView;
    }
}
