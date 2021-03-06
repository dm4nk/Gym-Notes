package com.dm4nk.gym_notes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dm4nk.gym_notes.domain.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Exercise> exerciseArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FloatingActionButton add_button, calculate_button;
    private DatabaseHelper myDB;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivityForResult(intent, 1);
        });

        calculate_button = findViewById(R.id.calculate_button);
        calculate_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
            startActivityForResult(intent, 2);
        });

        myDB = new DatabaseHelper(MainActivity.this);

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this, this, exerciseArrayList);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    public void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                exerciseArrayList.add(
                        Exercise.builder()
                                .id(cursor.getInt(0))
                                .name(cursor.getString(1))
                                .sets(cursor.getInt(2))
                                .reps(cursor.getInt(3))
                                .weight(cursor.getString(4))
                                .date(cursor.getLong(5))
                                .url(cursor.getString(6))
                                .build()
                );
            }
        }
    }
}