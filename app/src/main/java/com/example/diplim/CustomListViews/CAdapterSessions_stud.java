package com.example.diplim.CustomListViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.diplim.R;
import com.example.diplim.dbModels.DataModel_stud;

import java.util.ArrayList;

public class CAdapterSessions_stud extends ArrayAdapter<DataModel_stud> {

    private ArrayList<DataModel_stud> dataSet;
    Context mContext;

    private static class ViewHolder1{
        TextView tSubject, tProfname, tDate;
    }

    public CAdapterSessions_stud(ArrayList<DataModel_stud> dataModel_studs, Context context){
        super(context, R.layout.row_item_stud, dataModel_studs);

    }

    private int lastPos = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataModel_stud dataModel_stud =getItem(position);
        ViewHolder1 viewHolder1;
        final View result;

        if (convertView == null){
            viewHolder1 = new ViewHolder1();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_stud, parent, false);
            viewHolder1.tDate = convertView.findViewById(R.id.tV_date);
            viewHolder1.tProfname = convertView.findViewById(R.id.tv_profName);
            viewHolder1.tSubject = convertView.findViewById(R.id.tv_subject);

            result = convertView;
            convertView.setTag(viewHolder1);
        }else{
            viewHolder1 = (ViewHolder1) convertView.getTag();
            result = convertView;
        }
        viewHolder1.tDate.setText(dataModel_stud.getDate());
        viewHolder1.tSubject.setText(dataModel_stud.getSubject());
        viewHolder1.tProfname.setText(dataModel_stud.getProfName());
        return convertView;
    }
}
