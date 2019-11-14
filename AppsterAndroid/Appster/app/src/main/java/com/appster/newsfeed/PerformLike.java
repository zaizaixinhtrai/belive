package com.appster.newsfeed;

import android.content.Context;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.LikePostRequestModel;
import com.appster.activity.BaseActivity;
import com.apster.common.Constants;

/**
 * Created by User on 8/26/2015.
 */
public class PerformLike {

    String message = "";
    private Context mContext;
    private String post_id;
    private String post_owner_id = "";
    private int likeType;

    private FinishLike mFinishLike;
    private int positionListview;

    public PerformLike(Context context, String post_id, String post_owner_id, int likeType) {
        this.mContext = context;
        this.post_id = post_id;
        this.post_owner_id = post_owner_id;
        this.likeType = likeType;
        this.positionListview = 0;
        message = context.getResources().getString(R.string.connecting_msg);
    }

    public void setmFinishLike(FinishLike mFinishLike) {
        this.mFinishLike = mFinishLike;
    }

    public void likePost() {
        LikePostRequestModel request = new LikePostRequestModel();
        request.setLike(likeType == Constants.NEWS_FEED_LIKE);
        request.setPost_id(post_id);

        AppsterWebServices.get().likePost("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(likePostResponseModel -> {
                    if (likePostResponseModel == null) return;
                    if (likePostResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mFinishLike.successLikeListener(positionListview, likeType);
                    } else {
                        ((BaseActivity) mContext).handleError(likePostResponseModel.getMessage(),
                                likePostResponseModel.getCode());
                    }
                },error -> {
                    ((BaseActivity) mContext).handleError(error.getMessage(), Constants.RETROFIT_ERROR);

                    mFinishLike.errorLikeClickListener(positionListview);
                });


    }

    public interface FinishLike {
        void errorLikeClickListener(int positionListview);

        void successLikeListener(int positionListview, int typeLike);

    }
}
