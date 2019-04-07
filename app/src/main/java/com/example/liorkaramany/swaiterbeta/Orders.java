package com.example.liorkaramany.swaiterbeta;

import android.app.Dialog;
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
    AlertDialog.Builder adb;
    EditText phnum,email;
    LinearLayout myDialog;
    String mail,phone,y;
    Intent t;

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
        t= new Intent(this,OrdersList.class);
    }

    //AlertDialog jump
    public void start(final View view){
       myDialog=(LinearLayout)getLayoutInflater().inflate(R.layout.alert_layout,null);
       email=(EditText)myDialog.findViewById(R.id.email);
       phnum=(EditText)myDialog.findViewById(R.id.phnum);
       adb=new AlertDialog.Builder(this);
       adb.setView(myDialog);
       adb.setTitle("");
       adb.setMessage("The order process is almost complete. If you would like more information / updates on the status of your order please leave the following details:");
       adb.setPositiveButton("done", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               phone=phnum.getText().toString();
               mail=email.getText().toString();
               //em();
               tofb();
           }
       });
       adb.setNegativeButton("continue", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               tofb();
           }
       });
       adb.setNeutralButton("back", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.cancel();
           }
       });
       AlertDialog ad=adb.create();
       ad.show();
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
        start(view);
    }

    public void tofb(){
        DatabaseReference r = FirebaseDatabase.getInstance().getReference("orders");
        DatabaseReference phr = FirebaseDatabase.getInstance().getReference("phones");
        String id = r.push().getKey();
        //Upload the list to the database.
        for (int i = 0; i < orders.size(); i++) {
            r.child(id).child("" + i).setValue(orders.get(i));
        }
        if (phone != null)
            phr.child(id).setValue(phone);
        Toast.makeText(this, "Your order has been transferred to the kitchen and will begin to be prepared. Please be patient and give the waiter the tablet.", Toast.LENGTH_LONG).show();
    }

    public void ts( ArrayList<Dish> orders){
        String x;
        for (int i=0;i<orders.size();i++){
            x=orders.get(i).getName();
            y+=x+"/n";

        }
    }

    //E-mail send
    public void em() {
        String fromEmail = "smrtwaiter@gmail.com";
        String fromPassword = "la12062001";
        String toEmails = mail;
        String emailSubject = "YOUR ORDER";
        //String adminSubject = "App Registration Mail";
        y="";
        ts(orders);
        String emailBody = y;
        //String adminBody = "Your message";
        new SendMailTask(Orders.this).execute(fromEmail,
                fromPassword, toEmails, emailSubject, emailBody);
    }


    //remove item from the order list
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
