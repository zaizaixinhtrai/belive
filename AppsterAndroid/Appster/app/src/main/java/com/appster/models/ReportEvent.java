package com.appster.models;

import android.os.Parcel;

import com.appster.bundle.BaseBundle;

/**
 * Created by sonnguyen on 11/22/16.
 */

public class ReportEvent extends BaseBundle {
    String postId;
    int  isReport;
    boolean isStream;
    String slug;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postId);
        dest.writeString(this.slug);
        dest.writeInt(this.isReport);
        dest.writeByte((byte) (isStream ? 1 : 0));
    }
    public ReportEvent(){

    }
    protected ReportEvent(Parcel in) {
        this.postId = in.readString();
        this.slug = in.readString();
        this.isReport = in.readInt();
        this.isStream = in.readByte() != 0;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getIsReport() {
        return isReport;
    }

    public void setIsReport(int isReport) {
        this.isReport = isReport;
    }

    public boolean isStream() {
        return isStream;
    }

    public void setStream(boolean stream) {
        isStream = stream;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReportEvent> CREATOR = new Creator<ReportEvent>() {
        @Override
        public ReportEvent createFromParcel(Parcel source) {
            return new ReportEvent(source);
        }

        @Override
        public ReportEvent[] newArray(int size) {
            return new ReportEvent[size];
        }
    };
}
