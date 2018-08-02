package com.example.v3033032.last;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    //送信用のメッセージと場所を決定する

    Button backButton,addButton,deleteButton;//backButton MainActivityに戻るボタン addButton MainActivity3 に移るボタン deleteButton　データを消す
    ListView listView;
    ArrayList<Item> item_list;
    ArrayList<String>list;
    int requestCode = 2001,change_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        item_list = (ArrayList<Item>) getIntent().getSerializableExtra("data");//Itemのデータをもらう

        listView = (ListView)findViewById(R.id.list_view);

        list = new ArrayList<String>();
        //リストのItemをタップしたときにそのItemを変更するIntentを実行する
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                change_index = position;
                setItemIntent(position);
            }
        });

        //戻るボタン　データを返して向こうで保存させる
        backButton = (Button)findViewById(R.id.button5);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("data",item_list);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        //追加ボタン　新しいデータを追加する
        addButton = findViewById(R.id.button6);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemIntent();
            }
        });
        //データを消去する
        deleteButton = findViewById(R.id.button12);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        //リストを表示
        setText();
    }
    //Itemの情報を書くためのインテント
    void setItemIntent(){
        Intent intent = new Intent(this,Main3Activity.class);
        intent.putExtra("place",getIntent().getSerializableExtra("place"));
        startActivityForResult(intent,requestCode);
    }
    //リストから呼ばれるもの
    void setItemIntent(int position){
        Intent intent = new Intent(this,Main3Activity.class);
        intent.putExtra("place",getIntent().getSerializableExtra("place"));
        startActivityForResult(intent,requestCode+1);
    }

    //リザルト　追加されるものは下に、削除されるときはなくす、何もせずに戻ることもできる
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode == this.requestCode && resultCode == Activity.RESULT_OK){
            item_list.add(new Item(intent.getStringExtra("text"),
                    intent.getIntExtra("num",0),
                    (PlaceData)intent.getSerializableExtra("place")));
            viewText(item_list.get(item_list.size()-1));
            setText();
        }
        if(requestCode == this.requestCode+1){
            if(resultCode == Activity.RESULT_OK) {
                item_list.set(change_index, new Item(intent.getStringExtra("text"),
                        intent.getIntExtra("num", 0),
                        (PlaceData) intent.getSerializableExtra("place")));
                viewText(item_list.get(change_index));
            }else if(resultCode == Activity.RESULT_CANCELED){
                item_list.remove(change_index);
            }
            setText();
        }
    }

    //リストを表示するメソッド
    void setText(){
        list.clear();
        for(int i = 0;i<item_list.size();i++){
            list.add(i,item_list.get(i).view_text);
        }
        ArrayAdapter<String> test = new ArrayAdapter<String>( this,
                android.R.layout.simple_expandable_list_item_1,
                list);
        listView.setAdapter(test);
    }
    //リストに表示するためのStringを作る
    void viewText(Item item){
        String tmp = "";
        tmp = item.getPlaceData().item_name.get(item.getItem_number())
                + "  　付近で  " + item.getText() + "　　と送信";
        item.set(tmp);
    }
}
