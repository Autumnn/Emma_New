package com.example.wangqs.emma_new;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;

import java.io.File;

/**
 * Created by wangqs on 7/9/14.
 */
public class main_thread implements Runnable {

    private boolean run;
    private File image;
    private String webURL;
    private saveData cun, cun_file;
    private Handler lHandler_t, mHandler_t, tHandler_t;
//    private Timestamp timestamp_t;
    private Restart restart_t;

    private offloading offloading;
    private offloading offloading_restart;
    private local_restart local_restart;
//    private LocalRun localrun;
//    private RemoteRun remoterun;
//    private boolean network_state;

    private Thread offloading_thread;
    private Thread offloading_thread_restart;
//    private ExecutorService cached_offloading_thread = Executors.newCachedThreadPool();
    private Thread local_restart_thread;
//    private ExecutorService single_local_restart_thread;


//    private String temp_t;
    private String Recognized_Text;
//    private Toast toast_t;

    private boolean Activity_Connected;
//    private int method;

    private long startExperimentTime;
    private long ExperimentTime;
//    private long Interval;
    private Context current;

    private static double originalBatteryCapacity;
    private BatteryUpdate battery;

    public main_thread(File f, String url, Context context){
        image = f;
        webURL = url;
        restart_t = new Restart();
        current = context;


        startExperimentTime = SystemClock.elapsedRealtime();
        String no_prefix = image.getName().substring(0, image.getName().lastIndexOf("."));
        String Data_file = no_prefix + "_" + "_" + String.valueOf(startExperimentTime);
        cun = new saveData();
        cun.writeDir(Data_file,image.getParent());
        String Content_file = "Recognised_of_" + "_" + no_prefix + "_" + String.valueOf(startExperimentTime);
        cun_file = new saveData();
        cun_file.writeDir(Content_file,image.getParent());

        battery = new BatteryUpdate(current, 1500);
        run = false;

//        localrun = new LocalRun();
        Activity_Connected = false;
//        single_local_restart_thread = Executors.newSingleThreadExecutor();

    }

    public void end() {
        run = false;
    }

    public void Start_Connect(Handler l, Handler m, Handler t){
        lHandler_t = l;
        mHandler_t = m;
        tHandler_t = t;
        Activity_Connected = true;
    }

    public void Lost_Connect(){
        Activity_Connected = false;
    }

    public void show_method(int m){
        if (Activity_Connected) {
            int method = m;
            Bundle d = new Bundle();
            d.putInt("Method", method);
            Message signal_o_1 = lHandler_t.obtainMessage();
            signal_o_1.setData(d);
            signal_o_1.sendToTarget();
        }
    }

    public void show_toast(String in){
        if (Activity_Connected) {
            Bundle t_2 = new Bundle();
            t_2.putString("Notification", in);
            Message signal_t_2 = tHandler_t.obtainMessage();
            signal_t_2.setData(t_2);
            signal_t_2.sendToTarget();
        }
    }
    @Override
    public void run() {

        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);

        Initialization initialization = new Initialization(image, current);

        if(Timestamp.type!=1) {
            show_method(1);
            boolean Remote_Initialized = initialization.remote_Initialization(webURL);
            show_method(0);
            show_toast(initialization.get_notes());

            if (Remote_Initialized) {
                long temp = initialization.get_offloading_estimation();
                Timestamp.tau = 2 * temp;
                Timestamp.Offloading_Expectation_Time = temp;
            } else {
                return;
            }
        }

        if(Timestamp.type!=2) {
            show_method(2);
            boolean Local_Initialized = initialization.local_Initialization();
            show_method(0);
            show_toast(initialization.get_notes());

            if (Local_Initialized) {
                Timestamp.local_expectation = initialization.get_local_estimation();
            } else {
                return;
            }
        }

        switch (Timestamp.type) {

            case 1:
                Timestamp.tau = Timestamp.local_expectation;
                Timestamp.Offloading_Expectation_Time = Timestamp.local_expectation;
                Timestamp.local_expectation = Timestamp.Offloading_Expectation_Time*2;
                break;

            case 2:
                Timestamp.local_expectation = (3 * Timestamp.Offloading_Expectation_Time);
                break;

        }


        restart_t.initialLocalExecutionTime(Timestamp.local_expectation);

        restart_t.initialRemoteExecutionTime(Timestamp.Offloading_Expectation_Time);

        run = true;
        originalBatteryCapacity = battery.getCapacity();

        Timestamp.threshold = restart_t.initialization();
        Timestamp.Throughput = 0;

        do{
            Timestamp.Throughput++;
            long StartTime;
            long CompletedTime;
            long TransTime;
            long Waiting_Time = 0;
            long First_Waiting_Time = 0;
            long remote_period = 0;
            long Local_restart_start_time = 0;
            long Offloading_first_restart_start_time = 0;
            long restart_period_main_thread = 0;
            long restart_period_local_thread = 0;
            long t_tau = 0;
            boolean launch = false;
            boolean first_restarted = false;
            long threshold = 0;
            Timestamp.completed_by = 0;
            Timestamp.state = "None";
            int bar[] = new int[0];
            Timestamp.flag = false;
            Timestamp.succeed = false;
            Timestamp.Network_State = 0;
            String note;
            int restart_times = 0;

            StartTime = SystemClock.elapsedRealtime();
            threshold = Timestamp.threshold;

            switch (Timestamp.type){
                case 1:                     //local execution only
                    local_restart = new local_restart(image);
                    local_restart_thread = new Thread(local_restart);
                    local_restart_thread.start();
//                    single_local_restart_thread.execute(local_restart);
                    show_method(2);
                    break;

                case 2:
                    Timestamp.timeoutConnection = (int)(10*Timestamp.Offloading_Expectation_Time);
                    Timestamp.timeoutSocket = (int)(10*Timestamp.Offloading_Expectation_Time);
                    offloading = new offloading(image, webURL, current, StartTime, 0);
                    offloading_thread = new Thread(offloading);
                    offloading_thread.start();
                    show_method(1);
                    break;

                case 3:
                    switch (Timestamp.times){
                        case 10:                  //solo offloading restart
                            if(Timestamp.Throughput>(Restart.sum_of_update*Restart.delete_percentage/100)) {
                                t_tau = Timestamp.tau;
                                Timestamp.trigger = true;
                            }else{
                                t_tau = 10*Timestamp.Offloading_Expectation_Time;
                            }
                            break;
                        default:                  //local restart
                            if((threshold > Timestamp.local_expectation)&&(Timestamp.Throughput>30)){
//                                if(Timestamp.tau<Timestamp.Offloading_Expectation_Time) {
//                                    t_tau = (Timestamp.tau + Timestamp.Offloading_Expectation_Time)/2;
//                                }else{
                                    t_tau = Timestamp.tau;
//                                }
                                Timestamp.trigger = true;
                            }else{
                                t_tau = 10*Timestamp.Offloading_Expectation_Time;
                                Timestamp.trigger = false;
                            }
                            break;
                    }
                    Timestamp.timeoutConnection = (int)(t_tau);
                    Timestamp.timeoutSocket = (int)(t_tau);
                    offloading = new offloading(image, webURL, current, StartTime, 0);
                    offloading_thread = new Thread(offloading);
                    offloading_thread.start();
                    show_method(1);
                    break;
            }

            long temp_startTime = StartTime;

            while(!Timestamp.flag){

                switch (Timestamp.type) {
                    case 1:
                        Waiting_Time = SystemClock.elapsedRealtime() - temp_startTime;
                        if((!local_restart_thread.isAlive())||(Waiting_Time > (3*Timestamp.local_expectation))){
//                        if(single_local_restart_thread.isTerminated()||(Waiting_Time > (3*Timestamp.local_expectation))){
                            if ((!Timestamp.flag)) {
                                Timestamp.flag = true;
                            }
                        }
                        break;

                    case 2:
                        Waiting_Time = SystemClock.elapsedRealtime() - temp_startTime;
                        if((!offloading_thread.isAlive())||(Waiting_Time > (20*Timestamp.Offloading_Expectation_Time))){
                            if ((!Timestamp.flag)) {
                                Timestamp.flag = true;
                            }
                        }
                        break;

                    case 3:
                        if (!launch) {
                            Waiting_Time = SystemClock.elapsedRealtime() - temp_startTime;
                            if(Timestamp.Network_State != 2) {
                                if (Waiting_Time > t_tau) {
                                    if (restart_times < Timestamp.times) {
                                        if (first_restarted) {
                                            while (offloading_thread_restart.isAlive() && offloading_thread_restart != null) {
                                                if (!offloading_thread_restart.isInterrupted()) {
                                                    offloading_thread_restart.interrupt();
                                                }
                                                try {
                                                    offloading_thread_restart.join();
                                                } catch (InterruptedException e) {
                                                    note = "Restart Offloading Thread throw Exception";
                                                    show_toast(note);
                                                }
                                            }
                                        }
                                        if (!Timestamp.flag) {
                                            restart_times++;
                                            note = "The" + String.valueOf(restart_times) + "Offloading Restart!";
                                            show_toast(note);
                                            offloading_restart = new offloading(image, webURL, current, StartTime, restart_times);
                                            offloading_thread_restart = new Thread(offloading_restart);
                                            offloading_thread_restart.start();
                                            temp_startTime = SystemClock.elapsedRealtime();
                                            if (!first_restarted) {
                                                First_Waiting_Time = Waiting_Time;
                                                Waiting_Time = 0;
                                                while (offloading_thread.isAlive() && offloading_thread != null) {
                                                    if (!offloading_thread.isInterrupted()) {
                                                        offloading_thread.interrupt();
                                                    }
                                                    long ww = t_tau;
                                                    try {
                                                        offloading_thread.join(t_tau);
                                                    } catch (InterruptedException e) {
                                                        note = "Original Offloading Thread throw Exception";
                                                        show_toast(note);
                                                    }
                                                }
                                            }
                                            first_restarted = true;
                                        }
                                    } else if(Timestamp.times != 10) {
                                        launch = true;
                                    } else {
                                        if (!Timestamp.flag) {
                                            Timestamp.flag = true;
                                        }
                                    }
                                }
                            }else{
                                launch = true;
                            }

                            if(launch){
                                show_method(2);
                                local_restart = new local_restart(image);
                                local_restart_thread = new Thread(local_restart);
                                local_restart_thread.start();
//                                single_local_restart_thread.execute(local_restart);
                                Local_restart_start_time = SystemClock.elapsedRealtime();
                            }
                        } else {
                            //                          if(!(Local_run | Offload_run)){
                            restart_period_main_thread = SystemClock.elapsedRealtime() - Local_restart_start_time;
//                                restart_period_main_thread = (SystemClock.elapsedRealtime() - StartTime - Waiting_Time);
                            if (restart_period_main_thread < (2 * Timestamp.local_expectation)) {
                                if(first_restarted){
                                    if ((!offloading_thread_restart.isAlive()) && (!local_restart_thread.isAlive())) {
                                        if (!Timestamp.flag) {
                                            Timestamp.flag = true;
                                        }
                                    }
                                }else if ((!offloading_thread.isAlive()) && (!local_restart_thread.isAlive())) {
                                    if (!Timestamp.flag) {
                                        Timestamp.flag = true;
                                    }
                                }
                            } else {
                                if (!Timestamp.flag) {
                                    Timestamp.flag = true;
                                }
                            }
                        }
                        break;
                    }
                }
            show_method(0);

            if(launch){
//                    restart_period_main_thread = SystemClock.elapsedRealtime() - Local_restart_start_time;
                restart_period_local_thread = local_restart.Period_get();
            }

            if(!first_restarted){
                First_Waiting_Time = Waiting_Time;
                Waiting_Time = 0;
            }

            switch(Timestamp.completed_by){
                case 3:
                    Recognized_Text = offloading_restart.content_get();
                    remote_period = offloading_restart.period_get();
                    Timestamp.succeed = true;
                    break;
                case 2:             //local execution
                    Recognized_Text = local_restart.content_get();
                    if(Timestamp.type != 1) {
                        if (!first_restarted)
                            remote_period = offloading.period_get();
                        else
                            remote_period = offloading.period_get() + offloading_restart.period_get();
                        Timestamp.succeed = true;
                    }
                    break;
                case 1:             //remote execution

                    Recognized_Text = offloading.content_get();
                    remote_period = offloading.period_get();
                    Timestamp.succeed = true;
                    break;
                case 0:
                    Recognized_Text = offloading.content_get();
                    break;
            }

            CompletedTime = SystemClock.elapsedRealtime();
            TransTime = CompletedTime - StartTime;

            //               OffloadingTime = Device_Get_Time - Device_Send_Time - RmExecutionTime;

            long histogram_update_start = SystemClock.elapsedRealtime();
            switch (Timestamp.completed_by){
                case 3:
                    if(Timestamp.succeed){
                        Timestamp.Offloading_Expectation_Time = restart_t.updateRemoteExecutionTime(TransTime);
                        Timestamp.threshold = restart_t.Condition();
//                        if(Timestamp.threshold > Timestamp.local_expectation)
                        Timestamp.tau = restart_t.updateRestartTime();
                        bar = restart_t.update_chart();
 //                       Timestamp.timeoutConnection = (int)(2*Timestamp.Offloading_Expectation_Time);
 //                       Timestamp.timeoutSocket = (int)(3*Timestamp.Offloading_Expectation_Time);
                    }
                    break;
                case 2://local
                    if(Timestamp.type != 1) {
                        if (Timestamp.succeed) {
                        /*
                        long temp=0;
                        if(first_restarted){
                            temp = TransTime - Waiting_Time - First_Waiting_Time;
                        }else{
                            temp = TransTime - Waiting_Time;
                        }
                        if(temp > (2*Timestamp.local_expectation)){
                            if(restart_period_main_thread > restart_period_local_thread){
                                if(restart_period_local_thread < (2*Timestamp.local_expectation))
                                    Timestamp.local_expectation = restart_t.updateLocalExecutionTime(restart_period_local_thread);
                            }else{
                                if(restart_period_main_thread < (2*Timestamp.local_expectation))
                                    Timestamp.local_expectation = restart_t.updateLocalExecutionTime(restart_period_main_thread);
                            }
                        }else {
                            Timestamp.local_expectation = restart_t.updateLocalExecutionTime(temp);
                        }*/
                            Timestamp.local_expectation = restart_t.updateLocalExecutionTime(restart_period_local_thread);
                        }
                        if ((!Timestamp.trigger) && (Timestamp.Network_State != 2)) {
                            Timestamp.Offloading_Expectation_Time = restart_t.updateRemoteExecutionTime((TransTime - restart_period_local_thread));
                            Timestamp.threshold = restart_t.Condition();
//                        if(Timestamp.threshold > Timestamp.local_expectation)
                            Timestamp.tau = restart_t.updateRestartTime();
                        }
                    }
                    break;
                case 1://offload
                    if(Timestamp.succeed){
                        Timestamp.Offloading_Expectation_Time = restart_t.updateRemoteExecutionTime(TransTime);
                        Timestamp.threshold = restart_t.Condition();
//                        if(Timestamp.threshold > Timestamp.local_expectation)
                        Timestamp.tau = restart_t.updateRestartTime();
                        bar = restart_t.update_chart();
//                        Timestamp.timeoutConnection = (int)(2*Timestamp.Offloading_Expectation_Time);
//                        Timestamp.timeoutSocket = (int)(3*Timestamp.Offloading_Expectation_Time);
                    }
                    break;
                case 0: //failure
                    if(TransTime > (2*Timestamp.Offloading_Expectation_Time)){
                        Timestamp.Offloading_Expectation_Time = restart_t.updateRemoteExecutionTime(TransTime);
                        Timestamp.threshold = restart_t.Condition();
//                        if(Timestamp.threshold > Timestamp.local_expectation)
                        Timestamp.tau = restart_t.updateRestartTime();
                    }
                    break;
            }
            long histogram_update_finished = SystemClock.elapsedRealtime();
            long restart_cost = histogram_update_finished - histogram_update_start;

            Timestamp.currentBatteryVoltage = battery.getVoltage();
            Timestamp.currentBatteryCapacity = battery.getCapacity();
            Timestamp.presentBatteryCurrent = 0;
            Timestamp.Energy = Timestamp.currentBatteryCapacity - originalBatteryCapacity;

            String content;
            content = String.valueOf(Timestamp.Throughput) + "\t"
                    + String.valueOf(StartTime) + "\t"
                    + String.valueOf(threshold) + "\t"
                    + String.valueOf(t_tau) + "\t"
                    + String.valueOf(Timestamp.local_expectation) + "\t"
                    + String.valueOf(First_Waiting_Time) + "\t"
                    + String.valueOf(Waiting_Time) + "\t"
                    + String.valueOf(restart_period_main_thread) + "\t"
                    + String.valueOf(restart_period_local_thread) + "\t"
                    + String.valueOf(CompletedTime) + "\t"
                    + String.valueOf(TransTime) + "\t"
                    + String.valueOf(Timestamp.currentBatteryVoltage) + "\t"
                    + String.valueOf(Timestamp.currentBatteryCapacity) + "\t"
                    + String.valueOf(remote_period) + "\t"
                    + String.valueOf(restart_cost) + "\t"
                    + Timestamp.state + "\n";

            cun.Save(content);

            String rec_content;
            rec_content = String.valueOf(Timestamp.Throughput) + "\t"
                    + Recognized_Text + "\n";

            cun_file.Save(rec_content);

            if(Activity_Connected) {
                Message message = mHandler_t.obtainMessage();
                Bundle b = new Bundle();
                b.putString("Recognized_Word", Recognized_Text);
                b.putLong("Throughput_show", Timestamp.Throughput);
                b.putLong("Execution_Time", TransTime);
                b.putLong("Offloading_Time", Timestamp.threshold);
                b.putLong("Rm_Run_Time", Timestamp.tau);
                b.putLong("Current", Timestamp.presentBatteryCurrent);
                b.putDouble("Voltage", Timestamp.currentBatteryVoltage);
                b.putDouble("Energy_Consumption", Timestamp.Energy);
                b.putIntArray("Histogram", bar);
                b.putLong("Local_Expectation_Time", Timestamp.local_expectation);
                b.putLong("Offload_Expectation_Time", Timestamp.Offloading_Expectation_Time);
                message.setData(b);
                message.sendToTarget();
            }


            long st = SystemClock.elapsedRealtime();
            long et = 0;


//            if(network_state){
            if (launch) {
                if (local_restart_thread.isAlive()) {
//                if (!single_local_restart_thread.isTerminated()) {
                    try {
                        local_restart_thread.join();
//                        single_local_restart_thread.awaitTermination(Timestamp.local_expectation, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        note = "Local Restart Thread Stop Exception";
                        show_toast(note);
                    }
                }
            }

//            int Thread_Stop_Times = 0;
            if(Timestamp.type != 1) {
                while (offloading_thread.isAlive() && offloading_thread != null) {

                    if (!offloading_thread.isInterrupted()) {
                        offloading_thread.interrupt();
                    }

//                long tt = SystemClock.elapsedRealtime();
//                long wt = 0;
//                while (wt < 1000) {
                    //                   wt = SystemClock.elapsedRealtime() - tt;
                    //               }

//                Thread_Stop_Times++;
//                if(Thread_Stop_Times>3){
//                    break;
//                }


                    note = "Mission Finished! Offloading Thread is Stopping";
                    show_toast(note);
//                    offloading_thread.interrupt();
//                    offloading.throw_exception();

//                    offloading_thread.interrupt();

                    long ww = t_tau;
                    try {
                        offloading_thread.join(ww);
                    } catch (InterruptedException e) {
                        note = "Offloading Thread throw Exception";
                        show_toast(note);
                    }

                }
//
                if (first_restarted) {
                    while (offloading_thread_restart.isAlive() && offloading_thread_restart != null) {

                        if (!offloading_thread_restart.isInterrupted()) {
                            offloading_thread_restart.interrupt();
                        }

//                    long tt = SystemClock.elapsedRealtime();
//                    long wt = 0;
//                    while (wt < 1000) {
//                        wt = SystemClock.elapsedRealtime() - tt;
//                    }

//                Thread_Stop_Times++;
                        //              if(Thread_Stop_Times>3){
                        //                break;
                        //          }

                        note = "Mission Finished! Restart Offloading Thread is Stopping";
                        show_toast(note);
//                    offloading_thread.interrupt();
//                    offloading.throw_exception();

//                    offloading_thread.interrupt();

                        long ww = t_tau;
                        try {
                            offloading_thread_restart.join(ww);
                        } catch (InterruptedException e) {
                            note = "Restart Offloading Thread throw Exception";
                            show_toast(note);
                        }

                    }
                }
            }

        }while (run);
        ExperimentTime = SystemClock.elapsedRealtime() - startExperimentTime;
        String Recognized_Text = "Experiment is Completed!";
        String content = "The experiment result is:" + "\r" + "Experiment time =" + String.valueOf(ExperimentTime)
                + "\t" + "Energy consumption = " + String.valueOf(Timestamp.Energy) + "\t" + "Throughput ="
                + String.valueOf(Timestamp.Throughput) + "\r";
        cun.Save(content);

        if(Activity_Connected) {
            Message message = mHandler_t.obtainMessage();
            Bundle c = new Bundle();
            c.putString("Recognized_Word", Recognized_Text);
            int bar[] = new int[20];
            c.putIntArray("Histogram", bar);
            message.setData(c);
            message.sendToTarget();
        }

    }
}
