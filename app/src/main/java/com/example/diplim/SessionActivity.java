package com.example.diplim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplim.CustomListViews.CAdapterStudents;
import com.example.diplim.dbModels.Answer_answer;
import com.example.diplim.dbModels.Answer_post;
import com.example.diplim.dbModels.Question_answer;
import com.example.diplim.dbModels.Question_post;
import com.example.diplim.dbModels.StudClass_get;
import com.example.diplim.dbModels.StudentsModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionActivity extends AppCompatActivity {

    ArrayList<StudentsModel> studentsModels;

    final Context context = this;
    private static final String TAG="SessionActivity";

    private String TOKEN;
    private int LESSON_ID;

    private EditText ed_question, ed_ans1, ed_ans2, ed_ans3, ed_ans4, ed_path;
    private TextView tv_subject, tv_date, tv_question_asked;
    private ListView listView;
    private PieChart pieChart;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String exSubject, exTheme, exDate;
    private static CAdapterStudents adapterStudents;
    private Socket socket;
    private JSONPlaceHolderAPI jsonPlaceHolderAPI;
    final ArrayList<String> color = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        color.add("#4EB0E0");
        color.add("#5D6DE5");
        color.add("#49E78D");
        color.add("#FFAE50");

        Bundle args = getIntent().getExtras();
        if (args!=null){
            exSubject = args.getString("subject");
            exTheme = args.getString("theme");
            exDate = args.getString("date");
            TOKEN = args.getString("token");
            LESSON_ID = args.getInt("id");
            System.out.println("------------"+LESSON_ID);
        }

        initializeXML();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolderAPI = retrofit.create(JSONPlaceHolderAPI.class);

        socketConnect();

        tv_subject.setText(exSubject);
        tv_date.setText(exDate);

        listView = findViewById(R.id.lv_students);
        studentsModels = new ArrayList<>();
        /*studentsModels.add(new StudentsModel("Петрович Петров Рофлов","321 Б"));
        studentsModels.add(new StudentsModel("Веселов Гурий Ефимович","321 Б"));
        studentsModels.add(new StudentsModel("Молчанов Алан Алексеевич","321 Б"));
        studentsModels.add(new StudentsModel("Журавлёв Тимур Улебович","321 А"));
        studentsModels.add(new StudentsModel("Кулагин Бенедикт Юлианович","321 А"));
        studentsModels.add(new StudentsModel("Красильников Пантелеймон Эльдарович","321 А"));
        studentsModels.add(new StudentsModel("Ткаченко Жерар Владимирович","321 Б"));
        studentsModels.add(new StudentsModel("Крылов Юрий Валерьевич","321 Б"));
        studentsModels.add(new StudentsModel("Поляков Роберт Алексеевич","321 Б"));
        studentsModels.add(new StudentsModel("Яловой Владлен Григорьевич","321 А"));
        studentsModels.add(new StudentsModel("Воронцов Ленар Анатолиевич","321 А"));
        studentsModels.add(new StudentsModel("Савин Иван Васильевич","321 А"));
        studentsModels.add(new StudentsModel("Осипов Матвей Сергеевич","321 Б"));
        studentsModels.add(new StudentsModel("Пилипейко Донат Владимирович","321 А"));
        studentsModels.add(new StudentsModel("Стегайло Гордей Анатолиевич","321 А"));
        studentsModels.add(new StudentsModel("Селезнёв Пётр Романович","321 Б"));*/

        /*pieChart.addPieSlice(new PieModel("Понравилось", 20, Color.parseColor(color.get(1))));
        pieChart.addPieSlice(new PieModel("Скорее понравилось", 12, Color.parseColor(color.get(2))));
        pieChart.addPieSlice(new PieModel("Больше не понравилось, чем понравилось", 3, Color.parseColor(color.get(3))));
        pieChart.addPieSlice(new PieModel("Не понравилось", 6, Color.parseColor(color.get(0))));*/
        /*tv_question_asked.setText("Понравилось ли вам занятие?");*/
        getStudentsPresent(jsonPlaceHolderAPI, LESSON_ID);
        adapterStudents = new CAdapterStudents(studentsModels, getApplicationContext());
        listView.setAdapter(adapterStudents);
       // updateGraph();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStudentsPresent(jsonPlaceHolderAPI, LESSON_ID);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_session, menu);
        return true;
    }


    public void importExcel(MenuItem item) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.file_save_form, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Сохранение файла")
                .setView(promptsView)
                .setPositiveButton("Сохранить", null);
        ed_path = promptsView.findViewById(R.id.eT_path);
        ed_path.setText("Журнал посещений за "+exDate);
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
                String path = String.valueOf(ed_path.getText())+".xlsx";
                if (path.equals("")) {
                    ed_path.setError("Путь не может быть пустым");
                    ed_path.requestFocus();
                }else {
                    if (saveExcelFile(path, exDate));
                        opened = false;
                }
                if (!opened)
                    alertDialog.dismiss();
            }
        });

    }


    private boolean saveExcelFile(String fileName, String date){
        Workbook workbook = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cs = workbook.createCellStyle();
        cs.setFillBackgroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        Sheet sheet1;
        sheet1 = workbook.createSheet("Список студентов за "+date);

        Row row = sheet1.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Имя студента");
        cell = row.createCell(1);
        cell.setCellValue("Группа");

        for (int i=0; i<studentsModels.size(); i++){
            StudentsModel studentsModel = studentsModels.get(i);

            Row row1 = sheet1.createRow(i+1);
            cell = row1.createCell(0);
            cell.setCellValue(studentsModel.getFullName());
            cell = row1.createCell(1);
            cell.setCellValue(studentsModel.getFullGroup());
        }
        sheet1.setColumnWidth(0, 15*500);
        sheet1.setColumnWidth(1, 15*500);
            File file = new File(getApplicationContext().getExternalFilesDir(null), fileName);
            if (file.exists()){
                Toast.makeText(getApplicationContext(),"Файл с таким именем уже существует, выберете другое название", Toast.LENGTH_LONG).show();
                return false;
            }else {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                    workbook.write(os);
                    Log.w(TAG, "Writing file " + file);
                    os.close();
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return false;
    }

    private Map<String, Integer> answerMap = new HashMap<String, Integer>();

    private void updateGraph(){
        pieChart.clearChart();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                for (Map.Entry entry : answerMap.entrySet()){
                    if (i<=3) {
                        pieChart.addPieSlice(new PieModel(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()), Color.parseColor(color.get(i))));
                        i++;
                    }
                }
                pieChart.update();
            }
        });

    }

    private void socketConnect() {
        try{
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = 4;
            socket = IO.socket("http://10.0.2.2:3000", options);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("------CONNECTED as Teacher------");
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("------DISCONNECTED------");
                }
            }).on("POLL_ANSWERED_NOTIFICATION", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        String ans = jsonObject.getString("answer_text");
                        Integer val = answerMap.get(ans);
                        System.out.println("Text: "+ans+" Value: "+val);
                        if (val !=null) {
                            answerMap.put(ans, val+1);
                            System.out.println("Add to map value: "+val);
                        }else
                            answerMap.put(ans, 1);
                        updateGraph();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.connect();
            socket.emit("JOIN_ROOM", LESSON_ID);
        }catch (Exception e){
            Log.e(TAG, "Error when connecting to socket: "+e);
        }
    }

    private void initializeXML() {
        tv_subject = findViewById(R.id.tV_subject);
        tv_date = findViewById(R.id.tV_date);
        pieChart = findViewById(R.id.pieChart);
        swipeRefreshLayout = findViewById(R.id.act);
        tv_question_asked = findViewById(R.id.tv_question_asked);
    }

    public void AddQuestion(View view){
        answerMap.clear();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.question_input_form, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(promptsView)
                .setTitle("Создание опроса")
                .setPositiveButton("Сохранить", null);
        ed_question = promptsView.findViewById(R.id.eT_question);
        ed_ans1 = promptsView.findViewById(R.id.eT_answer1);
        ed_ans2 = promptsView.findViewById(R.id.eT_answer2);
        ed_ans3 = promptsView.findViewById(R.id.eT_answer3);
        ed_ans4 = promptsView.findViewById(R.id.eT_answer4);

        ed_question.setError(null);
        ed_ans1.setError(null);
        ed_ans2.setError(null);

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#2D95CA"));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#2D95CA"));

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean opened = true;
                String question = String.valueOf(ed_question.getText());
                String ans1 = String.valueOf(ed_ans1.getText());
                String ans2 = String.valueOf(ed_ans2.getText());
                String ans3 = String.valueOf(ed_ans3.getText());
                String ans4 = String.valueOf(ed_ans4.getText());

                if (question.equals("")){
                    ed_question.setError("Поле не может быть пустым");
                    ed_question.requestFocus();
                }else if (ans1.equals("")){
                    ed_ans1.setError("Необходимо указать минимум два ответа");
                    ed_ans1.requestFocus();
                }else if (ans2.equals("")){
                    ed_ans2.setError("Необходимо указать минимум два ответа");
                    ed_ans2.requestFocus();
                } else {
                    //Отправляем на сервер
                    ArrayList<String> answers = new ArrayList<String>();
                    answers.add(ans1);
                    answers.add(ans2);
                    if (ans3.equals("") && ans4.equals("")) {
                    }else if (ans3.equals("") || ans4.equals("")) {
                        if (!ans3.equals(""))
                            answers.add(ans3);
                        else if (!ans4.equals(""))
                            answers.add(ans4);
                    }else {
                        answers.add(ans3);
                        answers.add(ans4);
                    }
                    createQuestion(jsonPlaceHolderAPI, question, LESSON_ID, answers.size(), answers);
                    //--------------------
                    opened = false;
                }

                if (!opened){
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void getStudentsPresent(JSONPlaceHolderAPI jsonPlaceHolderAPI, final int class_id){
        Call<List<StudClass_get>> call = jsonPlaceHolderAPI.getStudentsByClass(TOKEN,class_id);
        call.enqueue(new Callback<List<StudClass_get>>() {
            @Override
            public void onResponse(Call<List<StudClass_get>> call, Response<List<StudClass_get>> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: "+response.code());
                    return;
                }
                studentsModels.clear();
                List<StudClass_get> studClass_gets = response.body();
                for (StudClass_get studClass_get : studClass_gets){
                    studentsModels.add(new StudentsModel(studClass_get.getStudent_name(),studClass_get.getGroup_name()));
                }
                adapterStudents.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<StudClass_get>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createQuestion(JSONPlaceHolderAPI jsonPlaceHolderAPI, final String question, final int lessonID, final int quantity, final ArrayList<String> answers) {
        final Question_post question_post = new Question_post(question);
        Call<Question_answer> call = jsonPlaceHolderAPI.createQuestion(question_post);

        call.enqueue(new Callback<Question_answer>() {
            @Override
            public void onResponse(Call<Question_answer> call, Response<Question_answer> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: "+response.code());
                    return;
                }
                Question_answer question_answer = response.body();
                tv_question_asked.setText(question);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("lesson_number", lessonID);
                    jsonObject.put("question_id", question_answer.getQuestion_id());
                    jsonObject.put("question_text", question_answer.getQuestion_text());
                        jsonObject.put("quantity", quantity);
                    JSONArray jsonArray = new JSONArray();
                    for (String answer : answers)
                        jsonArray.put(answer);
                    jsonObject.put("answers_list", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("CREATE_POLL", jsonObject);
                Toast.makeText(getApplicationContext(), "Вопрос создан",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Question_answer> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });

    }



}
