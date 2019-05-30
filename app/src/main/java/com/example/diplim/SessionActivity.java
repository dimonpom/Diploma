package com.example.diplim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.diplim.CustomListViews.CAdapterStudents;
import com.example.diplim.dbModels.StudentsModel;

import java.util.ArrayList;

public class SessionActivity extends AppCompatActivity {

    ArrayList<StudentsModel> studentsModels;
    ListView listView;
    final Context context = this;

    private static CAdapterStudents adapterStudents;
    private EditText ed_question, ed_ans1, ed_ans2, ed_ans3, ed_ans4;
    private TextView tv_subject, tv_date;
    private String exSubject, exTheme, exDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Bundle args = getIntent().getExtras();
        if (args!=null){
            exSubject = args.getString("subject");
            exTheme = args.getString("theme");
            exDate = args.getString("date");
        }

        //setTitle(exTheme+" "+exSubject);
        //menu.findItem(R.id.menu_date).setTitle("Exple");
        initializeXML();

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
                }else {
                    //Отправляем на сервер

                    opened = false;
                }

                if (!opened){
                    alertDialog.dismiss();
                }
            }
        });
    }

}
