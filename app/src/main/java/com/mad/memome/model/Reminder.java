package com.mad.memome.model;

//import com.orm.SugarRecord;

/**
 * The reminder model calss
 */
public class Reminder   {
    private int id;
    private String title;
    private String content;
    private String mend_dateAndTime;
    private String mstart_dateAndTime;
    private  String location;
    private  String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDateAndTime() {

        return mstart_dateAndTime;
    }
    public String getEndDateAndTime() {
        return mend_dateAndTime;
    }


    public Reminder setEndDateAndTime(String dateAndTime) {
        this.mend_dateAndTime = dateAndTime;
        return this;
    }
    public Reminder setStartDateAndTime(String dateAndTime) {
        this.mstart_dateAndTime = dateAndTime;
        return this;
    }

    public int getId() {
        return id;
    }

    public Reminder setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Reminder setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Reminder setContent(String content) {
        this.content = content;
        return this;
    }

    public Reminder(String title, String content, String mend_dateAndTime, String mstart_dateAndTime, String location) {
        this.title = title;
        this.content = content;
        this.mend_dateAndTime = mend_dateAndTime;
        this.mstart_dateAndTime = mstart_dateAndTime;
        this.location = location;
        this.status="Undone";
    }

    public Reminder(String title, String content, String mend_dateAndTime, String mstart_dateAndTime, String location,int id) {
        this.title = title;
        this.content = content;
        this.mend_dateAndTime = mend_dateAndTime;
        this.mstart_dateAndTime = mstart_dateAndTime;
        this.location = location;
        this.status="Undone";
        this.id=id;
    }

    public Reminder() {
    }


}
