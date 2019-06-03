package com.example.diplim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.diplim.CustomListViews.CAdapterStudents;
import com.example.diplim.dbModels.Answer_answer;
import com.example.diplim.dbModels.Answer_post;
import com.example.diplim.dbModels.Question_answer;
import com.example.diplim.dbModels.Question_post;
import com.example.diplim.dbModels.StudentsModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionActivity extends AppCompatActivity {

    ArrayList<StudentsModel> studentsModels;
    ListView listView;
    final Context context = this;
    private static final String TAG="SessionActivity";
    private static NetworkInterface networkInterface;

    private int questionID;

    private static CAdapterStudents adapterStudents;
    private int LESSON_ID;
    private EditText ed_question, ed_ans1, ed_ans2, ed_ans3, ed_ans4;
    private TextView tv_subject, tv_date;
    private String exSubject, exTheme, exDate;
    private Socket socket;

    private JSONPlaceHolderAPI jsonPlaceHolderAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Bundle args = getIntent().getExtras();
        if (args!=null){
            exSubject = args.getString("subject");
            exTheme = args.getString("theme");
            exDate = args.getString("date");
            LESSON_ID = 42;//args.getInt("id");
        }

        //setTitle(exTheme+" "+exSubject);
        //menu.findItem(R.id.menu_date).setTitle("Exple");
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
        studentsModels.add(new StudentsModel("Петрович Петров Рофлов","321 Б"));
        studentsModels.add(new StudentsModel("Веселов Гурий Ефимович","321 Б"));
        studentsModels.add(new StudentsModel("Молчанов Алан Алексеевич","321 Б"));
        studentsModels.add(new StudentsModel("Журавлёв Тимур Улебович","321 А"));
        studentsModels.add(new StudentsModel("Кулагин Бенедикт Юлианович","321 А"));
        studentsModels.add(new StudentsModel("Красильников Пантелеймон Эльдарович","321 А"));

        adapterStudents = new CAdapterStudents(studentsModels, getApplicationContext());
        listView.setAdapter(adapterStudents);
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
                    System.out.println("Student answered: "+jsonObject);
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
    }

    private String convertDate(String date){
        String finalDate =null;
        String[] rowDate = date.split("/");
        finalDate = rowDate[2]+"/"+rowDate[1]+"/"+rowDate[0].substring(rowDate[0].length()-2);
        return finalDate;
    }

    public void AddQuestion(View view){
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

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean opened = true;
                int quantity = 4;
                String question = String.valueOf(ed_question.getText());
                String ans1 = String.valueOf(ed_ans1.getText());
                String ans2 = String.valueOf(ed_ans2.getText());
                String ans3 = String.valueOf(ed_ans3.getText());
                String ans4 = String.valueOf(ed_ans4.getText());

                if (question.equals("")){
                    ed_question.setError("Question required");
                    ed_question.requestFocus();
                }else if (ans1.equals("")){
                    ed_ans1.setError("Two answers required");
                    ed_ans1.requestFocus();
                }else if (ans2.equals("")){
                    ed_ans2.setError("Two answers required");
                    ed_ans2.requestFocus();
                } else {
                    //Отправляем на сервер
                    ArrayList<String> answers = new ArrayList<String>();
                    answers.add(ans1);
                    answers.add(ans2);
                    if (ans3.equals("") && ans4.equals("")) {
                        quantity -= 2;
                    }else if (ans3.equals("") || ans4.equals("")) {
                        quantity -= 1;
                        if (!ans3.equals(""))
                            answers.add(ans3);
                        else if (!ans4.equals(""))
                            answers.add(ans4);
                    }else {
                        answers.add(ans3);
                        answers.add(ans4);
                    }

                   /* JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("lesson_number", 1);
                        jsonObject.put("question_id", 1);
                        jsonObject.put("question_text", "CHTO?");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(jsonObject);*/
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
                //questionID = question_answer.getQuestion_id();
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
                    //jsonObject.put("")
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("CREATE_POLL", jsonObject);
                System.out.println("Question posted");
            }

            @Override
            public void onFailure(Call<Question_answer> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
            }
        });

    }


}
