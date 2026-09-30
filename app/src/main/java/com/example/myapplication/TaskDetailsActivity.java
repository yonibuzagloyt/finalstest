package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TaskDetailsActivity extends AppCompatActivity {
    private TaskStorage TaskList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskdetails);
        TextView title = findViewById(R.id.title);
        TextView textViewWelcome = findViewById(R.id.textViewWelcome);
        Button buttonBackToMain = findViewById(R.id.btnBackToMain);
        Button buttonConfirm = findViewById(R.id.btnConfirm);
        Button DeleteBookButton = findViewById(R.id.btnDel);


        // קבלת הנתונים מה-Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        int position = intent.getIntExtra("ID", 0);
        TaskList = new TaskStorage(this, username);
        Task SelTask = TaskList.findById(position);
        // הצגת הנתונים על המסך
        title.setText(SelTask.getTitle());
        textViewWelcome.setText(SelTask.toString());
        buttonBackToMain.setOnClickListener(v -> finish());

        DeleteBookButton.setOnLongClickListener(v -> {
            confirmDelete(SelTask);
            return true;
        });
        buttonConfirm.setOnClickListener(v -> {
            if (SelTask.isDone())
            {
                SelTask.setDone(false);
                TaskList.updateTask(SelTask);
                buttonConfirm.setText("סמן כבוצע");
                Toast.makeText(this, "הסימון בוטל", Toast.LENGTH_SHORT).show();
                textViewWelcome.setText(SelTask.toString());
            }
            else if (!SelTask.isDone())
            {
                SelTask.setDone(true);
                TaskList.updateTask(SelTask);
                buttonConfirm.setText("בטל סימון");
                Toast.makeText(this, " כל הכבוד! הרווחת " + SelTask.getPoints() + " נקודות.", Toast.LENGTH_SHORT).show();
                textViewWelcome.setText(SelTask.toString());
            }
        });
    }
    private void confirmDelete(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת משימה")
                .setMessage("למחוק את \"" + task.getTitle() + "\"?")
                .setPositiveButton("מחק", (d, w) -> {
                    new AlertDialog.Builder(TaskDetailsActivity.this)
                            .setTitle("אזהרה אחרונה! בחר/י ברצינות!")
                            .setMessage("המשימה לא תוכל להשתכזר מתי שאת/ה מוחק/ת אותה! את/ה בטוח/ה בזה?")
                            .setPositiveButton("לא", null)
                            .setNegativeButton("כן", (dd, ww) -> {
                                Toast.makeText(TaskDetailsActivity.this, "המשימה " + task.getTitle() + " - " + task.getSubject() + "נמחקה.", Toast.LENGTH_SHORT).show();
                                TaskList.deleteById(task.getId());
                                Intent result = new Intent();
                                setResult(RESULT_OK, result);
                                finish();
                            }).show();
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

}

