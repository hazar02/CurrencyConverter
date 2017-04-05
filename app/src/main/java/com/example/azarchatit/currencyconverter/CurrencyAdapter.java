package com.example.azarchatit.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by azar on 03/04/2017.
 */

public class CurrencyAdapter extends BaseAdapter {
    Context context;
    int flags[];
    String[] currencyNames;
    LayoutInflater inflter;

    public CurrencyAdapter (Context applicationContext, int[] flags, String[] currency_name) {
        this.context = applicationContext;
        this.flags = flags;
        this.currencyNames = currency_name;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.currencyitem, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(flags[i]);
        names.setText(currencyNames[i]);
        return view;
    }
}
