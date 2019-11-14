package com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thanhbc on 11/13/17.
 */

public class DailyTreatListInfoEntity {
    /**
     * Items : [{"AmountMinimum":0,"AmountMaximum":0,"Claimed":true,"Value":"string","Id":0,"Title":"string","Description":"string","Image":"string","Amount":0,"Position":0,"TreatColor":0,"TreatRank":0}]
     * Id : 0
     * Title : string
     * Description : string
     */

    @SerializedName("Id")
    public int id;
    @SerializedName("Title")
    public String title;
    @SerializedName("Description")
    public String description;
    @SerializedName("Items")
    public List<TreatEntity> treats;
}
