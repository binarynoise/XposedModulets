package com.programminghoch10.MotionEventMod;

import android.view.MotionEvent;
import android.view.View;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHook implements IXposedHookLoadPackage {
    private static final String TAG = BuildConfig.APPLICATION_ID.split("[.]")[2];
    private static final long hover_timeout = 1000L;
    private long hover_exit_timestamp = 0;
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
//        Log.d(TAG, "handleLoadPackage: hooking package " + lpparam.packageName);
        XposedHelpers.findAndHookMethod(
            View.class, "dispatchTouchEvent", MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    MotionEvent event = (MotionEvent) param.args[0];
                    //Log.d(TAG, "dispatchTouchEvent: event=" + event);
                    if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) return;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_MOVE:
                            if (hover_exit_timestamp + hover_timeout > System.currentTimeMillis()) param.setResult(true);
                            break;
                    }
                }
            }
        );
        XposedHelpers.findAndHookMethod(
            View.class, "dispatchHoverEvent", MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    MotionEvent event = (MotionEvent) param.args[0];
                    //Log.d(TAG, "dispatchHoverEvent: event=" + event);
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_HOVER_ENTER:
                            //Log.d(TAG, "dispatchHoverEvent: Hover enter");
                            break;
                        case MotionEvent.ACTION_HOVER_EXIT:
                            hover_exit_timestamp = System.currentTimeMillis();
                            //Log.d(TAG, "dispatchHoverEvent: Hover exit");
                            break;
                    }
                }
            }
        );
    }
}
