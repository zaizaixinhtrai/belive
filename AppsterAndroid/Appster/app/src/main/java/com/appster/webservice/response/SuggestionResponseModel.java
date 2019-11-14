package com.appster.webservice.response;

import com.appster.models.SearchModel;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ngoc on 8/24/2017.
 */

public class SuggestionResponseModel {
    @SerializedName("Code")
    @Expose
    public int mCode;
    @SerializedName("Message")
    @Expose
    public String mMessage;
    @SerializedName("CodeDetails")
    @Expose
    public List<CodeDetail> mCodeDetails;
    @SerializedName("Data")
    @Expose
    public ArrayList<SearchModel> recent;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
