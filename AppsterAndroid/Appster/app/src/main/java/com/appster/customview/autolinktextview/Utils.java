package com.appster.customview.autolinktextview;

import android.util.Log;

import static com.appster.customview.autolinktextview.AutoLinkMode.MODE_CUSTOM;
import static com.appster.customview.autolinktextview.AutoLinkMode.MODE_EMAIL;
import static com.appster.customview.autolinktextview.AutoLinkMode.MODE_HASHTAG;
import static com.appster.customview.autolinktextview.AutoLinkMode.MODE_MENTION;
import static com.appster.customview.autolinktextview.AutoLinkMode.MODE_PHONE;
import static com.appster.customview.autolinktextview.AutoLinkMode.MODE_URL;

/**
 * Created by thanhbc on 6/19/17.
 */

public class Utils {
    private static boolean isValidRegex(String regex){
        return regex != null && !regex.isEmpty() && regex.length() > 2;
    }

    static String getRegexByAutoLinkMode(@AutoLinkMode String anAutoLinkMode,String customRegex) {
        switch (anAutoLinkMode) {
            case MODE_HASHTAG:
                return RegexParser.HASHTAG_PATTERN;
            case MODE_MENTION:
                return RegexParser.MENTION_PATTERN;
            case MODE_URL:
                return RegexParser.URL_PATTERN;
            case MODE_PHONE:
                return RegexParser.PHONE_PATTERN;
            case MODE_EMAIL:
                return RegexParser.EMAIL_PATTERN;
            case MODE_CUSTOM:
                if (!Utils.isValidRegex(customRegex)) {
                    Log.e(AutoLinkTextView.TAG, "Your custom regex is null, returning URL_PATTERN");
                    return RegexParser.URL_PATTERN;
                } else {
                    return customRegex;
                }
            default:
                return RegexParser.URL_PATTERN;
        }
    }
}
