package com.appster.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.appster.R;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Utils;

import uk.co.senab.photoview.OnPhotoTapListener;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ViewImageActivity extends Activity implements OnClickListener {
    public static String key_image_link = "Image_Link";
    public static String key_show_menu = "image_show_menu";
    public static String key_image_bitmap = "Image_Bitmap";

    Activity mActivity;
    private boolean ishowMenuSave = false;

    ImageView btnBack;
    PhotoView imgView;
    String imageLink;
    private PhotoViewAttacher mAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity = this;
        setupView();
    }

    private void setupView() {
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        imgView = (PhotoView) findViewById(R.id.imgView);
        ishowMenuSave = getIntent().getBooleanExtra(key_show_menu, false);
        imageLink = getIntent().getStringExtra(key_image_link);
        if (!TextUtils.isEmpty(imageLink)) {
            imgView.setTag(imageLink);
            final int screenSize = Utils.getScreenWidth();
            ImageLoaderUtil.displayUserImage(this, imageLink, imgView, false, screenSize, screenSize, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {

                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    if(mAttacher==null && imgView!=null) mAttacher = new PhotoViewAttacher(imgView);
                }
            });

            Bitmap bitmap = getIntent().getParcelableExtra(key_image_bitmap);
            if (bitmap != null) {
                imgView.setImageBitmap(bitmap);
            }
        }else {
            // The MAGIC happens here!
            mAttacher = new PhotoViewAttacher(imgView);
        }
//        mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
//        mAttacher.setZoomable(false);
        // Lets attach some listeners, not required though!
//        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mAttacher != null) {
//            mAttacher.clear();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            default:
                break;
        }
    }


    private class PhotoTapListener implements OnPhotoTapListener {
        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            onClick(view);
        }
    }
}
