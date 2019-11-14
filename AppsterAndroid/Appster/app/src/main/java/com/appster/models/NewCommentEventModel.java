package com.appster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.bundle.BaseBundle;
import com.appster.comments.ItemClassComments;

import java.util.ArrayList;

/**
 * Created by User on 4/21/2016.
 */
public class NewCommentEventModel extends BaseBundle {

    private ArrayList<ItemClassComments> arrComment;
    private String postId;
    private String slug;
    private boolean isStream;

    public ArrayList<ItemClassComments> getArrComment() {
        return arrComment;
    }

    public void setArrComment(ArrayList<ItemClassComments> arrComment) {
        this.arrComment = arrComment;
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
        dest.writeTypedList(arrComment);
        dest.writeString(this.postId);
        dest.writeString(this.slug);
        dest.writeByte((byte) (isStream ? 1 : 0));
    }

    public NewCommentEventModel() {
    }

    protected NewCommentEventModel(Parcel in) {
        this.arrComment = in.createTypedArrayList(ItemClassComments.CREATOR);
        this.postId = in.readString();
        this.slug = in.readString();
        this.isStream = in.readByte() != 0;
    }

    public static final Parcelable.Creator<NewCommentEventModel> CREATOR = new Parcelable.Creator<NewCommentEventModel>() {
        @Override
        public NewCommentEventModel createFromParcel(Parcel source) {
            return new NewCommentEventModel(source);
        }

        @Override
        public NewCommentEventModel[] newArray(int size) {
            return new NewCommentEventModel[size];
        }
    };
}
