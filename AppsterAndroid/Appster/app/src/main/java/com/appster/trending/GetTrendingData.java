package com.appster.trending;

import android.content.Context;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.TrendingListRequestModel;
import com.appster.models.LeaderBoardModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 9/26/2015.
 */
public class GetTrendingData {
    private Context mContext;
    private GetTrendingDataListener mGetTrendingDataListener;
    private int type;

    public GetTrendingData(Context context,int type) {
        this.mContext = context;
        this.type = type;
    }

    public void setGetTrendingDataListener(GetTrendingDataListener mGetTrendingDataListener) {
        this.mGetTrendingDataListener = mGetTrendingDataListener;
    }

    public void getDataTrending() {
        DialogManager.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.connecting_msg));
        TrendingListRequestModel request = new TrendingListRequestModel();
        request.setType(type);

        AppsterWebServices.get().getTrendingList("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(trendingListResponseModel -> {
                    DialogManager.getInstance().dismisDialog();
                    if (trendingListResponseModel == null) return;
                    if (trendingListResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mGetTrendingDataListener.onSuccess(trendingListResponseModel.getData());
                    } else {
                        mGetTrendingDataListener.onErrorLoading(trendingListResponseModel.getMessage(),
                                trendingListResponseModel.getCode());
                    }
                },error -> {
                    DialogManager.getInstance().dismisDialog();

                    mGetTrendingDataListener.onErrorLoading(error.getMessage(), Constants.RETROFIT_ERROR);
                });

    }

    public interface GetTrendingDataListener {
        void onSuccess(List<LeaderBoardModel> arrTrending);

        void onErrorLoading(String errorMessage, int errorCode);
    }
}
