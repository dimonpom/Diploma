package com.example.diplim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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

    private void login() {
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

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Не правильный адресс почты");
            emailText.requestFocus();
            validInputs =false;
        }else {
            emailText.setError(null);
        }
        if(password.isEmpty()){
            passwordText.setError("Поле не может быть пустым");
            passwordText.requestFocus();
            validInputs = false;
        }else if (password.length()<6){
            passwordText.setError("Не меньше 6 символов");
            passwordText.requestFocus();
            validInputs = false;
        }else {
            passwordText.setError(null);
        }
        return validInputs;
    }

    boolean twice;
    @Override
    public void onBackPressed() {
        if (twice){
            System.exit(0);
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

    //------------------------------AUTH-------------

    private void AuthStudent(JSONPlaceHolderAPI jsonPlaceHolderAPI, String stud_login, String stud_password){
        final Student_login student_login = new Student_login(stud_login, stud_password);

        Call<JSONResponseStud> call = jsonPlaceHolderAPI.Authenticate_student(student_login);
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
                Toast.makeText(getApplicationContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
                actionProcessButton.setProgress(0);
                actionProcessButton.setClickable(true);
                tg_stud.setClickable(true);
                tg_prof.setClickable(true);
                signupLink.setClickable(true);
            }
        });
    }
}
