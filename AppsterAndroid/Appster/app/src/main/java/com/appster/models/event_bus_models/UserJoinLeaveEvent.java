package com.appster.models.event_bus_models;

/**
 * Created by phutang on 11/4/15.
 */
public class UserJoinLeaveEvent {
    private boolean isJoined;
    private String userName;
    private String displayName;

    public UserJoinLeaveEvent(boolean isJoined, String userName, String displayName) {
        this.isJoined = isJoined;
        this.userName = userName;
        this.displayName = displayName;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setIsJoined(boolean isJoined) {
        this.isJoined = isJoined;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "UserJoinLeaveEvent{" +
                "isJoined=" + isJoined +
                ", userName='" + userName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
