package com.example.diplim;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.diplim.CustomListViews.CAdapterSessions_stud;
import com.example.diplim.dbModels.DataModel_stud;

import java.util.ArrayList;

public class MainActivity_stud extends AppCompatActivity {

    private static CAdapterSessions_stud adapter;
    private ArrayList<DataModel_stud> classesList_stud = new ArrayList<>();

    private ListView listView;
    private String TOKEN;
    private Integer GroupID;
    private int StudID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stud);
        initializeXML();

        Bundle args = getIntent().getExtras();
        if (args!=null){
            GroupID = args.getInt("idGroup");
            TOKEN = args.getString("token");
            StudID = args.getInt("idStud");
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
                Intent intent = new Intent(MainActivity_stud.this, SessionActivity_stud.class);
                intent.putExtra("id", dataModel_stud.getId());
                intent.putExtra("date", dataModel_stud.getDate());
                intent.putExtra("subject", dataModel_stud.getSubject());
                intent.putExtra("theme", dataModel_stud.getProfName());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel_stud dataModel = classesList_stud.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_stud.this);
                builder.setTitle("Удаление данных")
                        .setMessage("Удалить запись из бд?")
                        .setCancelable(true)
                        .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

        System.out.println("TOKEN"+TOKEN);

    }
    boolean twice;
    @Override
    public void onBackPressed() {
        if (twice){
            Intent intent = new Intent(MainActivity_stud.this, LoginActivity_new.class);
            startActivity(intent);
            finish();
        }

        Toast.makeText(getApplicationContext(), "Нажмите назад еще раз, чтобы выйти", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 3000);
        twice = true;
    }

    private void initializeXML() {
        listView = findViewById(R.id.listview);
    }
}
