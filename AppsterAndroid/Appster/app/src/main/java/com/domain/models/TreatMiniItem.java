package com.domain.models;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatMiniItem extends TreatItemModel {
    protected TreatMiniItem(Builder builder) {
        super(builder);
    }

    public static class Builder extends TreatItemModel.Builder<Builder> {
        public Builder() {
        }


        public TreatMiniItem build() {
            return new TreatMiniItem(this);
        }
    }
}
