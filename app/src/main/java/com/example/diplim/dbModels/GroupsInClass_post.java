package com.example.diplim.dbModels;

public class GroupsInClass_post {
    int group_id, class_id;

    public GroupsInClass_post(int group_id, int class_id) {
        this.group_id = group_id;
        this.class_id = class_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public int getClass_id() {
        return class_id;
    }
}
