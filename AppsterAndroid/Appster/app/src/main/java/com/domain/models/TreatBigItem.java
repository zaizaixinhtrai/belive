package com.domain.models;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatBigItem extends TreatItemModel {
    protected TreatBigItem(Builder builder) {
        super(builder);
    }

    public static class Builder extends TreatItemModel.Builder<Builder> {
        public Builder() {
        }


        public TreatBigItem build() {
            return new TreatBigItem(this);
        }
    }
}
