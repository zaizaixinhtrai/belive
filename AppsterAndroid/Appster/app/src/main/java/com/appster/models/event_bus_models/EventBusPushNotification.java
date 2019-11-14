package com.appster.models.event_bus_models;

import com.appster.models.NotificationPushModel;

/**
 * Created by User on 11/10/2015.
 */
public class EventBusPushNotification {

    public EventBusPushNotification(int unreadNumber, int notificationType){
        this.unreadNumber= unreadNumber;
        this.notificationType= notificationType;
    }

    public EventBusPushNotification(int unreadNumber, NotificationPushModel pushNotificationModel){
        this.unreadNumber= unreadNumber;
        this.notificationType= pushNotificationModel.getNotificationType();
        this.pushNotificationModel = pushNotificationModel;
    }
    int unreadNumber;
    int notificationType;
    NotificationPushModel pushNotificationModel;

    public int getUnreadNumber() {
        return unreadNumber;
    }

    public void setUnreadNumber(int unreadNumber) {
        this.unreadNumber = unreadNumber;

    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationPushModel getPushNotificationModel() {
        return pushNotificationModel;
    }

    public void setPushNotificationModel(NotificationPushModel pushNotificationModel) {
        this.pushNotificationModel = pushNotificationModel;
    }
}
