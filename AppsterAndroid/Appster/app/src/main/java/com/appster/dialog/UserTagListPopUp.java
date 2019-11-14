package com.appster.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.core.adapter.DisplayableItem;
import com.appster.customview.taggableedittext.UserTagAdapter;
import com.appster.customview.taggableedittext.UserTagViewHolder;
import com.apster.common.DiffCallBaseUtils;
import com.apster.common.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by linh on 19/06/2017.
 */

public class UserTagListPopUp extends PopupWindow {

    @Bind(R.id.rcv_taggable_user)
    RecyclerView rv;
    UserTagAdapter mAdapter;


    public static UserTagListPopUp newInstance(Context context, List<DisplayableItem> items, UserTagViewHolder.OnClickListener clickListener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_taggable_user_list, null);
        int width = Utils.getScreenWidth() - Utils.dpToPx(24);
        UserTagListPopUp popupWindow = new UserTagListPopUp(
                customView,
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT, items, clickListener
        );
        if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return popupWindow;
    }

    private UserTagListPopUp(View contentView, int width, int height, List<DisplayableItem> items, UserTagViewHolder.OnClickListener clickListener) {
        super(contentView, width, height);
        ButterKnife.bind(this, contentView);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setupRecyclerView(contentView.getContext(), items, clickListener);
        setBackgroundDrawable(new ColorDrawable(Color.BLUE));
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void unbind(){
        ButterKnife.unbind(this);
    }

    public void updateLocation(View anchor, int x, int y){
        update(anchor, x, y, -1, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void updateList(List<DisplayableItem> results, String query) {
        mAdapter.updateQuery(query);
        mAdapter.updateItems(results);
    }

    private void setupRecyclerView(Context context, List<DisplayableItem> items, UserTagViewHolder.OnClickListener clickListener) {
        mAdapter = new UserTagAdapter(new DiffCallBaseUtils(), items, clickListener);
        rv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        rv.setAdapter(mAdapter);
    }
}