package com.example.wangqs.emma_new;

import android.content.Context;
import android.os.SystemClock;

import java.io.File;

/**
 * Created by wangqs on 9/25/14.
 */
public class Initialization{

//    private int method;
    private File image;
    private String temp_received;
    private LocalRun localrun;
    private RemoteRun remoterun;
    private Context current;
    private GetElement getelement;

//    public Handler lHandler_i;
 //   public Handler mHandler_i;
//    public Timestamp timestamp_i;
    private long StartTime;
    private long CompletedTime;
    private long Local_Estimation;
    private long Offloading_Estimation;
    private String Recognized_Text;

    public Initialization(File f, Context c){
        image = f;
//        lHandler_i = l;
//        mHandler_i = m;
//        timestamp_i = t;
        current = c;

        localrun = new LocalRun();
        remoterun = new RemoteRun();
        getelement = new GetElement();

        StartTime = 0;
        CompletedTime = 0;
        Local_Estimation = 0;
        Offloading_Estimation = 0;

    }

    public boolean local_Initialization() {

        StartTime = SystemClock.elapsedRealtime();
        try{
            Recognized_Text = localrun.recognised(image);
        }catch (Exception e){
            Recognized_Text = "Local Initialize Failure!";
            return false;
        }
        CompletedTime = SystemClock.elapsedRealtime();
        Local_Estimation = CompletedTime - StartTime;

//        timestamp_i.local_expectation = timestamp_i.TransTime;
//        Identified_word.setText(Recognized_Text);

        return true;
    }

    public long get_local_estimation(){
        return Local_Estimation;
    }

    public boolean remote_Initialization(String w){
        String webURL = w;

        StartTime = SystemClock.elapsedRealtime();
        try{
            temp_received = remoterun.run(image, webURL, current, StartTime);
            Recognized_Text = getelement.getElementValueFromXML(temp_received, "Identifiedtext");
        }catch (Exception e){
            Recognized_Text = "Remote Initialize Failure!";
            return false;
        }
        CompletedTime = SystemClock.elapsedRealtime();
        Offloading_Estimation = CompletedTime - StartTime;

//        timestamp_i.Offloading_Expectation_Time = timestamp_i.TransTime;
//        Identified_word.setText(Recognized_Text);

        return true;
    }

    public long get_offloading_estimation(){
        return Offloading_Estimation;
    }

    public String get_notes(){
        return Recognized_Text;
    }
}
