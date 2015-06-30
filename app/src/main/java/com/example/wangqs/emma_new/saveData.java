package com.example.wangqs.emma_new;


import android.os.Environment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by wangqs on 9/25/14.
 */
public class saveData {
    String Dir;
    String content;

    public void writeDir(String name, String dir){
        Dir = dir + "/" + name + ".txt";      //      "/mnt/sdcard/Android/data/"
    }

    public String Save(String C){
        content = C;
        boolean check;		// check the External storage state
        String results;		// return the results

        check = isExternalStorageWritable();

        if (check) {
            results = "The External storage is OK. \n";
        } else {
            results = "No External storage.\n";
            return results;
        }

        FileOutputStream Doutt;
        OutputStreamWriter Dout;

        try{
            Doutt = new FileOutputStream(Dir, true);
            Dout = new OutputStreamWriter(Doutt);
        } catch(IOException e) {
            results += "Datafile cannot be found! \n";
            return results;
        }

        int length;
        length = content.length();
        if(length < 0){
            results += "There is nothing can be written. \n";
            return results;
        }
        try{
            Dout.write(content, 0, length);
        } catch(IOException e) {
            results += "IO Error! \n";
            return results;
        }
        try{
            Dout.close();
        } catch(IOException e) {
            results += "Cannot Close. \n";
            return results;
        }
        results += "Data save successfully! \n" ;
        return results;
    }

    private boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }
}
