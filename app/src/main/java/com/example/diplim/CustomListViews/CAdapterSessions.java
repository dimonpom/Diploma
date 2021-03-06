package com.example.diplim.CustomListViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.diplim.R;
import com.example.diplim.dbModels.DataModel;

import java.util.ArrayList;

public class CAdapterSessions extends ArrayAdapter<DataModel>{// {

    private ArrayList<DataModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView tSubject, tTheme, tDate;
    }

    public CAdapterSessions(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    /*@Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        Object object = getItem(pos);
        DataModel dataModel = (DataModel) object;

        *//*switch (v.getId()){
            case R.id.tv_date:*//*
        Toast.makeText(mContext, dataModel.getDate(), Toast.LENGTH_SHORT).show();
                Snackbar.make(v ,"Date: "+ dataModel.getDate(), Snackbar.LENGTH_SHORT).setAction("No Action", null).show();
                *//*break;
        }*//*
    }*/


    private int lastPosition = -1;

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        DataModel dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView ==null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView =inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.tDate = convertView.findViewById(R.id.tV_date);
            viewHolder.tTheme = convertView.findViewById(R.id.tv_group);
            viewHolder.tSubject = convertView.findViewById(R.id.tv_studName);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation( mContext,
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top );
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.tDate.setText(dataModel.getDate());
        viewHolder.tSubject.setText(dataModel.getSubject());
        viewHolder.tTheme.setText(dataModel.getTheme());
        return convertView;
    }
}
