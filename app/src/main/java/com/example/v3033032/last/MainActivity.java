package com.example.v3033032.last;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
//ほぼすべての処理が記述されている
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    Spinner messageSet, sendAddress, updateInterval, place;
    Button messageSetButton, sendAddressButton, updateIntervalButton, placeButton;
    PopupWindow popupWindow;
    View popupView, anchor;
    TextView popupMessage;
    EditText popupEditText;
    Switch aSwitch;

    boolean[] boolList;    //メッセージを送信したかどうかを保存する配列

    String tmpStr;                  //popup windowでつかう
    int tmpCount, eventNum;         //event method の時に使う

    boolean timerRunning = false;       //timerが走っているかどうか

    Global g;
    private GoogleMap mMap;
    private LocationManager manager;
    LatLng latLng;              //gpsでとった値を格納
    LatLng location;            //マーカー置くための位置情報
    SupportMapFragment mapFragment;
    Marker maker,myPosition;
    MainData mainData;      //保存情報を記録するクラス
    Timer timer;

    int requestCode;                        //intent を動かすときのリクエストコード
    boolean bool1, bool2, bool3, bool4;   //スピナーが追加かどうか確認するboolean

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mainData を設定する
        try (ObjectInputStream ois = new ObjectInputStream(openFileInput("MainData.txt"))) {
            //main dataが保存されていたら呼び出す
            mainData = (MainData) ois.readObject();
        } catch (FileNotFoundException e) {
            //なければ　NEWする
            mainData = new MainData();
            mainData.placeData.add_data("岐阜駅","35.409717,136.756965");
            mainData.placeData.add_data("忠節橋","35.429769,136.750371");
            mainData.placeData.add_data("マーサ21","35.447973,136.745913");
            mainData.placeData.add_data("岐阜大宇","35.462533,136.736131");
            mainData.placeData.add_data("名古屋駅","35.170587,136.881848");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        timer = new Timer();

        popupView = getLayoutInflater().inflate(R.layout.popup_window_layout, null);//popup viewを作った


        popupWindow = new PopupWindow(popupView, 800, 800);
        popupMessage = popupWindow.getContentView().findViewById(R.id.textView);
        popupEditText = popupWindow.getContentView().findViewById(R.id.editText);

        // タップ時に他のViewでキャッチされないための設定  http://uchida001tmhr.hatenablog.com/entry/2016/05/14/093907
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);


        g = (Global) this.getApplication();
        messageSet = (Spinner) findViewById(R.id.spinner);
        sendAddress = (Spinner) findViewById(R.id.spinner2);
        updateInterval = (Spinner) findViewById(R.id.spinner3);
        place = (Spinner) findViewById(R.id.spinner4);
        messageSetButton = (Button) findViewById(R.id.button);
        sendAddressButton = (Button) findViewById(R.id.button2);
        updateIntervalButton = (Button) findViewById(R.id.button3);
        placeButton = (Button) findViewById(R.id.button4);
        aSwitch = (Switch) findViewById(R.id.switch2);

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);

        latLng = new LatLng(0.0,0.0);

        //spinner を更新
        changeSpinner(messageSet, mainData.messageSetData.item_name);
        changeSpinner(sendAddress, mainData.addressData.item_name);
        changeSpinner(updateInterval, mainData.updateIntervalData.item_name);
        changeSpinner(place, mainData.placeData.item_name);

        //戻るボタン　popup window　を消す
        popupView.findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        //決定ボタン　
        popupView.findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (eventNum) {
                    case 1:
                        mainData.messageSetData.add_data(popupEditText.getText().toString(), "");
                        saveData();
                        popupWindow.dismiss();
                        changeSpinner(messageSet, mainData.messageSetData.item_name);
                        popupEditText.setText("Name");
                        break;
                    case 2:
                        event("電話番号", mainData.addressData);
                        break;
                    case 3:
                        event("時間[ms]", mainData.updateIntervalData);
                        break;
                    case 4:
                        event(mainData.placeData);
                        break;
                    case 5:
                        removeEvent();
                        break;
                }
            }
        });
//spinner の　listener boolに今追加モード化それ以外かを入れる
        messageSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bool1 = (position == mainData.messageSetData.item_name.size() - 1);
                if (bool1) messageSetButton.setText("追加");
                else messageSetButton.setText("編集");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bool2 = (position == mainData.addressData.item_name.size() - 1);
                if (bool2) sendAddressButton.setText("追加");
                else sendAddressButton.setText("削除");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        updateInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bool3 = (position == mainData.updateIntervalData.item_name.size() - 1);
                if (bool3) updateIntervalButton.setText("追加");
                else {
                    updateIntervalButton.setText("削除");
                    //スピナーのセレクティッドを変更するたびにgpsとタイマーの変更時間を変える
                    g.setMinTime(mainData.updateIntervalData.interval_time.get(position));
                    if(timerRunning) {
                        setLocationManager();
                        stop();
                        start();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bool4 = (position == mainData.placeData.item_name.size() - 1);
                if (bool4) placeButton.setText("追加");
                else placeButton.setText("削除");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //スイッチ　オンにすると　boolList にメッセージ分のリストをNewする　gps,timerをスタートさせる
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(!bool1) {
                        setLocationManager();
                        boolList = new boolean[mainData.messageSetData.item_data.get(messageSet.getSelectedItemPosition()).items.size()];
                        for (int i = 0; i < mainData.messageSetData.item_data.get(messageSet.getSelectedItemPosition()).items.size(); i++) {
                            boolList[i] = true;
                        }
                        start();
                    }
                } else {
                    stop();
                }
            }
        });

        //button のリスナ
        messageSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bool1) {
                    tmpCount = 0;
                    eventNum = 1;
                    anchor = v;

                    popupMessage.setText("名前");
                    popupWindow.dismiss();
                    popupWindow.setHeight(popupWindow.getMaxAvailableHeight(anchor) / 3);
                    popupWindow.showAsDropDown(anchor);
                } else {
                    setMassageSetDateIntent();
                }
            }
        });
        sendAddressButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (bool2) {
                    tmpCount = 1;
                    eventNum = 2;
                    anchor = v;

                    popupMessage.setText("宛先名");
                    popupWindow.dismiss();
                    popupWindow.setHeight(popupWindow.getMaxAvailableHeight(anchor) / 3);
                    popupWindow.showAsDropDown(anchor);
                } else {
                    mainData.addressData.remove_data(sendAddress.getSelectedItemPosition());
                    changeSpinner(sendAddress, mainData.addressData.item_name);
                }
            }
        });
        updateIntervalButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (bool3) {
                    tmpCount = 1;
                    eventNum = 3;
                    anchor = v;

                    popupMessage.setText("名前");
                    popupWindow.dismiss();
                    popupWindow.setHeight(popupWindow.getMaxAvailableHeight(anchor) / 3);
                    popupWindow.showAsDropDown(anchor);
                } else {
                    mainData.updateIntervalData.remove_data(updateInterval.getSelectedItemPosition());
                    changeSpinner(updateInterval, mainData.updateIntervalData.item_name);
                }
            }
        });
        placeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (bool4) {
                    tmpCount = 1;
                    eventNum = 4;
                    anchor = v;

                    popupMessage.setText("名前");
                    popupWindow.dismiss();
                    popupWindow.setHeight(popupWindow.getMaxAvailableHeight(anchor) / 3);
                    popupWindow.showAsDropDown(anchor);
                } else {
                    tmpCount = 0;
                    eventNum = 5;
                    anchor = v;

                    popupMessage.setText("削除するには yを入力");
                    popupWindow.dismiss();
                    popupWindow.setHeight(popupWindow.getMaxAvailableHeight(anchor) / 3);
                    popupWindow.showAsDropDown(anchor);
                }
            }
        });


        //TODO 権限がないと落ちる 落ちなくなった? やっぱり落ちる　SEND＿SMSのPermissionがないと落ちる模様
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {//拒否されたことがあればこちら
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 0);
            } else {//なければこっち
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 0);
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {//拒否されたことがあればこちら
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {//なければこっち
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }

    //画面遷移
    void setMassageSetDateIntent() {
        Intent intent = new Intent(this, Main2Activity.class);
        requestCode = 1001;
        intent.putExtra("place", mainData.placeData);
        intent.putExtra("data", mainData.messageSetData.item_data.get(messageSet.getSelectedItemPosition()).items);
        startActivityForResult(intent, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                mainData.messageSetData.item_data.get(messageSet.getSelectedItemPosition()).items = (ArrayList<Item>) intent.getSerializableExtra("data");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mainData.messageSetData.remove_data(messageSet.getSelectedItemPosition());
                changeSpinner(messageSet, mainData.messageSetData.item_name);
            }
            saveData();
        }
    }

    //popup windowから呼ばれるイベント
    // データを追加する　その後　スピナーを更新する
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void event(String str, Data data) {
        if (tmpCount == 1) {
            tmpStr = popupEditText.getText().toString();
            popupWindow.dismiss();
            popupMessage.setText(str);
            popupEditText.setText("Name");
            popupWindow.showAsDropDown(anchor);
            tmpCount--;
        } else {

            popupWindow.dismiss();
            data.add_data(tmpStr, popupEditText.getText().toString());
            saveData();
            switch (eventNum) {
                case 2:
                    changeSpinner(sendAddress, mainData.addressData.item_name);
                    break;
                case 3:
                    changeSpinner(updateInterval, mainData.updateIntervalData.item_name);
                    break;
                case 4:
                    changeSpinner(place, mainData.placeData.item_name);
                    break;
            }
            popupEditText.setText("Name");
        }
    }

    //4用のイベント
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void event(Data data) {

        popupWindow.dismiss();
        data.add_data(popupEditText.getText().toString(), Double.toString(g.getLati()) + ","
                + Double.toString(g.getLon()));
        saveData();
        changeSpinner(place, mainData.placeData.item_name);
        popupEditText.setText("Name");
    }
    void removeEvent(){//4を消すときに確認を追加する
        popupWindow.dismiss();
        if(popupEditText.getText().toString().equals("y")){
            mainData.placeData.remove_data(place.getSelectedItemPosition());
            changeSpinner(place, mainData.placeData.item_name);
        }
        popupEditText.setText("Name");
    }

//smsを送るためのメソッド群
    void smsSender(int i, Item item) {//送ったメッセージかどうかを確認
        if (boolList[i]) {
            checkSMSPermission(mainData.addressData.address.get(sendAddress.getSelectedItemPosition()),item.getText());
            //メッセージテキストとアドレスを設定し、SMSを送る
            boolList[i] = false;
        }
    }
    void checkSMSPermission(String address, String text) {//permission　をチェックしてsmsを送るためのメソッドにわたす
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSms(address, text);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {//拒否されたことがあればこちら
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 0);
            } else {//なければこっち
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 0);
            }
        }
    }
    void sendSms(String address, String text) {
        SmsManager s = SmsManager.getDefault();
        s.sendTextMessage(
                address,// +1 555 521 5554
                null,
                text,
                null,
                null
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                    }
                }
                break;
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(this);
                        }
                        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    }
                }
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.6811673,139.745433),15));

        //クリックリスナー　マーカーをクリックしたところに置く
        // このマーカーの位置をボタン4でつかう
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //method of clicked map
                if (maker != null) {
                    maker.remove();
                }

                location = new LatLng(latLng.latitude, latLng.longitude);
                g.setLati(latLng.latitude);
                g.setLon(latLng.longitude);
                maker = mMap.addMarker(new MarkerOptions().position(location).title(String.format(Locale.US, "%f, %f", latLng.latitude, latLng.longitude)));
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {//自分の位置をlatlngに、マーカーをMypositionにつける
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (myPosition != null) {
            myPosition.remove();
        }
        myPosition = mMap.addMarker(new MarkerOptions().position(latLng).title(String.format(Locale.US, "%f, %f", latLng.latitude, latLng.longitude)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
//スピナーを変更するためのメソッド
    void changeSpinner(Spinner spinner, ArrayList<String> str) {
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,
                str);
        spinner.setAdapter(ad);
    }

    //managerを設定する
    void setLocationManager() {
        if(manager != null)manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mapFragment != null) {
            }

            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, g.getMinTime(), 10, this);

        }

    }

    //TimerTaskのかわり　メッセージの登録地点との差を求めてメール送信制御メソッドに投げる
    void control() {
        if(messageSet.getSelectedItemPosition() == mainData.messageSetData.item_name.size()-1){

        }else {//messageDataSetにあるメッセージの分だけforを回し、現在地との誤差を求める

            for(int i = 0;i < mainData.messageSetData.item_data.get(messageSet.getSelectedItemPosition()).items.size();i++) {
                Item item = mainData.messageSetData.item_data.get(messageSet.getSelectedItemPosition())
                        .items.get(i);
                float[] res = new float[1];
                Location.distanceBetween(latLng.latitude,latLng.longitude
                        ,item.getPlaceData().lati.get(item.getItem_number())
                        ,item.getPlaceData().lon.get(item.getItem_number()),res);
                if(res[0]<500){//半径 0.5km　に入ったら　メールを送る制御
                    smsSender(i,item);
                }
            }
        }
    }
    //timerをとめる
    void stop(){
        timerRunning = false;
        timer.cancel();
        timer = new Timer();
    }
    //timerを始める
    void start(){
        timerRunning = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                control();
            }
        }, 100, g.minTime);
    }

    //dataを書き出す
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void saveData(){
        try (ObjectOutputStream oos = new ObjectOutputStream(openFileOutput("MainData.txt",Context.MODE_PRIVATE))) {
            oos.writeObject(mainData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//https://blog.integrityworks.co.jp/2016/11/21/post-2658/ permission
// https://qiita.com/izuki_y/items/925718fa9994204fa937 map
// https://techbooster.org/android/application/939/ serialize