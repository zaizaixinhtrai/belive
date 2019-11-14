package com.appster.webservice.response;

import com.appster.models.SearchModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ngoc on 8/24/2017.
 */

public class LatestPostTopResponseModel {

    @SerializedName("Result")
    @Expose
    public List<SearchModel> recent;
    @SerializedName("NextId")
    @Expose
    public int mNextId;
    @SerializedName("StartPage")
    @Expose
    public int mStartPage;
    @SerializedName("IsEnd")
    @Expose
    public boolean mIsEnd;
}
