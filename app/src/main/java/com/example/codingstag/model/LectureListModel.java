package com.example.codingstag.model;

public class LectureListModel {

    private String lectureNumber;
    private String lectureTitle;
    private String lectureUrl;

    public LectureListModel(){ }

    public LectureListModel(String lectureNumber, String lectureTitle, String lectureUrl) {
        this.lectureNumber = lectureNumber;
        this.lectureTitle = lectureTitle;
        this.lectureUrl = lectureUrl;
    }

    public String getLectureUrl() {
        return lectureUrl;
    }

    public void setLectureUrl(String lectureUrl) {
        this.lectureUrl = lectureUrl;
    }

    public String getLectureNumber() {
        return lectureNumber;
    }

    public void setLectureNumber(String lectureNumber) {
        this.lectureNumber = lectureNumber;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

}
