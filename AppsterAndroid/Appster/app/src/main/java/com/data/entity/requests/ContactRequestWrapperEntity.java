package com.data.entity.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thanhbc on 1/9/18.
 */

public class ContactRequestWrapperEntity {
    @SerializedName("Contacts")
    @Expose
    final List<ContactRequestEntity> contacts;

    public ContactRequestWrapperEntity(List<ContactRequestEntity> contacts) {
        this.contacts = contacts;
    }
}
