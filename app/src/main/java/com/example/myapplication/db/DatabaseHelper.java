package com.example.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT)");

        // Crear tabla de listas de compras
        db.execSQL("CREATE TABLE shopping_lists (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "name TEXT)");

        // Crear tabla de productos en listas
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "list_id INTEGER, " +
                "name TEXT, " +
                "price TEXT, " +
                "units TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Borrar tablas existentes
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS shopping_lists");
        db.execSQL("DROP TABLE IF EXISTS products");

        // Volver a crear todas las tablas
        onCreate(db);
    }

    // REGISTRO DE USUARIO
    public boolean registerUser(String username, String password) {
        if (checkUserExists(username)) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    // VERIFICAR USUARIO EXISTENTE
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, "username=?", new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // LOGIN
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, "username=? AND password=?", new String[]{username, password}, null, null, null);
        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }

    // AGREGAR LISTA
    public void addShoppingList(String username, String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("name", listName);
        db.insert("shopping_lists", null, values);
    }

    // OBTENER LISTAS DE USUARIO
    public Cursor getShoppingLists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id, name FROM shopping_lists WHERE username = ?", new String[]{username});
    }

    // AGREGAR PRODUCTO A LISTA
    public void addProductToList(int listId, String name, String price, String units) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("list_id", listId);
        values.put("name", name);
        values.put("price", price);
        values.put("units", units);
        db.insert("products", null, values);
    }

    // OBTENER PRODUCTOS DE UNA LISTA
    public Cursor getProductsInList(int listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id, name, price, units FROM products WHERE list_id = ?", new String[]{String.valueOf(listId)});
    }

    // ELIMINAR PRODUCTO
    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("products", "id = ?", new String[]{String.valueOf(productId)});
    }

    // ELIMINAR LISTA
    public void deleteShoppingList(int listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("products", "list_id = ?", new String[]{String.valueOf(listId)});
        db.delete("shopping_lists", "id = ?", new String[]{String.valueOf(listId)});
    }

}



