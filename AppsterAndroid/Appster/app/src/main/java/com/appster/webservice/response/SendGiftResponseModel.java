package com.appster.webservice.response;

import com.appster.models.DailyTopFanModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by User on 10/20/2015.
 */

public class SendGiftResponseModel {
    @SerializedName("Sender")
    @Expose
    private SenderBean mSender;

    @SerializedName("Receiver")
    @Expose
    private ReceiverBean mReceiver;

    @SerializedName("VotingScores")
    @Expose
    private int mVotingScores;

    @SerializedName("RankingList")
    @Expose
    public List<String> topFanList = new ArrayList<>();


    @SerializedName("Gifts")
    @Expose
    public Gifts gift;

    @SerializedName("DailyTopFans") @Expose
    public List<DailyTopFanModel> dailyTopFans;

    public SenderBean getSender() {
        return mSender;
    }

    public ReceiverBean getReceiver() {
        return mReceiver;
    }

    public int getVotingScores() {
        return mVotingScores;
    }

    public class SenderBean {
        @SerializedName("TotalBean")
        @Expose
        private long mTotalBean;
        @SerializedName("TotalGold")
        @Expose
        private long mTotalGold;
        @SerializedName("GiftId")
        @Expose
        private String mGiftId;
        @SerializedName("Amount")
        @Expose
        private int mAmount;
        @SerializedName("TotalGoldFans")
        @Expose
        private long totalGoldFans;

        public long getTotalGoldFans() {
            return totalGoldFans;
        }

        public void setTotalGoldFans(long totalGoldFans) {
            this.totalGoldFans = totalGoldFans;
        }

        public long getTotalBean() {
            return mTotalBean;
        }

        public void setTotalBean(long TotalBean) {
            this.mTotalBean = TotalBean;
        }

        public long getTotalGold() {
            return mTotalGold;
        }

        public void setTotalGold(long TotalGold) {
            this.mTotalGold = TotalGold;
        }

        public int getAmount() {
            return mAmount;
        }
    }

    public class ReceiverBean {
        @SerializedName("TotalBean")
        @Expose
        private long mTotalBean;
        @SerializedName("TotalGold")
        @Expose
        private long mTotalGold;
        @SerializedName("TotalGoldFans")
        @Expose
        private long mTotalGoldFans;

        public long getTotalGoldFans() {
            return mTotalGoldFans;
        }

        public void setTotalGoldFans(long totalGoldFans) {
            this.mTotalGoldFans = totalGoldFans;
        }

        public long getTotalBean() {
            return mTotalBean;
        }

        public void setTotalBean(long TotalBean) {
            this.mTotalBean = TotalBean;
        }

        public long getTotalGold() {
            return mTotalGold;
        }

        public void setTotalGold(long TotalGold) {
            this.mTotalGold = TotalGold;
        }
    }

    public class Gifts {
        @SerializedName("GiftId")
        @Expose
        public int giftId;
        @SerializedName("GiftColor")
        @Expose
        public int giftColor;
        @SerializedName("GiftImage")
        @Expose
        public String giftImage;
    }
}
