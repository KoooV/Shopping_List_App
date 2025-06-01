package com.example.myapplication.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import com.example.myapplication.R;
import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    DatabaseHelper db;
    EditText editTextListName;
    ImageButton btnAddList, btnLogout;
    ListView listViewLists;
    ArrayList<HashMap<String, String>> listCollection;
    SimpleAdapter adapter;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        if (!prefs.contains("username")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            User.currentUser = prefs.getString("username", null);
        }

        editTextListName = findViewById(R.id.editTextListName);
        btnAddList = findViewById(R.id.btnAddList);
        btnLogout = findViewById(R.id.btnLogout);
        listViewLists = findViewById(R.id.listViewLists);

        listCollection = new ArrayList<>();
        loadLists();

        btnAddList.setOnClickListener(v -> {
            String listName = editTextListName.getText().toString().trim();
            if (!listName.isEmpty()) {
                db.addShoppingList(User.currentUser, listName);
                editTextListName.setText("");
                loadLists();
            } else {
                Toast.makeText(this, getString(R.string.Cannot_add_an_empty_list), Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            User.currentUser = null;
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        listViewLists.setOnItemClickListener((parent, view, position, id) -> {
            // Mostrar diálogo con opciones
            int listId = Integer.parseInt(listCollection.get(position).get("id"));
            String listName = listCollection.get(position).get("name");

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(listName);
            builder.setItems(new CharSequence[]{getString(R.string.Open_list), getString(R.string.Delete_list)}, (dialog, which) -> {
                if (which == 0) {
                    // Abrir lista
                    Intent intent = new Intent(MainActivity.this, ListDetailsActivity.class);
                    intent.putExtra("list_id", String.valueOf(listId));
                    startActivity(intent);
                } else if (which == 1) {
                    // Borrar lista y sus productos
                    db.deleteShoppingList(listId);
                    loadLists();
                    Toast.makeText(MainActivity.this, getString(R.string.List_removed), Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        });
    }

    private void loadLists() {
        listCollection.clear();
        Cursor cursor = db.getShoppingLists(User.currentUser);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(cursor.getInt(0)));
            map.put("name", cursor.getString(1));
            listCollection.add(map);
        }
        adapter = new SimpleAdapter(this, listCollection, R.layout.item_shopping_list,
                new String[]{"name"}, new int[]{R.id.textListName});
        listViewLists.setAdapter(adapter);
    }
}



