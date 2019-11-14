package com.appster.iap_manager;

import android.app.Activity;
import android.content.Intent;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.refill.RefillListItem;
import com.appster.tracking.EventTracker;
import com.appster.utility.RxUtils;
import com.appster.utility.iab.IabHelper;
import com.appster.utility.iab.IabResult;
import com.appster.utility.iab.Inventory;
import com.appster.utility.iab.Purchase;
import com.appster.utility.iab.SkuDetails;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.VerifyIAPRequestModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.FileUtility;
import com.apster.common.LogUtils;
import com.pack.utility.DialogInfoUtility;
import com.playtoken.BeLivePlaytoken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

//import com.gtoken.common.net.prefs.ApiPreferences;

/**
 * Created by User on 11/27/2015.
 */
public class InAppBillHelper {
    private static final String TAG = InAppBillHelper.class.getSimpleName();

    public static interface InitListener {
        public void onDone();
    }

    public static interface BillListener {
        /**
         * Notification that a change in billing has occurred (products updated,
         * purchase made, or purchase consumed).
         */
        public void onChange();
    }

    public static interface BillTotalBeanListener {
        /**
         * Notification that a change in billing has occurred (products updated,
         * purchase made, or purchase consumed).
         */
        public void onTotalBeanIncreate(Long totalBean);
    }

    private static boolean isInitializing = false;

    // the singleton object
    private static InAppBillHelper singleton = null;

    private List<InitListener> initListeners = new ArrayList<>();

    // instance data
    private Set<BillListener> listeners = new HashSet<BillListener>();

    private List<String> skus = Collections.emptyList();
    private IabHelper iab = null;
    private Map<String, SkuDetails> products = null;
    private Map<String, Purchase> purchases = null;
    private Map<String, RefillListItem> refillItems = null;

    private BillTotalBeanListener listenerPurchasedSuccessBean;

    private static DialogInfoUtility utility = new DialogInfoUtility();
    private static String orderId;
    private static RefillListItem lastClickedItem;
    private static boolean purchaseFulfilled = true;
    private static CountDownLatch countdown;
    private static Activity activity;
    private static int tries;
//    private Integer registeredId = null;

    static CompositeSubscription mCompositeSubscription;

    private InAppBillHelper() {
        mCompositeSubscription = new CompositeSubscription();
    }

    /**
     * @param parent The 'owner' of the helper. Intended to be the top-level
     *               activity, that will be around for the life of the application.
     */
    public static synchronized void init(final Activity parent) {
        if (singleton != null) {
            LogUtils.logW(TAG, "In-app billing helper has already been initialized");
            return;
        }

        isInitializing = true;
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        singleton = new InAppBillHelper();
        singleton.iab = new IabHelper(parent, BuildConfig.IAP_KEY64BIT);

        activity = parent;

        // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
        // build list of skus from data to query get all purchased item id from server
//        singleton.skus = getAllStoreItemIDFromServer();

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        singleton.iab.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    LogUtils.logW(TAG, "Problem setting up in-app billing: " + result);
                    singleton.iab = null;
                } else {
                    //queryInventory();
                }

                synchronized (singleton.initListeners) {
                    isInitializing = false;
                    for (InitListener listener : singleton.initListeners) {
                        listener.onDone();
                    }
                    singleton.initListeners.clear();
                }
            }
        });

    }

    public static void addInitListener(InitListener listener) {
        synchronized (singleton.initListeners) {
            if (isInitializing) {
                singleton.initListeners.add(listener);
            } else {
                listener.onDone();
            }
        }
    }

    public static void destroy() {
        if (singleton != null && singleton.iab != null) {
            singleton.iab.dispose();
            singleton = null;
        } else if (singleton != null) {
            singleton = null;
        }
//        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
//        mCompositeSubscription = null;
        activity = null;
    }

    /**
     * Register with the singleton to receive notifications of when changes
     * occur
     */
    public static void register(BillListener listen) {
        if (singleton != null)
            singleton.listeners.add(listen);
    }

    public static void registerSuccessPurchasedBean(BillTotalBeanListener mBeanListener) {
        if (singleton != null)
            singleton.listenerPurchasedSuccessBean = mBeanListener;
    }

    public static void deRegisterSuccessPurchasedBean() {
        if (singleton != null)
            singleton.listenerPurchasedSuccessBean = null;
    }

    public static void deregister(BillListener listen) {
        if (singleton != null)
            singleton.listeners.remove(listen);
    }

    /**
     * @return Sku values passed from STORE data via server.
     */
    public static List<String> getSkus() {
        if (singleton != null)
            return singleton.skus;
        return Collections.emptyList();
    }

    public static void setListSku(List<RefillListItem> arrayList) {
        if (singleton != null) {
            if (arrayList != null && arrayList.size() > 0) {
                singleton.refillItems = new HashMap<>();

                ArrayList<String> arrayItemIAP = new ArrayList<String>();
                for (RefillListItem item : arrayList) {
                    arrayItemIAP.add(item.getIos_store_id());
                    singleton.refillItems.put(item.getIos_store_id(), item);
                }

                singleton.skus = arrayItemIAP;
                queryInventory();
            }
        }
    }

    /**
     * Find a specific sku info fetched from Google Play.
     */
    public static SkuDetails findProduct(String sku) {
        if (singleton != null && singleton.products != null)
            return singleton.products.get(sku);
        return null;
    }

    /**
     * Find the purchase of a specific sku for the default user, fetched from
     * Google Play. Don't understand how the base user is determined.
     */
    public static Purchase findPurchase(String sku) {
        if (singleton != null && singleton.purchases != null)
            return singleton.purchases.get(sku);
        return null;
    }

    /**
     * Allow code to manually re-invoke inventory query. This is mainly
     * available so that purchases can be re-queried and those not yet consumed
     * can be consumed, in the event there was an error initially.
     */
    public static void queryInventory() {
        if (singleton != null && singleton.iab != null) {
            addInitListener(() -> {
                try {
                    if (singleton.iab.isSetupDone() && !singleton.iab.isAsyncInProgress()) {
                        singleton.iab.queryInventoryAsync(true, singleton.skus, new QueryListen());
                    }
                } catch (Exception e) {
                    LogUtils.logE(TAG, "Failed to call queryInventory. error=" + e.getMessage());
                }
            });
        }
    }


    // not sure what I'd need with more complicated req code at this time.
    private static final int REQUEST_CODE = 47;

    /**
     * @param activity IMPORTANT!!! If this activity does not override
     *                 onActivityResult, the purchaseFinishListener will not be
     *                 called.
     * @param sku
     */
    public static void purchase(Activity activity, String sku) throws Exception {
        if (singleton != null) {
//            sku ="android.test.purchased";
            singleton.iab.launchPurchaseFlow(activity, sku, REQUEST_CODE, new PurchaseListen());
        }
    }

    /**
     * Any activity passed to purchase must override onActivityResult and call
     * this method in order for the purchase listener to work correctly. Ugly,
     * but there it is.
     */
    public static boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (singleton != null)
            return singleton.iab.handleActivityResult(requestCode, resultCode, data);
        return false;
    }

    /**
     * Notify listeners of change
     */
    private static void notifyListeners() {
        if (singleton != null) {
            for (BillListener listen : singleton.listeners) {
                listen.onChange();
            }
        }
    }

    /**
     * Not sure this will work with onActivityResult implement on associated
     * Activity.
     */
    private static class PurchaseListen implements IabHelper.OnIabPurchaseFinishedListener {


        PurchaseListen() {
        }

        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (singleton != null && purchase != null) { // valid purchase to process
                try {
                    FileUtility.writeToSD(purchase.toString(), "PurchasedModel");
//                    singleton.inAppBillNetwork.verifyPurchase(
//                            asess,
//                            purchase.getDeveloperPayload(),
//                            purchase.getOriginalJson(),
//                            purchase.getSignature());
                } catch (Exception e) {
//                    Ultil.LogE(TAG, "Failed to verify purchase: " + purchase + " - " + e.getMessage());
                    return;
                }

                // initiate consumption of the item (assuming all items are consumable)
                singleton.iab.consumeAsync(purchase, new ConsumeListen());
            }else{
                DialogManager.getInstance().dismisDialog();
            }
        }
    }

    ;

    /**
     * Listener gets requested (all) app skus and sets them in the local
     * variable: inAppItems. It also checks for any unconsumed purchased owned
     * by the user. If any are found, they are immediately consumed
     * asynchronously.
     */
    private static class QueryListen implements IabHelper.QueryInventoryFinishedListener {


        QueryListen() {
        }

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (singleton == null)
                return;

            if (result.isFailure()) {
                LogUtils.logW(TAG, "Failed to query inventory on in-app billing init: " + result);
                return;
            }

            Map<String, SkuDetails> mapSkus = new HashMap<String, SkuDetails>();
            Map<String, Purchase> mapPurchases = new HashMap<String, Purchase>();
            for (String sku : singleton.skus) {
                SkuDetails item = inventory.getSkuDetails(sku);
                if (item != null) {
                    mapSkus.put(sku, item);
                }

                Purchase purch = inventory.getPurchase(sku);
                if (purch != null) {
                    mapPurchases.put(sku, purch);
                    singleton.iab.consumeAsync(purch, new ConsumeListen());
                }
            }

            singleton.products = mapSkus;
            singleton.purchases = mapPurchases;

            notifyListeners();
        }
    }

    ;

    /**
     * Update when consume is completed
     */
    private static class ConsumeListen implements IabHelper.OnConsumeFinishedListener {


        ConsumeListen() {
        }

        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (singleton == null)
                return;

            if (!result.isSuccess()) {
                LogUtils.logW(TAG, "Error while consuming product: " + result);
                return;
            }

            try {

//                // notify server that purchase has been consumed, update
//                // relevant data on server.
                verifyPurchasedWithServer(purchase.getDeveloperPayload(), purchase.getSignature(), purchase.getOriginalJson(), purchase.getSku());
//                singleton.inAppBillNetwork.consumePurchase(
//                        asess,
//                        purchase.getDeveloperPayload(),
//                        purchase.getOriginalJson(),
//                        purchase.getSignature());
            } catch (Exception e) {
                LogUtils.logE(TAG, "Failed to consume purchase, order.id: "
                        + purchase.getOrderId() + ", sku: "
                        + purchase.getSku() + ", key: "
                        + purchase.getDeveloperPayload()
                        + ". Exception msg: " + e.getMessage());
                return;
            }

            // redraw to reflect updated server RP value
            notifyListeners();
        }
    }

    ;

    public static List<String> getAllStoreItemIDFromServer() {
        List<String> list = new ArrayList<String>();

        return list;
    }

    public static void verifyPurchasedWithServer(String payLoad, String jsonSignature, String receptionData, String productID) {
        if(!DialogManager.isShowing()) {
            DialogManager.getInstance().showDialog(activity, activity.getResources().getString(R.string.connecting_msg));
        }
        VerifyIAPRequestModel requestModel = new VerifyIAPRequestModel();
        requestModel.setStore_id(productID);
        requestModel.setDevice_type(Constants.ANDROID_DEVICE_TYPE);
        requestModel.setPayload(receptionData);
        requestModel.setSignature(jsonSignature);
//        FileUtility.writeToSD(requestModel.toString()+"", "VerifyIAPRequestModel");
//        FileUtility.writeToSD(AppsterApplication.mAppPreferences.getUserTokenRequest()+"", "TokenReques");
        mCompositeSubscription.add(AppsterWebServices.get().verifyIAPPurchased(AppsterApplication.mAppPreferences.getUserTokenRequest(),
                requestModel)
                .subscribe(verifyIAPResponeModel -> {
                    DialogManager.getInstance().dismisDialog();
                    if (verifyIAPResponeModel == null) return;
//                        FileUtility.writeToSD(verifyIAPResponeModel.toString(), "VerifyIAPDataRespone");
//                        FileUtility.writeToSD(verifyIAPResponeModel.getData().getTotalBeanIncrease()+" Code " +verifyIAPResponeModel.getCode(), "Codehaha");
                    if (verifyIAPResponeModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        Timber.e("Bean =" + verifyIAPResponeModel.getData().getTotalBeanIncrease());
                        AppsterApplication.mAppPreferences.getUserModel().setTotalBean(verifyIAPResponeModel.getData().getTotalBeanIncrease());
                        String transactionId = verifyIAPResponeModel.getData().getTransactionId();
                        long purchaseTime = verifyIAPResponeModel.getData().getPurchaseTime();
                        RefillListItem refilItem = singleton.refillItems.get(productID);

                        // PlayToken Transaction
                        SkuDetails product = singleton.products.get(productID);
                        RefillListItem refillItem = singleton.refillItems.get(productID);
                        BeLivePlaytoken.noticePurchaseTransaction(productID, transactionId, purchaseTime, refillItem, product, singleton.iab.getmContext());

                        // Amplitude
                        EventTracker.trackRevenue(productID, refilItem.getPrice_usd());

                        if (singleton != null) {
//                                FileUtility.writeToSD(verifyIAPResponeModel.getData().getTotalBeanIncrease()+"", "singleton != null");
                            if (singleton.listenerPurchasedSuccessBean != null) {
//                                    FileUtility.writeToSD(verifyIAPResponeModel.getData().getTotalBeanIncrease() + "", "singleton.listenerPurchasedSuccessBean!=null");
                                singleton.listenerPurchasedSuccessBean.onTotalBeanIncreate(verifyIAPResponeModel.getData().getTotalBeanIncrease());

                            }
                        }
                    }
                }, error -> {
                    DialogManager.getInstance().dismisDialog();
                    Timber.e(error.getMessage());
                }));

    }

}
