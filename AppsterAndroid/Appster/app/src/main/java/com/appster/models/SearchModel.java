package com.appster.models;

import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.UpdateableItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/13/2016.
 */
public class SearchModel implements UpdateableItem {

    @SerializedName("IsStreaming")
    @Expose
    private boolean IsStreaming;
    @SerializedName("Stream")
    @Expose
    private StreamModel Stream;
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
    @SerializedName("WebProfileUrl")
    @Expose
    private String WebProfileUrl;
    @SerializedName("IsFollow")
    @Expose
    private int IsFollow;

    @SerializedName("Post")
    @Expose
    public SearchPostModel searchPostModel;

    public String getTitleHeader() {
        return titleHeader;
    }

    public void setTitleHeader(String titleHeader) {
        this.titleHeader = titleHeader;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    private String titleHeader;
    private boolean isHeader;

    public SearchModel(String titleHeader, boolean isHeader) {
        this.titleHeader = titleHeader;
        this.isHeader = isHeader;
    }

    public SearchModel() {
    }

    public boolean isIsStreaming() {
        return IsStreaming;
    }

    public void setIsStreaming(boolean IsStreaming) {
        this.IsStreaming = IsStreaming;
    }

    public StreamModel getStream() {
        return Stream;
    }

    public void setStream(StreamModel Stream) {
        this.Stream = Stream;
    }

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

    public String getWebProfileUrl() {
        return WebProfileUrl;
    }

    public void setWebProfileUrl(String WebProfileUrl) {
        this.WebProfileUrl = WebProfileUrl;
    }

    @Override
    public boolean isSameItem(DisplayableItem item) {
        return item instanceof SearchModel && getUserId().equalsIgnoreCase(((SearchModel) item).getUserId());

    }

    @Override
    public boolean isSameContent(DisplayableItem item) {
        return item instanceof SearchModel && isIsStreaming() == ((SearchModel) item).isIsStreaming() && getUserImage().equalsIgnoreCase(((SearchModel) item).getUserImage());
    }

    public int getIsFollow() {
        return IsFollow;
    }

    public void setIsFollow(int isFollow) {
        IsFollow = isFollow;
    }
}
