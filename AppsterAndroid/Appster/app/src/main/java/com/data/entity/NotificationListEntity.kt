package com.data.entity

import com.google.gson.annotations.SerializedName


data class NotificationListEntity(
        @field:SerializedName("PostId")
        val postId: Int,
        @field:SerializedName("NotificationId")
        val notificationId: Int,
        @field:SerializedName("NotificationType")
        val notificationType: Int,
        @field:SerializedName("Message")
        val message: String,
        @field:SerializedName("Timestamp")
        val timestamp: Int,
        @field:SerializedName("IsRead")
        val isRead: Boolean,
        @field:SerializedName("Created")
        val created: String,
        @field:SerializedName("ActionUser")
        val actionUser: ActionUser?,
        @field:SerializedName("Receiver")
        val receiver: Receiver?,
        @field:SerializedName("StreamInfo")
        val streamInfo: StreamInfo?
)

data class StreamInfo(
        @field:SerializedName("Slug")
        val slug: String,
        @field:SerializedName("Status")
        val status: Int,
        @field:SerializedName("IsRecorded")
        val isRecorded: Boolean,
        @field:SerializedName("StreamUrl")
        val streamUrl: String
)

data class ActionUser(
        @field:SerializedName("UserId")
        val userId: Int,
        @field:SerializedName("UserName")
        val userName: String,
        @field:SerializedName("DisplayName")
        val displayName: String,
        @field:SerializedName("UserImage")
        val userImage: String,
        @field:SerializedName("Gender")
        val gender: String,
        @field:SerializedName("RoleId")
        val roleId: Int
)

data class Receiver(
        @field:SerializedName("UserId")
        val userId: String,
        @field:SerializedName("UserName")
        val userName: String,
        @field:SerializedName("DisplayName")
        val displayName: String,
        @field:SerializedName("UserImage")
        val userImage: String,
        @field:SerializedName("Gender")
        val gender: String,
        @field:SerializedName("RoleId")
        val roleId: Int
)