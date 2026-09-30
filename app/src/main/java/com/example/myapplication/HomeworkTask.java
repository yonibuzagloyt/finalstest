package com.example.myapplication;

public class HomeworkTask extends Task
{
    private int exercises;
    public HomeworkTask(Integer id, String title, String subject, String priority, String dueDate, int exercises)
    {
        super(id, title, subject, priority, dueDate);
        this.exercises = exercises;
    }
    public String GetTypeName()
    {
        return "שיעורי בית";
    }
    public int getPoints()
    {
        return (this.exercises * 2) + this.getPriorityBonus();
    }
    public String toString() {
        if (this.isDone())
        {
            return this.getTitle() + "\n סוג: " + this.GetTypeName() + "\n מקצוע: " + this.getSubject() + "\n תאריך הגשה: " + this.getDueDate() + "\n תרגילים: " + this.exercises + "\n סטטוס: בוצע" + "\n שווה " + this.getPoints() + " נקודות.";
        }
        else
        {
            return this.getTitle() + "\n סוג: " + this.GetTypeName() + "\n מקצוע: " + this.getSubject() + "\n תאריך הגשה: " + this.getDueDate() + "\n תרגילים: " + this.exercises + "\n סטטוס: פתוח" + "\n שווה " + this.getPoints() + " נקודות.";
        }
    }
    public int getExercises() {
        return exercises;
    }

    public void setExercises(int exercises) {
        this.exercises = exercises;
    }
}
