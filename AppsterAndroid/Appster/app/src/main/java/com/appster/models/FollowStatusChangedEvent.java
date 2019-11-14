package com.appster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.bundle.BaseBundle;

/**
 * Created by User on 4/6/2016.
 */
public class FollowStatusChangedEvent extends BaseBundle {

    private int followType;
    private String userId;
    private TypeFragment typeFragment;
    private boolean isStream;


    public FollowStatusChangedEvent() {

    }

    public FollowStatusChangedEvent(int followType, String userId, TypeFragment typeFragment) {
        this.followType = followType;
        this.userId = userId;
        this.typeFragment = typeFragment;
    }

    public TypeFragment getTypeFragment() {
        return typeFragment;
    }

    public void setTypeFragment(TypeFragment typeFragment) {
        this.typeFragment = typeFragment;
    }

    public int getFollowType() {
        return followType;
    }

    public void setFollowType(int followType) {
        this.followType = followType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isStream() {
        return isStream;
    }

    public void setStream(boolean stream) {
        isStream = stream;
    }

    public enum TypeFragment {
        TRENDING_LIVE,
        TRENDING_POST
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.followType);
        dest.writeString(this.userId);
        dest.writeInt(this.typeFragment == null ? -1 : this.typeFragment.ordinal());
        dest.writeByte((byte) (isStream ? 1 : 0));
    }

    protected FollowStatusChangedEvent(Parcel in) {
        this.followType = in.readInt();
        this.userId = in.readString();
        int tmpTypeFragment = in.readInt();
        this.typeFragment = tmpTypeFragment == -1 ? null : TypeFragment.values()[tmpTypeFragment];
        this.isStream = in.readByte() != 0;
    }

    public static final Parcelable.Creator<FollowStatusChangedEvent> CREATOR = new Parcelable.Creator<FollowStatusChangedEvent>() {
        @Override
        public FollowStatusChangedEvent createFromParcel(Parcel source) {
            return new FollowStatusChangedEvent(source);
        }

        @Override
        public FollowStatusChangedEvent[] newArray(int size) {
            return new FollowStatusChangedEvent[size];
        }
    };
}
