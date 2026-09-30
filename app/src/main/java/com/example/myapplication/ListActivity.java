//package com.example.myapplication;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class ListActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list);
//
//        TextView textViewWelcome = findViewById(R.id.textViewWelcome);
//        Button buttonBackToMain = findViewById(R.id.buttonBackToMain);
//
//        // קבלת הנתונים מה-Intent
//        Intent intent = getIntent();
//        String username = intent.getStringExtra("USERNAME");
//
//        // הצגת הנתונים על המסך
//        textViewWelcome.setText("Welcome, " + username + "!");
//
//        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // סיום ה-Activity וחזרה ל-MainActivity
//                finish();
//            }
//        });
//    }
//}
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private static final String ALL = "הכל";

    private TaskStorage storage;

    private TextView tvStats;
    private Spinner spFilter;

    private ArrayList<Task> allTasks = new ArrayList<>();          // everything this user has
    private final ArrayList<Task> shownTasks = new ArrayList<>();  // after the subject filter
    private final ArrayList<String> subjects = new ArrayList<>();  // spinner items, index 0 = ALL

    private ArrayAdapter<Task> taskAdapter;
    private ArrayAdapter<String> filterAdapter;
    private String selectedSubject = ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);

        // Keep the layout's own padding and add the system bars on top of it
        View root = findViewById(R.id.main);
        int l = root.getPaddingLeft(), t = root.getPaddingTop();
        int r = root.getPaddingRight(), b = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(l + bars.left, t + bars.top, r + bars.right, b + bars.bottom);
            return insets;
        });

        // The name arrives from MainActivity
        String username = getIntent().getStringExtra("USERNAME");
        if (username == null) {   // opened without a name, nothing to show
            finish();
            return;
        }
        storage = new TaskStorage(this, username);

        TextView tvHello = findViewById(R.id.tvHello);
        tvStats = findViewById(R.id.tvStats);
        TextView tvEmpty = findViewById(R.id.tvEmpty);
        spFilter = findViewById(R.id.spFilter);
        ListView lvTasks = findViewById(R.id.lvTasks);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvHello.setText("שלום " + username + "!");

        // The list uses Task.toString() for each row
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shownTasks);
        lvTasks.setAdapter(taskAdapter);
        lvTasks.setEmptyView(tvEmpty);

        filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);
        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = subjects.get(position);
                showFiltered();   // only re-filters; it never rebuilds the spinner, so no loop
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Tap: open the task (screen 4)
        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            // position is the place in the FILTERED list, so take the task itself and use its id
            Task task = shownTasks.get(position);
            // TODO screen 4: pass task.getId() to it
            Toast.makeText(this, task.getTitle(), Toast.LENGTH_SHORT).show();
        });

        // Long press: delete dialog
        lvTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(shownTasks.get(position));
            return true;
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, AddTaskActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            reload();
        });

        // Back to screen 1, where the name is already filled in
        btnLogout.setOnClickListener(v -> finish());
    }

    // Also runs when coming back from the add/edit screens, so the list refreshes itself
    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    // Reads storage again, rebuilds the spinner, keeps the current filter if it still exists
    private void reload() {
        allTasks = storage.loadAll();

        subjects.clear();
        subjects.add(ALL);
        // הוספת מקצועות קבועים מראש
        subjects.add("מתמטיקה");
        subjects.add("אנגלית");
        subjects.add("היסטוריה");
        subjects.add("מדעי המחשב");
        subjects.add("פיזיקה");

        for (Task task : allTasks) {
            if (!subjects.contains(task.getSubject())) {
                subjects.add(task.getSubject());
            }
        }
        filterAdapter.notifyDataSetChanged();

        int index = subjects.indexOf(selectedSubject);
        if (index < 0) {          // the last task of that subject was deleted
            index = 0;
            selectedSubject = ALL;
        }
        spFilter.setSelection(index);

        showFiltered();
        updateStats();
    }

    private void showFiltered() {
        shownTasks.clear();
        for (Task task : allTasks) {
            if (selectedSubject.equals(ALL) || selectedSubject.equals(task.getSubject())) {
                shownTasks.add(task);
            }
        }
        taskAdapter.notifyDataSetChanged();
    }

    // Stats cover all of the user's tasks, not just the filtered ones.
    // Points count only completed tasks.
    private void updateStats() {
        int done = 0;
        int points = 0;
        for (Task task : allTasks) {
            if (task.isDone()) {
                done++;
                points += task.getPoints();
            }
        }
        tvStats.setText("משימות: " + allTasks.size()
                + " | הושלמו: " + done
                + " | נקודות: " + points);
    }

    private void confirmDelete(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת משימה")
                .setMessage("למחוק את \"" + task.getTitle() + "\"?")
                .setPositiveButton("מחק", (d, w) -> {
                    new AlertDialog.Builder(ListActivity.this)
                            .setTitle("אזהרה אחרונה! בחר/י ברצינות!")
                            .setMessage("המשימה לא תוכל להשתכזר מתי שאת/ה מוחק/ת אותה! את/ה בטוח/ה בזה?")
                            .setPositiveButton("לא", null)
                            .setNegativeButton("כן", (dd, ww) -> {
                                Toast.makeText(ListActivity.this, "המשימה " + task.getTitle() + " - " + task.getSubject() + "נמחקה.", Toast.LENGTH_SHORT).show();
                                storage.deleteById(task.getId());
                                reload();
                            }).show();
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}
