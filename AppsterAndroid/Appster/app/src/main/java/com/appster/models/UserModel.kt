package com.appster.models

import android.os.Parcel
import android.os.Parcelable
import com.appster.BuildConfig
import com.appster.extensions.parcelableCreator
import com.appster.extensions.toInt
import com.apster.common.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pack.utility.StringUtil

/**
 * Created by User on 6/14/2016.
 */
class UserModel : Parcelable {
    @SerializedName("UserId")
    var userId = ""
    @SerializedName("RoleId")
    var roleId: String? = null
    @SerializedName("RefId")
    var refId: String? = null
    @SerializedName("ReferralId")
    var referralId: String? = null
    @SerializedName("UserName")
    var userName: String? = null
    @SerializedName("DisplayName")
    private var DisplayName: String? = null
    @SerializedName("Email")
    var email: String? = null
    @SerializedName("EmailVerified")
    var emailVerified: Int = 0
    @SerializedName("EmailToken")
    var emailToken: String? = null
    @SerializedName("UserImage")
    var userImage: String? = null
    @SerializedName("Gender")
    var gender: String? = null
    @SerializedName("DoB")
    var doB: String? = null
    @SerializedName("FbId")
    var fbId: String? = null
    @SerializedName("Nationality")
    var nationality: String? = null
    @SerializedName("Notification")
    var notification: Int = 0
    @SerializedName("NearbyFeature")
    var nearbyFeature: Int = 0
    @SerializedName("Searchable")
    var searchable: Int = 0
    @SerializedName("NotificationSound")
    var notificationSound: Int = 0
    @SerializedName("LiveNotification")
    @Expose
    var liveNotification: Int = 0
    @SerializedName("HideMessageDetails")
    var hideMessageDetails: Int = 0
    @SerializedName("VoiceCall")
    var voiceCall: Int = 0
    @SerializedName("VideoCall")
    var videoCall: Int = 0
    @SerializedName("Messaging")
    var messaging: Int = 0
    @SerializedName("Language")
    var language: Int = 0
    @SerializedName("GiftReceivedCount")
    var giftReceivedCount: Int = 0
    @SerializedName("GiftSentCount")
    var giftSentCount: Int = 0
    /**
     * gem
     */
    /**
     * gem
     */
    @SerializedName("TotalBean")
    var totalBean: Long = 0//gem
    /**
     * stars
     */
    /**
     * stars
     */
    @SerializedName("TotalGold")
    var totalGold: Long = 0//stars
    @SerializedName("Status")
    var status: Int = 0
    @SerializedName("Created")
    var created: String? = null
    @SerializedName("Streaming")
    var streaming: Int = 0
    @SerializedName("PlaytokenId")
    var playtokenId: String? = null
    @SerializedName("FollowerCount")
    var followerCount: Int = 0
    @SerializedName("FollowingCount")
    var followingCount: Int = 0
    @SerializedName("IsFollow")
    var isFollow: Int = 0
    @SerializedName("UnreadMessageCount")
    var unreadMessageCount: Int = 0
    @SerializedName("UnreadNotificationCount")
    var unreadNotificationCount: Int = 0
    @SerializedName("LoginLogoutStatus")
    var loginLogoutStatus: Int = 0
    @SerializedName("Distance")
    var distance: Double = 0.toDouble()
    @SerializedName("TotalPoint")
    var totalPoint: Int = 0
    @SerializedName("Latitude")
    var latitude: Double = 0.toDouble()
    @SerializedName("Longitude")
    var longitude: Double = 0.toDouble()
    @SerializedName("WebProfileUrl")
    var webProfileUrl: String? = null
    @SerializedName("About")
    var about: String? = null
    @SerializedName("AvatarVersion")
    var avatarVersion = 0
    @SerializedName("Address")
    var address: String? = null
    @SerializedName("TotalGoldFans")
    var totalGoldFans: Long = 0
    @SerializedName("Color")
    var color: String? = null
    @SerializedName("IsSeller")
    @Expose
    var isSeller = true
    @SerializedName("Type")
    private val type: Int = 0
    @SerializedName("PhoneNumber")
    var phoneNumber = ""


    @SerializedName("NormalizedPhone")
    val normalizedPhone = ""

    var points: Int = 0

    var displayName: String
        get() = StringUtil.decodeString(DisplayName)
        set(DisplayName) {
            this.DisplayName = DisplayName
        }

    val nameShowInClient: String?
        get() = if (StringUtil.isNullOrEmptyString(displayName)) {
            userName
        } else displayName

    val isDevUser: Boolean
        get() = this.type == 1

    fun increaseAvatarVersion() {
        avatarVersion++
    }

    fun setUserId(UserId: Int) {
        this.userId = UserId.toString()
    }

    fun getIsFollow() = isFollow

    fun setIsFollow(isFollow: Int) {
        this.isFollow = isFollow
    }

    constructor()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(userId)
        writeString(roleId)
        writeString(refId)
        writeString(referralId)
        writeString(userName)
        writeString(DisplayName)
        writeString(email)
        writeInt(emailVerified)
        writeString(emailToken)
        writeString(userImage)
        writeString(gender)
        writeString(doB)
        writeString(fbId)
        writeString(nationality)
        writeInt(notification)
        writeInt(nearbyFeature)
        writeInt(searchable)
        writeInt(notificationSound)
        writeInt(hideMessageDetails)
        writeInt(voiceCall)
        writeInt(videoCall)
        writeInt(messaging)
        writeInt(language)
        writeInt(giftReceivedCount)
        writeInt(giftSentCount)
        writeLong(totalBean)
        writeLong(totalGold)
        writeInt(status)
        writeString(created)
        writeInt(streaming)
        writeString(playtokenId)
        writeInt(followerCount)
        writeInt(followingCount)
        writeInt(isFollow)
        writeInt(unreadMessageCount)
        writeInt(unreadNotificationCount)
        writeInt(loginLogoutStatus)
        writeDouble(distance)
        writeInt(totalPoint)
        writeDouble(latitude)
        writeDouble(longitude)
        writeString(webProfileUrl)
        writeString(about)
        writeInt(avatarVersion)
        writeString(address)
        writeLong(totalGoldFans)
        writeString(color)
        writeByte(isSeller.toInt().toByte())
        writeInt(points)
    }

    protected constructor(p: Parcel) {
        userId = p.readString()
        roleId = p.readString()
        refId = p.readString()
        referralId = p.readString()
        userName = p.readString()
        DisplayName = p.readString()
        email = p.readString()
        emailVerified = p.readInt()
        emailToken = p.readString()
        userImage = p.readString()
        gender = p.readString()
        doB = p.readString()
        fbId = p.readString()
        nationality = p.readString()
        notification = p.readInt()
        nearbyFeature = p.readInt()
        searchable = p.readInt()
        notificationSound = p.readInt()
        hideMessageDetails = p.readInt()
        voiceCall = p.readInt()
        videoCall = p.readInt()
        messaging = p.readInt()
        language = p.readInt()
        giftReceivedCount = p.readInt()
        giftSentCount = p.readInt()
        totalBean = p.readLong()
        totalGold = p.readLong()
        status = p.readInt()
        created = p.readString()
        streaming = p.readInt()
        playtokenId = p.readString()
        followerCount = p.readInt()
        followingCount = p.readInt()
        isFollow = p.readInt()
        unreadMessageCount = p.readInt()
        unreadNotificationCount = p.readInt()
        loginLogoutStatus = p.readInt()
        distance = p.readDouble()
        totalPoint = p.readInt()
        latitude = p.readDouble()
        longitude = p.readDouble()
        webProfileUrl = p.readString()
        about = p.readString()
        avatarVersion = p.readInt()
        address = p.readString()
        totalGoldFans = p.readLong()
        color = p.readString()
        isSeller = p.readByte().toInt() != 0
        points = p.readInt()
    }


    companion object {
        @JvmStatic
        fun getUserImageByUserNameAndTime(username: String): String {
            return getUserImageByUserName(username) + "?t=" + System.currentTimeMillis()
        }

        @JvmStatic
        fun getUserImageThumbByUserNameAndTime(username: String): String {
            return getUserImageThumbByUserName(username) + "?t=" + System.currentTimeMillis()
        }

        @JvmStatic
        fun getUserImageByUserName(userName: String?): String {
            var s3ServerLink = Constants.AWS_S3_SERVER_LINK
            if (Constants.AWS_S3_SERVER_LINK.isNullOrEmpty()) {
                s3ServerLink = BuildConfig.AWS_S3_SERVER_LINK + "profile_image"
            }
            return "$s3ServerLink/$userName.jpg"
        }

        @JvmStatic
        fun getUserImageThumbByUserName(userName: String): String {
            var s3ServerLink = Constants.AWS_S3_SERVER_LINK
            if (Constants.AWS_S3_SERVER_LINK.isNullOrEmpty()) {
                s3ServerLink = BuildConfig.AWS_S3_SERVER_LINK + "profile_image_thum"
            }
            return "$s3ServerLink/$userName.jpg"
        }

        @JvmField
        val CREATOR = parcelableCreator(::UserModel)
    }
}
