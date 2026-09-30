package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class TaskStorage {
    private SharedPreferences prefs;
    private Gson gson;
    public String username;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public TaskStorage(Context context, String username) {
        this.prefs = context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.username = username;
    }
    public ArrayList<Task> loadAll()
    {
        // Get the JSON string from SharedPreferences
        String json = prefs.getString( this.username, null);
        if (json == null) return new ArrayList<>();

        ArrayList<Task> tasks = new ArrayList<>();
        // Convert the JSON string into a JsonArray
        // We are NOT converting directly to ArrayList<Book>
        // because we want to manually decide which subclass to create.
        com.google.gson.JsonArray jsonArray = gson.fromJson(json, com.google.gson.JsonArray.class);

        for (com.google.gson.JsonElement element : jsonArray)
        {
            // Convert element into JsonObject
            com.google.gson.JsonObject obj = element.getAsJsonObject();
            String TypeName = obj.get("TypeName").getAsString();

            Task task;

            // Based on the type value, we decide which class to create
            switch (TypeName) {
                case "שיעורי בית":
                    task = gson.fromJson(obj, HomeworkTask.class);
                    break;

                case "מבחן":
                    task = gson.fromJson(obj, ExamTask.class);
                    break;

                default:
                    task = gson.fromJson(obj, Task.class);
                    break;
            }

            tasks.add(task);
        }
        return tasks;
    }
    public void saveAll(ArrayList<Task> TaskList) {
        SharedPreferences.Editor editor = prefs.edit();
        //  יוצר ליסט של jsonobjects כדי שנוכל להכניס TYPE לכל JSON
        ArrayList<com.google.gson.JsonObject> jsonList = new ArrayList<>();

        for (Task task : TaskList) {

            // Convert the current book (PrintedBook/EBook/AudioBook)
            // into a JsonObject (not a String yet).
            // gson.toJsonTree(book) keeps ALL subclass fields (pages, sizeMB, Length...)
            com.google.gson.JsonObject obj =
                    gson.toJsonTree(task).getAsJsonObject();
            // VERY IMPORTANT:
            // We manually add a new field called "type"
            // This field will tell us later which class to rebuild.
            obj.addProperty("TypeName", task.GetTypeName());  // 👈 ADD TYPE INFO
            // Add the modified JsonObject into our list
            jsonList.add(obj);
        }

        // Convert the entire list into a JSON string
        String json = gson.toJson(jsonList);
        // שומר לSHAREDPERFERNCES
        editor.putString(this.username, json);
        editor.apply();
    }
    public void addTask(Task task)
    {
        ArrayList<Task> TaskList = this.loadAll();
        TaskList.add(task);
        this.saveAll(TaskList);
    }
    public Task findById(int findId)
    {
        ArrayList<Task> TaskList = this.loadAll();
        for (Task task : TaskList) {
            if (task.getId() == findId)
            {
                return task;
            }
        }
        return null;
    }
    public void updateTask(Task newTask) {
        ArrayList<Task> list = this.loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i).getId(), newTask.getId())) {
                list.set(i, newTask);
                break;
            }
        }
        saveAll(list);
    }

    public void deleteById(int delId) {
        ArrayList<Task> list = this.loadAll();
        list.removeIf(t -> t.getId() == delId);   // API 24+
        saveAll(list);
    }
    public int nextId()
    {
        int max = -1;
        for (Task t : this.loadAll()) max = Math.max(max, t.getId());
        return max + 1;
    }
    public boolean userExists() {
        return prefs.contains(username); // האם הוא מכיל את השם משתמש
    }

    public void createUser() {
        saveAll(new ArrayList<>());   // writes "[]" so the user now exists
    }

    public void deleteUser() {
        prefs.edit().remove(username).apply();   // only this user's data
    }
}
