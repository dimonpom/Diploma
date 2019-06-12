package com.example.diplim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.diplim.dbModels.Group;
import com.example.diplim.dbModels.JSONResponseProf;
import com.example.diplim.dbModels.JSONResponseStud;
import com.example.diplim.dbModels.Professor_login;
import com.example.diplim.dbModels.Professor_register;
import com.example.diplim.dbModels.SpinnerWithID;
import com.example.diplim.dbModels.Student;
import com.example.diplim.dbModels.Student_register;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

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
    private int StudID;
    private int ProfID;
    private int GroupID;

    private boolean isProf=true;

    private ActionProcessButton signupButton;
    private RadioButton rbProf, rbStud;
    private TextView loginLink, tv_forSpinner;
    private EditText nameText,emailText,passwordText, confPassword;
    private Spinner spinner_groups;

    private ArrayAdapter<SpinnerWithID> arrayAdapter;
    private List<SpinnerWithID> group_list;

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
        readGroups(jsonPlaceHolderAPI);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rbStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_forSpinner.setVisibility(View.VISIBLE);
                spinner_groups.setVisibility(View.VISIBLE);
            }
        });
        rbProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_forSpinner.setVisibility(View.INVISIBLE);
                spinner_groups.setVisibility(View.INVISIBLE);
            }
        });
        arrayAdapter = new ArrayAdapter<SpinnerWithID>(this, R.layout.spinner_item, group_list);
        spinner_groups.setAdapter(arrayAdapter);

        spinner_groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerWithID s = (SpinnerWithID) parent.getItemAtPosition(position);
                GroupID = s.group_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Нечего ложить в группу", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void initializeXML(){
        spinner_groups = findViewById(R.id.spinner_groups);
        signupButton = findViewById(R.id.btn_signup);
        signupButton.setMode(ActionProcessButton.Mode.ENDLESS);
        loginLink = findViewById(R.id.link_login);
        tv_forSpinner = findViewById(R.id.tv_forSpinner);
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
        signupButton.setClickable(false);
        loginLink.setClickable(false);
        rbProf.setClickable(false);
        rbStud.setClickable(false);
        signupButton.setProgress(1);
        if (rbProf.isChecked()){
            isProf = true;
            CreateProfAccount(jsonPlaceHolderAPI, name, email, password);
        }else if (rbStud.isChecked()){
            isProf = false;
            CreateStudAccount(jsonPlaceHolderAPI, name, email, password, GroupID);
        }
    }

    public void signupContinue(){
        System.out.println("Success register");
        if (isProf){
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.putExtra("idProf", ProfID);
            intent.putExtra("token", TOKEN);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(SignupActivity.this, MainActivity_stud.class);
            intent.putExtra("idGroup", GroupID);
            intent.putExtra("token", TOKEN);
            intent.putExtra("idStud", StudID);
            startActivity(intent);
            finish();
        }
    }

    private void CreateStudAccount(JSONPlaceHolderAPI jsonPlaceHolderAPI, String stud_name, String stud_login, String stud_password, final Integer stud_group){
        final Student_register student_register = new Student_register(stud_name, stud_login, stud_password, stud_group);

        Call<JSONResponseStud> call = jsonPlaceHolderAPI.createStudent(student_register);

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
                    Student student = response.body().getAccount();
                    TOKEN = student.getToken();
                    GroupID = student.getGroup();
                    StudID = student.getStudent_id();
                    signupContinue();
                }else {
                    Toast.makeText(getApplicationContext(), server_message, Toast.LENGTH_LONG).show();
                }
                signupButton.setClickable(true);
                loginLink.setClickable(true);
                rbProf.setClickable(true);
                rbStud.setClickable(true);
                signupButton.setProgress(0);
            }

            @Override
            public void onFailure(Call<JSONResponseStud> call, Throwable t) {
                signupButton.setClickable(true);
                loginLink.setClickable(true);
                rbProf.setClickable(true);
                rbStud.setClickable(true);
                signupButton.setProgress(0);
                Log.e(TAG, "-------Error when connecting register--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CreateProfAccount(JSONPlaceHolderAPI jsonPlaceHolderAPI, String prof_name, String prof_login, String prof_password) {
        final Professor_register professor_register = new Professor_register(prof_name,prof_login,prof_password);

        Call<JSONResponseProf> call = jsonPlaceHolderAPI.createProfessor(professor_register);
        /*signupButton.setClickable(false);
        loginLink.setClickable(false);
        signupButton.setProgress(1);*/
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
                    Professor_login professor_login = response.body().getAccount();
                    TOKEN = professor_login.getToken();
                    ProfID = professor_login.getProfessor_id();
                    signupContinue();
                }else {
                    Toast.makeText(getApplicationContext(), server_message, Toast.LENGTH_LONG).show();
                }
                signupButton.setClickable(true);
                loginLink.setClickable(true);
                rbProf.setClickable(true);
                rbStud.setClickable(true);
                signupButton.setProgress(0);
            }

            @Override
            public void onFailure(Call<JSONResponseProf> call, Throwable t) {
                signupButton.setClickable(true);
                loginLink.setClickable(true);
                rbProf.setClickable(true);
                rbStud.setClickable(true);
                signupButton.setProgress(0);
                Log.e(TAG, "-------Error when connecting register--------\n"+t.getMessage());
                Toast.makeText(getApplicationContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void readGroups(JSONPlaceHolderAPI jsonPlaceHolderAPI){
        group_list = new ArrayList<SpinnerWithID>();
        Call<List<Group>> call = jsonPlaceHolderAPI.getGroups();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }
                List<Group> groups = response.body();
                for (Group group : groups){
                    group_list.add(new SpinnerWithID(group.getGroup_name()+" "+group.getSubgroup(), group.getGroup_id()));
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    public boolean validate(String name, String email, String password, String passwordConfirm) {
        boolean valid = true;

        if (name.isEmpty()) {
            nameText.setError("Поле не может быть пустым");
            nameText.requestFocus();
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty()){
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

