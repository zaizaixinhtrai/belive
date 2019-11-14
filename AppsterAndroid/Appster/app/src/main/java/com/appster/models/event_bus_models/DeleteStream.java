package com.appster.models.event_bus_models;

/**
 * Created by linh on 31/05/2017.
 */

public class DeleteStream {
    public String mSlug;
    public DeleteStream(String slug) {
        mSlug = slug;
    }
}
