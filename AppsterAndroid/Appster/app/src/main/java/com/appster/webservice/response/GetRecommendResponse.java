package com.appster.webservice.response;

import com.appster.models.SearchModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 9/13/2016.
 */
public class GetRecommendResponse {
    @SerializedName("Suggest")
    @Expose
    public List<SearchModel> suggest;
    @SerializedName("Recent")
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
