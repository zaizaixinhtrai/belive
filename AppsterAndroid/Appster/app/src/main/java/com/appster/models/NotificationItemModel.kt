package com.appster.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by User on 6/24/2016.
 */
class NotificationItemModel {

    var postId: Int = 0
    var notificationId: Int = 0
    var notificationType: Int = 0
    var message: String? = null
    var timestamp: Int = 0
    var isIsRead: Boolean = false
    var created: String? = null
    var actionUser: ActionUserBean? = null
    var receiver: ReceiverBean? = null
    @SerializedName("StreamInfo")
    @Expose
    var shortStreamInfoViewModel: ShortStreamInfoViewModel? = null

    class ActionUserBean(
            var userId: Int = 0,
            var userName: String? = "",
            var displayName: String? = "",
            var userImage: String? = "",
            var gender: String? = "",
            var roleId: Int = 0
    )

    class ReceiverBean(
            var userId: String? = "",
            var userName: String? = "",
            var displayName: String? = "",
            var userImage: String? = "",
            var gender: String? = "",
            var roleId: Int = 0
    )

    class ShortStreamInfoViewModel(
            @SerializedName("Slug")
            @Expose
            var slug: String? = "",
            @SerializedName("Status")
            @Expose
            var status: Int = 0,
            @SerializedName("IsRecorded")
            @Expose
            var isRecorded: Boolean = false,
            @SerializedName("StreamUrl")
            @Expose
            var streamUrl: String? = ""

    )

}
