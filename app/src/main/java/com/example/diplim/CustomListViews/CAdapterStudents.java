package com.example.diplim.CustomListViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.diplim.R;

import java.util.ArrayList;

public class CAdapterStudents extends ArrayAdapter<StudentsModel> {

    private ArrayList<StudentsModel> studentsData;
    Context context;

    private static class ViewHolder{
        TextView txtName, txtGroup;
    }

    public CAdapterStudents(ArrayList<StudentsModel> studentsData, Context context){
        super(context, R.layout.student_item, studentsData);
        this.studentsData = studentsData;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StudentsModel StudentsModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.student_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.tv_studName);
            viewHolder.txtGroup = (TextView) convertView.findViewById(R.id.tv_group);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.txtName.setText(StudentsModel.getFullName());
        viewHolder.txtGroup.setText(StudentsModel.getFullGroup());

        return convertView;
    }
    }
