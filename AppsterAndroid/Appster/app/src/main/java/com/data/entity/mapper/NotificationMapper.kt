package com.data.entity.mapper

import com.appster.models.NotificationItemModel
import com.appster.webservice.response.BaseDataPagingResponseModel
import com.data.entity.NotificationListEntity

class NotificationMapper {
    fun transform(notificationListEntity: BaseDataPagingResponseModel<NotificationListEntity>?): BaseDataPagingResponseModel<NotificationItemModel>? {
        return notificationListEntity?.let {
            return BaseDataPagingResponseModel<NotificationItemModel>().apply {
                isEnd = it.isEnd
                nextId = it.nextId
                setResult(it.result?.map {
                    NotificationItemModel().apply {
                        postId = it.postId
                        notificationId = it.notificationId
                        notificationType = it.notificationType
                        message = it.message
                        timestamp = it.timestamp
                        isIsRead = it.isRead
                        created = it.created
                        actionUser = it.actionUser?.run { NotificationItemModel.ActionUserBean(userId, userName, displayName, userImage, gender, roleId) }
                        receiver = it.receiver?.run { NotificationItemModel.ReceiverBean(userId, userName, displayName, userImage, gender, roleId) }
                        shortStreamInfoViewModel = it.streamInfo?.run { NotificationItemModel.ShortStreamInfoViewModel(slug, status, isRecorded, streamUrl) }
                    }
                })
            }
        }
    }
}