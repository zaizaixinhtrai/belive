package com.playtoken;

import android.content.Context;

import com.appster.refill.RefillListItem;
import com.appster.utility.iab.SkuDetails;
import com.apster.common.LogUtils;
//import com.gtoken.common.Playground;
//import com.gtoken.common.net.model.Transaction;

import static com.appster.AppsterApplication.mAppPreferences;
import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by gaku on 5/19/17.
 */

public class BeLivePlaytoken {

    public static void noticePurchaseTransaction(String itemId, String transactionId, long purchaseTime, RefillListItem refillItem, SkuDetails product, Context context) {
        try {
            String playtokenUsername = mAppPreferences.getPlayTokenUserName();
            if (playtokenUsername == null) {
                return;
            }

            double price = 0;
            try {
//                String priceStr = "";
//                priceStr = product.getPrice().replaceAll("[^0-9/.]", "");
//                price = Double.valueOf(priceStr);

                // only use USD now
                price = Double.valueOf(refillItem.getPrice_usd());

            } catch (Exception e) {
                //LogUtils.logE(TAG, "Failed to convert price to double. price=" + product.getPrice(), e);
                LogUtils.logE(TAG, "Failed to convert price to double. price=" + refillItem.getPrice_usd(), e);
            }

//            Transaction obj = new Transaction();
//            obj.setTransactionId(transactionId);
//            obj.setPurchaseDate(purchaseTime);
//            obj.setPlatform("android");
//            obj.setItemName(product.getDescription());
//            obj.setCurrency("USD");
//            obj.setItemId(product.getSku());
//            obj.setItemPrice(price);

//            Playground.getNotificationRepository().notifyTransaction(playtokenUsername, obj)
//                    .compose(AppsterApplication.get(context).applySchedulers())
//                    .subscribe(baseResponse -> {
//                    }, throwable -> {
//                        LogUtils.logE(TAG, throwable.getMessage(), throwable);
//                    });

        } catch (Exception e) {
            LogUtils.logE(TAG, e.getMessage(), e);
        }
    }

    public static void noticeIAppPayPurchaseTransaction(String transactionId, long purchaseTime, RefillListItem refillItem, Context context) {
        try {
            String playtokenUsername = mAppPreferences.getPlayTokenUserName();
            if (playtokenUsername == null) {
                return;
            }

            double price = 0;
            try {
                price = Double.valueOf(refillItem.getPrice_cny());

            } catch (Exception e) {
                LogUtils.logE(TAG, "Failed to convert price to double. price=" + refillItem.getPrice_cny(), e);
            }

//            Transaction obj = new Transaction();
//            obj.setTransactionId(transactionId);
//            obj.setPurchaseDate(purchaseTime);
//            obj.setPlatform("android");
//            obj.setItemName(refillItem.getName());
//            obj.setCurrency("CNY");
//            obj.setItemId(refillItem.getAndroid_store_id());
//            obj.setItemPrice(price);

//            Playground.getNotificationRepository().notifyTransaction(playtokenUsername, obj)
//                    .compose(AppsterApplication.get(context).applySchedulers())
//                    .subscribe(baseResponse -> {
//                    }, throwable -> {
//                        LogUtils.logE(TAG, throwable.getMessage(), throwable);
//                    });

        } catch (Exception e) {
            LogUtils.logE(TAG, e.getMessage(), e);
        }
    }
}
