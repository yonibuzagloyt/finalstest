package com.example.myapplication;

public class ExamTask extends Task
{
    private int topics;
    public ExamTask(Integer id, String title, String subject, String priority, String dueDate, int topics)
    {
        super(id, title, subject, priority, dueDate);
        this.topics = topics;
    }
    public String GetTypeName()
    {
        return "מבחן";
    }
    public int getPoints()
    {
        return 10 + (this.topics * 5) + this.getPriorityBonus();
    }
    public String toString() {
        if (this.isDone())
        {
            return this.getTitle() + "\n סוג: " + this.GetTypeName() + "\n מקצוע: " + this.getSubject() + "\n תאריך הגשה: " + this.getDueDate() + "\n נושאים למבחן: " + this.topics + "\n סטטוס: בוצע" + "\n שווה " + this.getPoints() + " נקודות.";
        }
        else
        {
            return this.getTitle() + "\n סוג: " + this.GetTypeName() + "\n מקצוע: " + this.getSubject() + "\n תאריך הגשה: " + this.getDueDate() + "\n נושאים למבחן: " + this.topics + "\n סטטוס: פתוח" + "\n שווה " + this.getPoints() + " נקודות.";
        }
    }
    public int getTopics() {
        return topics;
    }

    public void setTopics(int topics) {
        this.topics = topics;
    }
}
