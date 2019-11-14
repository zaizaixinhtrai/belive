package com.data.entity;

import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 3/13/2018.
 */

public class TriviaRankingListPagingEntity {
    @SerializedName("TriviaRankingType")
    public int triviaRankingType;

    @SerializedName("ImageCacheParam")
    public String imageCacheParam;

    @SerializedName("Users")
    public BaseDataPagingResponseModel<TriviaRankingListEntity> users;
}
