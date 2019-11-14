package com.appster.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.appster.R;
import com.appster.customview.CircleImageView;
import com.appster.utility.ImageLoaderUtil;
import com.pack.utility.StringUtil;

import com.stickyheaders.SectioningAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sonnguyen on 11/15/16.
 */

public class WallFeedStickHeaderViewHolder extends SectioningAdapter.HeaderViewHolder{
    @Bind(R.id.userimage)
    public CircleImageView imgUserImange;
    @Bind(R.id.txt_dislayname)
    public  TextView tvDisplayName;
    @Bind(R.id.address)
    TextView tvAddress;
    @Bind(R.id.date_time_text)
    TextView tvDateTime;

    public WallFeedStickHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
    public void onBindData(Context mContext,String urlAvata, String displayName, String address, String dateTime){
        ImageLoaderUtil.displayUserImage(mContext, urlAvata,
                imgUserImange);
        tvDisplayName.setText(displayName);
        if(StringUtil.isNullOrEmptyString(address)){
            tvAddress.setVisibility(View.GONE);
        }else {
            tvAddress.setVisibility(View.VISIBLE);
            tvAddress.setText(address);
        }
        tvDateTime.setText(dateTime);



    }
}
