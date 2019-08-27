package com.inspiration.inspirationrewards.model;

/**
 * Created by zolipe on 06-Apr-19.
 */

public class RewardModel {

    String studentId;
    String name;
    String username;
    String date;
    String notes;
    String value;

    public RewardModel(String studentId, String name, String username, String date, String notes, String value) {
        this.studentId = studentId;
        this.name = name;
        this.username = username;
        this.date = date;
        this.notes = notes;
        this.value = value;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
