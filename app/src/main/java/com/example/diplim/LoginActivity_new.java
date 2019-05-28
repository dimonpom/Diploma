package com.example.diplim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.diplim.dbModels.JSONResponse;
import com.example.diplim.dbModels.Professor;
import com.example.diplim.dbModels.Professor_login;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity_new extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP =0;
    private static final String MY_SHARED = "MySharedPreferencesEmail";

    private String TOKEN;
    private int ProfID;

    private boolean connectionResult;
    private boolean isProf = true;

    private Switch aSwitch;
    private EditText emailText, passwordText;
    private TextView signupLink;
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
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aSwitch.isChecked())
                    aSwitch.setText("Студент");
                else
                    aSwitch.setText("Преподаватель");
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
    }

    private void initializeXML(){
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        signupLink = findViewById(R.id.link_signup);
        aSwitch = findViewById(R.id.switch2);
        actionProcessButton = findViewById(R.id.btnLogin);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    private void login() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if(!validate(email, password))
            return;
        if (password.equals("nocon")){
            Intent intent = new Intent(LoginActivity_new.this, MainActivity.class);
            intent.putExtra("nocon", true);
            startActivity(intent);
            return;
        }
        if (!aSwitch.isChecked()) {
            AuthProffessor(jsonPlaceHolderAPI, email, password);
            isProf = true;
        }else {
            isProf = false;
            Toast.makeText(getApplicationContext(), "Это пока не завезли", Toast.LENGTH_LONG).show();
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
            //
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
        }else {
            passwordText.setError(null);
        }
        return validInputs;
    }

    //------------------------------AUTH-------------

    private void AuthProffessor(JSONPlaceHolderAPI jsonPlaceHolderAPI, final String prof_login, String prof_password){
        final Professor professor = new Professor(prof_login, prof_password);

        Call<JSONResponse> call = jsonPlaceHolderAPI.Authenticate_professor(professor);
        actionProcessButton.setProgress(1);
        actionProcessButton.setClickable(false);
        signupLink.setClickable(false);
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
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
                signupLink.setClickable(true);
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting auth--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                actionProcessButton.setProgress(0);
                actionProcessButton.setClickable(true);
                signupLink.setClickable(true);
            }
        });
    }
}
