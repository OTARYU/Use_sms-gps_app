package com.example.v3033032.last;

import java.io.Serializable;

public class Item implements Serializable{
    private String text;            //送信用メッセージ
    private int item_number;    //place位置
    private PlaceData placeData;    //今あるプレイスデータをすべて保存
    String view_text;               //Listで表示する用のString
    Item(){
        text = "";
        item_number = -1;
        placeData = null;
        view_text = "";
    }//空の初期値
    Item(String str,int num,PlaceData data){
        text = str;
        item_number = num;
        placeData = data;
        view_text = "";
    }
    Item(String str,int num,PlaceData data,String viewtxt){
        text = str;
        item_number = num;
        placeData = data;
        view_text = viewtxt;
    }
    public void set(String viewtxt){
        view_text = viewtxt;
    }
    public void add(String str,int num,PlaceData data){
        text = str;
        item_number = num;
        placeData = data;
    }
    public void add(String str){
        text = str;
    }
    public void add(int num){
        item_number = num;
    }
    public void add(PlaceData data){
        placeData = data;
    }
    public String getText(){
        return text;
    }
    public int getItem_number(){
        return item_number;
    }
    public PlaceData getPlaceData() {
        return placeData;
    }
}
