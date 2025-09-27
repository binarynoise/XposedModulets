package com.programminghoch10.LuckyXposed;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.AlternativeBillingOnlyAvailabilityListener;
import com.android.billingclient.api.AlternativeBillingOnlyInformationDialogListener;
import com.android.billingclient.api.AlternativeBillingOnlyReportingDetailsListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingConfigResponseListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ExternalOfferAvailabilityListener;
import com.android.billingclient.api.ExternalOfferInformationDialogListener;
import com.android.billingclient.api.ExternalOfferReportingDetailsListener;
import com.android.billingclient.api.GetBillingConfigParams;
import com.android.billingclient.api.InAppMessageParams;
import com.android.billingclient.api.InAppMessageResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

public class BillingClientStub extends BillingClient {
    private static final BillingResult BillingResultOK = 
        BillingResult
            .newBuilder()
            .setResponseCode(BillingResponseCode.OK)
            .setDebugMessage("BillingClientStub BillingResultOK")
            .build();
    private static final String TAG = "Logger";
    private final Context context;
    private final PurchasesUpdatedListener purchasesUpdatedListener;
    
    BillingClientStub(Context context, PurchasesUpdatedListener purchasesUpdatedListener) {
        super();
        this.context = context;
        this.purchasesUpdatedListener = purchasesUpdatedListener;
        Log.d(TAG, "BillingClientStub: construct stub");
    }
    
    @Override
    public int getConnectionState() {
        Log.d(TAG, "getConnectionState: ");
        return ConnectionState.CONNECTED;
    }
    
    @NonNull
    @Override
    public BillingResult isFeatureSupported(@NonNull String s) {
        Log.d(TAG, "isFeatureSupported: ");
        return BillingResultOK;
    }
    
    @NonNull
    @Override
    public BillingResult launchBillingFlow(@NonNull Activity activity, @NonNull BillingFlowParams billingFlowParams) {
        Log.d(TAG, "launchBillingFlow: ");
        return BillingResultOK;
    }
    
    @NonNull
    @Override
    public BillingResult showAlternativeBillingOnlyInformationDialog(
        @NonNull Activity activity,
        @NonNull AlternativeBillingOnlyInformationDialogListener alternativeBillingOnlyInformationDialogListener
    ) {
        throw new IllegalStateException("not implemented");
    }
    
    @NonNull
    @Override
    public BillingResult showExternalOfferInformationDialog(
        @NonNull Activity activity,
        @NonNull ExternalOfferInformationDialogListener externalOfferInformationDialogListener
    ) {
        throw new IllegalStateException("not implemented");
    }
    
    @NonNull
    @Override
    public BillingResult showInAppMessages(
        @NonNull Activity activity,
        @NonNull InAppMessageParams inAppMessageParams,
        @NonNull InAppMessageResponseListener inAppMessageResponseListener
    ) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void acknowledgePurchase(
        @NonNull AcknowledgePurchaseParams acknowledgePurchaseParams,
        @NonNull AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener
    ) {
        Log.d(TAG, "acknowledgePurchase: ");
        acknowledgePurchaseResponseListener.onAcknowledgePurchaseResponse(BillingResultOK);
    }
    
    @Override
    public void consumeAsync(@NonNull ConsumeParams consumeParams, @NonNull ConsumeResponseListener consumeResponseListener) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void createAlternativeBillingOnlyReportingDetailsAsync(@NonNull AlternativeBillingOnlyReportingDetailsListener alternativeBillingOnlyReportingDetailsListener) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void createExternalOfferReportingDetailsAsync(@NonNull ExternalOfferReportingDetailsListener externalOfferReportingDetailsListener) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void endConnection() {
        Log.d(TAG, "endConnection: ");
        this.isReady = false;
    }
    
    @Override
    public void getBillingConfigAsync(
        @NonNull GetBillingConfigParams getBillingConfigParams,
        @NonNull BillingConfigResponseListener billingConfigResponseListener
    ) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void isAlternativeBillingOnlyAvailableAsync(@NonNull AlternativeBillingOnlyAvailabilityListener alternativeBillingOnlyAvailabilityListener) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void isExternalOfferAvailableAsync(@NonNull ExternalOfferAvailabilityListener externalOfferAvailabilityListener) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void queryProductDetailsAsync(
        @NonNull QueryProductDetailsParams queryProductDetailsParams,
        @NonNull ProductDetailsResponseListener productDetailsResponseListener
    ) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void queryPurchasesAsync(
        @NonNull QueryPurchasesParams queryPurchasesParams,
        @NonNull PurchasesResponseListener purchasesResponseListener
    ) {
        throw new IllegalStateException("not implemented");
    }
    
    BillingClientStateListener billingClientStateListener = null;
    @Override
    public void startConnection(@NonNull BillingClientStateListener billingClientStateListener) {
        Log.d(TAG, "startConnection: ");
        this.billingClientStateListener = billingClientStateListener;
        this.isReady = true;
        billingClientStateListener.onBillingSetupFinished(BillingResultOK);
    }
    
    boolean isReady = false;
    @Override
    public boolean isReady() {
        Log.d(TAG, "isReady: " + isReady);
        return isReady;
    }
    
    public static BillingClientBuilderStub newBuilderStub(@NonNull Context context) {
        return new BillingClientBuilderStub(context);
    }
    
}
