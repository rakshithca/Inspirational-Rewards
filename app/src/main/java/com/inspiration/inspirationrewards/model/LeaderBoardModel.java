package com.inspiration.inspirationrewards.model;

/**
 * Created by zolipe on 31-Mar-19.
 */

public class LeaderBoardModel implements Comparable<LeaderBoardModel>{

    String studentId;
    String firstName;
    String lastName;
    String username;
    String department;
    String story;
    String position;
    String pointsToAward;
    String admin;
    String imageBytes;
    String location;
    String rewards;

    public LeaderBoardModel(String studentId, String firstName, String lastName, String username, String department, String story, String position, String pointsToAward, String admin, String imageBytes, String location, String rewards) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.pointsToAward = pointsToAward;
        this.admin = admin;
        this.imageBytes = imageBytes;
        this.location = location;
        this.rewards = rewards;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPointsToAward() {
        return pointsToAward;
    }

    public void setPointsToAward(String pointsToAward) {
        this.pointsToAward = pointsToAward;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(String imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    @Override
    public int compareTo(LeaderBoardModel lm){
        if (getRewards() == null || lm.getRewards() == null) {
            return 0;
        }
        int r1 = Integer.parseInt(getRewards());
        int r2 = Integer.parseInt(lm.getRewards());
        if (r1<r2){
            return 1;
        }
        return -1;
    }

}
