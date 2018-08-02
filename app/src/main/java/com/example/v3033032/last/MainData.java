package com.example.v3033032.last;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//すべてシリアライズで保存可能にする
public class MainData implements Serializable {
    MessageSetData messageSetData;
    AddressData addressData;
    UpdateIntervalData updateIntervalData;
    PlaceData placeData;
    MainData(){
        messageSetData = new MessageSetData();
        addressData = new AddressData();
        updateIntervalData = new UpdateIntervalData();
        placeData = new PlaceData();
    }
}
//データを構造的に用意する

class MessageSetData implements Serializable,Data{
    ArrayList<String> item_name;
    ArrayList<Items> item_data;

    MessageSetData(){
        item_name = new ArrayList<String>();
        item_data = new ArrayList<Items>();
        item_name.add("めっせ～じせっと");
        item_data.add(null);
    }
    @Override
    public void add_data(String add_item, String data) {//新規作成のみ？
        item_name.add(item_name.size()-1,add_item);
        item_data.add(item_data.size()-1,new Items());
    }

    @Override
    public void remove_data(int index) {
        item_name.remove(index);
        item_data.remove(index);

    }
    class Items implements Serializable{
        ArrayList<Item> items;
        Items(){
            items = new ArrayList<Item>();
        }

        void add(String str,int num,PlaceData place){
            items.add(new Item(str,num,place));
        }
        void remove(int index){
            items.remove(index);
        }

    }
}
class AddressData implements Serializable,Data{
    ArrayList<String> item_name;
    ArrayList<String> address;
    AddressData(){
        item_name = new ArrayList<String>();
        address = new ArrayList<String>();
        item_name.add("れんらくさき");
        address.add("-----------");
    }
    public void add_data(String add_item, String add_address){
        item_name.add(item_name.size()-1,add_item);
        address.add(address.size()-1,add_address);
    }
    public void remove_data(int index){
        item_name.remove(index);
        address.remove(index);
    }
}
class UpdateIntervalData implements Serializable,Data{
    ArrayList<String> item_name;
    ArrayList<Integer> interval_time;
    UpdateIntervalData(){
        item_name = new ArrayList<String>();
        interval_time = new ArrayList<Integer>();
        item_name.add("5ふん");
        interval_time.add( 5*60*1000);
        item_name.add("10ふん");
        interval_time.add(10*60*1000);
        item_name.add("30ふん");
        interval_time.add(30*60*1000);
        item_name.add("更新間隔");
        interval_time.add(null);
    }
    public void add_data(String add_item, String add_interval_time){
        item_name.add(item_name.size()-1,add_item);
        interval_time.add(interval_time.size()-1,Integer.parseInt(add_interval_time));
    }
    public void remove_data(int index){
        item_name.remove(index);
        interval_time.remove(index);
    }
}
class PlaceData implements Serializable, Data {
    ArrayList<String> item_name;
    ArrayList<Double> lati,lon;

    PlaceData(){
        item_name = new ArrayList<String>();
        lati = new ArrayList<Double>();
        lon = new ArrayList<Double>();
        item_name.add("追加");
        lati.add(0.0);
        lon.add(0.0);

    }
    @Override
    public void add_data(String add_item, String data) {
        String[] str = data.split(",");
        item_name.add(item_name.size()-1,add_item);
        lati.add(lati.size()-1,Double.parseDouble(str[0]));
        lon.add(lon.size()-1,Double.parseDouble(str[1]));
    }

    @Override
    public void remove_data(int index) {
        item_name.remove(index);
        lati.remove(index);
        lon.remove(index);
    }
}
interface  Data extends Serializable {
    void add_data(String add_item,String data);
    void remove_data(int index);
}