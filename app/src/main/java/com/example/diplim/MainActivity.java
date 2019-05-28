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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplim.CustomListViews.CAdapterSessions;
import com.example.diplim.CustomListViews.DataModel;
import com.example.diplim.dbModels.ClassPost;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private String TOKEN;

    private boolean nocon;
    private TextView date_tv;
    private ImageView dateB;
    private Spinner subjectSpinner;
    private EditText themeEd;
    private static CAdapterSessions adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<String> subjectList = new ArrayList<>();
    private ArrayList<DataModel> classesList = new ArrayList<>();

    private JSONPlaceHolderAPI jsonPlaceHolderAPI;
    private final API api = new API();

    public Calendar Timecal = Calendar.getInstance();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeXML();

        Bundle args = getIntent().getExtras();
        if (args!=null){
            nocon = args.getBoolean("nocon");
            ProfID = args.getInt("idProf");
            TOKEN = args.getString("token");
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
            System.out.println("CONNECT EST: " + TOKEN);
            readClassesbyProf(jsonPlaceHolderAPI, 1);
        }else {
            classesList.add(new DataModel(1,"TEST","TEST1", "TEST2"));
            adapter = new CAdapterSessions(classesList, getApplicationContext());
            listView.setAdapter(adapter);
        }

        subjectList = api.readSubjects(jsonPlaceHolderAPI, TOKEN);
       // api.readGroups(jsonPlaceHolderAPI);
       // api.readProf(jsonPlaceHolderAPI);
        //api.deleteGroup(jsonPlaceHolderAPI, 22);
        //api.deleteSubject(jsonPlaceHolderAPI,71);
        //api.putGroups(jsonPlaceHolderAPI, 22, "505", "Tf");
        //api.putSubjects(jsonPlaceHolderAPI, 71, "Test");
        //api.createSubject(jsonPlaceHolderAPI, "Prikolchik");
        //api.createGroup(jsonPlaceHolderAPI,"404", "gg");

        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel = dataModels.get(position);
                boolean deleteSuccessful = new TableControllerClasses(context).delete(dataModel.getId());
                if (deleteSuccessful){
                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(context, "Smth wrong", Toast.LENGTH_SHORT);
                }
                ((MainActivity) context).readDBRecords();
                return false;
            }
        });*/

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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //readDBRecords();
                        //subjectList = api.readSubjects(jsonPlaceHolderAPI);
                        readClassesbyProf(jsonPlaceHolderAPI, 3);
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
                    themeEd.setError("Обязаельное поле");
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

    public String convertStreamToString(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line).append("\n");
        } catch (IOException e){
            System.out.println("-----------Converting Error------");
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    private void readClassesbyProf(JSONPlaceHolderAPI jsonPlaceHolderAPI, int professor_id){
        System.out.println("-----------"+TOKEN);
        Call<List<ClassPost>> call = jsonPlaceHolderAPI.getClassByProfessor("Bearer "+TOKEN,professor_id);
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
                    list.add(new ClassPost(
                            classPost.getId(), classPost.getClass_date(),
                            classPost.getSubject(), classPost.getProfessor(),
                            classPost.getTheme(), classPost.getGroups()));
                }
                classesList = convertTask(list);
                adapter = new CAdapterSessions(classesList, getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ClassPost>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    private ArrayList<DataModel> convertTask(ArrayList<ClassPost> classPosts){
        ArrayList<DataModel> data = new ArrayList<>();
        if (classPosts.size()>0){
            for (ClassPost classPost : classPosts){
                int id = classPost.getId();
                String classTheme = classPost.getTheme();
                String classSubject = classPost.getSubject();
                String[] strings = classPost.getClass_date().split("T");
                String classDate = strings[0];
                //String classDate = classPost.getClass_date();
                data.add(new DataModel(id, classSubject, classTheme, classDate));
            }
        }
        return data;
    }
}
