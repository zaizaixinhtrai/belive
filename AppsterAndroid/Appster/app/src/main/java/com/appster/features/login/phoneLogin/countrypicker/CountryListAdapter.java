package com.appster.features.login.phoneLogin.countrypicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appster.R;

import java.util.List;
/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class CountryListAdapter extends BaseAdapter {

    private final Context mContext;
    private static final String TAG = CountryListAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private List<Country> countries;
    private boolean showDialingCode;

    public CountryListAdapter(Context context, List<Country> countries, boolean showDialingCode) {
        mContext = context;
        this.countries = countries;
        this.showDialingCode = showDialingCode;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        Item item;
        Country country = countries.get(position);

        if (convertView == null) {
            item = new Item();
            itemView = inflater.inflate(R.layout.item_country, parent, false);
            item.name = (TextView) itemView.findViewById(R.id.txt_country_name);
            item.code = (TextView) itemView.findViewById(R.id.txt_country_code);
            itemView.setTag(item);
        } else {
            item = (Item) itemView.getTag();
        }

         item.name.setText(country.getName());
        item.code.setText(country.getDialingCode());

        return itemView;
    }

    public void update(List<Country> countries){
        this.countries = countries;
        notifyDataSetChanged();
    }

    public static class Item {
        private TextView name;
        private TextView code;
    }
}
