package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button buttonToSecondActivity = findViewById(R.id.btnEnter);
        Button DeleteBookButton = findViewById(R.id.btnReset);
        EditText editTitle = findViewById(R.id.etName);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        // Returning user: fill in the name and update the welcome text
        String lastUser = loadLastUser();
        if (lastUser != null) {
            editTitle.setText(lastUser);
            tvWelcome.setText("ברוך שובך, " + lastUser);
        }
        buttonToSecondActivity.setOnClickListener(v -> {
            String name = editTitle.getText().toString().trim();
            if (name.length() < 2) {
                Toast.makeText(MainActivity.this, "יש להכניס שם של 2 תווים לפחות!", Toast.LENGTH_SHORT).show();
                return;
            }

            TaskStorage storage = new TaskStorage(MainActivity.this, name);
            if (!storage.userExists()) storage.createUser();
            saveLastUser(name);

            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra("USERNAME", name);
            startActivity(intent);
        });
        DeleteBookButton.setOnLongClickListener(v -> {
            showConfirmDialog();
            return true; // consume the long click
        });
    }
    private void showFinalHoldDialog() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("אזהרה אחרונה!!")
                .setMessage("הנתונים שלך לא מגובשים! לא ניתן לבטל פעולה זו! את/ה בטוח/ה שאת/ה רוצה למחוק את הנתונים שלך?")
                .setPositiveButton("לא משנה..", null)
                .setNegativeButton("כן!", null)
                .create();

        dialog.show();

        Button btnYes = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnYes.setOnClickListener(v -> {
        });
        btnYes.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            String last = loadLastUser();
            if (last != null) {
                new TaskStorage(MainActivity.this, last).deleteUser();
                lastUserPrefs().edit().clear().apply();
            }
            startActivity(intent);
            Toast.makeText(MainActivity.this, "כל הנתונים אופסו", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return true;
        });
    }
    private void showConfirmDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("איפוס נתונים")
                .setMessage("כל המשימות והשם יימחקו. להמשיך?")
                .setPositiveButton("כן", (d, w) -> {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("הנתונים לא יכולו להשתכזר מתי שאת/ה מוחק/ת אותם! את/ה בטוח/ה בזה?")
                            .setPositiveButton("כן", (dd, ww) -> {
                                showFinalHoldDialog();
                            })
                            .setNegativeButton("לא", null).show();

                }).setNegativeButton("ביטול", null).show();
    }
    private SharedPreferences lastUserPrefs() {
        return getSharedPreferences("last_user_prefs", MODE_PRIVATE);
    }

    // The file holds at most one entry: the username itself
    private String loadLastUser() {
        Set<String> names = lastUserPrefs().getAll().keySet();
        return names.isEmpty() ? null : names.iterator().next();
    }

    private void saveLastUser(String name) {
        // clear() always runs first, so only the newest name remains
        lastUserPrefs().edit().clear().putBoolean(name, true).apply();
    }
}