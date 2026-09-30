package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity
{
    private ArrayList<Task> TaskList;
    private ArrayAdapter<String> filterAdapter;
    private ArrayAdapter<String> PriorityAdapter;
    private ArrayAdapter<String> TheOtherAdapter;
    // Create array of Strings and store the names of courses
    private String[] courses = {
            "מתמטיקה", "אנגלית",
            "מדעי המחשב", "פיזיקה",
            "היסטוריה"
    };
    private String[] priorities = {
            "נמוחה","בינונית", "גבוהה"
    };
    private String[] types = {
            "שיעורי בית","מבחן"
    };
    private String type;
    private String subject;
    private String priority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String username = getIntent().getStringExtra("USERNAME");
        TaskStorage ts = new TaskStorage(this, username);
        setContentView(R.layout.activity_addtask);
        Spinner editType = findViewById(R.id.spType);
        EditText editTitle = findViewById(R.id.etTitle);
        Spinner editSubject = findViewById(R.id.spSubject);
        Spinner editPriority = findViewById(R.id.spPriority);
        EditText editDueDate = findViewById(R.id.etDueDate);
        TextView tvAmountLabel = findViewById(R.id.tvAmountLabel);
        EditText editAmount = findViewById(R.id.etAmount);
        Button SaveButton = findViewById(R.id.btnSave);
        Button buttonBackToMain = findViewById(R.id.btnCancel);

        // קבלת הנתונים מה-Intent
        Intent intent = getIntent();
        filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSubject.setAdapter(filterAdapter);
        editSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = courses[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        PriorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        PriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editPriority.setAdapter(PriorityAdapter);
        editPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority = priorities[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        TheOtherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        TheOtherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editType.setAdapter(TheOtherAdapter);
        editType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = types[position];
                if (Objects.equals(type, "מבחן"))
                {
                    tvAmountLabel.setText("מספר נושאים למבחן:");
                    editAmount.setHint("למשל 3");
                }
                else
                {
                    tvAmountLabel.setText("מספר תרגילים:");
                    editAmount.setHint("למשל 8");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String dueDate = editDueDate.getText().toString();
                String amount = editAmount.getText().toString();
                // בדיקה: שם ריק
                if (title.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "יש להכניס כותרת", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "יש להכניס מספר נושאים למבחן", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Objects.equals(type, "מבחן"))
                {
                    ExamTask NewTask = new ExamTask(ts.nextId(), title, subject, priority, dueDate, Integer.parseInt(amount));
                    ts.addTask(NewTask);
                }
                else
                {
                    HomeworkTask NewTask = new HomeworkTask(ts.nextId(), title, subject, priority, dueDate, Integer.parseInt(amount));
                    ts.addTask(NewTask);
                }
                Toast.makeText(AddTaskActivity.this,
                        "Task added!", Toast.LENGTH_SHORT).show();
                // סיום ה-Activity וחזרה ל-MainActivity
                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
            }
        });
        buttonBackToMain.setOnClickListener(v -> finish());
    }
}
