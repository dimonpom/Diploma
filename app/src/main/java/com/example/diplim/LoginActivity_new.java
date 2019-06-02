package com.example.diplim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.diplim.dbModels.Answer_answer;
import com.example.diplim.dbModels.Answer_post;
import com.example.diplim.dbModels.JSONResponseProf;
import com.example.diplim.dbModels.JSONResponseStud;
import com.example.diplim.dbModels.Professor;
import com.example.diplim.dbModels.Professor_login;
import com.example.diplim.dbModels.Question_answer;
import com.example.diplim.dbModels.Question_post;
import com.example.diplim.dbModels.Student;
import com.example.diplim.dbModels.Student_login;
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

public class LoginActivity_new extends Activity {

    private static final String TAG = "LoginActivity_new";
    private static final int REQUEST_SIGNUP =0;
    private static final String MY_SHARED = "MySharedPreferencesEmail";

    private String TOKEN;
    private int ProfID;
    private int StudID;
    private int GroupID;
    private int questionID;

    private boolean connectionResult;
    private boolean isProf = true;
    private Socket socket;

    private EditText emailText, passwordText;
    private TextView signupLink;
    private ToggleButton tg_prof, tg_stud;
    private ActionProcessButton actionProcessButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private JSONPlaceHolderAPI jsonPlaceHolderAPI;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        initializeXML();
        sharedPreferences = getSharedPreferences(MY_SHARED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.contains("Email")){
            String sharedEmail = sharedPreferences.getString("Email","def");
            emailText.setText(sharedEmail);
        }

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
               startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
        actionProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        tg_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tg_prof.setChecked(true);
                tg_stud.setChecked(false);
            }
        });
        tg_stud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tg_stud.setChecked(true);
                tg_prof.setChecked(false);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolderAPI = retrofit.create(JSONPlaceHolderAPI.class);
        //socketTest();
    }

    private void initializeXML(){
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        signupLink = findViewById(R.id.link_signup);
        tg_prof = findViewById(R.id.toggleButton3);
        tg_stud = findViewById(R.id.toggleButton4);
        actionProcessButton = findViewById(R.id.btnLogin);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
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
                    System.out.println("------CONNECTED Teacher------");
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
            }).on("POLL_ANSWERED_NOTIFICATION", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("OTVETIL PIDORAS!");
                }
            }).on("POLL_STARTED", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject jsonObject = (JSONObject) args[0];
                    System.out.println("jsonOBJECT----"+jsonObject);
                    System.out.println("Chtototam startanulo");
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.emit("JOIN_ROOM", 42);
    }

    private void createQuestion(JSONPlaceHolderAPI jsonPlaceHolderAPI, final String question) {
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
                JSONObject test = new JSONObject();
                try {
                    test.put("lesson_number", 42);
                    test.put("question_id", question_answer.getQuestion_id());
                    test.put("question_text", question_answer.getQuestion_text());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("JSON----"+ test);
                //questionID = question_answer.getQuestion_id();
                socket.emit("CREATE_POLL", test);

                System.out.println("Question posted");
                //System.out.println(question_answer.getQuestion_text());
            }

            @Override
            public void onFailure(Call<Question_answer> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
            }
        });

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
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("lesson_number",42);
                    jsonObject.put("answer_id", answer_answer.getAnswer_id());
                    jsonObject.put("question_id", answer_answer.getQuestion_id());
                    jsonObject.put("answer_text", answer_answer.getAnswer_text());
                    jsonObject.put("student_id", answer_answer.getStudent_id());
                } catch (JSONException e) {
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

    private void login() {

        /*String email = emailText.getText().toString();
        if (email.equals("1")){ //--------------------------------------prof
            System.out.println(socket.connected());
            createQuestion(jsonPlaceHolderAPI, "TESTTIROVANIE");
        }else if (email.equals("2")){ //--------------------------------stud
            System.out.println(socket.connected());
            //createAnswer(jsonPlaceHolderAPI, );
        }else if (email.equals("exit")){
            socket.disconnect();
        }*/
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (password.equals("prof")){
            Intent intent = new Intent(LoginActivity_new.this, MainActivity.class);
            intent.putExtra("nocon", true);
            startActivity(intent);
            return;
        }else if (password.equals("stud")){
            Intent intent = new Intent(LoginActivity_new.this, MainActivity_stud.class);
            startActivity(intent);
        }
        if(!validate(email, password))
            return;

        actionProcessButton.setProgress(1);
        actionProcessButton.setClickable(false);
        tg_stud.setClickable(false);
        tg_prof.setClickable(false);
        signupLink.setClickable(false);
        if (tg_prof.isChecked()){
            AuthProffessor(jsonPlaceHolderAPI, email, password);
            isProf = true;
        }else if (tg_stud.isChecked()){
            AuthStudent(jsonPlaceHolderAPI, email, password);
            isProf = false;
        }
        editor.putString("Email", email);
        editor.commit();
    }

    private void loginContinue(){
        System.out.println("Success login");

        if (isProf){
            Intent intent = new Intent(LoginActivity_new.this, MainActivity.class);
            intent.putExtra("idProf", ProfID);
            intent.putExtra("token", TOKEN);
            startActivity(intent);
        }else {
            Intent intent = new Intent(LoginActivity_new.this, MainActivity_stud.class);
            intent.putExtra("idGroup", GroupID);
            intent.putExtra("idStud", StudID);
            intent.putExtra("token", TOKEN);
            startActivity(intent);
        }

    }

    private boolean validate(String email, String password) {
        boolean validInputs = true;

        /*if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Not valid email address");
            emailText.requestFocus();
            validInputs =false;
        }else {
            emailText.setError(null);
        }*/

        if(password.isEmpty()){
            passwordText.setError("Input valid password");
            passwordText.requestFocus();
            validInputs = false;
        }else if (password.length()<6){
            passwordText.setError("Не меньше 6 символов");
            passwordText.requestFocus();
        }else {
            passwordText.setError(null);
        }
        return validInputs;
    }

    //------------------------------AUTH-------------

    private void AuthStudent(JSONPlaceHolderAPI jsonPlaceHolderAPI, String stud_login, String stud_password){
        final Student_login student_login = new Student_login(stud_login, stud_password);

        Call<JSONResponseStud> call = jsonPlaceHolderAPI.Authenticate_student(student_login);
        /*actionProcessButton.setProgress(1);
        actionProcessButton.setClickable(false);
        signupLink.setClickable(false);*/
        call.enqueue(new Callback<JSONResponseStud>() {
            @Override
            public void onResponse(Call<JSONResponseStud> call, Response<JSONResponseStud> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: "+response.code());
                    return;
                }
                String server_message = response.body().getMessage();
                Boolean server_status = response.body().getStatus();
                if (server_status){
                    Student student = response.body().getStudent();
                    TOKEN = student.getToken();
                    GroupID = student.getGroup();
                    StudID = student.getStudent_id();
                    loginContinue();
                }else {
                    Toast.makeText(getApplicationContext(), server_message, Toast.LENGTH_LONG).show();
                }
                actionProcessButton.setProgress(0);
                actionProcessButton.setClickable(true);
                tg_stud.setClickable(true);
                tg_prof.setClickable(true);
                signupLink.setClickable(true);
            }

            @Override
            public void onFailure(Call<JSONResponseStud> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                actionProcessButton.setProgress(0);
                actionProcessButton.setClickable(true);
                tg_stud.setClickable(true);
                tg_prof.setClickable(true);
                signupLink.setClickable(true);
            }
        });
    }

    private void AuthProffessor(JSONPlaceHolderAPI jsonPlaceHolderAPI, String prof_login, String prof_password){
        final Professor professor = new Professor(prof_login, prof_password);

        Call<JSONResponseProf> call = jsonPlaceHolderAPI.Authenticate_professor(professor);
        /*actionProcessButton.setProgress(1);
        actionProcessButton.setClickable(false);
        signupLink.setClickable(false);*/
        call.enqueue(new Callback<JSONResponseProf>() {
            @Override
            public void onResponse(Call<JSONResponseProf> call, Response<JSONResponseProf> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: "+response.code());
                    return;
                }
                String server_message = response.body().getMessage();
                Boolean server_status = response.body().getStatus();
                if (server_status){
                    Professor_login professor_login = response.body().getProfessor();
                    TOKEN = professor_login.getToken();
                    ProfID = professor_login.getProfessor_id();
                    loginContinue();
                }else {
                    Toast.makeText(getApplicationContext(), server_message, Toast.LENGTH_LONG).show();
                }
                actionProcessButton.setProgress(0);
                actionProcessButton.setClickable(true);
                tg_stud.setClickable(true);
                tg_prof.setClickable(true);
                signupLink.setClickable(true);
            }

            @Override
            public void onFailure(Call<JSONResponseProf> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                actionProcessButton.setProgress(0);
                actionProcessButton.setClickable(true);
                tg_stud.setClickable(true);
                tg_prof.setClickable(true);
                signupLink.setClickable(true);
            }
        });
    }
}
