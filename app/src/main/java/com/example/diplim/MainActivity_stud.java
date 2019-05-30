package com.example.diplim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.diplim.CustomListViews.CAdapterSessions_stud;
import com.example.diplim.CustomListViews.DataModel_stud;
import com.example.diplim.dbModels.Group;

import java.util.ArrayList;

public class MainActivity_stud extends AppCompatActivity {

    private static CAdapterSessions_stud adapter;
    private ArrayList<DataModel_stud> classesList_stud = new ArrayList<>();

    private ListView listView;
    private String TOKEN;
    private Integer GroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stud);
        initializeXML();

        Bundle args = getIntent().getExtras();
        if (args!=null){
            GroupID = args.getInt("idGroup");
            TOKEN = args.getString("token");
        }

        classesList_stud.add(new DataModel_stud(1, "TEST", "Test Testov Testovich", "1998-05-03"));
        classesList_stud.add(new DataModel_stud(1, "TEST1", "Test Testov Testovich3", "1998-01-27"));
        classesList_stud.add(new DataModel_stud(1, "TEST2", "Test Testov Testovich1231", "1998-04-03"));
        adapter = new CAdapterSessions_stud(classesList_stud, getApplicationContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel_stud dataModel_stud =  classesList_stud.get(position);

            }
        });

        System.out.println(TOKEN);
    }

    private void initializeXML() {
        listView = findViewById(R.id.listview);
    }
}
