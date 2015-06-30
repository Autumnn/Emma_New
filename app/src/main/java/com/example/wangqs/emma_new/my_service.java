package com.example.wangqs.emma_new;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class my_service extends Service {

    public static final String TAG = "my_service";
    private String File_name;
/*
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){
            //sample
            long endTime = System.currentTimeMillis() + 5*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            //sample end
        }
    }
*/
    private File image;
    private String webURL;
    private IBinder mBinder = new LocalBinder();



//    private OnProgressListener onProgressListener;

    public Handler lHandler_s;
    public Handler mHandler_s;
    public Handler tHandler_s;

//    public Timestamp timestamp_s;
    private Context context;
    private main_thread m_thread;
    private Thread thread;



        @Override
        public  void onCreate(){
            super.onCreate();
            /*
            HandlerThread thread_sample = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
            thread_sample.start();

            mServiceLooper = thread_sample.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);
            */
            Log.d(TAG, "onCreate() executed");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId){
            Log.d(TAG, "onStartCommand() executed");
/*
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
            return START_STICKY;
            */
//            Intent intent = getIntent();
            File_name = intent.getStringExtra(My_main.EXTRA_MESSAGE);
            String dir = getString(R.string.Dir);
            webURL = getString(R.string.Fu_URL);
            image = new File(dir, File_name);
            context = this.getApplicationContext();
            m_thread = new main_thread(image, webURL, context);
            thread = new Thread(m_thread);
            thread.start();

            Notification notification = new Notification(R.drawable.icon, getString(R.string.Notify_ticker), System.currentTimeMillis());
            Intent notificationIntent = new Intent(this, my_display.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notification.setLatestEventInfo(this, getString(R.string.Notify_title),getString(R.string.Notify_message), pendingIntent);
            startForeground(1, notification);

            return super.onStartCommand(intent, flags, startId);

        }

    public class LocalBinder extends Binder {
        my_service getService() {
            Log.d(TAG, "onBind() executed");
            return my_service.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    public void Start_Bind(Handler l, Handler m, Handler t) {

        lHandler_s = l;
        mHandler_s = m;
        tHandler_s = t;
        m_thread.Start_Connect(lHandler_s, mHandler_s, tHandler_s);

    }

    public void  Lost_Bind(){
        m_thread.Lost_Connect();
    }


/*
//        boolean initialized_check = check_Initialized();
//        if(!initialized_check) {
        if(!Initialized)
            Initialized = Initialize();

        }else{
            Initialized = true;
        }

        if(Initialized){
            boolean iterating_check = mService.check_Iterating();
            if(!iterating_check){
                mService.Iteration();
            }

        }else{
            if(mBound){
                getApplicationContext().unbindService(mConnection);
                mBound = false;
            }
            Toast toast = Toast.makeText(current, "Initialization Failure", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
    */

    public void end(){
        m_thread.end();
        stopForeground(true);
        try {
            thread.join();
        }catch (Exception e){
            Log.d(TAG, "Service Finished!");
        }

    }
/*
    public boolean check_Initialized(){
        return initialized;
    }

    public boolean check_Iterating(){
        return iterating;
    }
    /*
        public interface OnProgressListener {
            void onProgress(int progress)        }
    /*
        public void setOnProgressListener(OnProgressListener onProgressListener){
            this.onProgressListener = onProgressListener;
        }
    */


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }
}
