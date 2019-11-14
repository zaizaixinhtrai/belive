package com.appster.webservice.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 9/8/17.
 */

public class SubStreamData {
    @SerializedName("AfkStatus")
    public int afkStatus;
    @SerializedName("Description")
    public String description;
    @SerializedName("Id")
    public int id;
    @SerializedName("Slug")
    public String slug;
    @SerializedName("Status")
    public int status;
    @SerializedName("StreamParentId")
    public int streamParentId;
    @SerializedName("StreamUrl")
    public String streamUrl;
    @SerializedName("Title")
    public String title;
    @SerializedName("UserId")
    public int userId;
    @SerializedName("UserImage")
    public String userImage;
    @SerializedName("DisplayName")
    public String displayName;

    @SerializedName("Receiver")
    public Receiver receiver;

    @Override
    public String toString() {
        return "SubStreamData{" +
                "afkStatus=" + afkStatus +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", slug='" + slug + '\'' +
                ", status=" + status +
                ", streamParentId=" + streamParentId +
                ", streamUrl='" + streamUrl + '\'' +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                ", userImage='" + userImage + '\'' +
                ", displayName='" + displayName + '\'' +
                ", receiver=" + receiver +
                '}';
    }


    public static class Receiver {
        /**
         * IsFollow : 0
         * TotalGold : 0
         * VotingScores : 0
         * TotalGoldFans : 0
         * IsSeller : 0
         * UserId : 2164
         * UserName : aaa
         * DisplayName : Appsters Appster
         * UserImage : https://static-dev-profile.belive.sg/profile_image_thum/aaa.jpg?t=636425125440000000
         * Gender : Male
         * Handle : @aaa
         * FollowerCount : 0
         * UserThumbnailImage : null
         */

        @SerializedName("IsFollow")
        public int isFollow;
        @SerializedName("TotalGold")
        public int totalGold;
        @SerializedName("VotingScores")
        public int votingScores;
        @SerializedName("TotalGoldFans")
        public int totalGoldFans;
        @SerializedName("IsSeller")
        public int isSeller;
        @SerializedName("UserId")
        public int userId;
        @SerializedName("UserName")
        public String userName;
        @SerializedName("DisplayName")
        public String displayName;
        @SerializedName("UserImage")
        public String userImage;
        @SerializedName("Gender")
        public String gender;
        @SerializedName("Handle")
        public String handle;
        @SerializedName("FollowerCount")
        public int followerCount;

        @Override
        public String toString() {
            return "Receiver{" +
                    "isFollow=" + isFollow +
                    ", totalGold=" + totalGold +
                    ", votingScores=" + votingScores +
                    ", totalGoldFans=" + totalGoldFans +
                    ", isSeller=" + isSeller +
                    ", userId=" + userId +
                    ", userName='" + userName + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", userImage='" + userImage + '\'' +
                    ", gender='" + gender + '\'' +
                    ", handle='" + handle + '\'' +
                    ", followerCount=" + followerCount +
                    '}';
        }
    }
}
