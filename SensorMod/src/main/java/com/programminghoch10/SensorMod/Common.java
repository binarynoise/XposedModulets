package com.programminghoch10.SensorMod;

import android.hardware.Sensor;

import java.util.Objects;

public class Common {
    
    public static boolean DEFAULT_SENSOR_STATE = true;
    
    public static String getKey(Sensor sensor) {
        return "sensor_" + Objects.hash(sensor.getName(), sensor.getId(), sensor.getVendor(), sensor.getVersion(), sensor.getType());
    }
}
