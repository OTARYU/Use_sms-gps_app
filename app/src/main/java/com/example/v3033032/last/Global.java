package com.example.v3033032.last;

import android.app.Application;


public class Global extends Application {
    //global variable　複数のアクティビティにわたって緯度経度を受け渡ししようとした。現在はMainActivityのみでつかわれている（はず
    double lati = 0.0,lon = 0.0;
    int minTime;        //更新の時間を設定

    void setMinTime(int time){minTime = time;}
    int getMinTime(){return minTime;}
    void setLati(double set){
        lati = set;
    }
    void setLon(double set){
        lon = set;
    }
    double getLati(){
        return lati;
    }
    double getLon(){
        return lon;
    }

}


//http://tech-gym.com/2012/10/android/959.html