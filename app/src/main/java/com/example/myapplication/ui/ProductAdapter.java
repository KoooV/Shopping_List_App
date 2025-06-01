package com.example.myapplication.ui;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.myapplication.R;
import com.example.myapplication.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;
    private DatabaseHelper db;
    private int listId;

    public ProductAdapter(Context context, ArrayList<HashMap<String, String>> data, DatabaseHelper db, int listId) {
        this.context = context;
        this.data = data;
        this.db = db;
        this.listId = listId;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int i) { return data.get(i); }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(data.get(i).get("id"));
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);

        TextView textProduct = view.findViewById(R.id.textProduct);
        ImageButton btnDelete = view.findViewById(R.id.btnDelete);

        HashMap<String, String> product = data.get(i);
        textProduct.setText(product.get("display"));

        btnDelete.setOnClickListener(v -> {
            int productId = Integer.parseInt(product.get("id"));
            db.deleteProduct(productId);
            ((ListDetailsActivity) context).lastDeletedProduct = new HashMap<>(product);
            data.remove(i);
            notifyDataSetChanged();

        });

        return view;
    }
}

