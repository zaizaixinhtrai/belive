package com.appster.pocket;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appster.AppsterApplication;
import com.appster.models.PocketHistoryModel;
import com.appster.R;
import com.appster.utility.ConstantBundleKey;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.GetBeanHistoryRequestModel;
import com.appster.activity.BaseToolBarActivity;
import com.apster.common.Constants;
import com.apster.common.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 11/4/2015.
 */
public class ActivityBeans extends BaseToolBarActivity {

    public static final int Pocket_Type_Bean = 1;
    public static final int Pocket_Type_USD = 2;
    private int type = Pocket_Type_Bean;

    @Bind(R.id.imv_bean)
    ImageView imvBean;
    @Bind(R.id.txt_usd)
    TextView txtUsd;
    @Bind(R.id.llSenderMessageContainer)
    TextView llLeft;
    @Bind(R.id.txt_bean)
    TextView txtBean;
    @Bind(R.id.edt_date_start)
    TextView edtDateStart;
    @Bind(R.id.pocket_to)
    TextView pocketTo;
    @Bind(R.id.edt_date_end)
    TextView edtDateEnd;
    @Bind(R.id.lv_bean_history)
    PullToRefreshListView lvBeanHistory;

    private boolean isEnd = false;
    private int nextIndex;
    private String startDate;
    private String endDate;

    private AdapterBeansHistory adapter;
    private ArrayList<PocketHistoryModel> arrItem = new ArrayList<>();

    DatePickerDialog.OnDateSetListener mDateStartListen = null;
    DatePickerDialog.OnDateSetListener mDateEndListen = null;

    private int typeStart = 1;
    private int typeEnd = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        // Get Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt(ConstantBundleKey.BUNDLE_POCKET_TYPE, Pocket_Type_Bean);
            long value = extras.getLong(ConstantBundleKey.BUNDLE_POCKET_VALUE);
            txtBean.setText(value + "");

            if (type == Pocket_Type_Bean) {

                llLeft.setText(getString(R.string.pocket_Beans));

            } else {

                imvBean.setBackgroundResource(R.drawable.image_gold);
                txtBean.setTextColor(Color.parseColor("#F7941D"));

                llLeft.setText(getString(R.string.pocket_USD));
            }
        }

        adapter = new AdapterBeansHistory(this, R.layout.adapter_beans_history_row, arrItem);
        lvBeanHistory.setAdapter(adapter);

        lvBeanHistory.setMode(PullToRefreshBase.Mode.BOTH);
        lvBeanHistory.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (CheckNetwork.isNetworkAvailable(ActivityBeans.this)) {
                    nextIndex = 0;
                    getData(false);
                } else {
                    scrollBottomOfListView(lvBeanHistory);
                    toastTextWhenNoInternetConnection("");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (!isEnd) {

                    scrollBottomOfListView(lvBeanHistory);
                    toastTextOnTheEndListListener("");
                    return;
                }

                if (CheckNetwork.isNetworkAvailable(ActivityBeans.this)) {
                    getData(false);
                } else {
                    scrollBottomOfListView(lvBeanHistory);
                    toastTextWhenNoInternetConnection("");
                }

            }
        });

        getCurrentDate();

        if (CheckNetwork.isNetworkAvailable(this)) {
            getData(true);
        }
    }

    private void getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        endDate = Utils.checkDigit(day) + "/" + Utils.checkDigit(month + 1) + "/" + year;//year + "-" + Utils.checkDigit(month + 1) + "-" + Utils.checkDigit(day);
        edtDateEnd.setText(endDate);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTopBarTile(getString(R.string.pocket_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_pocket_beans;
    }

    @Override
    public void init() {

        ButterKnife.bind(this);

        mDateStartListen = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                startDate =Utils.checkDigit(dayOfMonth) + "/" + Utils.checkDigit(monthOfYear + 1) + "/" + year ;// + "-" + Utils.checkDigit(monthOfYear + 1) + "-" + Utils.checkDigit(dayOfMonth);
                edtDateStart.setText(startDate);
                if (SetDateTime.compareDate(startDate, endDate)) {

                    nextIndex = 0;
                    getData(true);
                } else {
                    DialogInfoUtility dialog = new DialogInfoUtility();
                    dialog.showMessage(getString(R.string.app_name), getString(R.string.pocket_start_date_over_current),
                            ActivityBeans.this);
                }
            }
        };

        mDateEndListen = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtDateEnd.setText(Utils.checkDigit(dayOfMonth) + "/" + Utils.checkDigit(monthOfYear + 1) + "/" + year);
                endDate = year + "-" + Utils.checkDigit(monthOfYear + 1) + "-" + Utils.checkDigit(dayOfMonth);

                if (SetDateTime.compareDate(startDate, endDate)) {
                    nextIndex = 0;
                    getData(true);
                } else {
                    DialogInfoUtility dialog = new DialogInfoUtility();
                    dialog.showMessage(getString(R.string.app_name), getString(R.string.pocket_start_date_over_current),
                            ActivityBeans.this);
                }
            }
        };


        edtDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (preventMultiClicks()) {
                    return;
                }

                viewChooseDate(mDateStartListen, typeStart);
            }
        });

        edtDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (preventMultiClicks()) {
                    return;
                }

                viewChooseDate(mDateEndListen, typeEnd);
            }
        });

        goneNotify(true);
    }

    private void viewChooseDate(final DatePickerDialog.OnDateSetListener mDateListen, int type) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String dob = "";

        if (type == typeStart) {
            dob = edtDateStart.getText().toString().trim();
        } else if (type == typeEnd) {
            dob = edtDateEnd.getText().toString().trim();
        }

        if (!StringUtil.isNullOrEmptyString(dob)) {
            if (dob.contains("/")) {
                String[] arrDob = dob.split("/");

                if (arrDob != null && arrDob.length == 3) {

                    year = Integer.parseInt(arrDob[2]);
                    month = Integer.parseInt(arrDob[1]) - 1;
                    day = Integer.parseInt(arrDob[0]);
                }
            }
        }

        final DatePickerDialog mDateDialog = new DatePickerDialog(ActivityBeans.this, mDateListen, year, month, day);
        mDateDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getData(boolean isViewConnect) {

        if (type == Pocket_Type_Bean) {
            getBeansHistory(isViewConnect);
        } else {
            getGoldHistory(isViewConnect);
        }

    }

    private void getBeansHistory(boolean isViewConnect) {

        if (isViewConnect) {
            showDialog(ActivityBeans.this, getString(R.string.connecting_msg));
        }

        GetBeanHistoryRequestModel request = new GetBeanHistoryRequestModel();
        request.setIndex(nextIndex);
        request.setLimit(Constants.PAGE_LIMITED);
        request.setUserId(AppsterApplication.mAppPreferences.getUserModel().getUserId());
        request.setFrom_date(startDate);
        request.setTo_date(endDate);

        AppsterWebServices.get().getBeansHistory("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(beanHistoryDataResponse -> {
                    dismisDialog();
                    lvBeanHistory.onRefreshComplete();


                    if (beanHistoryDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (nextIndex == 0) {

                            arrItem.clear();
                        }

                        if (beanHistoryDataResponse.getData() != null &&
                                beanHistoryDataResponse.getData().getResult().size() > 0) {
                            arrItem.addAll(beanHistoryDataResponse.getData().getResult());

                        }

                        txtBean.setText(beanHistoryDataResponse.getData().getTotalBean() + "");
                        adapter.notifyDataSetChanged();

                        nextIndex = beanHistoryDataResponse.getData().getNextId();
                        isEnd = beanHistoryDataResponse.getData().isEnd();

                    } else {
                        handleError(beanHistoryDataResponse.getMessage(), beanHistoryDataResponse.getCode());
                    }
                },error -> {
                    lvBeanHistory.onRefreshComplete();
                    dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                });

    }

    private void getGoldHistory(boolean isViewConnect) {

        if (isViewConnect) {
            showDialog(ActivityBeans.this, getString(R.string.connecting_msg));
        }

        GetBeanHistoryRequestModel request = new GetBeanHistoryRequestModel();
        request.setIndex(nextIndex);
        request.setLimit(Constants.PAGE_LIMITED);
        request.setUserId(AppsterApplication.mAppPreferences.getUserModel().getUserId());
        request.setFrom_date(startDate);
        request.setTo_date(endDate);

        AppsterWebServices.get().getGoldHistory("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(beanHistoryDataResponse -> {
                    lvBeanHistory.onRefreshComplete();
                    dismisDialog();
                    if (beanHistoryDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        if (nextIndex == 0) {

                            arrItem.clear();
                        }

                        if (beanHistoryDataResponse.getData() != null &&
                                beanHistoryDataResponse.getData().getResult().size() > 0) {
                            arrItem.addAll(beanHistoryDataResponse.getData().getResult());

                        }

                        txtUsd.setText(beanHistoryDataResponse.getData().getTotalGold() + " " +
                                getString(R.string.pocket_USD));
                        adapter.notifyDataSetChanged();

                        nextIndex = beanHistoryDataResponse.getData().getNextId();
                        isEnd = beanHistoryDataResponse.getData().isEnd();

                    } else {
                        handleError(beanHistoryDataResponse.getMessage(), beanHistoryDataResponse.getCode());
                    }
                },error -> {
                    lvBeanHistory.onRefreshComplete();
                    dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                });
    }
}
