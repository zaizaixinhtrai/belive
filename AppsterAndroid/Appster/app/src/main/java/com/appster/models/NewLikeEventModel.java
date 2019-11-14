package com.appster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.bundle.BaseBundle;

/**
 * Created by User on 4/22/2016.
 */
public class NewLikeEventModel extends BaseBundle {
    
    private String postId;
    private int isLike;
    private int likeCount;
    private boolean isStream;
    private String slug;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isStream() {
        return isStream;
    }

    public void setStream(boolean stream) {
        isStream = stream;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postId);
        dest.writeInt(this.isLike);
        dest.writeInt(this.likeCount);
        dest.writeByte((byte) (isStream ? 1 : 0));
        dest.writeString(this.slug);
    }

    public NewLikeEventModel() {
    }

    protected NewLikeEventModel(Parcel in) {
        this.postId = in.readString();
        this.isLike = in.readInt();
        this.likeCount = in.readInt();
        this.isStream = in.readByte() != 0;
        this.slug = in.readString();
    }

    public static final Parcelable.Creator<NewLikeEventModel> CREATOR = new Parcelable.Creator<NewLikeEventModel>() {
        @Override
        public NewLikeEventModel createFromParcel(Parcel source) {
            return new NewLikeEventModel(source);
        }

        @Override
        public NewLikeEventModel[] newArray(int size) {
            return new NewLikeEventModel[size];
        }
    };
}
