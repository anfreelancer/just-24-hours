package com.luteapp.just24hoursplus;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.Arrays;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private BillingClient mBillingClient;
    private ConsumeResponseListener mListener;
    private static final String SUB1 = BillingClientSetup.ITEM_BUY_SUB_1;
    private static final String SUB2 = BillingClientSetup.ITEM_BUY_SUB_2;
    private static final String SUB3 = BillingClientSetup.ITEM_BUY_SUB_3;

    private static final String INAPP1 = BillingClientSetup.ITEM_BUY_INAPP_1;
    private static final String INAPP2 = BillingClientSetup.ITEM_BUY_INAPP_2;
    private static final String INAPP3 = BillingClientSetup.ITEM_BUY_INAPP_3;
    private SkuDetailsParams mSkuDetailsParamsSubs;
    private SkuDetailsParams mSkuDetailsParamsInApp;

    private boolean mIsSubCLick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        setupBillingClient();
        mSkuDetailsParamsSubs = SkuDetailsParams.newBuilder().setSkusList(Arrays.asList(SUB1, SUB2, SUB3))
                .setType(BillingClient.SkuType.SUBS)
                .build();
        mSkuDetailsParamsInApp = SkuDetailsParams.newBuilder().setSkusList(Arrays.asList(INAPP1, INAPP2, INAPP3))
                .setType(BillingClient.SkuType.INAPP)
                .build();
        handleEvents();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            BillingClientSetup.upgradeSuccess(this);
            if(mIsSubCLick) {
                handlePurchase(list);
            } else {
                clearPurchase(list);
            }
        }
    }

    private void handleEvents() {
        findViewById(R.id.btnSub1).setOnClickListener(view -> launchBillingSub(SUB1));
        findViewById(R.id.btnSub2).setOnClickListener(view -> launchBillingSub(SUB2));
        findViewById(R.id.btnSub3).setOnClickListener(view -> launchBillingSub(SUB3));
        findViewById(R.id.btnInApp1).setOnClickListener(view -> launchBillingInApp(INAPP1));
        findViewById(R.id.btnInApp2).setOnClickListener(view -> launchBillingInApp(INAPP2));
        findViewById(R.id.btnInApp3).setOnClickListener(view -> launchBillingInApp(INAPP3));
        findViewById(R.id.btnBack).setOnClickListener(view -> onBackPressed());
    }

    private void launchBillingSub(String sku) {
        if (mBillingClient.isReady()) {
            mIsSubCLick = true;
            mBillingClient.querySkuDetailsAsync(
                    mSkuDetailsParamsSubs,
                    (billingResult, list) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (SkuDetails skuDetail : list) {
                                if (skuDetail.getSku().equals(sku)) {
                                    handleBilling(skuDetail);
                                }
                            }
                        }
                    });
        }
    }

    private void launchBillingInApp(String sku) {
        if (mBillingClient.isReady()) {
            mIsSubCLick = false;
            mBillingClient.querySkuDetailsAsync(
                    mSkuDetailsParamsInApp,
                    (billingResult, list) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (SkuDetails skuDetail : list) {
                                if (skuDetail.getSku().equals(sku)) {
                                    handleBilling(skuDetail);
                                }
                            }
                        }
                    });
        }
    }

    private void handleBilling(SkuDetails sku) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(sku)
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams).getResponseCode();
    }

    private void setupBillingClient() {
        mListener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // Consume OK!
            }
        };
        mBillingClient = BillingClientSetup.getInstance(this, this);
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                // none
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Connect Success!
                    List<Purchase> purchases = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                    handleArlreadyPurchase(purchases);
                } else {
                    // Connect Fail
                }
            }
        });
    }

    private void handleArlreadyPurchase(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            if (purchase.getSkus().indexOf(SUB1) > 0 || purchase.getSkus().indexOf(SUB2) > 0 || purchase.getSkus().indexOf(
                    SUB3
            ) > 0
            ) {
                ConsumeParams consumParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                mBillingClient.consumeAsync(consumParams, mListener);
            }
        }
    }

    private void showNotice(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }


    private void handlePurchase(List<Purchase> p1) {
        for (Purchase purchase : p1) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                        @Override
                        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                showNotice("Subscribed Successfully");
                            }
                        }
                    });
                }
            }
        }
    }

    private void clearPurchase(List<Purchase> list) {
        Purchase purchase = list.get(0);
        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
            }
        });
    }
}
