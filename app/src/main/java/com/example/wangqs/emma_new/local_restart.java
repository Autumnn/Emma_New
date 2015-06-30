package com.example.wangqs.emma_new;

import android.os.*;
import android.os.Process;

import java.io.File;

/**
 * Created by wangqs on 10/1/14.
 */
public class local_restart implements Runnable {
    private File image;
//    private Timestamp timestamp_l;
    private String temp_local;
    private LocalRun localrun;
    private long Execution_time;

    public local_restart(File f){
        image = f;
//        timestamp_l = t;
        temp_local = null;
        localrun = new LocalRun();
        Execution_time = 0;
    }

    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
        long Start_time = SystemClock.elapsedRealtime();
        try{
            temp_local = localrun.recognised(image);
//            synchronized (this){
                if((Timestamp.completed_by!=1)&&(Timestamp.completed_by!=3)){
                    Timestamp.completed_by = 2;
                    if(Timestamp.Network_State==2){
                        if(Timestamp.trigger){
                            Timestamp.state = "s_l_w_r_n_f";              //local restart is successful after waiting a long time
                        }else{
                            Timestamp.state = "s_l_n_f";                              //local execution is successful when the network is failure
                        }
                    }else {
                        if (Timestamp.trigger)
                            Timestamp.state = "s_l_w_r";              //local restart is successful after waiting a long time
                        else
                            Timestamp.state = "s_l_o_l";              //offloading is over time, so local restart is used
                    }
                }
                if(!Timestamp.flag)
                    Timestamp.flag = true;
//            }
        }catch (Exception e){
//            notes = "Local Recognition Failure";
            if((Timestamp.completed_by!=1)&&(Timestamp.completed_by!=3)) {
                if(Timestamp.Network_State==2){
                    Timestamp.state = "f_l_n_f";                              //local execution is failure when the network is failure
                }else{
                    Timestamp.state = "f_l_w_r";              //local restart is failure after being launched
                }
            }
        }
        long End_time = SystemClock.elapsedRealtime();
        Execution_time = End_time - Start_time;
    }

    public String content_get(){
        return temp_local;
    }

    public long Period_get(){
        return Execution_time;
    }
}
