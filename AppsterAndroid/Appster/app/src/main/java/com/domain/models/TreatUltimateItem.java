package com.domain.models;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatUltimateItem extends TreatItemModel {
    protected TreatUltimateItem(Builder builder) {
        super(builder);
    }

    public static class Builder extends TreatItemModel.Builder<Builder> {
        public Builder() {
        }


        public TreatUltimateItem build() {
            return new TreatUltimateItem(this);
        }
    }
}
