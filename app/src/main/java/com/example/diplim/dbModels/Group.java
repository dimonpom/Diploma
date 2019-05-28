package com.example.diplim.dbModels;

public class Group {
    private Integer group_id;
    private String group_name;
    private String subgroup;

    public Group(Integer group_id, String group_name, String subgroup) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.subgroup = subgroup;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getSubgroup() {
        return subgroup;
    }
}
