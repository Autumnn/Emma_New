package com.example.wangqs.emma_new;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by wangqs on 7/9/14.
 */
public class BatteryUpdate {
    private double level;
    private double scale;
    private double capacity;
    private double C;
    private int    BatteryCapacity;

    public BatteryUpdate(Context current, int i) {

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                level = (double) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                scale = (double) intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                capacity = (double) intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            }
        };
        current.registerReceiver(batteryReceiver, filter);
        BatteryCapacity = i;
    }

    public double getVoltage() {
        return level;
    }

    public double getCapacity() {
        C = (capacity/scale)*BatteryCapacity;
        return C;
    }
}
