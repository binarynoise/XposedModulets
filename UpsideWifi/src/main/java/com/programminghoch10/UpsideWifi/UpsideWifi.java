package com.programminghoch10.UpsideWifi;

import android.annotation.SuppressLint;
import android.os.Build;
import android.widget.ImageView;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class UpsideWifi implements IXposedHookInitPackageResources {
    final String PACKAGE_SYSTEMUI = "com.android.systemui";
    ImageView wifiIndicator;
    
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        if (!resparam.packageName.equals(PACKAGE_SYSTEMUI)) {
            return;
        }
        String layoutName = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
            ? "new_status_bar_wifi_group"
            : (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "status_bar_wifi_group" : "signal_cluster_view");
        resparam.res.hookLayout(
            PACKAGE_SYSTEMUI, "layout", layoutName, new XC_LayoutInflated() {
                @SuppressLint("DiscouragedApi")
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) {
                    wifiIndicator = liparam.view.findViewById(liparam.res.getIdentifier("wifi_signal", "id", PACKAGE_SYSTEMUI));
                    wifiIndicator.setRotation(180);
                }
            }
        );
    }
}
