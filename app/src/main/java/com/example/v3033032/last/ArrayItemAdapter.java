package com.example.v3033032.last;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.List;

public class ArrayItemAdapter extends ArrayAdapter<Item> {

    public ArrayItemAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects) {
        super(context, resource, objects);
    }
}
