package com.appster.interfaces;

/**
 * Created by User on 11/16/2015.
 */
public interface OnSetFollowUserListener {
    void onFinishFollow(boolean isFollow);

    void onError(int errorCode, String message);
}
