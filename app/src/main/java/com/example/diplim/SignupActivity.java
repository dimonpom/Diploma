package com.example.diplim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.diplim.dbModels.JSONResponse;
import com.example.diplim.dbModels.Professor_login;
import com.example.diplim.dbModels.Professor_register;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private JSONPlaceHolderAPI jsonPlaceHolderAPI;

    private String TOKEN;
    private int ProfID;

    private boolean isProf=true;

    private ActionProcessButton signupButton;
    private RadioButton rbProf, rbStud;
    private TextView loginLink;
    private EditText nameText,emailText,passwordText, confPassword;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        initializeXML();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolderAPI = retrofit.create(JSONPlaceHolderAPI.class);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void initializeXML(){
        signupButton = findViewById(R.id.btn_signup);
        signupButton.setMode(ActionProcessButton.Mode.ENDLESS);
        loginLink = findViewById(R.id.link_login);
        nameText = findViewById(R.id.input_name);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        confPassword = findViewById(R.id.input_password_confirm);
        rbProf = findViewById(R.id.rB_prof);
        rbStud = findViewById(R.id.rB_stud);
    }

    public void signup() {
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordConfirm = confPassword.getText().toString();

        if (!validate(name, email, password, passwordConfirm))
            return;

        if (rbProf.isChecked()){
            isProf = true;
            CreateProfAccount(jsonPlaceHolderAPI, name, email, password);
        }else if (rbStud.isChecked()){
            Toast.makeText(getApplicationContext(), "Хер тебе в рыло, сраный урод",Toast.LENGTH_LONG).show();
            isProf = false;
        }
    }

    public void signupContinue(){
        System.out.println("Success register");
        if (isProf){
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.putExtra("idProf", ProfID);
            intent.putExtra("token", TOKEN);
            startActivity(intent);
        }else {

        }
    }

    private void CreateProfAccount(JSONPlaceHolderAPI jsonPlaceHolderAPI, String prof_name, String prof_login, String prof_password) {
        final Professor_register professor_register = new Professor_register(prof_name,prof_login,prof_password);

        Call<JSONResponse> call = jsonPlaceHolderAPI.createProfessor(professor_register);
        signupButton.setClickable(false);
        loginLink.setClickable(false);
        signupButton.setProgress(1);
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
                    Professor_login professor_login = response.body().getAccount();
                    TOKEN = professor_login.getToken();
                    ProfID = professor_login.getProfessor_id();
                    signupContinue();
                }else {
                    Toast.makeText(getApplicationContext(), server_message, Toast.LENGTH_LONG).show();
                }
                signupButton.setClickable(true);
                loginLink.setClickable(true);
                signupButton.setProgress(0);
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                signupButton.setClickable(true);
                loginLink.setClickable(true);
                signupButton.setProgress(0);
                Log.e(TAG, "-------Error when connecting register--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*final Professor professor = new Professor(prof_login, prof_password);

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
        });*/
    public boolean validate(String name, String email, String password, String passwordConfirm) {
        boolean valid = true;

        if (name.isEmpty()) {
            nameText.setError("Поле не может быть пустым");
            nameText.requestFocus();
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty()){ //|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Поле не может быть пустым");
            emailText.requestFocus();
            valid = false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.requestFocus();
            emailText.setError("Это не похоже на адресс почты");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.requestFocus();
            passwordText.setError("Поле не может быть пустым");
            valid = false;
        } else if (password.length()<6){
            passwordText.requestFocus();
            passwordText.setError("Пароль должен быть длинее 5 символов");
            valid = false;
        }else {
            if (!passwordConfirm.equals(password)){
                confPassword.requestFocus();
                confPassword.setError("Пароли не совпадают");
                valid = false;
            }else {
                confPassword.setError(null);
            }
            passwordText.setError(null);
        }
        return valid;
    }

}

