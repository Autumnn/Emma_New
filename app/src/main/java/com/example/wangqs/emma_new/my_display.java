package com.example.wangqs.emma_new;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;

// ActionBarActivity
public class my_display extends ActionBarActivity {

    my_service mService;
    boolean mBound =  false;

    private TextView local;
    private TextView offload;
    private LinearLayout layout_chart;
    private TextView Identified_word;
    private TextView tvThroughput;
    private TextView tvExecutionTime;
    private TextView tvOffloadingTime;
    private TextView tvRmExecutionTime;
    private TextView tvCurrent;
    private TextView tvBatteryVol;
    private TextView tvEnergyCon;
    private TextView tvLocalTvalue;
    private TextView tvReOfftvalue;
    private GraphicalView hist;

    public Handler lHandler;
    public Handler mHandler;
    public Handler tHandler;

    private Context current;
    private Chart chart_view;
    private int Initial_bar[];

    private String s;
    private long f;
    private long ExT;
    private long OfT;
    private long ReT;
    private long c;
    private double v;
    private double e;
    private int m;
    private int h[];
    private long let;
    private long oet;
    private String notes;

//    private Intent service;
//    private String File_name;
//    private boolean created;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_display);

        current = this.getApplicationContext();

        local           = (TextView) findViewById(R.id.local);
        offload         = (TextView) findViewById(R.id.offload);

        layout_chart    =   (LinearLayout)findViewById(R.id.chartArea);
        Identified_word =   (TextView) findViewById(R.id.Recognised_word);
        tvThroughput        = (TextView)    findViewById(R.id.Tvalue);
        tvExecutionTime     = (TextView)    findViewById(R.id.TrTvalue);
        tvOffloadingTime    = (TextView)    findViewById(R.id.OffTvalue);
        tvRmExecutionTime   = (TextView)    findViewById(R.id.RmTvalue);
        tvCurrent           = (TextView)    findViewById(R.id.Cuvalue);
        tvBatteryVol        = (TextView)    findViewById(R.id.BVvalue);
        tvEnergyCon	        = (TextView)    findViewById(R.id.Evalue);
        tvLocalTvalue       = (TextView)    findViewById(R.id.LocalTvalue);
        tvReOfftvalue       = (TextView)    findViewById(R.id.ReOffTvalue);

        Identified_word.setVisibility(View.VISIBLE);
        tvThroughput.setVisibility(View.VISIBLE);
        tvExecutionTime.setVisibility(View.VISIBLE);
        tvOffloadingTime.setVisibility(View.VISIBLE);
        tvRmExecutionTime.setVisibility(View.VISIBLE);
        tvCurrent.setVisibility(View.VISIBLE);
        tvBatteryVol.setVisibility(View.VISIBLE);
        tvEnergyCon.setVisibility(View.VISIBLE);
        tvLocalTvalue.setVisibility(View.VISIBLE);
        tvReOfftvalue.setVisibility(View.VISIBLE);

        chart_view = new Chart();

        Initial_bar = new int[20];
        hist = chart_view.draw(Initial_bar, current);
        layout_chart.addView(hist, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message inputMessage){

                super.handleMessage(inputMessage);

                Bundle b = inputMessage.getData();

                s = b.getString("Recognized_Word");
                f = b.getLong("Throughput_show");
                ExT = b.getLong("Execution_Time");
                OfT = b.getLong("Offloading_Time");
                ReT = b.getLong("Rm_Run_Time");
                c = b.getLong("Current");
                v = b.getDouble("Voltage");
                e = b.getDouble("Energy_Consumption");
                h = b.getIntArray("Histogram");
                let = b.getLong("Local_Expectation_Time");
                oet = b.getLong("Offload_Expectation_Time");

                Identified_word.setText(s);
                tvThroughput.setText(Long.toString(f));
                tvExecutionTime.setText(Long.toString(ExT));
                tvOffloadingTime.setText(Long.toString(OfT));
                tvRmExecutionTime.setText(Long.toString(ReT));
                tvCurrent.setText(Double.toString(c));
                tvBatteryVol.setText(Double.toString(v));
                tvEnergyCon.setText(Double.toString(e));
                tvLocalTvalue.setText(Long.toString(let));
                tvReOfftvalue.setText(Long.toString(oet));

                hist = chart_view.update(h);
                //               layout_chart.addView(hist, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

            }
        };

        lHandler = new Handler(){
            @Override
            public void handleMessage(Message input_signal){
                super.handleMessage(input_signal);
                Bundle c = input_signal.getData();
                m = c.getInt("Method");

                if(m==2)
                    local.setVisibility(View.VISIBLE);
                else if(m==1)
                    offload.setVisibility(View.VISIBLE);
                else if(m==0)
                {
                    local.setVisibility(View.INVISIBLE);
                    offload.setVisibility(View.INVISIBLE);
                }
            }

        };

        tHandler = new Handler(){
            @Override
            public void handleMessage(Message input_toast){
                super.handleMessage(input_toast);
                Bundle t = input_toast.getData();
                notes = t.getString("Notification");
                Toast toast = Toast.makeText(current, notes, Toast.LENGTH_SHORT);
                toast.show();

            }
        };

//        mBound = false;
//        Intent intent = getIntent();
//        File_name = intent.getStringExtra(My_main.EXTRA_MESSAGE);


//        bindService(service, mConnection, Context.BIND_AUTO_CREATE);



    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent_s = new Intent(this, my_service.class);
        getApplicationContext().bindService(intent_s, mConnection, Service.BIND_AUTO_CREATE);

    }

    public void EndExperiment(View view){
        mService.end();
        if(mBound){
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
        Intent intent_d = new Intent(this, my_service.class);
        stopService(intent_d);
    }

    @Override
    protected void onResume(){
        super.onResume();

        Identified_word.setText(s);
        tvThroughput.setText(Long.toString(ExT));
        tvOffloadingTime.setText(Long.toString(OfT));
        tvRmExecutionTime.setText(Long.toString(ReT));
        tvCurrent.setText(Long.toString(c));
        tvBatteryVol.setText(Double.toString(v));
        tvEnergyCon.setText(Double.toString(e));
        tvThroughput.setText(Long.toString(f));
    }

    @Override
    protected void onStop(){
        if(mBound){
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroy(){
        if(mBound){
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            my_service.LocalBinder binder = (my_service.LocalBinder) service;
            mService = binder.getService();
            mService.Start_Bind(lHandler, mHandler, tHandler);
            mBound = true;
            /*
            if(mBound){

                boolean initialized_check = mService.check_Initialized();
                if(!initialized_check) {
                    Initialized = mService.Initialize(File_name, lHandler, mHandler);
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

            }else{
                Toast toast = Toast.makeText(current, "Service UnBound", Toast.LENGTH_SHORT);
                toast.show();
            }

            /**
            background.setOnProgressListener(new my_service.OnProgressListener() {
                @Override
                public void onProgress(int progress) {

                }
            });**/


        }

        @Override
        public void onServiceDisconnected(ComponentName name_end) {
            mService.Lost_Bind();
            mBound = false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
