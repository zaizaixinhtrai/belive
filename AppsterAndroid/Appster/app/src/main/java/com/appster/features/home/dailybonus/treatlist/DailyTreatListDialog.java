package com.appster.features.home.dailybonus.treatlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.core.adapter.DisplayableItem;
import com.appster.dialog.NoTitleDialogFragment;
import com.appster.features.home.dailybonus.treatlist.adapter.TreatListAdapterDelegate;
import com.appster.webservice.AppsterWebServices;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by thanhbc on 11/7/17.
 */

public class DailyTreatListDialog extends NoTitleDialogFragment implements DailyTreatListContract.View {
    @Bind(R.id.ivBackToTreatMachine)
    ImageView ivBackToTreatMachine;

    DailyTreatListContract.UserActions mPresenter;

    TreatListAdapterDelegate mTreatListAdapterDelegate;
    @Bind(R.id.rcvWeekTreatList)
    RecyclerView rcvWeekTreatList;

    public static DailyTreatListDialog newInstance() {
        DailyTreatListDialog fragment = new DailyTreatListDialog();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        mPresenter = new DailyTreatListPresenter(AppsterWebServices.get());
        mPresenter.attachView(this);
        mTreatListAdapterDelegate = new TreatListAdapterDelegate(null);
        mTreatListAdapterDelegate.setItems(new ArrayList<>());
        rcvWeekTreatList.setAdapter(mTreatListAdapterDelegate);
        mPresenter.getThisWeekTreatList();
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_daily_treat_list;
    }

    @Override
    protected boolean isDimDialog() {
        return true;
    }


    @Override
    protected int getWindowAnimation() {
        return R.style.DialogFadeAnimation;
    }

    @Override
    protected float dimAmount() {
        return 0.9f;
    }

    @OnClick(R.id.ivBackToTreatMachine)
    public void onViewClicked() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {

        try {
            return super.show(transaction, tag);
        }catch (IllegalStateException e){
            Timber.e(e);
        }
        return -1;
    }

    @Override
    public Context getViewContext() {
        return getContext();
    }

    @Override
    public void loadError(String errorMessage, int code) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onTreatsListReceived(List<DisplayableItem> displayableItems) {
        mTreatListAdapterDelegate.updateItems(displayableItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
