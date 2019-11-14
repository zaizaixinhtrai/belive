package com.appster.viewholder;

import android.content.Context;
import android.view.View;


import butterknife.ButterKnife;

/**
 * Created by thuc on 11/10/2015.
 */

public class BaseViewHolderImageLoader<T1, T2> implements View.OnClickListener {
    public T1 model;
    public T2 listener;

    public BaseViewHolderImageLoader(View view, T1 model, Context context) {
        this.model = model;
        ButterKnife.bind(this, view);
    }

    public BaseViewHolderImageLoader(View view, Context context) {
        ButterKnife.bind(this, view);

    }


    public void init(Context context, T2 listener) {
        setHolderListener(listener);


    }

    public void init(Context context, T2 listener, T1 model) {
        this.model = model;
        setHolderListener(listener);


    }

    public void init(Context context, T2 listener, T1 model, int position) {
        this.model = model;
        setHolderListener(listener);


    }

    public void init(T1 model) {
        setHolderListener(listener);

    }

    public T1 getModel() {
        return model;
    }


    protected void setOnclickListener() {

    }

    protected void setHolderListener(T2 listener) {
        this.listener = listener;

    }

    @Override
    public void onClick(View view) {

    }
}
