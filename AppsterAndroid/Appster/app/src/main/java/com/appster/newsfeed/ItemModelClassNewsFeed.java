package com.appster.newsfeed;

import android.os.Parcel;

import com.appster.bundle.BaseBundle;
import com.appster.models.PostDataModel;
import com.appster.models.PostDetailModel;
import com.appster.AppsterApplication;
import com.appster.comments.ItemClassComments;
import com.pack.utility.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * by User on 17/7/2015.
 */
public class ItemModelClassNewsFeed extends BaseBundle {
    int position;
    private int id_in_sqlte;
    private String UserName = "";
    private String UserId = "";
    private String PostId = "";
    private String Title = "";
    private int MediaType;
    private String MediaImage = "";
    private String MediaVideo = "";
    private String UserImage = "";
    private int IsLike;
    private int IsReport;
    private int IsFollow;
    private String Address = "";
    private String DisplayName = "";
    private int LikeCount;
    private int CommentCount;
    private String Gender;
    private ArrayList<ItemClassComments> CommentList;
    private String ThumbnailImage;
    private String WebPostUrl;
    private double Latitude;
    private double Longitude;
    private String Created;
    private byte type;
    private long ViewCount;
    public String slug;


    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getMediaVideo() {
        return MediaVideo;
    }

    public void setMediaVideo(String mediaVideo) {
        MediaVideo = mediaVideo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public String getWebPostUrl() {
        return WebPostUrl;
    }

    public void setWebPostUrl(String webPostUrl) {
        WebPostUrl = webPostUrl;
    }

    public String getThumbnailImage() {
        return ThumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.ThumbnailImage = thumbnailImage;
    }

    private ArrayList<HashMap<String, String>> listHashMapComment = new ArrayList<HashMap<String, String>>();

    public ArrayList<ItemClassComments> getCommentList() {
        return CommentList != null ? CommentList : new ArrayList<>();
    }

    public void setCommentList(ArrayList<ItemClassComments> commentList) {
        CommentList = commentList;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getId_in_sqlte() {
        return id_in_sqlte;
    }

    public void setId_in_sqlte(int id_in_sqlte) {
        this.id_in_sqlte = id_in_sqlte;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public String getNameShowInClient() {
        if (StringUtil.isNullOrEmptyString(getDisplayName())) {
            return getUserName();
        } else {
            return getDisplayName();
        }
    }

    public void setDisplayName(String displayName) {
        this.DisplayName = displayName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public int getIsFollow() {
        return IsFollow;
    }

    public void setIsFollow(int isFollow) {
        this.IsFollow = isFollow;
    }

    public int getIsReport() {
        return IsReport;
    }

    public void setIsReport(int isReport) {
        this.IsReport = isReport;
    }

    public int getIsLike() {
        return IsLike;
    }

    public void setIsLike(int isLike) {
        this.IsLike = isLike;
    }

    public ArrayList<HashMap<String, String>> getListHashMapComment() {
        return listHashMapComment;
    }

    public void setListHashMapComment(
            ArrayList<HashMap<String, String>> listHashMapComment) {
        this.listHashMapComment = listHashMapComment;
    }

    public String getNfs_userid() {
        return UserId;
    }

    public void setNfs_userid(String nfs_userid) {
        this.UserId = nfs_userid;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        this.UserImage = userImage;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(int likeCount) {
        this.LikeCount = likeCount;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        this.Created = created;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int commentCount) {
        this.CommentCount = commentCount;
    }


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        this.PostId = postId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public int getMediaType() {
        return MediaType;
    }

    public void setMediaType(int mediaType) {
        this.MediaType = mediaType;
    }

    public String getMediaImage() {
        return MediaImage;
    }

    public void setMediaImage(String mediaImage) {
        this.MediaImage = mediaImage;
    }

    public String getNfs_MediyaVideo() {
        return MediaVideo;
    }

    public void setNfs_MediyaVideo(String nfs_MediyaVideo) {
        this.MediaVideo = nfs_MediyaVideo;
    }

    public long getViewCount() {
        return ViewCount;
    }

    public void setViewCount(long viewCount) {
        ViewCount = viewCount;
    }

    public ItemModelClassNewsFeed() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.position);
        dest.writeInt(this.id_in_sqlte);
        dest.writeString(this.UserId);
        dest.writeString(this.PostId);
        dest.writeString(this.Title);
        dest.writeInt(this.MediaType);
        dest.writeString(this.MediaImage);
        dest.writeString(this.MediaVideo);
        dest.writeString(this.UserName);
        dest.writeString(this.UserImage);
        dest.writeInt(this.IsLike);
        dest.writeInt(this.IsReport);
        dest.writeInt(this.IsFollow);
        dest.writeString(this.Address);
        dest.writeString(this.DisplayName);
        dest.writeInt(this.LikeCount);
        dest.writeInt(this.CommentCount);
        dest.writeString(this.Gender);
        dest.writeTypedList(CommentList);
        dest.writeByte(this.type);
        dest.writeList(this.listHashMapComment);
        dest.writeString(this.WebPostUrl);
        dest.writeString(this.Created);
        dest.writeLong(this.ViewCount);


    }

    protected ItemModelClassNewsFeed(Parcel in) {
        super(in);
        this.position = in.readInt();
        this.id_in_sqlte = in.readInt();
        this.UserId = in.readString();
        this.PostId = in.readString();
        this.Title = in.readString();
        this.MediaType = in.readInt();
        this.MediaImage = in.readString();
        this.MediaVideo = in.readString();
        this.UserName = in.readString();
        this.UserImage = in.readString();
        this.IsLike = in.readInt();
        this.IsReport = in.readInt();
        this.IsFollow = in.readInt();
        this.Address = in.readString();
        this.DisplayName = in.readString();
        this.LikeCount = in.readInt();
        this.CommentCount = in.readInt();
        this.Gender = in.readString();
        this.CommentList = in.createTypedArrayList(ItemClassComments.CREATOR);
        this.type = in.readByte();
        this.listHashMapComment = new ArrayList<>();
        in.readList(this.listHashMapComment, List.class.getClassLoader());
        this.WebPostUrl = in.readString();
        this.Created = in.readString();
        this.ViewCount = in.readLong();
    }

    public static final Creator<ItemModelClassNewsFeed> CREATOR = new Creator<ItemModelClassNewsFeed>() {
        public ItemModelClassNewsFeed createFromParcel(Parcel source) {
            return new ItemModelClassNewsFeed(source);
        }

        public ItemModelClassNewsFeed[] newArray(int size) {
            return new ItemModelClassNewsFeed[size];
        }
    };

    public ItemModelClassNewsFeed(PostDetailModel postDetail) {

        this.UserId = postDetail.getUser_id();
        this.PostId = postDetail.getId();
        this.Title = postDetail.getTitle();
        this.MediaType = postDetail.getMedia_type();
        this.MediaImage = postDetail.getMedia_image();
        this.MediaVideo = postDetail.getMedia_video();
        this.UserName = postDetail.getUsername();
        this.UserImage = postDetail.getUser_image();
        this.IsLike = postDetail.getIs_like();
        this.IsReport = postDetail.getIs_report();
        this.IsFollow = postDetail.getIs_follow();
        this.Address = postDetail.getAddress();
        this.DisplayName = postDetail.getDisplay_name();
        this.LikeCount = postDetail.getLike_count();
        this.CommentCount = postDetail.getComment_count();
        this.Gender = postDetail.getGender();
        this.CommentList = postDetail.getComments();
        this.WebPostUrl = postDetail.getWebPostUrl();
        this.Created = postDetail.getTimestamp();

    }

    public ItemModelClassNewsFeed(PostDataModel postDataModel) {

        this.UserId = AppsterApplication.mAppPreferences.getUserModel().getUserId();
        this.PostId = postDataModel.getId();
        this.Title = postDataModel.getTitle();
        this.MediaType = postDataModel.getMedia_type();
        this.MediaImage = postDataModel.getMedia_image();
        this.MediaVideo = postDataModel.getMedia_video();
        this.UserName = AppsterApplication.mAppPreferences.getUserModel().getUserName();
        this.UserImage = AppsterApplication.mAppPreferences.getUserModel().getUserImage();
        this.Address = postDataModel.getAddress();
        this.Gender = AppsterApplication.mAppPreferences.getUserModel().getGender();
        this.WebPostUrl = postDataModel.getWebPostUrl();
    }
}