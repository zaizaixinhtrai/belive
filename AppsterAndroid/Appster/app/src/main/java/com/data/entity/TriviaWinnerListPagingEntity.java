package com.data.entity;

import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 3/14/2018.
 */

public class TriviaWinnerListPagingEntity {
    @SerializedName("Winners")
    public BaseDataPagingResponseModel<TriviaRankingListEntity> users;
}
