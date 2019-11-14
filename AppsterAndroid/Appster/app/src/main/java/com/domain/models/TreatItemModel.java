package com.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.dialog.DailyTreatRevealPrizeDialog;

/**
 * Created by thanhbc on 11/8/17.
 */

public class TreatItemModel implements DisplayableItem, Parcelable {
    public boolean isClaimed;
    public String prizeImgUrl = "https://static-dev-clients.belive.sg/gift_image/e5085112-b034-4060-acff-8fb5e6853a51Gift_RoseBouquet.png";
    public int prizeAmount = 0;
    public String prizeName = "";
    public int prizeRank = DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_MINI;
    public String value= "";
    public int treatColor=0;
    public String prizeDesc="";
    public int treatId;
    public String title;
    public TreatItemModel() {
    }

    public TreatItemModel(boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    protected TreatItemModel(Builder builder) {
        isClaimed = builder.isClaimed;
        prizeImgUrl = builder.prizeImgUrl;
        prizeAmount = builder.prizeAmount;
        prizeName = builder.prizeName;
        prizeRank = builder.prizeRank;
        value = builder.value;
        treatColor = builder.treatColor;
        prizeDesc = builder.prizeDesc;
        treatId = builder.treatId;
        title = builder.title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isClaimed ? (byte) 1 : (byte) 0);
        dest.writeString(this.prizeImgUrl);
        dest.writeInt(this.prizeAmount);
        dest.writeString(this.prizeName);
        dest.writeInt(this.prizeRank);
        dest.writeString(this.value);
        dest.writeInt(this.treatColor);
        dest.writeString(this.prizeDesc);
        dest.writeInt(this.treatId);
        dest.writeString(this.title);
    }

    protected TreatItemModel(Parcel in) {
        this.isClaimed = in.readByte() != 0;
        this.prizeImgUrl = in.readString();
        this.prizeAmount = in.readInt();
        this.prizeName = in.readString();
        this.prizeRank = in.readInt();
        this.value = in.readString();
        this.treatColor = in.readInt();
        this.prizeDesc = in.readString();
        this.treatId = in.readInt();
        this.title = in.readString();
    }

    public static final Creator<TreatItemModel> CREATOR = new Creator<TreatItemModel>() {
        @Override
        public TreatItemModel createFromParcel(Parcel source) {
            return new TreatItemModel(source);
        }

        @Override
        public TreatItemModel[] newArray(int size) {
            return new TreatItemModel[size];
        }
    };

    public static class Builder<T extends Builder<T>> {
        private boolean isClaimed;
        private String prizeImgUrl;
        private int prizeAmount;
        private String prizeName;
        private int prizeRank;
        private String value;
        private int treatColor;
        private String prizeDesc;
        private int treatId;
        private String title;

        public Builder() {
        }

        public T isClaimed(boolean val) {
            isClaimed = val;
            return (T) this;
        }

        public T prizeImgUrl(String val) {
            prizeImgUrl = val;
            return (T) this;
        }

        public T prizeAmount(int val) {
            prizeAmount = val;
            return (T) this;
        }

        public T prizeName(String val) {
            prizeName = val;
            return (T) this;
        }

        public T prizeRank(int val) {
            prizeRank = val;
            return (T) this;
        }

        public T value(String val) {
            value = val;
            return (T) this;
        }

        public T treatColor(int val) {
            treatColor = val;
            return (T) this;
        }

        public T prizeDesc(String val) {
            prizeDesc = val;
            return (T) this;
        }

        public T treatId(int val) {
            treatId = val;
            return (T) this;
        }

        public T title(String val) {
            title = val;
            return (T) this;
        }

        public TreatItemModel build() {
            return new TreatItemModel(this);
        }
    }
}
