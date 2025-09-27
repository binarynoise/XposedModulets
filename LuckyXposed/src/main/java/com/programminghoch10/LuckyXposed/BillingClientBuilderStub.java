package com.programminghoch10.LuckyXposed;

import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.UserChoiceBillingListener;

public class BillingClientBuilderStub {
    private static final String TAG = "Logger";
    
    Context context;
    BillingClientBuilderStub(Context context) {
        this.context = context;
    }
    
    public BillingClientBuilderStub enableAlternativeBillingOnly() {
        return this;
    }
    
    public BillingClientBuilderStub enableExternalOffer() {
        return this;
    }
    
    public BillingClientBuilderStub enablePendingPurchases() {
        return this;
    }
    
    public BillingClientBuilderStub enableUserChoiceBilling(UserChoiceBillingListener userChoiceBillingListener) {
        return this;
    }
    
    PurchasesUpdatedListener purchasesUpdatedListener;
    public BillingClientBuilderStub setListener(PurchasesUpdatedListener purchasesUpdatedListener) {
        this.purchasesUpdatedListener = purchasesUpdatedListener;
        return this;
    }
    
    public BillingClient build() {
        Log.d(TAG, "build: build billingclient");
        if (purchasesUpdatedListener == null) 
            throw new IllegalArgumentException("no purchasesUpdatedListener specified");
        return new BillingClientStub(context, purchasesUpdatedListener);
    }
}
