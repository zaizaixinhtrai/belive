package com.appster.utility;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ThanhBan on 9/28/2016.
 */

public class RxUtils {
    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public static void unsubscribeIfNotNull(CompositeSubscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.clear();
        }
    }


    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }
        return subscription;
    }
}
