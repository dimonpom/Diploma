package com.example.diplim;

import android.media.session.MediaSession;
import android.util.Log;

import com.example.diplim.dbModels.Group;
import com.example.diplim.dbModels.Professor;
import com.example.diplim.dbModels.Professor_login;
import com.example.diplim.dbModels.SubjectPost;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class API {

    private String TAG = "--API Class--";



    //--------------------------------------------------------READ---------------------------------------------

    ArrayList<String> readSubjects(JSONPlaceHolderAPI jsonPlaceHolderAPI, String Token){
        Call<List<SubjectPost>> call = jsonPlaceHolderAPI.getSubjects("Bearer "+Token);
        final ArrayList<String> subjectList = new ArrayList<>();
        call.enqueue(new Callback<List<SubjectPost>>() {
            @Override
            public void onResponse(Call<List<SubjectPost>> call, Response<List<SubjectPost>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }
                List<SubjectPost> posts = response.body();
                for (SubjectPost subjectPost : posts){
                    subjectList.add(subjectPost.getSubject_name());
                }
            }

            @Override
            public void onFailure(Call<List<SubjectPost>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });
        return subjectList;
    }



    /*void readProf(JSONPlaceHolderAPI jsonPlaceHolderAPI){
        Call<List<Professor>> call = jsonPlaceHolderAPI.getProfessors();
        call.enqueue(new Callback<List<Professor>>() {
            @Override
            public void onResponse(Call<List<Professor>> call, Response<List<Professor>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }
                List<Professor> professors = response.body();
                for (Professor professor : professors){
                    System.out.println(professor.getProf_id()+" "+professor.getProf_name());
                }
            }

            @Override
            public void onFailure(Call<List<Professor>> call, Throwable t) {
                Log.e(TAG, "-------Error when connecting--------\n"+t.getMessage());
            }
        });
    }*/


    //-------------------------------------------------------CREATE--------------------------------------------

    void createSubject(JSONPlaceHolderAPI jsonPlaceHolderAPI, String subjectName, String Token) {
        SubjectPost post = new SubjectPost(null, subjectName);

        Call<SubjectPost> call = jsonPlaceHolderAPI.createSubjects(post);
        call.enqueue(new Callback<SubjectPost>() {
            @Override
            public void onResponse(Call<SubjectPost> call, Response<SubjectPost> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }

                SubjectPost subjectPost = response.body();
                System.out.println(subjectPost.getSubject_name());
            }

            @Override
            public void onFailure(Call<SubjectPost> call, Throwable t) {
                Log.e(TAG, "-------2Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    void createGroup(JSONPlaceHolderAPI jsonPlaceHolderAPI, String groupName, String subgroupName){
        Group group = new Group(null, groupName, subgroupName);

        Call<Group> call = jsonPlaceHolderAPI.createGroup(group);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }
                Group groupPost = response.body();
                System.out.println(groupPost.getGroup_name()+groupPost.getSubgroup());
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.e(TAG, "-------2Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    //--------------------------------------------------------PUT----------------------------------------------

    void putSubjects(JSONPlaceHolderAPI jsonPlaceHolderAPI, int subjectID, String subjectName){
        SubjectPost post = new SubjectPost(subjectID, subjectName);
        Call<Void> call = jsonPlaceHolderAPI.putSubjects(subjectID,post);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "-------1Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    void putGroups(JSONPlaceHolderAPI jsonPlaceHolderAPI, int groupID, String groupName, String subgroupName){
        Group group = new Group(groupID, groupName, subgroupName);
        Call<Void> call = jsonPlaceHolderAPI.putGroup(groupID, group);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "------Not successful response with code: " + response.code());
                    return;
                }
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "-------1Error when connecting--------\n"+t.getMessage());
            }
        });
    }

    //-------------------------------------------------------DELETE--------------------------------------------

    void deleteSubject(JSONPlaceHolderAPI jsonPlaceHolderAPI, int subjectID){
        Call<Void> call = jsonPlaceHolderAPI.deleteSubjects(subjectID);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Code: "+response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "-------2Error when connecting--------\n"+t.getMessage());
            }
        });
    }


}
