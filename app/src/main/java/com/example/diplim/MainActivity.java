package com.example.diplim;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplim.CustomListViews.CAdapterSessions;
import com.example.diplim.dbModels.DataModel;
import com.example.diplim.dbModels.ClassPost;
import com.example.diplim.dbModels.Group;
import com.example.diplim.dbModels.SpinnerWithID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    final Context context = this;

    private static final String TAG = "MainACtivity";

    private int ProfID;

    private boolean nocon;
    private TextView date_tv;
    private ImageView dateB;
    private Spinner subjectSpinner, spinner_groups;
    private EditText themeEd, tv_groups_add, tv_subgroups_add;
    private static CAdapterSessions adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String TOKEN = null;

    private ArrayList<String> subjectList = new ArrayList<>();
    private ArrayList<DataModel> classesList = new ArrayList<>();
    private ArrayAdapter<SpinnerWithID> groupsAdapter;
    private List<SpinnerWithID> group_list;


    private JSONPlaceHolderAPI jsonPlaceHolderAPI;
    private final API api = new API();

    public Calendar Timecal = Calendar.getInstance();

    ListView listView;

    boolean twice;
    @Override
    public void onBackPressed() {
        if (twice){
            Intent intent = new Intent(MainActivity.this, LoginActivity_new.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeXML();


        Bundle args = getIntent().getExtras();
        if (args!=null){
            nocon = args.getBoolean("nocon");
            ProfID = args.getInt("idProf");
            TOKEN = "Bearer "+args.getString("token");
        }

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        jsonPlaceHolderAPI = retrofit.create(JSONPlaceHolderAPI.class);
        if (!nocon) {
            readClassesbyProf(jsonPlaceHolderAPI, 1, TOKEN);
        }else {
            classesList.add(new DataModel(1,"Предмет","Тема", "10-03-1998"));
            classesList.add(new DataModel(1,"Предмет2","Тема2", "12-04-1998"));
            adapter = new CAdapterSessions(classesList, getApplicationContext());
            listView.setAdapter(adapter);
        }
        readGroups(jsonPlaceHolderAPI);
        subjectList = api.readSubjects(jsonPlaceHolderAPI, TOKEN);

        groupsAdapter = new ArrayAdapter<SpinnerWithID>(this, R.layout.spinner_item, group_list);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel = classesList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel = classesList.get(position);
                System.out.println(dataModel.getSubject()+" "+dataModel.getTheme());
                Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                intent.putExtra("id", dataModel.getId());
                intent.putExtra("date", dataModel.getDate());
                intent.putExtra("subject", dataModel.getSubject());
                intent.putExtra("theme", dataModel.getTheme());
                startActivity(intent);
            }
        });

        //----------------------Потягуси вниз
        final String finalTOKEN = TOKEN;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readClassesbyProf(jsonPlaceHolderAPI, 1, finalTOKEN);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });
    }

    private void initializeXML() {
        listView = findViewById(R.id.listview);
        swipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Timecal.set(Calendar.YEAR, year);
            Timecal.set(Calendar.MONTH, month);
            Timecal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date_tv.setText(GetDateToStr(Timecal));
        }
    };

    private String GetDateToStr(Calendar c1) {
        String string;
        string = c1.get(Calendar.YEAR)+"/"
                +( (c1.get(Calendar.MONTH)+1)<10 ? ( "0"+(c1.get(Calendar.MONTH)+1) ) : ((c1.get(Calendar.MONTH)+1)) )+"/"
                +( c1.get(Calendar.DAY_OF_MONTH)<10 ? ( "0"+c1.get(Calendar.DAY_OF_MONTH) ) : (c1.get(Calendar.DAY_OF_MONTH)) );
        return string;
    }

    public void setDate(View view) {
        new DatePickerDialog(MainActivity.this, d,
                Timecal.get(Calendar.YEAR),
                Timecal.get(Calendar.MONTH),
                Timecal.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    public void addGroup(final MenuItem item) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.add_group_form, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Добавление группы")
                .setView(view)
                .setPositiveButton("Добавить", null);
        tv_groups_add = view.findViewById(R.id.et_group);
        tv_subgroups_add = view.findViewById(R.id.et_subgroup);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean opened = true;
                String group = String.valueOf(tv_groups_add.getText());
                String subgroup = String.valueOf(tv_subgroups_add.getText());
                if (group.equals("")){
                    tv_groups_add.setError("Поле не может быть пустым");
                }if (subgroup.equals("")){
                    tv_subgroups_add.setError("Поле не может быть пустым");
                }else {
                    opened = false;
                }

                if (!opened){
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void deleteGroup(MenuItem item) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.delet_group_form, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Удаление группы")
                .setView(view);
        spinner_groups = view.findViewById(R.id.spinner_groups);

        spinner_groups.setAdapter(groupsAdapter);

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpinnerWithID spinnerWithID = (SpinnerWithID) spinner_groups.getSelectedItem();
                deleteGroup(jsonPlaceHolderAPI, spinnerWithID.group_id, TOKEN);
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void Add_Action(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.lesson_input_form, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Добавление занятия")
                .setView(promptsView)
                .setPositiveButton("Создать", null);
        subjectSpinner = promptsView.findViewById(R.id.spinner_subject);
        dateB = promptsView.findViewById(R.id.iV1);
        date_tv = promptsView.findViewById(R.id.date_tv);
        themeEd = promptsView.findViewById(R.id.editText);
        date_tv.setText(GetDateToStr(Timecal));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, subjectList);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        subjectSpinner.setAdapter(spinnerAdapter);

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean opened = true;
                String RqYear = String.valueOf(Timecal.get(Calendar.YEAR));
                String RqMonth = String.valueOf(Timecal.get(Calendar.MONTH)+1);
                String RqDay = ( Timecal.get(Calendar.DAY_OF_MONTH)<10 ?  "0"+Timecal.get(Calendar.DAY_OF_MONTH)  : Timecal.get(Calendar.DAY_OF_MONTH) )+"";

                String theme = String.valueOf(themeEd.getText());
                String subject = String.valueOf(subjectSpinner.getSelectedItem());
                String date = RqYear+"/"+RqMonth+"/"+RqDay;

                if (theme.equals("")) {
                    themeEd.setError("Обязательное поле");
                    themeEd.requestFocus();
                }else {
                    //Отправляем на сервер

                    opened = false;
                }

                if (!opened){
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void readClassesbyProf(JSONPlaceHolderAPI jsonPlaceHolderAPI, int professor_id, String TOKEN){
        System.out.println("-----------"+TOKEN);
        Call<List<ClassPost>> call = jsonPlaceHolderAPI.getClassByProfessor(TOKEN,professor_id);
        final ArrayList<ClassPost> list = new ArrayList<>();
        call.enqueue(new Callback<List<ClassPost>>() {
            @Override
            public void onResponse(Call<List<ClassPost>> call, Response<List<ClassPost>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "------Not successful response with code: " + response.code()
                            + "\n Response message: "+response.raw());
                    return;
                }
                List<ClassPost> classPosts = response.body();

                for (ClassPost classPost : classPosts){

                    String[] strings = classPost.getClass_date().split("T");
                    String classDate = strings[0];
                    classesList.add(new DataModel(
                            classPost.getId(), classPost.getSubject(), classPost.getTheme(), classDate));
                }
                adapter = new CAdapterSessions(classesList, getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ClassPost>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    void deleteGroup(JSONPlaceHolderAPI jsonPlaceHolderAPI, int groupID, String Token){
        Call<Void> call = jsonPlaceHolderAPI.deleteGroup(Token,groupID);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: " + response.code()
                            + "\n Response message: "+response.raw());
                    return;
                }
                Toast.makeText(getApplicationContext(), "Успешно удалено", Toast.LENGTH_SHORT).show();
                group_list.remove(spinner_groups.getSelectedItem());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readGroups(JSONPlaceHolderAPI jsonPlaceHolderAPI){
        group_list = new ArrayList<SpinnerWithID>();
        Call<List<Group>> call = jsonPlaceHolderAPI.getGroups();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }
                List<Group> groups = response.body();
                for (Group group : groups){
                    group_list.add(new SpinnerWithID(group.getGroup_name()+" "+group.getSubgroup(), group.getGroup_id()));
                }
                groupsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });
    }


}
