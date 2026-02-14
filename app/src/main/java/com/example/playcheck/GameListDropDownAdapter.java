package com.example.playcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/*
This class is used to bind an array of data (filter options) to the Game list drop down filter. Each row in this adapter is one option row object of type String
 */
public class GameListDropDownAdapter extends ArrayAdapter<String> {
    public GameListDropDownAdapter(Context context, ArrayList<String> options){
        super(context, 0, options);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //this is the value shown on the spinner before user chooses an option
        return initialView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //show the filter options after use has pressed the spinner
        return initialView(position, convertView, parent);
    }

    /* helper method that return the View. Methods getView and getDropDownView use the same initial view so this method reduces repetition.
    * Parameter convertView is the View we want to recycle. If there is no view to reuse, create a new one.*/
    private View initialView(int position, View convertView, @NonNull ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gamelist_dropdown_filter_item, parent,false);
        }
        TextView option = convertView.findViewById(R.id.gamelistDropDownOption);

        String filterOption = getItem(position);

        option.setText(filterOption);

        return convertView;
    }
}
