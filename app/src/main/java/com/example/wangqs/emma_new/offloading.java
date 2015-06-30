package com.example.wangqs.emma_new;

import android.content.Context;
import android.os.*;
import android.os.Process;
import android.widget.Toast;

import java.io.File;

/**
 * Created by wangqs on 10/1/14.
 */
public class offloading implements Runnable{

    private String temp_received;
    private String temp_recognized;
    private long   offloading_period;
    private File image;
    private String webURL;
    private Context current;
//    private Timestamp timestamp_o;
//    private Toast toast_o;
    private String notes;
    private long StartTime;
    private boolean Network_State;
    private int Restart_times;      //0:first offloading    1:First Offloading restart

    public offloading(File f, String web, Context c, long s, int t){
        image = f;
        webURL = web;
        current = c;
//        timestamp_o = t;
        StartTime = s;
        notes = null;
        Network_State = false;
        Restart_times = t;

    }

    public void run() {

        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
        RemoteRun remoterun = new RemoteRun();
        switch (Timestamp.type) {
            case 2:
                Network_State = true;
                break;
            default:
                Network_State = remoterun.checkNetworkInfo();
                break;

        }


        if(Network_State) {

            Timestamp.Network_State = 1;
            temp_received = null;
            temp_recognized = "Offloading has started";

            try {
                temp_received = remoterun.run(image, webURL, current, StartTime);
                GetElement getelement = new GetElement();
                try {
                    temp_recognized = getelement.getElementValueFromXML(temp_received, "Identifiedtext");
                    String server_received_time = getelement.getElementValueFromXML(temp_received, "ReceivedTime");
                    String server_return_finish_time = getelement.getElementValueFromXML(temp_received, "ReturnFinish");
                    offloading_period = Long.parseLong(server_return_finish_time) - Long.parseLong(server_received_time);
                    //               offloading_period = Long.getLong(server_return_finish_time) - Long.getLong(server_received_time);     this method can trigger restart, because the getLong() cannot return the long value so the program will halt here for a long time
                    //               synchronized (this){
                    if (Timestamp.completed_by != 2) {
                        switch (Restart_times){
                            case 0:
                                if(Timestamp.completed_by != 3)
                                    Timestamp.completed_by = 1;
                                break;
                            default:
                                if(Timestamp.completed_by != 1)
                                    Timestamp.completed_by = 3;
                                break;
                        }

                        if (Timestamp.trigger) {
                            switch (Restart_times){
                                case 0:
                                    if(Timestamp.completed_by != 3)
                                        Timestamp.state = "s_o_w_r";              //offloading is successful with local restart is launched.
                                    break;
                                default:
                                    if(Timestamp.completed_by != 1)
                                        Timestamp.state = "s_o_w_r_f_r";            // restarted offloading is successful
                                    break;
                            }

                        }else {
                            switch (Restart_times){
                                case 0:
                                    if(Timestamp.completed_by != 3)
                                        Timestamp.state = "s_o_n_r";
                                    break;
                                default:
                                    if(Timestamp.completed_by != 1)
                                        Timestamp.state = "s_o_n_r_f_r";        //Restarted offloading is successful without restart but network timeout
                                    break;
                            }

                        }
                    }
                    if (!Timestamp.flag)
                        Timestamp.flag = true;
//                }
                } catch (Exception e) {
                    notes = "Offloading Get Element Failure";
                /*
                Looper.prepare();
                Toast toast_o_2 = Toast.makeText(current, notes, Toast.LENGTH_SHORT);
                toast_o_2.show();
                Looper.loop();
                */
                    temp_recognized = "Offloading Content Catch Failure";
//            timestamp_o.succeed = false;
                    if (Timestamp.trigger)
                        Timestamp.state = "f_o_c_f";          //offloading is successful but element get is failure with local restart
                    else
                        Timestamp.state = "f_o_c_f_n_r";      //offloading is successful but element get is failure without local restart
                }
            } catch (Exception e) {
                notes = "Offloading Recognition Failure";
                Looper.prepare();
                Toast toast_o_1 = Toast.makeText(current, notes, Toast.LENGTH_SHORT);
                toast_o_1.show();
                Looper.loop();
                temp_received = "Offloading Failure";
                if (Timestamp.completed_by != 2) {
                    if (Timestamp.trigger)
                        Timestamp.state = "f_o_w_r";          //offloading is failure with local restart is launched.
                    else
                        Timestamp.state = "f_o_n_r";
                }
                temp_recognized = temp_received;
            }
        }else{
            switch (Timestamp.type){
                case 3:
                    if(Restart_times == Timestamp.times){
                        Timestamp.Network_State = 2;
                        temp_recognized = "No Network";
                    }
                    break;
                default:
                    Timestamp.Network_State = 2;
                    temp_recognized = "No Network";
                    break;
            }
        }
    }

    public String content_get(){
        return temp_recognized;
    }

    public long period_get(){
        return offloading_period;
    }
/*
    public void throw_exception(){
        temp_recognized = null;
    }
    */
}
