package com.appster.manager;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.appster.webservice.AppsterWebServices;

import java.util.Locale;

/**
 * Created by Ngoc on 10/22/2015.
 */
public class AppLanguage {

    public static void setLocale(Activity activity, String lang) {
        AppsterWebServices.resetAppsterWebserviceAPI();
//        Locale myLocale = new Locale(lang);
//        Resources res = activity.getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
    }
}
