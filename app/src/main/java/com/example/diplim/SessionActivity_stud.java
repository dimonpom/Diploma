package com.example.diplim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplim.dbModels.Answer_answer;
import com.example.diplim.dbModels.Answer_post;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionActivity_stud extends AppCompatActivity {

    private static final String TAG = "SessionActivity_student";
    private int LESSON_NUMBER = 42;
    private TextView tv_subject, tv_date;
    private Button presentBtn;
    private Socket socket;
    private JSONPlaceHolderAPI jsonPlaceHolderAPI;

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

        socketTest();

        presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentBtn.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Вы успели!", Toast.LENGTH_SHORT).show();
                createAnswer(jsonPlaceHolderAPI, 20, "OTVETUSi", 65);

            }
        });
    }

    private void initilizeXML() {
        presentBtn = findViewById(R.id.btn_present);
        tv_date = findViewById(R.id.tV_date);
        tv_subject = findViewById(R.id.tV_subject);
    }

    private void socketTest() {
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
            }).on("error", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("Server Response", args[0].toString());
                }
            }).on("POLL_STARTED", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Server Asked question");
                }
            }).on("POLL_STARTED", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Started poll");
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.emit("JOIN_ROOM", LESSON_NUMBER);
    }

    private void createAnswer(JSONPlaceHolderAPI jsonPlaceHolderAPI, final int questionID, final String answer, int studentID){
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
                System.out.println("ЕСТЬ СВЯЗЬ????"+socket.connected());
                socket.emit("POLL_ANSWERED", LESSON_NUMBER);
                System.out.println("Answer posted");
            }

            @Override
            public void onFailure(Call<Answer_answer> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
            }
        });
    }
}
