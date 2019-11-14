package com.appster.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appster.interfaces.GiftStoreListener;
import com.appster.models.GiftReceiverModel;
import com.appster.R;
import com.appster.utility.ImageLoaderUtil;

import butterknife.Bind;

/**
 * Created by User on 11/4/2015.
 */
public class GiftStoreHolder extends BaseViewHolderImageLoader<GiftReceiverModel, GiftStoreListener> {

    @Bind(R.id.imv_gift)
    ImageView imvGift;
    @Bind(R.id.txt_count)
    TextView txtCount;

    GiftReceiverModel model;
    Context context;

    public GiftStoreHolder(View view, Context context) {
        super(view, context);
        this.context = context;
    }

    @Override
    public void init(Context context, GiftStoreListener listener, GiftReceiverModel model) {
        super.init(context, listener, model);
        this.model = model;
        ImageLoaderUtil.displayMediaImage(context, model.getGift_image(), imvGift);
        txtCount.setText("x" + " " + model.getGift_count());
    }

    @Override
    public void init(GiftReceiverModel model) {
        super.init(model);
        this.model = model;
//        ImageLoader.getInstance().displayUserImage(model.getGift_image(), imvGift, options, this);
        txtCount.setText("x" + " " + model.getGift_count());

        // Set user image
        ImageLoaderUtil.displayMediaImage(context, model.getGift_image(), imvGift);
    }

    @Override
    public GiftReceiverModel getModel() {
        return super.getModel();
    }

    @Override
    protected void setOnclickListener() {
        super.setOnclickListener();
    }

    @Override
    protected void setHolderListener(GiftStoreListener listener) {
        super.setHolderListener(listener);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }
}