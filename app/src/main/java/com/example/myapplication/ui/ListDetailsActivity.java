package com.example.myapplication.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.myapplication.R;
import com.example.myapplication.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

    public class ListDetailsActivity extends Activity {

    DatabaseHelper db;
    EditText editProduct, editPrice, editUnits;
    ImageButton btnAddProduct, btnUndo;
    ListView listView;
    ArrayList<HashMap<String, String>> productList;
    ProductAdapter adapter;
    int listId;
    HashMap<String, String> lastDeletedProduct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        db = new DatabaseHelper(this);
        listId = Integer.parseInt(getIntent().getStringExtra("list_id"));

        editProduct = findViewById(R.id.editTextProduct);
        editPrice = findViewById(R.id.editTextPrice);
        editUnits = findViewById(R.id.editTextUnits);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnUndo = findViewById(R.id.btnUndoDelete);
        listView = findViewById(R.id.listViewProducts);

        productList = new ArrayList<>();
        loadProducts();

        btnAddProduct.setOnClickListener(v -> {
            String name = editProduct.getText().toString().trim();
            String price = editPrice.getText().toString().trim();
            String units = editUnits.getText().toString().trim();
            if (!name.isEmpty() && !price.isEmpty() && !units.isEmpty()) {
                db.addProductToList(listId, name, price, units);
                editProduct.setText("");
                editPrice.setText("");
                editUnits.setText("");
                loadProducts();
            } else {
                Toast.makeText(this, getString(R.string.Complete_all_fields), Toast.LENGTH_SHORT).show();
            }
        });

        btnUndo.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.Refurbished_product), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProducts() {
        productList.clear();
        Cursor cursor = db.getProductsInList(listId);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(cursor.getInt(0)));
            map.put("name", cursor.getString(1));
            map.put("price", cursor.getString(2));
            map.put("units", cursor.getString(3));
            map.put("display", cursor.getString(1) + " - $" + cursor.getString(2) + " x " + cursor.getString(3));
            productList.add(map);
        }
        adapter = new ProductAdapter();
        listView.setAdapter(adapter);
    }

    private class ProductAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public Object getItem(int position) {
            return productList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Integer.parseInt(productList.get(position).get("id"));
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_product, parent, false);
            }

            TextView textProduct = convertView.findViewById(R.id.textProduct);
            ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

            HashMap<String, String> product = productList.get(position);
            textProduct.setText(product.get("display"));

            btnDelete.setOnClickListener(v -> {
                int productId = Integer.parseInt(product.get("id"));

                // Guardar el último producto eliminado
                lastDeletedProduct = new HashMap<>(product);

                db.deleteProduct(productId);
                loadProducts();
                Toast.makeText(ListDetailsActivity.this, getString(R.string.Product_removed), Toast.LENGTH_SHORT).show();
            });

            return convertView;
        }
    }
}


