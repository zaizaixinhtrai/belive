package com.appster.comments;

import android.os.Parcel;

import com.appster.bundle.BaseBundle;
import com.pack.utility.StringUtil;

/**
 * Created by User on 9/4/2015.
 */
public class ItemClassComments extends BaseBundle{

    private int CommentId;
    private String PostId;
    private String Message = "";
    private String DisplayName = "";
    private String UserId = "";
    private String UserImage = "";
    private String Gender = "";
    private String Timestamp;
    private String UserName;
    private String Created;


    public ItemClassComments() {

    }

    public ItemClassComments(Parcel in) {
        this.CommentId = in.readInt();
        this.Message = in.readString();
        this.DisplayName = in.readString();
        this.UserId = in.readString();
        UserImage = in.readString();
        Gender = in.readString();
        Timestamp = in.readString();
        this.PostId = in.readString();
        UserName = in.readString();
        this.Created = in.readString();

    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.Timestamp = timestamp;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getUser_image() {
        return UserImage;
    }

    public void setUser_image(String user_image) {
        this.UserImage = user_image;
    }

    public String getUser_id() {
        return UserId;
    }

    public void setUser_id(String user_id) {
        this.UserId = user_id;
    }

    public String getDisplay_name() {
        return DisplayName;
    }

    public String getNameShowUI(){
        if(StringUtil.isNullOrEmptyString(getDisplay_name())){
            return getUserName();
        }
        return getDisplay_name();
    }
    public void setDisplay_name(String display_name) {
        this.DisplayName = display_name;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public int getId() {
        return CommentId;
    }

    public void setId(int id) {
        this.CommentId = id;
    }

    /**
     * Define the kind of object that you gonna parcel,
     * You can use hashCode() here
     */
    @Override
    public int describeContents() {
        return 0;
    }


    /**
     * Actual object serialization happens here, Write object content
     * to parcel one by one, reading should be done according to this write order
     *
     * @param dest  parcel
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CommentId);
        dest.writeString(Message);
        dest.writeString(DisplayName);
        dest.writeString(UserId);

        dest.writeString(UserImage);
        dest.writeString(Gender);
        dest.writeString(Timestamp);
        dest.writeString(PostId);
        dest.writeString(UserName);
        dest.writeString(Created);
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     * <p/>
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Creator<ItemClassComments> CREATOR = new Creator<ItemClassComments>() {

        public ItemClassComments createFromParcel(Parcel in) {
            return new ItemClassComments(in);
        }

        public ItemClassComments[] newArray(int size) {
            return new ItemClassComments[size];
        }
    };

}
