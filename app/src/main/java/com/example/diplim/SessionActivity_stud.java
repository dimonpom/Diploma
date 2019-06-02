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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionActivity_stud extends AppCompatActivity {

    private static final String TAG = "SessionActivity_student";
    private int LESSON_ID;
    private int QUESTION_ID;
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
            LESSON_ID = 42;//args.getInt("id");
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

        socketConnect();

        presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentBtn.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Вы успели!", Toast.LENGTH_SHORT).show();
                createAnswer(jsonPlaceHolderAPI, 20, "OTVETUSi", 65, LESSON_ID);

            }
        });
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
                    try {
                        question = jsonObject.getString("question_text");
                        QUESTION_ID = jsonObject.getInt("question_id");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Voprosiki?: "+question);
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
                System.out.println("Answer posted");
            }

            @Override
            public void onFailure(Call<Answer_answer> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
            }
        });
    }
}
