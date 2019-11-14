package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by User on 9/15/2015.
 */
public class ChatPostImageResquestModel extends TypeOutputModel {
    @SerializedName("Image") @Expose
    private File mImage;

    public ChatPostImageResquestModel(File image) {
        this.mImage = image;
        handleAddPartData();
    }

    public File getImage() {
        return mImage;
    }

    public void setImage(File image) {
        this.mImage = image;
    }

    private void handleAddPartData() {
        if (mImage != null) {
//            TypedFile fileUpload = new TypedFile("image/png", getImage());
            mTypeOutput.addFormDataPart("Image","image_upload.png", RequestBody.create(okhttp3.MediaType.parse("image/png"),getImage()));
//            mTypeOutput.addPart("mImage", fileUpload);
        }
    }
}
