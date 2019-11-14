package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 10/4/2017.
 */

public class NewWallfeedResponseModel {
    @Expose
    @SerializedName("NumberNewItem")
    public int numberNewItem;
}
