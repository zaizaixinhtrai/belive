package com.appster.features.login.phoneLogin.countrypicker;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.appster.AppsterApplication.getApplication;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class Utils {


    static List<Country> COUNTRIES = null;

    public static int getMipmapResId(Context context, String drawableName) {
        return context.getResources().getIdentifier(
                drawableName.toLowerCase(Locale.ENGLISH), "mipmap", context.getPackageName());
    }

    public static List<Country> getCountriesJSON(Context context) {
        String json = null;
        InputStream is = null;
        try {
            is = context.getAssets().open("country_code.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, "UTF-8");
            is.close();
        } catch (IOException ex) {
            Timber.e(ex);
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }

        return Arrays.asList(new Gson().fromJson(json, Country[].class));
    }

    public static String getCountryCodeByPhoneNum(String phoneNum) {
        if (COUNTRIES == null) COUNTRIES = getCountriesJSON(getApplication());
        for (Country country : COUNTRIES) {
            if (phoneNum.contains(country.getDialingCode())) {
                return country.getDialingCode();
            }
        }
        return "";
    }

    public static String getPhoneNumWithoutCountryCode(String phoneNum) {
        if (COUNTRIES == null) COUNTRIES = getCountriesJSON(getApplication());
        String noCountryCodePhoneNum = phoneNum;
        for (Country country : COUNTRIES) {
            if (phoneNum.contains(country.getDialingCode())) {
                noCountryCodePhoneNum = phoneNum.replace(country.getDialingCode(), "");
            }
        }
        return noCountryCodePhoneNum;
    }

    public static Country getDefaultCountry(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String code = mTelephonyManager.getNetworkCountryIso();
        if (TextUtils.isEmpty(code)) {
            code = context.getResources().getConfiguration().locale.getCountry();
        }
        List<Country> countryList = getCountriesJSON(context);
        for (Country country : countryList) {
            if (country.getIsoCode().equalsIgnoreCase(code))
                return country;
        }
        return new Country();
    }
}
