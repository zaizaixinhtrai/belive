package com.appster.webservice.response;

import com.appster.models.StreamModel;
import com.appster.models.UserModel;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mUser on 9/19/2015.
 */

public class UserProfileResponseModel {
    @SerializedName("Posts") @Expose
    private List<ItemModelClassNewsFeed> mPosts;
    @SerializedName("User") @Expose
    private UserModel mUserModel;
    @SerializedName("CanChangePassword") @Expose
    private boolean mCanChangePassword;
    @SerializedName("IsStreaming") @Expose
    private boolean mIsStreaming;
    @SerializedName("CurrentStream") @Expose
    private String mCurrentStream;
    @SerializedName("Stream")@Expose
    private StreamModel mStreamDetail;

    public boolean isStreaming() {
        return mIsStreaming;
    }

    public void setStreaming(boolean streaming) {
        mIsStreaming = streaming;
    }

    public StreamModel getStreamDetail() {
        return mStreamDetail;
    }

    public void setStreamDetail(StreamModel streamDetail) {
        this.mStreamDetail = streamDetail;
    }

    public String getCurrentStream() {
        return mCurrentStream;
    }

    public void setCurrentStream(String currentStream) {
        mCurrentStream = currentStream;
    }

    public UserModel getUser() {
        return mUserModel;
    }

    public void setUser(UserModel user) {
        mUserModel = user;
    }

    public List<ItemModelClassNewsFeed> getPosts() {
        return mPosts;
    }

    public void setPosts(List<ItemModelClassNewsFeed> posts) {
        mPosts = posts;
    }

    public boolean isCanChangePassword() {
        return mCanChangePassword;
    }

    public void setCanChangePassword(boolean canChangePassword) {
        mCanChangePassword = canChangePassword;
    }
}
