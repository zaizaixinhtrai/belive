package com.appster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.bundle.BaseBundle;

/**
 * Created by sonnguyen on 11/22/16.
 */

public class ViewVideosEvent extends BaseBundle {
    String postId;
    long  viewCount;
    boolean isStream;
    String slug;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postId);
        dest.writeLong(this.viewCount);
        dest.writeString(this.slug);
        dest.writeByte((byte) (isStream ? 1 : 0));
    }
    public ViewVideosEvent(){

    }
    protected ViewVideosEvent(Parcel in) {
        this.postId = in.readString();
        this.slug = in.readString();
        this.viewCount = in.readInt();
        this.isStream = in.readByte() != 0;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
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

    public static final Parcelable.Creator<ViewVideosEvent> CREATOR = new Parcelable.Creator<ViewVideosEvent>() {
        @Override
        public ViewVideosEvent createFromParcel(Parcel source) {
            return new ViewVideosEvent(source);
        }

        @Override
        public ViewVideosEvent[] newArray(int size) {
            return new ViewVideosEvent[size];
        }
    };
}
