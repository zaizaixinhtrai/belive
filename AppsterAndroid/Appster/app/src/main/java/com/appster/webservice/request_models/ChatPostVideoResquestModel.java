package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by User on 9/16/2015.
 */
public class ChatPostVideoResquestModel extends TypeOutputModel {
    @SerializedName("Image") @Expose
    private File mImage;
    @SerializedName("Video") @Expose
    private File mVideo;

    public ChatPostVideoResquestModel(File media_image, File media_video) {
        this.mImage = media_image;
        this.mVideo = media_video;
        handleAddPartData();
    }

    public File getMedia_image() {
        return mImage;
    }

    public void setMedia_image(File media_image) {
        this.mImage = media_image;
    }

    public File getMedia_video() {
        return mVideo;
    }

    public void setMedia_video(File media_video) {
        this.mVideo = media_video;
    }

    private void handleAddPartData() {
        if (mImage != null) {
//            TypedFile fileUpload = new TypedFile("image/png", getMedia_image());
            mTypeOutput.addFormDataPart("Image","image_upload.png", RequestBody.create(okhttp3.MediaType.parse("image/png"),getMedia_image()));
            mTypeOutput.addFormDataPart("Video","video_upload.mp4", RequestBody.create(okhttp3.MediaType.parse("video/mp4"),getMedia_video()));
//            mTypeOutput.addPart("mImage", fileUpload);
//            mTypeOutput.addPart("mVideo", new TypedFile("video/mp4", getMedia_video()));
        }
    }
}
