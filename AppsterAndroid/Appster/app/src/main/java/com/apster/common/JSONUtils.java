package com.apster.common;

import com.appster.models.BanUserMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by thanhbc on 4/18/17.
 */

public final class JSONUtils {
    private static final Gson gson = new Gson();

    private JSONUtils(){}

    public static boolean isJSONValid(String jsonInString) {
        try {
            gson.fromJson(jsonInString, BanUserMessage.class);
            return true;
        } catch(JsonSyntaxException ex) {
            return false;
        }
    }
}
