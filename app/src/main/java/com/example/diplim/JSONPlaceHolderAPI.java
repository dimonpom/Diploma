package com.example.diplim;

import com.example.diplim.dbModels.Answer_answer;
import com.example.diplim.dbModels.Answer_post;
import com.example.diplim.dbModels.ClassPost;
import com.example.diplim.dbModels.Group;
import com.example.diplim.dbModels.JSONResponseProf;
import com.example.diplim.dbModels.JSONResponseStud;
import com.example.diplim.dbModels.Professor;
import com.example.diplim.dbModels.Professor_register;
import com.example.diplim.dbModels.Question_answer;
import com.example.diplim.dbModels.Question_post;
import com.example.diplim.dbModels.Student_login;
import com.example.diplim.dbModels.Student_register;
import com.example.diplim.dbModels.SubjectPost;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface JSONPlaceHolderAPI {

    //------------------Subjects

    @GET("subjects")
    Call<List<SubjectPost>> getSubjects(@Header("Authorization") String authKey);

    @POST("subjects")
    Call<SubjectPost> createSubjects(@Body SubjectPost subjectPost);

    @PUT("subjects/{id}")
    Call<Void> putSubjects(@Path("id") int id, @Body SubjectPost subjectPost);

    @DELETE("subjects/{id}")
    Call<Void> deleteSubjects(@Path("id") int id);

    //------------------Groups

    @GET("groups")
    Call<List<Group>> getGroups();

    @POST("groups")
    Call<Group> createGroup(@Body Group group);

    @PUT("groups/{id}")
    Call<Void> putGroup(@Path("id") int id, @Body Group group);

    @DELETE("groups/{id}")
    Call<Void> deleteGroup(@Path("id") int id);

    //------------------Professors

    @GET("professors")
    Call<List<Professor>> getProfessors();

    @POST("professors/new")
    Call<JSONResponseProf> createProfessor(@Body Professor_register professor_register);

    @POST("professors/login")
    Call<JSONResponseProf> Authenticate_professor(@Body Professor professor);
    //Call<Professor_login> Authenticate_professor(@Body Professor professor);

    /*@PUT("professors/{id}")
    Call<Void> putProfessor(@Path("id") int id, @Body Professor professor);*/

    @DELETE
    Call<Void> deleteProfessors(@Path("id") int id);

    //------------------Student

    @POST("students/login")
    Call<JSONResponseStud> Authenticate_student(@Body Student_login student_login);

    @POST("students/new")
    Call<JSONResponseStud> createStudent(@Body Student_register student_register);

    //------------------Classes

    @GET("/class/professors/{professor_id}")
    Call<List<ClassPost>> getClassByProfessor(@Header("Authorization") String authKey, @Path("professor_id") int id);

    //------------------Questions

    @POST("questions")
    Call<Question_answer> createQuestion(@Body Question_post question_post);

    //------------------Answers

    @POST("answers")
    Call<Answer_answer> createAnswer(@Body Answer_post answer_post);
}
