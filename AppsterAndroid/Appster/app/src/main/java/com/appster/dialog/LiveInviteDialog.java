package com.appster.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.appster.R;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.RippleBackground;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ngoc on 9/7/2017.
 */

public class LiveInviteDialog extends DialogFragment {
    private static final String HOST_DISPLAY_NAME = "hot_display_name";
    private static final String USER_IMAGE_LINK = "user_image_link";
    @Bind(R.id.imv_profile_image)
    CircleImageView imvProfileImage;
    @Bind(R.id.rippleBackground)
    RippleBackground rippleBackground;
    @Bind(R.id.dialog_title)
    CustomFontTextView dialogTitle;
    @Bind(R.id.tv_message)
    CustomFontTextView tvMessage;

    private LiveInviteListener mLiveInviteListener;
    private String mHostDisplayName;
    private String mUserImageLink;

    public static LiveInviteDialog newInstance(String hostDisplayName, String userImageLink) {
        LiveInviteDialog dialog = new LiveInviteDialog();
        Bundle bundle = new Bundle();
        bundle.putString(HOST_DISPLAY_NAME, hostDisplayName);
        bundle.putString(USER_IMAGE_LINK, userImageLink);
        dialog.setArguments(bundle);
        return dialog;
    }

    public void setLiveInviteListener(LiveInviteListener liveInviteListener) {
        this.mLiveInviteListener = liveInviteListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(Utils.dpToPx(280), ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogZoomAnimation;
        }

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.live_invite_dialog, null);
        mHostDisplayName = getArguments().getString(HOST_DISPLAY_NAME);
        mUserImageLink = getArguments().getString(USER_IMAGE_LINK);

        ButterKnife.bind(this, view);
        ImageLoaderUtil.displayUserImage(getContext().getApplicationContext(), mUserImageLink, imvProfileImage);
        tvMessage.setText(getContext().getString(R.string.video_call_host_invitation,mHostDisplayName));
        rippleBackground.startRippleAnimation();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
    }

    @OnClick(R.id.ok)
    public void onClickAccept() {
        if (mLiveInviteListener != null) {
            mLiveInviteListener.onAccept();
        }
//        dismiss();
    }

    @OnClick(R.id.cancel)
    public void onClickDecline() {
        if (mLiveInviteListener != null) {
            mLiveInviteListener.onDecline();
        }
        dismissAllowingStateLoss();
    }

    public void onPermissionDenied(){
        if(isAdded()) onClickDecline();
    }

    public void onPermissionGranted(){
        if(isAdded()) onClickAccept();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface LiveInviteListener {
        void onDecline();

        void onAccept();
    }
}
