package com.example.wangqs.emma_new;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Arrays;

/**
 * Created by wangqs on 9/30/14.
 */
public class Restart {

    public static int number_of_buckets = 20;
    public static int sum_of_update = 100;
    public static int delete_percentage = 50;

    private long Local_Execution_Time;
    private long Restart_upper;
    private long Remote_Execution_Time;
    private long Min_Remote_ET;
    private double h;
    private int number_Local;
    private int number_remote;
    private int number_out_Buckets;
    private int Buckets;

    private double Each_Bucket_Min[];
    private double Each_Bucket_Max[];
    private double Each_Bucket_Number[];
    private double Each_Bucket_Average[];
    private double Out_value[];

    private double Et[];

    private long Optimal_Local_Restart_tau;
    private long Local_Restart_Threshold;

    public static void initial_histogram(int b, int s, int p){
        number_of_buckets = b;
        sum_of_update = s;
        delete_percentage = p;
    }

    public void initialLocalExecutionTime(long t){
        Local_Execution_Time = t;
//        Restart_upper = Local_Execution_Time;
        number_Local = 1;
    }

    public void initialRemoteExecutionTime(long t){
        Remote_Execution_Time = t;
        Min_Remote_ET = Remote_Execution_Time;
        Restart_upper = Local_Execution_Time;
//        Restart_upper = Remote_Execution_Time + 1000;
        number_remote = 1;
    }

    public long initialization(){
        Buckets = number_of_buckets;
        Each_Bucket_Average = new double[Buckets];
        Each_Bucket_Number = new double[Buckets];
        Each_Bucket_Max = new double[Buckets];
        Each_Bucket_Min = new double[Buckets];
        int i;
        for (i=0; i<Buckets; i++)
        {
            h = (Restart_upper-Min_Remote_ET)/Buckets;
            Each_Bucket_Min[i] = i * h;
            Each_Bucket_Max[i] = (i + 1) * h;
        }
        number_out_Buckets = 0;
        Restart_upper = (long)(Min_Remote_ET + Each_Bucket_Max[Buckets-1]);
        Local_Restart_Threshold = 0;
        return Local_Restart_Threshold;
    }

    public long updateLocalExecutionTime(long t){
        Local_Execution_Time = (Local_Execution_Time + t)/2;
        number_Local++;
        return Local_Execution_Time;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)

    public long updateRemoteExecutionTime(long t){

//        Remote_Execution_Time = (Remote_Execution_Time*number_remote + t)/(number_remote + 1);
        number_remote++;
        //   t=t+5000;

        if(t > Restart_upper){
            number_out_Buckets++;
            if(number_out_Buckets==1){
                Out_value = new double[number_out_Buckets];
                Out_value[0] = t - Min_Remote_ET;
            }else{
                int l = Out_value.length + 1;
                Out_value = Arrays.copyOf(Out_value, l);
                Out_value[l-1] = t - Min_Remote_ET;
            }

        }else if(t < Min_Remote_ET){
            double distance = (double)(Min_Remote_ET - t)/h;
            int K = (int)Math.ceil(distance);
            Buckets = Buckets + K;
            Each_Bucket_Average = Arrays.copyOf(Each_Bucket_Average, Buckets);
            Each_Bucket_Max     = Arrays.copyOf(Each_Bucket_Max, Buckets);
            Each_Bucket_Min     = Arrays.copyOf(Each_Bucket_Min, Buckets);
            Each_Bucket_Number  = Arrays.copyOf(Each_Bucket_Number, Buckets);

            for(int i= (Buckets-1); i>=K; i--){
                Each_Bucket_Min[i]      = Each_Bucket_Min[i-K] + (h * K);
                Each_Bucket_Max[i]      = Each_Bucket_Max[i-K] + (h * K);
                Each_Bucket_Number[i]   = Each_Bucket_Number[i-K];
                if(Each_Bucket_Average[i]!=0)
                    Each_Bucket_Average[i] = Each_Bucket_Average[i-K] + (h * K);
                else
                    Each_Bucket_Average[i] = 0;
            }

            for(int i=1; i<K; i++){
                Each_Bucket_Average[i] = 0;
                Each_Bucket_Number[i] = 0;
                Each_Bucket_Min[i] = i*h;
                Each_Bucket_Max[i] = (i+1)*h;
            }

            Min_Remote_ET = Min_Remote_ET-(long)(h*K);
            Each_Bucket_Number[0] = 1;
            Each_Bucket_Average[0] = t - Min_Remote_ET;


        }else{
            int i=0;
            while(((t-Min_Remote_ET)>=Each_Bucket_Max[i])&(i<=(Buckets-1))){
                i++;
            }
            if(i==Buckets){
                Each_Bucket_Average[i-1] = (Each_Bucket_Average[i-1]*Each_Bucket_Number[i-1] + Each_Bucket_Max[i-1])/(Each_Bucket_Number[i-1]+1);
                Each_Bucket_Number[i-1] = Each_Bucket_Number[i-1]+1;
            }else{
                Each_Bucket_Average[i] = (Each_Bucket_Average[i]*Each_Bucket_Number[i] + (t-Min_Remote_ET))/(Each_Bucket_Number[i]+1);
                Each_Bucket_Number[i]++;
            }
        }


        double n = sum((Buckets-1)) + number_out_Buckets;
        double temp_expectation = 0;
        for(int i=0; i<Buckets; i++){
            double p = Each_Bucket_Number[i]/n;
            temp_expectation = temp_expectation + Each_Bucket_Average[i]*p;
        }
        if(number_out_Buckets>0){
            for(int i=0; i<Out_value.length; i++){
                temp_expectation = temp_expectation + Out_value[i]/n;
            }
        }
        Remote_Execution_Time = (long)(temp_expectation + Min_Remote_ET);
        return Remote_Execution_Time;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public long Condition(){
        long max_gt=0;
        Et = new double[Buckets];
        double gt[] = new double[Buckets];
        double M[] = new double[Buckets];
        double N[] = new double[Buckets];
        double C_M[] = new double[Buckets];
        double C_N[] = new double[Buckets];
        double n = sum((Buckets-1)) + number_out_Buckets;    //total number in consider (!= number_remote)
        long expectation= Remote_Execution_Time - Min_Remote_ET;
        for(int i=0; i<Buckets; i++){
            M[i] = sum_average(i);
            N[i] = sum(i);
            C_M[i] = cover_sum_average(i);
            C_N[i] = cover_sum(i);
            if((N[i]==0)||(N[i]>=n)){
//                gt[i]=expectation-Each_Bucket_Max[i];
                gt[i]=Min_Remote_ET;
                Et[i]=Remote_Execution_Time;
            }
            else{
                switch (Timestamp.times){
                    case 2:
                        gt[i]=(long)((expectation-M[i]/N[i])/((1-N[i]/n)*(1-N[i]/n)*(1-N[i]/n))-(M[i]/N[i]+Each_Bucket_Max[i])/(1-N[i]/n) -(M[i]/N[i]+Each_Bucket_Max[i])/((1-N[i]/n)*(1-N[i]/n)) -Each_Bucket_Max[i]);
                        Et[i]=(long)((M[i]/N[i])+((1-N[i]/n)*((M[i]/N[i])+Each_Bucket_Max[i]))+((1-N[i]/n)*(1-N[i]/n)*((M[i]/N[i])+Each_Bucket_Max[i]))+(1-N[i]/n)*(1-N[i]/n)*(1-N[i]/n)*(Local_Execution_Time + Each_Bucket_Max[i]));
                        break;
                    case 1:
//                        gt[i]=(long)((expectation*n-M[i])/((n-N[i])*(1-N[i]/n)*(1-N[i]/n))-(M[i]/N[i]+Each_Bucket_Max[i])/(1-N[i]/n) -Each_Bucket_Max[i]);
                        gt[i]=(long)((expectation-M[i]/N[i])/((1-N[i]/n)*(1-N[i]/n))-(M[i]/N[i]+Each_Bucket_Max[i])/(1-N[i]/n) -Each_Bucket_Max[i]);
                        Et[i]=(long)((M[i]/N[i])+((1-N[i]/n)*((M[i]/N[i])+Each_Bucket_Max[i]))+(1-N[i]/n)*(1-N[i]/n)*(Local_Execution_Time + Each_Bucket_Max[i]));

                        break;
                    case 0:
//                        gt[i]=(long)(expectation*n-M[i])/((n-N[i])*(1-N[i]/n))-Each_Bucket_Max[i];
                      gt[i]=(long)(expectation-M[i]/N[i])/(1-N[i]/n)-Each_Bucket_Max[i];
//                gt[i]=(long)(C_M[i]/C_N[i])/(1-N[i]/n)-Each_Bucket_Max[i];
                        Et[i]=(long)(M[i]/N[i])+(1-N[i]/n)*(Local_Execution_Time + Each_Bucket_Max[i]);
                        break;
                    case 10:
                        gt[i]=(long)(expectation-M[i]/N[i])/(1-N[i]/n)-Each_Bucket_Max[i];
                        Et[i]=(long)(M[i]/N[i])/(N[i]/n)+(1-N[i]/n)*Each_Bucket_Max[i]/(N[i]/n);
                        break;
                }
            }
            if(max_gt < gt[i]) {
                max_gt = (long) gt[i];
            }
        }

        if(n>sum_of_update){
            for(int i=0; i<Buckets; i++){
                if(Each_Bucket_Number[i]>=1)
                    Each_Bucket_Number[i] = Math.floor(Each_Bucket_Number[i] * delete_percentage / 100);
//                  Each_Bucket_Number[i] = Math.ceil(Each_Bucket_Number[i]/2);
//                    Each_Bucket_Number[i]--;
            }
            if(number_out_Buckets>0){
                double d = (double)(number_out_Buckets * delete_percentage / 100);
                number_out_Buckets = (int)Math.ceil(d);
//                number_out_Buckets--
                if(number_out_Buckets!=0){
                    int l = Out_value.length;
                    if(l>1){
                        double temp[];
                        int ini = l - number_out_Buckets;
                        temp = Arrays.copyOfRange(Out_value,ini,l);
                        Out_value = temp;
                    }
                }
            }
        }

        return max_gt;
    }

    public long updateRestartTime(){
        double min_Et = Et[0];
        Optimal_Local_Restart_tau = (long)Each_Bucket_Max[0];
        for(int i=1; i<Buckets; i++){
            if(min_Et > Et[i]){
                min_Et = Et[i];
                Optimal_Local_Restart_tau = (long)Each_Bucket_Max[i];
            }
        }
        Optimal_Local_Restart_tau = Optimal_Local_Restart_tau + Min_Remote_ET;
        return Optimal_Local_Restart_tau;
    }

    private double sum(int up_lim){
        double num = 0;
        for(int i=0; i<=up_lim; i++){
            num = num + Each_Bucket_Number[i];
        }
        return num;
    }

    private double sum_average(int up_lim){
        double mean = 0;
        for(int i=0; i<=up_lim; i++){
            mean = mean + Each_Bucket_Number[i]*Each_Bucket_Average[i];
        }
        return mean;
    }

    private double cover_sum(int up_lim){
        double num = 0;
        for(int i=Buckets-1; i>=up_lim; i--){
            num = num + Each_Bucket_Number[i];
        }
        num = num + number_out_Buckets;
        return num;
    }

    private double cover_sum_average(int up_lim){
        double mean = 0;
        for(int i=Buckets-1; i>=up_lim; i--){
            mean = mean + Each_Bucket_Number[i]*Each_Bucket_Average[i];
        }
        if(number_out_Buckets>0){
            for(int j=0; j<Out_value.length; j++){
                mean = mean + Out_value[j];
            }
        }

        return mean;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public int[] update_chart(){
        int l = Each_Bucket_Number.length;
        int[] hist = new int[l+1];
        for(int i = 0; i<l; i++)
            hist[i] = (int)Each_Bucket_Number[i];
        hist[l] = number_out_Buckets;
        return hist;
    }
}
