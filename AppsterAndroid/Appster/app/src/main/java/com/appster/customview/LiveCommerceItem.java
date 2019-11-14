package com.appster.customview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appster.R;
import com.appster.models.ProductModel;
import com.appster.utility.CustomTabUtils;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.CopyTextUtils;
import com.pack.utility.StringUtil;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by linh on 17/10/2017.
 */

public class LiveCommerceItem extends RelativeLayout {
    @Bind(R.id.img_product_img)
    ImageView mImgProductImage;
    @Bind(R.id.txt_product_name)
    CustomFontTextView mTxtProductName;
    @Bind(R.id.txt_price)
    CustomFontTextView mTxtPrice;
    @Bind(R.id.txt_promo_price)
    CustomFontTextView mTxtPromoPrice;
    @Bind(R.id.txt_description)
    CustomFontTextView mTxtDescription;
    @Bind(R.id.txt_promo_code)
    CustomFontTextView mTxtPromoCode;
    @Bind(R.id.btn_order)
    CustomFontButton mBtnOrder;
    @Bind(R.id.ll_promo_code)
    LinearLayout llPromoCode;

    private ProductModel mProduct;
    private Activity mActivity;
    private OnItemCallback mOnItemCallback;
    private String mOrderButtonLabel;

    public LiveCommerceItem(Context context, ProductModel product, String orderButtonLabel, OnItemCallback callback) {
        super(context);
        this.mProduct = product;
        this.mOnItemCallback = callback;
        mActivity = (Activity) context;
        mOrderButtonLabel = orderButtonLabel;
        constructor();
    }

    public LiveCommerceItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructor();
    }

    public LiveCommerceItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveCommerceItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        constructor();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_close)
    public void onCloseButtonClicked() {
        if (mOnItemCallback != null) mOnItemCallback.onCloseButtonClicked();
    }

    // variable to track event time
    private long mLastClickTime = 0;

    @OnClick(R.id.btn_order)
    public void onOrderButtonClicked() {
        if (mProduct == null || mActivity == null) return;
        // Preventing multiple clicks, using threshold of 1,5 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        CustomTabUtils.openChromeTab((Activity) getContext(), mProduct.getUrl());
        mOnItemCallback.onOrderButtonClicked();
    }

    @OnLongClick(R.id.ll_promo_code)
    public boolean onPromoAreaClicked() {
        if (mProduct == null) return false;
        CopyTextUtils.CopyClipboard(getContext(), mProduct.getPromoCode(), getContext().getString(R.string.copied));
        return true;
    }

    private void constructor() {
        inflateViews();
    }

    private void inflateViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_live_commerce, this, true);
        ButterKnife.bind(this);
        mTxtPrice.setPaintFlags(mTxtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mTxtDescription.setMovementMethod(new ScrollingMovementMethod());
//        mTxtDescription.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (mTxtDescription == null || mTxtDescription.getHeight() <= 0) return;
//                mTxtDescription.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                Timber.e("** onGlobalLayout");
//                mTxtDescription.setHeight(mTxtDescription.getHeight());
//                mTxtDescription.requestLayout();
//            }
//        });
        bindView();
    }

    private void bindView() {
        if (!TextUtils.isEmpty(mOrderButtonLabel)) mBtnOrder.setText(mOrderButtonLabel);
        if (mProduct == null) return;
        ImageLoaderUtil.displayMediaImage(getContext(), mProduct.getImageUrl(), mImgProductImage);
        String price = "U.P $" + String.format(Locale.getDefault(), "%.02f", mProduct.getPrice());
        String promoPrice = "$" + String.format(Locale.getDefault(), "%.02f", mProduct.getPromotionPrice());
        mTxtProductName.setText(mProduct.getName());
        mTxtPrice.setText(price);
        mTxtPromoPrice.setText(promoPrice);
        if (StringUtil.isNullOrEmptyString(mProduct.getPromoCode())) {
            llPromoCode.setVisibility(View.GONE);
        } else {
            mTxtPromoCode.setText(mProduct.getPromoCode());
        }
        mTxtDescription.setText(mProduct.getDescription());
    }

    //===== inner classes ==========================================================================
    public interface OnItemCallback {
        void onCloseButtonClicked();

        void onOrderButtonClicked();
    }
}
