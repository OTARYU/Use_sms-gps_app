package com.example.v3033032.last;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

//data 渡される　place　をアダプターに入れる　リストを表示　、追加を抜いておく　追加のみだったら場所を入れてくださいとToastして戻る　（入力させない）
// ItemにPlace,Text,Item_numberをいれて戻る
public class Main3Activity extends AppCompatActivity {

    Button backB, setB,deleteB;
    Spinner place;
    EditText messages;
    PlaceData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        data = (PlaceData) getIntent().getSerializableExtra("place");


        backB = findViewById(R.id.button10);
        setB = findViewById(R.id.button11);
        deleteB = findViewById(R.id.button7);
        place = findViewById(R.id.spinner5);
        messages = findViewById(R.id.editText2);

        changeSpinner(place,data.item_name);



        //戻るときに何もしないということで int 2 をセットしてフィニッシュ
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(2);
                finish();
            }
        });

        //データをセットするボタン　リザルトにRESULT_OKをセットしてフィニッシュ　
        // データをセットする
        setB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("text",messages.getText().toString());
                intent.putExtra("num",place.getSelectedItemPosition());
                intent.putExtra("place",data);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        //リザルトキャンセルを返すとデータを消す
        deleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    void changeSpinner(Spinner spinner,ArrayList<String> str){
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,
                str);
        spinner.setAdapter(ad);
    }

}
