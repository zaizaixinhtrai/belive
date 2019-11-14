package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 7/13/2016.
 */
public class PopularByTagRequestModel extends BasePagingRequestModel {
    @SerializedName("TagId") @Expose
    private int mTagId;

    public int getTagId() {
        return mTagId;
    }

    public void setTagId(int TagId) {
        this.mTagId = TagId;
    }

}
