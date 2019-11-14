package com.appster.webservice.request_models;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by ThanhBan on 10/6/2016.
 */

public class StreamChatHistoryRequestModel  extends TypeOutputModel {

    public StreamChatHistoryRequestModel(String slug, File historyChatFile ) {
        mTypeOutput.addFormDataPart("Slug",slug);
        mTypeOutput.addFormDataPart("HistoryChatFile","historychat.txt", RequestBody.create(okhttp3.MediaType.parse("text/plain"),historyChatFile));
//        mTypeOutput.addPart("Slug", new TypedString(slug));
//        mTypeOutput.addPart("HistoryChatFile", new TypedFile("text/plain", historyChatFile));
    }


}
