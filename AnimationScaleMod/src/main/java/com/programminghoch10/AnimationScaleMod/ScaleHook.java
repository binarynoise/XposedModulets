
package com.programminghoch10.AnimationScaleMod;

import android.content.res.XModuleResources;
import android.content.res.XResources;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

public class ScaleHook implements IXposedHookInitPackageResources, IXposedHookZygoteInit {
    private static final String PACKAGE_SETTINGS = "com.android.settings";
    private final static String[] arrayResourceNames = {
            "window_animation",
            "transition_animation",
            "animator_duration",
    };
    private static String modulePath = null;
    
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals(PACKAGE_SETTINGS)) return;
        XResources resources = resparam.res;
        XModuleResources moduleResources = XModuleResources.createInstance(modulePath, resources);
        for (String arrayResourceName : arrayResourceNames) {
            arrayResourceName += "_scale";
            resources.setReplacement(PACKAGE_SETTINGS, "array", arrayResourceName + "_values", moduleResources.fwd(R.array.animation_scale_values));
            String animationOff = resources.getStringArray(resources.getIdentifier(arrayResourceName + "_entries", "array", PACKAGE_SETTINGS))[0];
            String[] entries = resources.getStringArray(resources.getIdentifier(arrayResourceName + "_values", "array", PACKAGE_SETTINGS));
            entries = Arrays.stream(entries).map(entry -> Float.parseFloat(entry) + "x").toArray(String[]::new);
            entries[0] = animationOff;
            resources.setReplacement(PACKAGE_SETTINGS, "array", arrayResourceName + "_entries", entries);
        }
    }
    
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        modulePath = startupParam.modulePath;
    }
    
}
