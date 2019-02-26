package com.example.liorkaramany.swaiterbeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Orders extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<Dish> orders;
    ListView list;
    DishList adapter;
    TextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Intent gt = getIntent();
        list = (ListView) findViewById(R.id.list);
        price = (TextView) findViewById(R.id.price);
        orders = (ArrayList<Dish>) gt.getSerializableExtra("orders");
        adapter = new DishList(this, orders);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        getSumOfPrices();
        //hi
    }



    private void getSumOfPrices() {
        double sum = 0;
        for (int i = 0; i < orders.size(); i++)
            sum += orders.get(i).getPrice();
        price.setText("Total price: "+sum);
    }

    public void back(View view) {
        passOrdersBack();
    }

    public void send(View view) {
        DatabaseReference r = FirebaseDatabase.getInstance().getReference("orders");

        String id = r.push().getKey();
        //Upload the list to the database.
        for (int i = 0; i < orders.size(); i++)
            r.child(id).child(""+i).setValue(orders.get(i));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        orders.remove(position);
        getSumOfPrices();
        Toast.makeText(this, "Item has been removed", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        passOrdersBack();
        super.onBackPressed();  // optional depending on your needs
    }

    private void passOrdersBack() {
        Intent t = getIntent();
        t.putExtra("orders", orders);
        setResult(RESULT_OK, t);
        finish();
    }
}
