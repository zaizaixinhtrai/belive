package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hassanabidpk on 6/29/18.
 */

public class TriviaFinishRequestEntity {
    @SerializedName("TriviaId")
    private final int id;


    public TriviaFinishRequestEntity(int id) {
        this.id = id;
    }
}
