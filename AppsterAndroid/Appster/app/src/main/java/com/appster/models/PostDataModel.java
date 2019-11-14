package com.appster.models;

import android.os.Parcel;

import com.appster.bundle.BaseBundle;

/**
 * Created by User on 4/25/2016.
 */
public class PostDataModel extends BaseBundle {
    private String PostId;
    private String Title;
    private int MediaType;
    private String Address;
    private String MediaImage;
    private String MediaVideo;
    private String WebPostUrl;
    private String MediaImageThumbnail;

    public String getMediaImageThumbnail() {
        return MediaImageThumbnail;
    }

    public void setMediaImageThumbnail(String mediaImageThumbnail) {
        MediaImageThumbnail = mediaImageThumbnail;
    }

    public String getWebPostUrl() {
        return WebPostUrl;
    }

    public void setWebPostUrl(String webPostUrl) {
        WebPostUrl = webPostUrl;
    }

    public String getMedia_video() {
        return MediaVideo;
    }

    public void setMedia_video(String media_video) {
        this.MediaVideo = media_video;
    }

    public String getId() {
        return PostId;
    }

    public void setId(String id) {
        this.PostId = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public int getMedia_type() {
        return MediaType;
    }

    public void setMedia_type(int media_type) {
        this.MediaType = media_type;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getMedia_image() {
        return MediaImage;
    }

    public void setMedia_image(String media_image) {
        this.MediaImage = media_image;
    }

    public PostDataModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.PostId);
        dest.writeString(this.Title);
        dest.writeInt(this.MediaType);
        dest.writeString(this.Address);
        dest.writeString(this.MediaImage);
        dest.writeString(this.MediaVideo);
        dest.writeString(this.WebPostUrl);
        dest.writeString(this.MediaImageThumbnail);
    }

    protected PostDataModel(Parcel in) {
        super(in);
        this.PostId = in.readString();
        this.Title = in.readString();
        this.MediaType = in.readInt();
        this.Address = in.readString();
        this.MediaImage = in.readString();
        this.MediaVideo = in.readString();
        this.WebPostUrl = in.readString();
        this.MediaImageThumbnail = in.readString();
    }

    public static final Creator<PostDataModel> CREATOR = new Creator<PostDataModel>() {
        @Override
        public PostDataModel createFromParcel(Parcel source) {
            return new PostDataModel(source);
        }

        @Override
        public PostDataModel[] newArray(int size) {
            return new PostDataModel[size];
        }
    };
}
