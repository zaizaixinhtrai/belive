package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by sonnguyen on 1/9/17.
 */

public class StreamDefaultImageRequest extends TypeOutputModel  {
    @SerializedName("Image") @Expose
    private File mImage;
    @SerializedName("Slug") @Expose
    private String mSlug;

    public File getImage() {
        return mImage;
    }

    public  StreamDefaultImageRequest(String slug, File image){
        setSlug(slug);
        setImage(image);
        handleAddMultiPar();
    }

    public void setImage(File image) {
        this.mImage = image;
    }

    private void handleAddPartData() {
        if (mImage != null) {
            mTypeOutput.addFormDataPart("Image","image_upload.png", RequestBody.create(okhttp3.MediaType.parse("image/png"),getImage()));
//            TypedFile fileUpload = new TypedFile("image/png", getImage());
//            mTypeOutput.addPart("Image", fileUpload);
        }
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    private void handleAddMultiPar() {
        addPartNotEmptyString("Slug", getSlug());
        handleAddPartData();
    }

}
