package com.appster.services;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by ThanhBan on 1/10/2017.
 */

public class BeLiveTwitterApiClient extends TwitterApiClient {
    public BeLiveTwitterApiClient(TwitterSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public BeLiveTwitterApi getCustomService() {
        return getService(BeLiveTwitterApi.class);
    }

   public interface BeLiveTwitterApi {
        @FormUrlEncoded
        @POST("/1.1/statuses/update.json")
        Call<Tweet> postTweet(@Field("status") String shareString);
    }
}
