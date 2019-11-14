package com.appster.models;

import com.appster.core.adapter.DisplayableItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 6/28/2016.
 */
public class HomeItemModel implements DisplayableItem {
    @SerializedName("typeModel")
    @Expose
    private int typeModel;
    @SerializedName("Publisher")
    @Expose
    private PublisherBean Publisher;
    @SerializedName("StreamRecording")
    @Expose
    private StreamRecordingBean StreamRecording;
    @SerializedName("StreamId")
    @Expose
    private int StreamId;
    @SerializedName("UserId")
    @Expose
    private int UserId;
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
    private int ViewCount;
    @SerializedName("StreamType")
    @Expose
    private int StreamType;
    @SerializedName("StreamUrl")
    @Expose
    private String StreamUrl;
    @SerializedName("Status")
    @Expose
    private int Status;
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
    @SerializedName("WebStreamUrl")
    @Expose
    private String WebStreamUrl;
    @SerializedName("TagName")
    @Expose
    private String TagName;
    @SerializedName("Distance")
    @Expose
    private String Distance;

    public int getTypeModel() {
        return typeModel;
    }

    public void setTypeModel(int typeModel) {
        this.typeModel = typeModel;
    }

    public PublisherBean getPublisher() {
        return Publisher;
    }

    public StreamRecordingBean getStreamRecording() {
        return StreamRecording;
    }

    public int getStreamId() {
        return StreamId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
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

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int ViewCount) {
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

    public int getFollowerCount() {
        return Publisher.getFollowerCount();
    }

    public String getDistance() {
        return Distance;
    }

    public static class PublisherBean {
        @SerializedName("UserId")
        @Expose
        private String UserId;
        @SerializedName("UserName")
        @Expose
        private String UserName;
        @SerializedName("DisplayName")
        @Expose
        private String DisplayName;
        @SerializedName("UserImage")
        @Expose
        private String UserImage;
        @SerializedName("Gender")
        @Expose
        private String Gender;
        @SerializedName("Handle")
        @Expose
        private String Handle;
        @SerializedName("FollowerCount")
        @Expose
        private int FollowerCount;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getDisplayName() {
            return DisplayName;
        }

        public void setDisplayName(String DisplayName) {
            this.DisplayName = DisplayName;
        }

        public String getUserImage() {
            return UserImage;
        }

        public void setUserImage(String UserImage) {
            this.UserImage = UserImage;
        }

        public String getGender() {
            return Gender;
        }

        public void setGender(String Gender) {
            this.Gender = Gender;
        }

        public String getHandle() {
            return Handle;
        }

        public void setHandle(String Handle) {
            this.Handle = Handle;
        }

        public int getFollowerCount() {
            return FollowerCount;
        }

    }

    public static class StreamRecordingBean {
        @SerializedName("DownloadUrl")
        @Expose
        private String DownloadUrl;
        @SerializedName("PlayUrl")
        @Expose
        private String PlayUrl;

        public String getDownloadUrl() {
            return DownloadUrl;
        }

    }
}
