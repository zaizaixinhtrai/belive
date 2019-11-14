package com.appster.giftreceive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.appster.AppsterApplication;
import com.appster.models.GiftReceiverModel;
import com.appster.R;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.GiftStoreRequestModel;
import com.appster.activity.BaseActivity;
import com.appster.adapters.AdapterGiftStoreTemp;
import com.appster.fragment.BaseFragment;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pack.utility.CheckNetwork;

import java.util.ArrayList;

/**
 * Created by User on 11/3/2015.
 */
public class GiftStoreFragment extends BaseFragment {

    public enum GiftType {
        Received,
        Sent
    }

    private GiftType giftType;

    private View rootView;
    private int nextIndex = 0;
    private boolean isTheEnd = false;
    private PullToRefreshListView lv_gift;
    private AdapterGiftStoreTemp adapter;
    private ArrayList<GiftReceiverModel> arrItem = new ArrayList<>();

    private boolean _areLecturesLoaded = false;

    private TextView noDataView;

    public static GiftStoreFragment newInstance(GiftType notifyType) {

        GiftStoreFragment giftStoreFragment = new GiftStoreFragment();
        giftStoreFragment.giftType = notifyType;
        return giftStoreFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_gift_store, container, false);
        lv_gift = (PullToRefreshListView) rootView.findViewById(R.id.lv_gift);
        noDataView = (TextView) rootView.findViewById(R.id.no_data_view);
        lv_gift.setMode(PullToRefreshBase.Mode.BOTH);
        adapter = new AdapterGiftStoreTemp(getActivity(), R.layout.gift_store_adapter_row, arrItem);
        lv_gift.setAdapter(adapter);

        lv_gift.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (CheckNetwork.isNetworkAvailable(getActivity())) {
                    nextIndex = 0;
                    getData(false);
                } else {
                    ((BaseActivity) getActivity()).scrollBottomOfListView(lv_gift);
                    ((BaseActivity) getActivity()).toastTextWhenNoInternetConnection("");
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (isTheEnd) {
                    ((BaseActivity) getActivity()).scrollBottomOfListView(lv_gift);
                    return;
                }

                if (CheckNetwork.isNetworkAvailable(getActivity())) {
                    getData(false);
                } else {
                    ((BaseActivity) getActivity()).scrollBottomOfListView(lv_gift);
                    ((BaseActivity) getActivity()).toastTextWhenNoInternetConnection("");
                }
            }
        });

        if (CheckNetwork.isNetworkAvailable(getActivity())) {
            getData(true);
        }

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !_areLecturesLoaded) {

            _areLecturesLoaded = true;
        }
    }


    private void getData(boolean isShowDialog) {

        if (giftType == GiftType.Received) {

            getGiftHasBeenReceived(isShowDialog);
        } else {
            getGiftHasBeenSend(isShowDialog);
        }
    }

    private void getGiftHasBeenSend(final boolean isShowDialog) {

        if (isShowDialog) {
            DialogManager.getInstance().showDialog(getActivity(), getActivity().getResources().getString(R.string.connecting_msg));
        }

        GiftStoreRequestModel request = new GiftStoreRequestModel();
        request.setLimit(Constants.PAGE_LIMITED);
        request.setNextId(nextIndex);
        AppsterWebServices.get().getListGiftSend("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(giftReceiverResponseModel -> {
                    lv_gift.onRefreshComplete();
                    if (isShowDialog) {
                        DialogManager.getInstance().dismisDialog();
                    }

                    if (giftReceiverResponseModel == null) return;
                    if (giftReceiverResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        if (nextIndex == 0) {
                            arrItem.clear();
                        }

                        if (giftReceiverResponseModel.getData().getResult() != null
                                && giftReceiverResponseModel.getData().getResult().size() > 0) {

                            arrItem.addAll(giftReceiverResponseModel.getData().getResult());
                        }
                        adapter.notifyDataSetChanged();

                        isTheEnd = giftReceiverResponseModel.getData().isEnd();
                        nextIndex = giftReceiverResponseModel.getData().getNextId();

                    } else {
                        ((BaseActivity) getActivity()).handleError(giftReceiverResponseModel.getMessage(),
                                giftReceiverResponseModel.getCode());
                    }
                },error -> {
                    lv_gift.onRefreshComplete();
                    if (isShowDialog) DialogManager.getInstance().dismisDialog();
                    ((BaseActivity) getActivity()).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                });
    }

    private void getGiftHasBeenReceived(final boolean isShowDialog) {

        if (isShowDialog) {
            DialogManager.getInstance().showDialog(getActivity(), getActivity().getResources().getString(R.string.connecting_msg));
        }

        GiftStoreRequestModel request = new GiftStoreRequestModel();
        request.setLimit(Constants.PAGE_LIMITED);
        request.setNextId(nextIndex);
        AppsterWebServices.get().getListGiftReceive("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(giftReceiverResponseModel -> {

                    lv_gift.onRefreshComplete();
                    if (isShowDialog) {
                        DialogManager.getInstance().dismisDialog();
                    }

                    if (giftReceiverResponseModel == null) return;
                    if (giftReceiverResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        if (nextIndex == 0) {
                            arrItem.clear();
                        }

                        if (giftReceiverResponseModel.getData().getResult() != null
                                && giftReceiverResponseModel.getData().getResult().size() > 0) {

                            arrItem.addAll(giftReceiverResponseModel.getData().getResult());
                        } else if (isShowDialog &&
                                (giftReceiverResponseModel.getData().getResult() == null
                                        || giftReceiverResponseModel.getData().getResult().size() == 0)) {
                            noDataView.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();

                        isTheEnd = giftReceiverResponseModel.getData().isEnd();
                        nextIndex = giftReceiverResponseModel.getData().getNextId();

                    } else {
                        ((BaseActivity) getActivity()).handleError(giftReceiverResponseModel.getMessage(),
                                giftReceiverResponseModel.getCode());
                    }
                },error -> {
                    lv_gift.onRefreshComplete();
                    if (isShowDialog) DialogManager.getInstance().dismisDialog();
                    ((BaseActivity) getActivity()).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                });


    }
}
