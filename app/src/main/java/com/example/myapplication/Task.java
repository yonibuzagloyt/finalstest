package com.example.myapplication;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Task implements Rewardable
{
    private Integer id;
    private String title;
    private String subject;
    private String  priority;
    private String dueDate;
    private boolean done;
    public Task(Integer id, String title, String subject, String priority, String dueDate)
    {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.dueDate = dueDate;
        this.done = false;
    }
    public String GetTypeName()
    {
        return "Regular Task";
    }
    public int getPoints()
    {
        return this.getPriorityBonus();
    }
    protected int getPriorityBonus()
    {
        if (Objects.equals(this.priority, "גבוהה"))
        {
            return 5;
        }
        else if (Objects.equals(this.priority, "בינונית"))
        {
            return 3;
        }
        else
        {
            return 1;
        }
    }
    public String toString() {
        if (this.done)
        {
            return this.title + "\n סוג: " + this.GetTypeName() + "\n מקצוע: " + this.subject + "\n תאריך הגשה: " + this.dueDate + "\n סטטוס: בוצע" + "\n שווה " + this.getPoints() + " נקודות.";
        }
        else
        {
            return this.title + "\n סוג: " + this.GetTypeName() + "\n מקצוע: " + this.subject + "\n תאריך הגשה: " + this.dueDate + "\n סטטוס: פתוח" + "\n שווה " + this.getPoints() + " נקודות.";
        }
    }
    public Integer getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getPriority() {
        return this.priority;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public boolean isDone() {
        return this.done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
