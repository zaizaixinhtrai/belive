package com.data.entity

import com.appster.core.adapter.DisplayableItem
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 5/23/2018.
 */

class StreamsRecentEntity : DisplayableItem {
    @SerializedName("Publisher")
    val publisher: PublisherBean? = null
    @SerializedName("StreamRecording")
    val streamRecording: StreamRecordingBean? = null
    @SerializedName("WebStreamUrl")
    val webStreamUrl: String? = null
    @SerializedName("StreamId")
    val streamId: Int = 0
    @SerializedName("UserId")
    val userId: Int = 0
    @SerializedName("Slug")
    val slug: String? = null
    @SerializedName("CoverImage")
    val coverImage: String? = null
    @SerializedName("Title")
    val title: String? = null
    @SerializedName("Description")
    val description: String? = null
    @SerializedName("ViewCount")
    val viewCount: Int = 0
    @SerializedName("StreamType")
    val streamType: Int = 0
    @SerializedName("CountryCode")
    val countryCode: String? = null
    @SerializedName("StreamUrl")
    val streamUrl: String? = null
    @SerializedName("Status")
    val status: Int = 0
    @SerializedName("StartTime")
    val startTime: String? = null
    @SerializedName("EndTime")
    val endTime: String? = null
    @SerializedName("Tags")
    val tags: String? = null
    @SerializedName("IsRecorded")
    val isRecorded: Boolean = false
    @SerializedName("IsTrivia")
    val isTrivia: Boolean = false
    @SerializedName("TriviaId")
    val triviaId: Int = 0
    @SerializedName("Created")
    val created: String? = null
    @SerializedName("BeginStream")
    val beginStream: String? = null
    @SerializedName("EndStream")
    val endStream: String? = null
    @SerializedName("Latitude")
    val latitude: String? = null
    @SerializedName("Longitude")
    val longitude: String? = null
    @SerializedName("Address")
    val address: String? = null
    @SerializedName("FrameRate")
    val frameRate: Int = 0
    @SerializedName("CommentCount")
    val commentCount: Int = 0
    @SerializedName("StreamResultUrl")
    val streamResultUrl: String? = null
    @SerializedName("Hide")
    val hide: Boolean = false
    @SerializedName("OrderButtonText")
    val orderButtonText: String? = null
    @SerializedName("OrderInProgressText")
    val orderInProgressText: String? = null
    @SerializedName("TagName")
    val tagName: String? = null
    @SerializedName("Distance")
    val distance: String? = null
    @SerializedName("TitlePlainText")
    val titlePlainText: String? = null

    class PublisherBean {
        @SerializedName("UserId")
        val userId: Int = 0
        @SerializedName("UserName")
        val userName: String? = null
        @SerializedName("DisplayName")
        val displayName: String? = null
        @SerializedName("UserImage")
        val userImage: String? = null
        @SerializedName("Gender")
        val gender: String? = null
        @SerializedName("Handle")
        val handle: String? = null
        @SerializedName("FollowerCount")
        val followerCount: Int = 0
        @SerializedName("UserThumbnailImage")
        val userThumbnailImage: String? = null
    }

    class StreamRecordingBean {
        @SerializedName("DownloadUrl")
        val downloadUrl: String? = null
        @SerializedName("PlayUrl")
        val playUrl: String? = null
    }
}
