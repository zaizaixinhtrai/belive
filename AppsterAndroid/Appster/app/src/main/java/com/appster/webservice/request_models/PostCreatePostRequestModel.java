package com.appster.webservice.request_models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

import okhttp3.RequestBody;

/**
 * Created by User on 9/18/2015.
 */
public class PostCreatePostRequestModel extends TypeOutputModel {
    @SerializedName("Address")
    @Expose
    private String mAddress;
    @SerializedName("Latitude")
    @Expose
    private double mLatitude;
    @SerializedName("Longitude")
    @Expose
    private double mLongitude;
    @SerializedName("Title")
    @Expose
    private String mTitle;
    @SerializedName("MediaType")
    @Expose
    private int mMediaType;
    @SerializedName("Image")
    @Expose
    private File mImage;
    @SerializedName("Video")
    @Expose
    private File mVideo;
    @SerializedName("TagUsers")
    @Expose
    private String mTaggedUsers;

    public PostCreatePostRequestModel(String address,
                                      double latitude,
                                      double longitude,
                                      String title,
                                      int media_type,
                                      File media_image,
                                      File media_video) {
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mTitle = title;
        this.mMediaType = media_type;
        this.mImage = media_image;
        this.mVideo = media_video;

        handleAddMultiPar(media_type);
    }

    public PostCreatePostRequestModel(String address,
                                      double latitude,
                                      double longitude,
                                      String title,
                                      int media_type,
                                      File media_image) {
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mTitle = title;
        this.mMediaType = media_type;
        this.mImage = media_image;
        handleAddMultiPar(media_type);
    }

    public PostCreatePostRequestModel(String address,
                                      double latitude,
                                      double longitude,
                                      String title,
                                      int media_type) {
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mTitle = title;
        this.mMediaType = media_type;
        handleAddMultiPar(media_type);
    }

    private void handleAddMultiPar(int type) {
        addPartNotEmptyString("Address", getAddress());
        addPartNotEmptyString("Latitude", String.valueOf(getLatitude()));
        addPartNotEmptyString("Longitude", String.valueOf(getLongitude()));
        addPartNotEmptyString("Title", getTitle());
        addPartNotEmptyString("MediaType", String.valueOf(getMedia_type()));

        handleAddPartData(type);
    }

    private void handleAddPartData(int type) {

        if (type == 1) {

            if (mImage != null) {
//                TypedFile fileUpload = new TypedFile("image/png", getMedia_image());
                mTypeOutput.addFormDataPart("Image", "image_upload.png", RequestBody.create(okhttp3.MediaType.parse("image/png"), getMedia_image()));
            }
        }

        if (type == 2) {
            if (mVideo != null) {
                mTypeOutput.addFormDataPart("Image", "image_upload.png", RequestBody.create(okhttp3.MediaType.parse("image/png"), getMedia_image()));
                mTypeOutput.addFormDataPart("Video", "video_upload.mp4", RequestBody.create(okhttp3.MediaType.parse("video/mp4"), getMedia_video()));
//                mTypeOutput.addPart("Image", new TypedFile("image/png", getMedia_image()));
//                mTypeOutput.addPart("Video", new TypedFile("video/mp4", getMedia_video()));
            }
        }
    }


    public int getMedia_type() {
        return mMediaType;
    }

    public void setMedia_type(int media_type) {
        this.mMediaType = media_type;
    }

    public File getMedia_video() {
        return mVideo;
    }

    public void setMedia_video(File media_video) {
        this.mVideo = media_video;
    }

    public File getMedia_image() {
        return mImage;
    }

    public void setMedia_image(File media_image) {
        this.mImage = media_image;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTaggedUsers() {
        return mTaggedUsers;
    }

    public void setTaggedUsers(String taggedUsers) {
        mTaggedUsers = taggedUsers;
        addPartNotEmptyString("TagUsers", taggedUsers);
    }
}
