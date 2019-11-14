package com.appster.services;

import com.appster.models.NotificationPushModel;
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gaku on 1/2/18.
 */
public class BeLivePushManagerTest {

    public static final String TAG = BeLivePushManagerTest.class.getSimpleName();

    @Test
    public void getPushModelFromJson() throws Exception {
        String json = "{\"pushImageUrl\":\"\",\"message_type\":\"gcm\",\"notificationType\":10,\"message\":\"test2\",\"title\":\"test1\",\"userId\":93}";
        NotificationPushModel model = BeLivePushManager.getInstance().getPushModelFromJson(json);

        System.out.println("model=" + model);

        if (model != null) {
            Gson gson = new Gson();
            System.out.println("json=" + gson.toJson(model));
        }

        Assert.assertNotNull(model);
    }

}
