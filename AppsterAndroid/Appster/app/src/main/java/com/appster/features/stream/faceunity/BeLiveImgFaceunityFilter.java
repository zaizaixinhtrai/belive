package com.appster.features.stream.faceunity;

import android.content.Context;

import com.ksyun.media.diversity.faceunity.kit.ImgFaceunityFilter;
import com.ksyun.media.streamer.util.gles.GLRender;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by thanhbc on 7/5/17.
 */

public class BeLiveImgFaceunityFilter extends ImgFaceunityFilter {

    public BeLiveImgFaceunityFilter(Context context, GLRender glRender) {
        super(context, glRender);
    }

    @Override
    protected InputStream openFaceunityFile(String currentPropPath) throws IOException {
        return new FileInputStream(currentPropPath);
    }

    public void setFilePath(String filePath){
        mPropPath = filePath;
    }
}
