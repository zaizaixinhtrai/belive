package com.appster.trending;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.appster.models.LeaderBoardModel;
import com.appster.fragment.BaseFragment;
import com.appster.R;
import com.apster.common.Constants;
import com.pack.utility.CheckNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 9/26/2015.
 */
public class FragmentTrending extends BaseFragment {

    public static FragmentTrending newInstance() {
        FragmentTrending fragment = new FragmentTrending();
        return fragment;
    }

    public enum TypeList {
        THIS_WEEK,
        THIS_MONTH,
        ALL_TIME;

        public int getValue() {

            switch (this) {
                case THIS_WEEK:
                    return Constants.TRENDING_THIS_WEEK_TYPE;
                case THIS_MONTH:
                    return Constants.TRENDING_THIS_MONTH_TYPE;
                default:
                    return Constants.TRENDING_THIS_ALL_TIME_TYPE;
            }
        }
    }

    private static final String BUNDEL_TYPE_LIST = "BUNDEL_TYPE_LIST";
    private TypeList typeList = TypeList.THIS_WEEK;

    private View rootView;
    private boolean _areLecturesLoaded = false;
    private GetTrendingData getData;
    private ListView lv_trending;
    private AdapterTrending adapter;
    private ArrayList<LeaderBoardModel> arrItem = new ArrayList<>();
    private TextView noDataView;

    public static FragmentTrending getInstance(TypeList typeList) {
        FragmentTrending f = new FragmentTrending();
        Bundle args = new Bundle();
        args.putInt(BUNDEL_TYPE_LIST, typeList.getValue());
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            return rootView;
        }

        int intValueType = getArguments() != null ? getArguments().getInt(BUNDEL_TYPE_LIST) : TypeList.ALL_TIME.getValue();
        typeList = TypeList.values()[intValueType];
        rootView = inflater.inflate(R.layout.trending_popular_layout, container, false);
        lv_trending = (ListView) rootView.findViewById(R.id.lv_trending);
        noDataView = (TextView) rootView.findViewById(R.id.no_data_view);
        adapter = new AdapterTrending(arrItem, getActivity());
        lv_trending.setAdapter(adapter);

        initGetData();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !_areLecturesLoaded) {

            if (CheckNetwork.isNetworkAvailable(getActivity())) {
                if (getData == null) {
                    initGetData();
                }

                getData.getDataTrending();
            }

            _areLecturesLoaded = true;
        }
    }

    private void initGetData() {

        getData = new GetTrendingData(getActivity(), typeList.getValue());
        getData.setGetTrendingDataListener(new GetTrendingData.GetTrendingDataListener() {
            @Override
            public void onSuccess(List<LeaderBoardModel> arrTrending) {

                if (arrTrending != null && arrTrending.size() > 0) {
                    arrItem.addAll(arrTrending);
                    adapter.notifyDataSetChanged();
                } else if (arrTrending == null || arrTrending.size() == 0) {
                    noDataView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onErrorLoading(String errorMessage, int errorCode) {
                onErrorWebServiceCall(errorMessage, errorCode);
            }
        });
    }
}


