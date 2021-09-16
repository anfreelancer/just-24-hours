package com.luteapp.just24hoursplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;

public class BillingClientSetup {

    public static final String ITEM_BUY_INAPP_1 = "inapp1";
    public static final String  ITEM_BUY_INAPP_2 = "inapp2";
    public static final String  ITEM_BUY_INAPP_3 = "inapp3";

    public static final String ITEM_BUY_SUB_1 = "buyapp1";
    public static final String  ITEM_BUY_SUB_2 = "buyapp2";
    public static final String  ITEM_BUY_SUB_3 = "buyapp3";

    private static final String key = "Upgraded";
    private  static BillingClient instance;

    public static BillingClient getInstance(Context context, PurchasesUpdatedListener listener){
        return instance == null ? setupBillingClient(context, listener) : instance;
    }

    private static BillingClient setupBillingClient(Context context, PurchasesUpdatedListener listener){
        BillingClient billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(listener)
                .build();

        return billingClient;
    }

    public static boolean isUpgraded(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppUpgrade", Context.MODE_PRIVATE);
        boolean isUpgraded = sharedPreferences.getBoolean(key, false);
        if(!isUpgraded) {
            Toast.makeText(context, "Please Upgrade!", Toast.LENGTH_LONG).show();
        }
        return isUpgraded;
    }

    public static void upgradeSuccess(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppUpgrade", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, true);
        editor.apply();
    }
}
