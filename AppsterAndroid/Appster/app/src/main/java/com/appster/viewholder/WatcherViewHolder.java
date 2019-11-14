package com.appster.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.BuildConfig;
import com.appster.R;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;
import com.pack.utility.StringUtil;

/**
 * Created by sonnguyen on 9/11/16.
 */
public class WatcherViewHolder extends RecyclerView.ViewHolder {
    private  ImageView viewersPicture;
    public WatcherViewHolder(View view){
        super(view);
        viewersPicture = (ImageView) view.findViewById(R.id.viewersPicture);
    }
    public void bindViewHolder(String userName){
        String s3ServerLink = Constants.AWS_S3_SERVER_LINK;
        if (StringUtil.isNullOrEmptyString(Constants.AWS_S3_SERVER_LINK)) {
            s3ServerLink  = BuildConfig.AWS_S3_SERVER_LINK+"profile_image";
        }
        String imageUrl = s3ServerLink + "/" + userName + ".jpg";
        ImageLoaderUtil.displayUserImage(itemView.getContext().getApplicationContext(), imageUrl,
                viewersPicture);
    }


}
