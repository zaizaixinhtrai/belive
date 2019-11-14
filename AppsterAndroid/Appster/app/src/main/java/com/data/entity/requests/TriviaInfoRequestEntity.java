package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaInfoRequestEntity {
    @SerializedName("TriviaId")
    private final int id;

    @SerializedName("ClientCurrentDateTime")
    private final long clientCurrentDateTime;

    public TriviaInfoRequestEntity(int id, long clientCurrentDateTime) {
        this.id = id;
        this.clientCurrentDateTime = clientCurrentDateTime;
    }
}
