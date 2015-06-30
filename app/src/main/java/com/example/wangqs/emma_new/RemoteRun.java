package com.example.wangqs.emma_new;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Created by wangqs on 9/30/14.
 */
public class RemoteRun {
    String device = "mobile";
    String notify = "";
    String result = "";
    StringBuffer buffer;
    InputStream ins = null;
    private long SendTime;
    private long ReceivedTime;
    private long ReceivedFinishedTime;
    private long start_time;
    private static Context current;
    private boolean networkstate;
    private boolean wait;


    public String run(File f, String url, Context c, long st){

        current = c;
        start_time = st;
        wait = true;
        do{
            networkstate = checkNetworkInfo();
            long wait_period = SystemClock.elapsedRealtime() - start_time;
            if(wait_period < 3000){
                wait = true;
            }else{
                wait = false;
            }
        }while((!networkstate) && wait);

        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntity.setBoundary("----------ThIs_Is_tHe_bouNdaRY_$");
        multipartEntity.setCharset(Charset.defaultCharset());

        StringBody key = new StringBody(device, ContentType.TEXT_PLAIN);
        multipartEntity.addPart("devicekey", key);

        File file = f;
        String imagename = f.getName();
        FileBody image = new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, imagename);

        multipartEntity.addPart("imagefile",image);
        HttpPost request = new HttpPost(url);
        request.setEntity(multipartEntity.build());
        request.addHeader("Content-Type","multipart/form-data; boundary=----------ThIs_Is_tHe_bouNdaRY_$");


        try{
            ins = current.getAssets().open("offload.cer");
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(ins);
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);
            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
            Scheme sch = new Scheme("https", socketFactory, 443);

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Timestamp.timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, Timestamp.timeoutSocket);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);

            try{
                SendTime = SystemClock.elapsedRealtime();
                HttpResponse response = httpClient.execute(request);
                ReceivedTime = SystemClock.elapsedRealtime();
                InputStream is = response.getEntity().getContent();
                BufferedReader inn = new BufferedReader(new InputStreamReader(is));
                buffer = new StringBuffer();
                String line = "";
                while ((line = inn.readLine()) != null) {
                    buffer.append(line);
                }
            }catch (IOException e){
                notify = "IOException Error!";
                ReceivedTime = 0;
                return notify;
            }

        }catch (Exception e){
            notify = "SSL Error";
            ReceivedTime = 0;
            return  notify;
        }

        result = buffer.toString();
        ReceivedFinishedTime = SystemClock.elapsedRealtime();
        return  result;
    }

    public long getSendTime(){
        return SendTime;
    }

    public long getReceivedTime(){
        return ReceivedTime;
    }

    public long getReceivedFinishedTime(){
        return ReceivedFinishedTime;
    }

    public static boolean checkNetworkInfo(){

        ConnectivityManager connMgr = (ConnectivityManager)current.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }

    }

}
