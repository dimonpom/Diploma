package com.example.diplim;

import android.content.Intent;
import android.support.v4.app.INotificationSideChannel;

public class SpinnerWithID {
    public String group;
    public Integer group_id;

    public SpinnerWithID(String group, Integer group_id) {
        this.group = group;
        this.group_id = group_id;
    }

    @Override
    public String toString() {
        return group;
    }
}
