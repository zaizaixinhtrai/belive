package com.apster.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appster.models.NotificationPushModel;
import com.appster.R;
import com.appster.customview.CircleImageView;
import com.appster.utility.ImageLoaderUtil;

import com.apster.common.DateHelper;
import com.pack.utility.StringUtil;


/**
 * Created by User on 11/13/2015.
 */
public class NotificationView extends FrameLayout {

    private CircleImageView ivNotifyAvatar;
    private TextView tvNotifyUser;
    private TextView tvNotifyTime;
    private Animation animationShow;
    private Animation animationHidle;
    private NotificationPushModel pushModel;
    private Context context;
    private FrameLayout flNotificationContainer;
    public NotificationView(Context context) {
        super(context);
        this.context = context;
        setUpView();
    }

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setUpView();
    }

    private void setUpView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.notification_customeview, this, true);
        ivNotifyAvatar = (CircleImageView) findViewById(R.id.ivNotifyAvatar);
        tvNotifyUser = (TextView) findViewById(R.id.tvNotifyUser);
        tvNotifyTime = (TextView) findViewById(R.id.tvNotifyTime);
        flNotificationContainer = (FrameLayout) findViewById(R.id.flNotificationContainer);
        animationShow = AnimationUtils.loadAnimation(getContext(), R.anim.notification_show_animation);
        animationHidle = AnimationUtils.loadAnimation(getContext(), R.anim.notification_hidle_animation);

    }

    public void updateDataNotification(NotificationPushModel pushModel) {
        this.pushModel = pushModel;
        ImageLoaderUtil.displayUserImage(context, pushModel.getUserImage(), ivNotifyAvatar);
        tvNotifyUser.setText(StringUtil.decodeString(pushModel.getMessage()));
        tvNotifyTime.setText(DateHelper.getCurrentDateTimeNotification(context));
    }

    public void showAnimation() {
        this.startAnimation(animationShow);
        this.setVisibility(VISIBLE);
    }

    public void showAnimationThenAutoDismiss(){
        showAnimation();
        postDelayed(this::hideAnimation, 5000);
    }

    public void showAnimationThenAutoDismissFullscreen(){
        if(flNotificationContainer!=null) flNotificationContainer.setPadding(0,0,0,0);
        showAnimationThenAutoDismiss();
    }
    public void hideAnimation() {
        this.startAnimation(animationHidle);
        this.setVisibility(GONE);
    }

    public NotificationPushModel getPushModel() {
        return this.pushModel;
    }


}
