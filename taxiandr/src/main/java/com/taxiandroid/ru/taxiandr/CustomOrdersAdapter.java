package com.taxiandroid.ru.taxiandr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by saperov on 28.10.15.
 */

public class CustomOrdersAdapter extends ArrayAdapter<Orders> {
    public CustomOrdersAdapter (Context context,ArrayList<Orders> orders){
        super(context,0,orders);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
    Orders order = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        TextView tvAdres = (TextView) convertView.findViewById(R.id.tvAdres);
        TextView tvSroch = (TextView) convertView.findViewById(R.id.tvSroch);
        TextView tvIndiv = (TextView) convertView.findViewById(R.id.tvIndiv);

        tvAdres.setText((CharSequence) order.adres);
        tvSroch.setText((CharSequence) order.sroch);
        tvIndiv.setText((CharSequence) order.indiv);


        return convertView;
    }
}
