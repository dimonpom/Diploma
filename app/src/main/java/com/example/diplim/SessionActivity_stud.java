package com.example.diplim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SessionActivity_stud extends AppCompatActivity {

    private TextView tv_subject, tv_date;
    private Button presentBtn;

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

        presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentBtn.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Вы успели!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initilizeXML() {
        presentBtn = findViewById(R.id.btn_present);
        tv_date = findViewById(R.id.tV_date);
        tv_subject = findViewById(R.id.tV_subject);
    }
}
