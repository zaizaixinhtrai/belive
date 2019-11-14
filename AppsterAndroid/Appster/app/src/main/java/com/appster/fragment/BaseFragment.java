package com.appster.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.models.ListenerEventModel;
import com.appster.utility.RxUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by USER on 10/8/2015.
 */

public class BaseFragment extends Fragment {
    protected View mRootView;
    protected OnFragmentInteractionListener mListener;
    protected Bundle mArgument;
    com.pack.progresshud.ProgressHUD dialog;
    String dialogMessage = "";
    public boolean _areLecturesLoaded = false;

    protected CompositeSubscription mCompositeSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        createDialogLoading(getActivity(), dialogMessage);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mListener != null) {
            mListener.onFragmentAttachToActivity(this);
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        if (dialog != null && dialog.isShowing()) {
            dismissDilaog();
        }
        dialog = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = AppsterApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentAttachToActivity(BaseFragment fragment);
    }

    protected Bundle getBundleData() {
        return mArgument;
    }

    public void setData(Bundle args) {
        mArgument = args;
    }


    public void createDialogLoading(Context mcContext, String message) {
        dialog = new com.pack.progresshud.ProgressHUD(mcContext,
                R.style.ProgressHUD);
        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_hudd);
        if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
        } else {
            TextView txt = (TextView) dialog.findViewById(R.id.message);
            txt.setText(message);
        }
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);

    }

    protected void showDialog() {
        if (isFragmentUIActive()) {
            if (dialog != null) dialog.show();
        }
    }

    protected void dismissDilaog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    public void onErrorWebServiceCall(String errorMessage, int erroCode) {

        Activity activity = getActivity();

        if (activity == null)
            return;

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            ((BaseActivity) getActivity()).handleError(errorMessage, erroCode);
        }
    }

    public void eventChange(ListenerEventModel listenerEventModel) {

    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    public void scrollTopUpRecyclerView(RecyclerView recyclerView, boolean isSmoothScroll) {
        if (isFragmentUIActive() && recyclerView != null) {
//            recyclerView.post(() -> recyclerView.smoothScrollToPosition(0));
            if (isSmoothScroll) {
                recyclerView.smoothScrollToPosition(0);
            } else {
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        }
    }


}
