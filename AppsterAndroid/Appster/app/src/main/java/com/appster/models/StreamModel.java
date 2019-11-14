package com.appster.models;

import com.appster.comments.ItemClassComments;
import com.appster.webservice.response.SubStreamData;
import com.apster.common.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 6/15/2016.
 */
public class StreamModel {

    @SerializedName("Publisher")
    @Expose
    private StreamPublisherModel Publisher;

    @SerializedName("StreamRecording")
    @Expose
    private StreamRecordingBean StreamRecording;

    @SerializedName("StreamId")
    @Expose
    private int StreamId;

    @SerializedName("UserId")
    @Expose
    private String UserId;

    @SerializedName("Slug")
    @Expose
    private String Slug;

    @SerializedName("CoverImage")
    @Expose
    private String CoverImage;

    @SerializedName("Title")
    @Expose
    private String Title;

    @SerializedName("Description")
    @Expose
    private String Description;


    @SerializedName("ViewCount")
    @Expose
    private long ViewCount;

    @SerializedName("StreamType")
    @Expose
    private int StreamType;

    @SerializedName("StreamUrl")
    @Expose
    private String StreamUrl;

    @SerializedName("Status")
    @Expose
    private int Status;// 2: ended stream, 1: streaming, 0: create

    @SerializedName("StartTime")
    @Expose
    private String StartTime;

    @SerializedName("EndTime")
    @Expose
    private String EndTime;

    @SerializedName("Tags")
    @Expose
    private String Tags;

    @SerializedName("IsRecorded")
    @Expose
    private boolean IsRecorded;

    @SerializedName("Created")
    @Expose
    private String Created;

    @SerializedName("BeginStream")
    @Expose
    private String BeginStream;

    @SerializedName("EndStream")
    @Expose
    private String EndStream;

    @SerializedName("Hide")
    @Expose
    private boolean mIsHide;

    @SerializedName("TagName")
    @Expose
    private String TagName;

    @SerializedName("GiftCount")
    @Expose
    private int GiftCount;

    @SerializedName("LikeCount")
    @Expose
    private int LikeCount;

    @SerializedName("IsLike")
    @Expose
    private int IsLike;

    @SerializedName("WebStreamUrl")
    @Expose
    private String WebStreamUrl;

    @SerializedName("Duration")
    @Expose
    private long Duration;

    @SerializedName("IsReport")
    @Expose
    private int IsReport;

    @SerializedName("HistoryChat")
    @Expose
    private String HistoryChat;

    @SerializedName("StreamResultUrl")
    @Expose
    private String StreamResultUrl;

    @SerializedName("FrameRate")
    @Expose
    private int FrameRate;

    @SerializedName("ViewSessionId")
    @Expose
    private int ViewSessionId;

    @SerializedName("CommentCount")
    @Expose
    private int mTotalCommentCount;

    @SerializedName("CommentList")
    @Expose
    private ArrayList<ItemClassComments> mCommentList;

    @SerializedName("AfkStatus")
    @Expose
    public int AfkStatus;

    @SerializedName("StatusMessage")
    @Expose
    public String statusMessage;

    @SerializedName("isSeller")
    @Expose
    private boolean mIsSeller;

    @SerializedName("TitlePlainText")
    @Expose
    public String titlePlainText;

    @SerializedName("SubStream")
    @Expose
    public SubStreamData subStream;

    @SerializedName("OrderButtonText")
    @Expose
    public String liveShopOrderButtonNowLabel;

    @SerializedName("OrderInProgressText")
    @Expose
    public String liveCommerceAnnouncementMessage;

    @SerializedName("IsTrivia")
    public boolean isTrivia = false;
    @SerializedName("TriviaId")
    public int triviaId;
    public List<String> rankingList = new ArrayList<>();
    public List<DailyTopFanModel> dailyTopFansList;
    @SerializedName("Point")
    public PointInfo pointInfo;

    public int getWowzaVideoFrameRate() {
        return FrameRate;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    private String Address;

    public String getStreamResultUrl() {
        return StreamResultUrl;
    }

    public long getDuration() {
        return Duration;
    }

    public boolean isStreamBeingLive() {
        return !(isIsRecorded() && getStatus() == Constants.StreamStatus.StreamEnd);

    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(int likeCount) {
        LikeCount = likeCount;
    }

    public boolean isLike() {
        return IsLike == 1;
    }

    public void setLike(int like) {
        IsLike = like;
    }

    public int getGiftCount() {
        return GiftCount;
    }

    public StreamPublisherModel getPublisher() {
        return Publisher;
    }

    public void setPublisher(StreamPublisherModel Publisher) {
        this.Publisher = Publisher;
    }

    public StreamRecordingBean getStreamRecording() {
        return StreamRecording;
    }

    public int getStreamId() {
        return StreamId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getSlug() {
        return Slug;
    }

    public void setSlug(String Slug) {
        this.Slug = Slug;
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public long getViewCount() {
        return ViewCount;
    }

    public void setViewCount(long ViewCount) {
        this.ViewCount = ViewCount;
    }

    public String getStreamUrl() {
        return StreamUrl;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public boolean isIsRecorded() {
        return IsRecorded;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String Created) {
        this.Created = Created;
    }

    public String getTagName() {
        return TagName;
    }

    public int getIsReport() {
        return IsReport;
    }

    public void setIsReport(int isReport) {
        IsReport = isReport;
    }

    public String getHistoryChat() {
        return HistoryChat;
    }

    public int getViewSessionId() {
        return ViewSessionId;
    }

    public String getWebStreamUrl() {
        return WebStreamUrl;
    }

    public ArrayList<ItemClassComments> getCommentList() {
        return mCommentList;
    }

    public void setCommentList(ArrayList<ItemClassComments> commentList) {
        mCommentList = commentList;
    }

    public int getTotalCommentCount() {
        return mTotalCommentCount;
    }

    public void setTotalCommentCount(int totalCommentCount) {
        mTotalCommentCount = totalCommentCount;
    }

    public void setIsHide(boolean mIsHide) {
        this.mIsHide = mIsHide;
    }

    public boolean getIsHide() {
        return this.mIsHide;
    }

    public String getUserPointInfoUrl() {
        if (pointInfo != null) {
            return pointInfo.pointInfoUrl;
        }
        return "";
    }

    public int getViewerPoint() {
        if (pointInfo != null) {
            return pointInfo.userPoint;
        }
        return 0;
    }

    public int getHostPoint() {
        if (Publisher != null) {
            return Publisher.point;
        }
        return 0;
    }

    public class StreamRecordingBean {
        @SerializedName("DownloadUrl")
        @Expose
        private String DownloadUrl;

        @SerializedName("PlayUrl")
        @Expose
        public String PlayUrl;// for status 2

        public String getDownloadUrl() {
            return DownloadUrl;
        }

    }

    private class PointInfo {
        @SerializedName("PointInfoURL")
        private String pointInfoUrl;
        @SerializedName("UserPoint")
        private int userPoint;
    }
}
