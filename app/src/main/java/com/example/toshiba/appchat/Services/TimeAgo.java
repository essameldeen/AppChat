package com.example.toshiba.appchat.Services;

import android.app.Application;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo extends Application
{

    public static  final  int secondMilles=1000;
    public static  final  int minuteMilles=secondMilles*60;
    public static  final  int hoursMilles=minuteMilles*60;
    public static  final  int dayMilles=hoursMilles*24;

    public  static String getTime (long time , Context context){
            if(time  < 1000000000000L){
                time *=1000;
            }
        long timeNow = System.currentTimeMillis();
            if(time >timeNow || time <=0){
                return  null;
            }
            final  long different = timeNow-time;
            if(different < minuteMilles){
                return "just now";
            }
            else if(different <  2* minuteMilles){
                return "A minute ago";
            }else  if (different < 50 *minuteMilles){
                return  different /minuteMilles + " minute ago";
            }else if(different < 90 * minuteMilles){
                return  "An hour ago";
            }else  if(different < 24*hoursMilles){
                return  different/hoursMilles + " hours ago";
            }else if(different < 48 * hoursMilles){
                return "yesterday";
            }else {
                return different/dayMilles + " Days ago";
            }


    }



}