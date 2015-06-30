package com.example.wangqs.emma_new;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;


public class My_main extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

//    my_service mService;
//    boolean mBound;

    private EditText Image_file;
    private EditText Number_Buckets;
    private EditText Update_Threshold;
    private EditText Remain_Percentage;
    public final static String EXTRA_MESSAGE = "Emma_main.Image_Name";
    private RadioGroup radioGroup;
//    private RadioButton restart, offload;
    private boolean Service_running;

    private Spinner spinner_restart;
    private ArrayAdapter<CharSequence> adapter_restart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);

        spinner_restart = (Spinner)findViewById(R.id.restart_spinner);
        adapter_restart = ArrayAdapter.createFromResource(this, R.array.Restart_array, android.R.layout.simple_spinner_item);
        adapter_restart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_restart.setAdapter(adapter_restart);
        spinner_restart.setOnItemSelectedListener(this);

        Image_file = (EditText) findViewById(R.id.Content);
        Number_Buckets = (EditText) findViewById(R.id.bucket_number);
        Update_Threshold = (EditText) findViewById(R.id.update_threshold);
        Remain_Percentage = (EditText) findViewById(R.id.remain_percentage);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
 //       restart = (RadioButton) findViewById(R.id.restart);
 //       offload = (RadioButton) findViewById(R.id.offloading);
        radioGroup.setOnCheckedChangeListener(listen);
        Service_running = false;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String item = parent.getSelectedItem().toString();
        if(item.equals("0")){
            Timestamp.times = 0;
        }else if(item.equals("1")){
            Timestamp.times = 1;
        }else if(item.equals("2")){
            Timestamp.times = 2;
        }else if(item.equals("infinite")){
            Timestamp.times = 10;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Timestamp.times = 0;
    }

    private RadioGroup.OnCheckedChangeListener listen = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId){
            switch(checkedId) {
                case R.id.local:
                    Timestamp.type = 1;
                    break;
                case R.id.offloading:
                    Timestamp.type = 2;
                    break;
                case R.id.restart:
                    Timestamp.type = 3;
                    break;
            }
        }
    };

    public void StartIdentify(View view) {

        //test
 //       Intent intent_s = new Intent(this, my_service.class);

        //test end
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = activityManager.getRunningServices(99);
        if(!(infos.size()>0)){
            Service_running = false;
        }

        String service_name;

        for(int i=0; i<infos.size();i++){
            service_name = infos.get(i).service.getClassName();
            if(service_name.equals("com.example.wangqs.emma_new.my_service")){
                Service_running = true;
                break;
            }
        }

        if(!Service_running) {
            String message = Image_file.getText().toString();
            String bucket_number = Number_Buckets.getText().toString();
            int initial_number_of_buckets = Integer.parseInt(bucket_number);
            String update_threshold = Update_Threshold.getText().toString();
            int Account_of_update_threshold = Integer.parseInt(update_threshold);
            String remain_percentage = Remain_Percentage.getText().toString();
            int histogram_remain_percentage = Integer.parseInt(remain_percentage);
            Restart.initial_histogram(initial_number_of_buckets, Account_of_update_threshold, histogram_remain_percentage);

            Intent service = new Intent(this, my_service.class);
            service.putExtra(EXTRA_MESSAGE, message);
            startService(service);

            Intent intent = new Intent(this, my_display.class);
//        intent.putExtra(EXTRA_MESSAGE, message);
//        getApplicationContext().bindService(intent_s, mConnection, Context.BIND_AUTO_CREATE);
            startActivity(intent);

        }else{
            Toast toast = Toast.makeText(this, "Service is running, please bind it.", Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void BindActivity(View view){
        Intent intent = new Intent(this, my_display.class);
//        intent.putExtra(EXTRA_MESSAGE, message);
//        getApplicationContext().bindService(intent_s, mConnection, Context.BIND_AUTO_CREATE);
        startActivity(intent);
    }

/*
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            my_service.LocalBinder binder = (my_service.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

        }
        @Override
        public void onServiceDisconnected(ComponentName name_end) {
            mBound = false;
        }
    };
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_main, menu);
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
