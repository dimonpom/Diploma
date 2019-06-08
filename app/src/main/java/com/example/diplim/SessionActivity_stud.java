package com.example.diplim;

import android.app.usage.UsageEvents;
import android.service.autofill.FillEventHistory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplim.dbModels.Answer_answer;
import com.example.diplim.dbModels.Answer_post;
import com.example.diplim.dbModels.Presence_post;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionActivity_stud extends AppCompatActivity {

    private static final String TAG = "SessionActivity_student";
    private int LESSON_ID;
    private int QUESTION_ID, STUDENT_ID;

    private TextView tv_subject, tv_date, tv_question;
    private Button presentBtn, ans_button1, ans_button2, ans_button3, ans_button4;

    private Socket socket;
    private JSONPlaceHolderAPI jsonPlaceHolderAPI;

    AlertDialog alertDialog;

    private String exDate, exSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_stud);
        initilizeXML();

        Bundle args = getIntent().getExtras();
        if (args!=null){
            exDate = args.getString("date");
            exSubject = args.getString("subject");
            LESSON_ID = 42;//args.getInt("id");
            STUDENT_ID = args.getInt("studID");
        }
        tv_date.setText(exDate);
        tv_subject.setText(exSubject);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolderAPI = retrofit.create(JSONPlaceHolderAPI.class);

        //socketConnect();
        //studentPresent(jsonPlaceHolderAPI, STUDENT_ID, LESSON_ID);

        presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentBtn.setVisibility(View.INVISIBLE);
                studentPresent(jsonPlaceHolderAPI, STUDENT_ID, LESSON_ID);
               // createAnswer(jsonPlaceHolderAPI, 20, "OTVETUSi", 65, LESSON_ID);

            }
        });
    }

    private void studentPresent(JSONPlaceHolderAPI jsonPlaceHolderAPI, int studID, int classID){
        Presence_post presence_post = new Presence_post(studID, classID);
        Call<Void> call = jsonPlaceHolderAPI.createPresence(presence_post);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "Error, with response code: "+response.code()+"\n"+response.message());
                    return;
                }
                Toast.makeText(getApplicationContext(), "Вы успешно зачислены на занятие", Toast.LENGTH_LONG).show();

                socketConnect();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    private void makeQuestionWindow(String question, int ans_quantity, ArrayList<String> arrayList){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView;
        final String[] answer = new String[1];
        if (ans_quantity == 3){
            promptsView = layoutInflater.inflate(R.layout.answer_input_form3, null);
            ans_button1 = promptsView.findViewById(R.id.btn1);
            ans_button1.setText(arrayList.get(0));
            ans_button2 = promptsView.findViewById(R.id.btn2);
            ans_button2.setText(arrayList.get(1));
            ans_button3 = promptsView.findViewById(R.id.btn3);
            ans_button3.setText(arrayList.get(2));
            ans_button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer[0] = (String) ans_button3.getText();
                    createAnswer(jsonPlaceHolderAPI, QUESTION_ID, answer[0], STUDENT_ID, LESSON_ID);
                    alertDialog.dismiss();
                }
            });
        }else if (ans_quantity == 4){
            promptsView = layoutInflater.inflate(R.layout.answer_input_form4, null);
            ans_button1 = promptsView.findViewById(R.id.btn1);
            ans_button1.setText(arrayList.get(0));
            ans_button2 = promptsView.findViewById(R.id.btn2);
            ans_button2.setText(arrayList.get(1));
            ans_button3 = promptsView.findViewById(R.id.btn3);
            ans_button3.setText(arrayList.get(2));
            ans_button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer[0] = (String) ans_button3.getText();
                    createAnswer(jsonPlaceHolderAPI, QUESTION_ID, answer[0], STUDENT_ID, LESSON_ID);
                    alertDialog.dismiss();
                }
            });
            ans_button4 = promptsView.findViewById(R.id.btn4);
            ans_button4.setText(arrayList.get(3));
            ans_button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer[0] = (String) ans_button4.getText();
                    createAnswer(jsonPlaceHolderAPI, QUESTION_ID, answer[0], STUDENT_ID, LESSON_ID);
                    alertDialog.dismiss();
                }
            });
        }else {
            promptsView = layoutInflater.inflate(R.layout.answer_input_form, null);
            ans_button1 = promptsView.findViewById(R.id.btn1);
            ans_button1.setText(arrayList.get(0));
            ans_button2 = promptsView.findViewById(R.id.btn2);
            ans_button2.setText(arrayList.get(1));
        }
        tv_question = promptsView.findViewById(R.id.tV_question);
        ans_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer[0] = (String) ans_button1.getText();
                createAnswer(jsonPlaceHolderAPI, QUESTION_ID, answer[0], STUDENT_ID, LESSON_ID);
                alertDialog.dismiss();
            }
        });
        ans_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer[0] = (String) ans_button2.getText();
                createAnswer(jsonPlaceHolderAPI, QUESTION_ID, answer[0], STUDENT_ID, LESSON_ID);
                alertDialog.dismiss();
            }
        });

        tv_question.setText(question);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(promptsView)
                .setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void socketConnect() {
        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = 3;
            socket = IO.socket("http://10.0.2.2:3000", options);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("------CONNECTED Student------");
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("------DISCONNECTED------");
                }
            }).on("POLL_STARTED", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject jsonObject = (JSONObject) args[0];
                    String question = null;
                    int ans_quntity;
                    try {
                        question = jsonObject.getString("question_text");
                        QUESTION_ID = jsonObject.getInt("question_id");
                        ans_quntity = jsonObject.getInt("quantity");

                        ArrayList<String> arrayList = new ArrayList<String>();
                        JSONArray jsonArray = jsonObject.getJSONArray("answers_list");
                        for (int i=0; i<jsonArray.length(); i++){
                            arrayList.add(jsonArray.getString(i));
                        }
                        final String inp0 = question;
                        final int inp1 = ans_quntity;
                        final ArrayList<String> inp2 = arrayList;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeQuestionWindow(inp0, inp1, inp2);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.connect();
            socket.emit("JOIN_ROOM", LESSON_ID);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initilizeXML() {
        presentBtn = findViewById(R.id.btn_present);
        tv_date = findViewById(R.id.tV_date);
        tv_subject = findViewById(R.id.tV_subject);
    }

    private void createAnswer(JSONPlaceHolderAPI jsonPlaceHolderAPI, final int questionID, final String answer, int studentID, final int lessonID){
        final Answer_post answer_post = new Answer_post(questionID, studentID, answer);
        Call<Answer_answer> call = jsonPlaceHolderAPI.createAnswer(answer_post);

        call.enqueue(new Callback<Answer_answer>() {
            @Override
            public void onResponse(Call<Answer_answer> call, Response<Answer_answer> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: "+response.code());
                    return;
                }
                Answer_answer answer_answer = response.body();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("lesson_number",lessonID);
                    jsonObject.put("answer_id", answer_answer.getAnswer_id());
                    jsonObject.put("question_id", answer_answer.getQuestion_id());
                    jsonObject.put("answer_text", answer_answer.getAnswer_text());
                    jsonObject.put("student_id", answer_answer.getStudent_id());
                }catch (Exception e){
                    e.printStackTrace();
                }
                socket.emit("POLL_ANSWERED", jsonObject);
                Toast.makeText(getApplicationContext(), "Вы успешно ответили", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Answer_answer> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
            }
        });
    }

}
