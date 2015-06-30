package com.example.wangqs.emma_new;

/**
 * Created by wangqs on 9/30/14.
 */
public class Timestamp {
//    public static long ExperimentTime;
//    public static long StartTime;
//    public static long CompletedTime;
//    public static long TransTime;

    public static double originalBatteryCapacity;
    public static double currentBatteryCapacity;
    public static double currentBatteryVoltage;
    public static long presentBatteryCurrent;
    public static double Energy;

    public static long tau;
    public static long threshold;
    public static long local_expectation;
    public static long Offloading_Expectation_Time;
//    public static String Recognized_Text;

    public static int Throughput;
    public static int type;             //1: local Only, 2: Offloading Only 3: restart
    public static int times;            //0:direct Local restart. 1:once offloading restart 2:twice offloading restart 10:infinite offloading restart
//    public static int method;       // 1: offload 2:local 0:end
    public static int completed_by;  //1:offload 2:local 0:failure 3:offloading restart
    public static boolean flag;
    public static boolean trigger;
    public static boolean succeed;
    public static String state;     // s:successful f:failure l:local o:offload

    public static int Network_State;    // 0:checking 1:true 2:false

    public static int timeoutConnection;
    public static int timeoutSocket;

    public Timestamp(){
//        ExperimentTime  =   0;
//        StartTime   =   0;
//        CompletedTime    =  0;
//        TransTime   =   0;


        originalBatteryCapacity =   0;
        currentBatteryCapacity  =   0;
        currentBatteryVoltage   =   0;
        presentBatteryCurrent   =   0;
        Energy  =   0;

        tau =   0;
        threshold   =   0;
        local_expectation   =   0;
        Offloading_Expectation_Time =   0;
//        Recognized_Text = null;

        Throughput  =   0;
        completed_by    =   0;
        type = 1;
        times = 0;
//        method  =   0;

        flag = false;
        trigger = false;
        succeed = false;
        state = null;
        Network_State = 0;

        timeoutConnection = 6000;
        timeoutSocket = 18000;

    }
}
