package com.appster.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.appster.R;
import com.appster.features.user_profile.DialogUserProfileFragment;
import com.appster.interfaces.DiaLogDismissListener;
import com.appster.layout.TopPanLayout;
import com.appster.models.UserModel;
import com.apster.common.Utils;

/**
 * Created by User on 9/5/2016.
 */
public class TopFanDialog implements DialogInterface.OnDismissListener {

    private Dialog dialogTopFan;
    private Context context;
    private UserModel mUserModel;
    private FrameLayout topPanContent;
    private TopPanLayout topPanLayout;
    private LinearLayout contentLayout;
    private FrameLayout rootView;
    //    private ImageView close;
    private FrameLayout header;
    private DiaLogDismissListener mDialogDismisListener;
    private DialogUserProfileFragment.UserProfileActionListener mUserProfileActionListener;
    private boolean isViewer;

    public static TopFanDialog newInstance(Context context, UserModel userModel, boolean isHot, boolean isViewer) {
        return new TopFanDialog(context, userModel, isHot, isViewer);
    }

    public TopFanDialog(Context context, UserModel userModel, boolean isHot, boolean isViewer) {

        this.context = context;
        this.mUserModel = userModel;
        this.isViewer = isViewer;

        if (isHot) {
            dialogTopFan = new Dialog(context, R.style.DialogTopFanHotSlideToUpAnim);
            dialogTopFan.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialogTopFan = new Dialog(context, R.style.DialogTopFanViewerSlideToUpAnim);
        }

//        if (isHot) {
        Window window = dialogTopFan.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            WindowManager.LayoutParams layoutParams = dialogTopFan.getWindow().getAttributes();
//            layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            window.setAttributes(layoutParams);
//        }

        dialogTopFan.setCanceledOnTouchOutside(true);
        dialogTopFan.setContentView(R.layout.top_fan_dialog);
        WindowManager.LayoutParams layoutParams = dialogTopFan.getWindow().getAttributes();
        layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(layoutParams);
        if (isHot) {
            dialogTopFan.getWindow().setLayout(Utils.getScreenWidth(),
                    Utils.getScreenHeight());
        } else {
            dialogTopFan.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        dialogTopFan.setOnDismissListener(this);
        topPanContent = (FrameLayout) dialogTopFan.findViewById(R.id.toppan_list);
        contentLayout = (LinearLayout) dialogTopFan.findViewById(R.id.contentLayout);
        rootView = (FrameLayout) dialogTopFan.findViewById(R.id.root_view);
//        close = (ImageView) dialogTopFan.findViewById(R.id.close);
        header = (FrameLayout) dialogTopFan.findViewById(R.id.header);

        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
        params.height = Utils.getScreenHeight() / 2;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        contentLayout.setLayoutParams(params);

        addLayout();
        bindEvent();
    }

    private void bindEvent() {
        rootView.setOnClickListener(v -> dismiss());

//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

        header.setOnClickListener(null);
        topPanContent.setOnClickListener(null);
    }

    public void setUserProfileActionListener(DialogUserProfileFragment.UserProfileActionListener userProfileActionListener) {
        mUserProfileActionListener = userProfileActionListener;
        if (topPanLayout != null) {
            topPanLayout.setUserProfileActionListener(mUserProfileActionListener);
        }
    }

    private void addLayout() {
        if (mUserModel != null) {
            topPanLayout = new TopPanLayout(context, mUserModel, true, true, isViewer);
            topPanLayout.setUserProfileActionListener(mUserProfileActionListener);
            topPanContent.addView(topPanLayout);
        }
    }

    public void show() {

        if (dialogTopFan != null) {
            dialogTopFan.show();
        }
    }

    public void dismiss() {
        if (dialogTopFan != null && dialogTopFan.isShowing()) {
            dialogTopFan.dismiss();
            dialogTopFan = null;
        }
    }


    public void setDialogDismisListener(DiaLogDismissListener dialogDismisListener) {
        mDialogDismisListener = dialogDismisListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDialogDismisListener != null) mDialogDismisListener.onDiaLogDismissed();
    }


    public void setOnDismissListener(DiaLogDismissListener dismissListener) {
        this.mDialogDismisListener = dismissListener;
    }

}
