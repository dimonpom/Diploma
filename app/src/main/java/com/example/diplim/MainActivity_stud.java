package com.example.diplim;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.diplim.CustomListViews.CAdapterSessions_stud;
import com.example.diplim.dbModels.ClassStud_get;
import com.example.diplim.dbModels.DataModel_stud;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity_stud extends AppCompatActivity {

    private static final String TAG = "MainActivity_stud ";
    private static CAdapterSessions_stud adapter;
    private ArrayList<DataModel_stud> classesList_stud = new ArrayList<>();

    private ListView listView;
    private String TOKEN;
    private Integer GroupID;
    private int StudID;
    private JSONPlaceHolderAPI jsonPlaceHolderAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stud);
        initializeXML();

        Bundle args = getIntent().getExtras();
        if (args!=null){
            GroupID = args.getInt("idGroup");
            TOKEN = "Bearer "+args.getString("token");
            StudID = args.getInt("idStud");
        }

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        jsonPlaceHolderAPI = retrofit.create(JSONPlaceHolderAPI.class);

        /*classesList_stud.add(new DataModel_stud(1, "TEST", "Test Testov Testovich", "1998-05-03"));
        classesList_stud.add(new DataModel_stud(2, "TEST1", "Test Testov Testovich3", "1998-01-27"));
        classesList_stud.add(new DataModel_stud(13, "TEST2", "Test Testov Testovich1231", "1998-04-03"));*/
        readClassesByStud(jsonPlaceHolderAPI, StudID, TOKEN);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel_stud dataModel_stud =  classesList_stud.get(position);
                Intent intent = new Intent(MainActivity_stud.this, SessionActivity_stud.class);
                intent.putExtra("id", dataModel_stud.getId());
                intent.putExtra("date", dataModel_stud.getDate());
                intent.putExtra("subject", dataModel_stud.getSubject());
                intent.putExtra("theme", dataModel_stud.getProfName());
                intent.putExtra("studID", StudID);
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

    private void readClassesByStud(JSONPlaceHolderAPI jsonPlaceHolderAPI,int student_id, String TOKEN){
        Call<List<ClassStud_get>> call = jsonPlaceHolderAPI.getClassByStudent(TOKEN, student_id);
        call.enqueue(new Callback<List<ClassStud_get>>() {
            @Override
            public void onResponse(Call<List<ClassStud_get>> call, Response<List<ClassStud_get>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "------Not successful response with code: " + response.code()
                            + "\n Response message: "+response.raw());
                    return;
                }
                List<ClassStud_get> classStud_gets = response.body();
                for (ClassStud_get classStud_get : classStud_gets){
                    String[] strings = classStud_get.getClass_date().split("T");
                    String classDate = strings[0];
                    classesList_stud.add(new DataModel_stud(
                            classStud_get.getClass_id(), classStud_get.getSubject(),
                            classStud_get.getProfessor(), classDate
                    ));
                }
                adapter = new CAdapterSessions_stud(classesList_stud, getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ClassStud_get>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });

    }
}
