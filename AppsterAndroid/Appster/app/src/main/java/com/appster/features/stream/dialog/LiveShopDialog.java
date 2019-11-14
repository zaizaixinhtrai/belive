package com.appster.features.stream.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appster.R;
import com.appster.customview.LiveCommerceItem;
import com.appster.dialog.ImmersiveDialogFragment;
import com.appster.models.ProductModel;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.stephentuso.welcome.WelcomeViewPagerIndicator;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import rx.Subscription;


/**
 * Created by linh on 24/08/2017.
 */

public class LiveShopDialog extends ImmersiveDialogFragment implements LiveCommerceItem.OnItemCallback, LiveShopDialogContract.View {
    private static final String ARG_USER_ID = "ARG_USER_ID";
    private static final String ARG_AUTHENTICATION = "ARG_AUTHENTICATION";
    private static final String ARG_SELLER_NAME = "ARG_SELLER_NAME";
    private static final String ARG_IS_SELLER = "ARG_IS_SELLER";
    private static final String ARG_ORDER_BUTTON_LABEL = "ARG_ORDER_BUTTON_LABEL";

    @Bind(R.id.pager)
    ViewPager mViewPager;
    @Bind(R.id.indicator)
    WelcomeViewPagerIndicator mIndicator;

    private String mSellerId;
    private String mSellerName;
    private boolean mIsSeller;
    private String mOrderButtonLabel;
    private String mAuth;
    private Subscription mSubscription;
    private List<ProductModel> mProducts;
    private int mCurrentSelectedPage;

    private LiveShopCallback mLiveShopCallback;

    DecimalFormat decimalFormat;
    LiveShopDialogContract.UserActions mPresenter;


    public static LiveShopDialog newInstance(String auth, String sellerId, String sellerName, boolean isSeller, String orderButtonLabel) {
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, sellerId);
        args.putString(ARG_AUTHENTICATION, auth);
        args.putString(ARG_SELLER_NAME, sellerName);
        args.putBoolean(ARG_IS_SELLER, isSeller);
        args.putString(ARG_ORDER_BUTTON_LABEL, orderButtonLabel);
        LiveShopDialog fragment = new LiveShopDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public LiveShopDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSellerId = bundle.getString(ARG_USER_ID);
            mAuth = bundle.getString(ARG_AUTHENTICATION);
            mSellerName = bundle.getString(ARG_SELLER_NAME);
            mIsSeller = bundle.getBoolean(ARG_IS_SELLER);
            mOrderButtonLabel = bundle.getString(ARG_ORDER_BUTTON_LABEL);
        }
        decimalFormat = new DecimalFormat("#.##");
        mPresenter = new LiveShopDialogPresenter(AppsterWebServices.get(), mAuth);
        mPresenter.attachView(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
        if (mProducts == null) {
            mPresenter.getProductDetail(mSellerId);
        }else {
            onGetProductDetailSuccessfully(mProducts);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //((ViewGroup)mBtnOrder.getParent()).setVisibility((mIsSeller) ? View.GONE : View.VISIBLE);//
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentSelectedPage = mViewPager.getCurrentItem();
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_live_shop;
    }

    @Override
    protected boolean isDimDialog() {
        return true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        RxUtils.unsubscribeIfNotNull(mSubscription);
        mPresenter.detachView();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) return;
        super.show(manager, tag);
    }

    //========= event handlers =====================================================================

    //========= mvp callback =======================================================================
    @Override
    public Context getViewContext() {
        return null;
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
    public void onGetProductDetailSuccessfully(@Nullable List<ProductModel> data) {
        if(!isFragmentUIActive()) return;
        mProducts = data;
        setupAndAttachAdapter(data);
    }

    @Override
    public void onGetProductDetailFailed(String message) {
        if(!isFragmentUIActive()) return;
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    //======== callbacks ===========================================================================
    @Override
    public void onCloseButtonClicked() {
        dismiss();
    }

    @Override
    public void onOrderButtonClicked() {
        if (mLiveShopCallback != null) mLiveShopCallback.onOrderButtonClicked();
    }

    //======== inner methods =======================================================================
    public void setLiveShopCallback(LiveShopCallback liveShopCallback) {
        mLiveShopCallback = liveShopCallback;
    }

    private void setupViewPager(){
        mViewPager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        mViewPager.setPadding(100, 0, 100, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mViewPager.setPageMargin(20);
    }

    private void setupAndAttachAdapter(List<ProductModel> products){
        if(products == null) return;
        //Disable clip to padding
        mViewPager.setAdapter(new LiveShopAdapter(getContext(), products, mOrderButtonLabel, this));
        mIndicator.setTotalPages(products.size());
        mIndicator.setShouldHideAtLastPage(false);
        mIndicator.setVisibility(View.VISIBLE);
        mViewPager.addOnPageChangeListener(mIndicator);
        mViewPager.setCurrentItem(mCurrentSelectedPage);
    }

    //======== inner classes =======================================================================
    private static class LiveShopAdapter extends PagerAdapter {
        private List<ProductModel> products;
        private LiveCommerceItem.OnItemCallback mOnItemCallback;
        private Context mContext;
        private String mOrderButtonLabel;

        public LiveShopAdapter(Context context, List<ProductModel> products, String orderButtonLabel, LiveCommerceItem.OnItemCallback callback) {
            this.mContext = context;
            this.products = products;
            this.mOrderButtonLabel = orderButtonLabel;
            mOnItemCallback = callback;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LiveCommerceItem item = new LiveCommerceItem(mContext, getItem(position), mOrderButtonLabel, mOnItemCallback);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private ProductModel getItem(int position){
            return products.get(position);
        }
    }

    public interface LiveShopCallback{
        void onOrderButtonClicked();
    }
}
