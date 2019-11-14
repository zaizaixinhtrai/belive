package com.appster.features.login.phoneLogin.countrypicker;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.view.ViewCompat;

import com.appster.R;
import com.appster.customview.CustomFontEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class CountryPickerDialog extends AppCompatDialog {
    private final static int DELAYED_TIME_OF_CLICK = 500;

    private List<Country> mFilteredCountries;
    private List<Country> mCountries;
    private CountryPickerCallbacks callbacks;
    private ListView listview;
    private CustomFontEditText mEdtSearchBar;
    private String headingCountryCode;
    private boolean showDialingCode;
    CountryListAdapter adapter;
    private long mTimeCreated;

    public CountryPickerDialog(Context context, CountryPickerCallbacks callbacks) {
        this(context, callbacks, null, true);
    }

    public CountryPickerDialog(Context context, CountryPickerCallbacks callbacks, @Nullable String headingCountryCode) {
        this(context, callbacks, headingCountryCode, true);
    }

    /**
     * You can set the heading country in headingCountryCode to show
     * your favorite country as the head of the list
     *
     * @param context
     * @param callbacks
     * @param headingCountryCode
     */
    public CountryPickerDialog(Context context, CountryPickerCallbacks callbacks,
                               @Nullable String headingCountryCode, boolean showDialingCode) {
        super(context);
        this.callbacks = callbacks;
        this.headingCountryCode = headingCountryCode;
        this.showDialingCode = showDialingCode;
        mCountries = Utils.getCountriesJSON(this.getContext());
        mFilteredCountries = filter("");
//        Collections.sort(mFilteredCountries, new Comparator<Country>() {
//            @Override
//            public int compare(Country country1, Country country2) {
//                final Locale locale = getContext().getResources().getConfiguration().locale;
//                final Collator collator = Collator.getInstance(locale);
//                collator.setStrength(Collator.PRIMARY);
//                return collator.compare(
//                        new Locale(locale.getLanguage(), country1.getIsoCode()).getDisplayCountry(),
//                        new Locale(locale.getLanguage(), country2.getIsoCode()).getDisplayCountry());
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.country_picker);
        ViewCompat.setElevation(getWindow().getDecorView(), 3);
        listview = (ListView) findViewById(R.id.country_picker_listview);
        mEdtSearchBar = (CustomFontEditText) findViewById(R.id.edt_input_search);
        adapter = new CountryListAdapter(this.getContext(), mFilteredCountries, showDialingCode);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener((parent, view, position, id) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mTimeCreated <= DELAYED_TIME_OF_CLICK) return;
            hide();
            Country country = mFilteredCountries.get(position);
            callbacks.onCountrySelected(country, Utils.getMipmapResId(getContext(),
                    country.getIsoCode().toLowerCase(Locale.ENGLISH) + "_flag"));
        });
        mEdtSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query(s.toString());
            }
        });

        scrollToHeadingCountry();
    }

    @Override
    public void show() {
        super.show();
        mTimeCreated = System.currentTimeMillis();
    }

    private void query(String q){
        mFilteredCountries = filter(q);
        adapter.update(mFilteredCountries);
    }

    private List<Country> filter(String q){
        if (TextUtils.isEmpty(q)) return mCountries;
        List<Country> result = new ArrayList<>();
        for (Country country : mCountries){
            if (country.getName().toLowerCase().contains(q)
                    || country.getIsoCode().toLowerCase().contains(q)
                    || country.getDialingCode().toLowerCase().contains(q))
                result.add(country);
        }
        return result;
    }

    private void scrollToHeadingCountry() {
        if (headingCountryCode != null) {
            for (int i = 0; i < listview.getCount(); i++) {
                if (((Country) listview.getItemAtPosition(i)).getIsoCode().toLowerCase()
                        .equals(headingCountryCode.toLowerCase())) {
                    listview.setSelection(i);
                }
            }
        }
    }

    public Country getCountryFromIsoCode(String isoCode) {
        for (int i = 0; i < mFilteredCountries.size(); i++) {
            if (mFilteredCountries.get(i).getIsoCode().equals(isoCode.toUpperCase())) {
                return mFilteredCountries.get(i);
            }
        }
        return null;
    }
}
