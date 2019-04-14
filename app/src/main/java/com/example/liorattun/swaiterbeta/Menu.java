package com.example.liorattun.swaiterbeta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity implements View.OnCreateContextMenuListener, AdapterView.OnItemClickListener {

    ListView menu;
    DatabaseReference df, d1, d2, d3;
    int choice = 1;
    List<Dish> mainList, dessertsList, drinksList;
    ArrayList<Dish> dishes;
    DatabaseReference ref;
    Button bt;
    int num1,id;
    Boolean master;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu = (ListView) findViewById(R.id.menu);
        bt = (Button) findViewById(R.id.bt);

        Intent t = getIntent();
        num1 = t.getIntExtra("num", 1);
        if (num1 == 2) master = true;
        else master = false;

        if (master) bt.setText("New dish");
        else bt.setText("Your orders ->");

        mainList = new ArrayList<>();
        dessertsList = new ArrayList<>();
        drinksList = new ArrayList<>();
        dishes = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("orders");
        df = FirebaseDatabase.getInstance().getReference("dishes");
        d1 = df.child("main");
        d2 = df.child("desserts");
        d3 = df.child("drinks");


        menu.setOnCreateContextMenuListener(this);
        menu.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null)
            dishes = (ArrayList<Dish>) data.getSerializableExtra("orders");

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get all the children from the database an display them in the ListView.
        if (choice == 1) {
            d1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mainList.clear();

                    for (DataSnapshot dishSnapshot : dataSnapshot.getChildren()) {
                        Dish dish = dishSnapshot.getValue(Dish.class);

                        mainList.add(dish);
                    }

                    DishList adapter = new DishList(Menu.this, mainList);
                    menu.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (choice == 2) {
            d2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    dessertsList.clear();

                    for (DataSnapshot dishSnapshot : dataSnapshot.getChildren()) {
                        Dish dish = dishSnapshot.getValue(Dish.class);

                        dessertsList.add(dish);
                    }

                    DishList adapter = new DishList(Menu.this, dessertsList);
                    menu.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            d3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    drinksList.clear();

                    for (DataSnapshot dishSnapshot : dataSnapshot.getChildren()) {
                        Dish dish = dishSnapshot.getValue(Dish.class);

                        drinksList.add(dish);
                    }

                    DishList adapter = new DishList(Menu.this, drinksList);
                    menu.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (master) {
            menu.setHeaderTitle("Options");
            menu.add("Edit");
            menu.add("Delete");
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (master) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            String id = "";
            String url = "";
            switch (choice) {
                case 1:
                    id = mainList.get(index).getId();
                    url = mainList.get(index).getUrl();
                    break;
                case 2:
                    id = dessertsList.get(index).getId();
                    url = dessertsList.get(index).getUrl();
                    break;
                case 3:
                    id = drinksList.get(index).getId();
                    url = drinksList.get(index).getUrl();
                    break;
            }

            String oper = item.getTitle().toString();
            if (oper.equals("Delete")) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                ref.delete();
                deleteDish(id);
            } else if (oper.equals("Edit")) {
                editDish(index);
            }

        }
        return super.onContextItemSelected(item);

    }


    public void deleteDish(String id) {
        if (master) {
            DatabaseReference dr;
            switch (choice) {
                case 1:
                    dr = FirebaseDatabase.getInstance().getReference("dishes").child("main").child(id);
                    dr.removeValue();
                    break;
                case 2:
                    dr = FirebaseDatabase.getInstance().getReference("dishes").child("desserts").child(id);
                    dr.removeValue();
                    break;
                case 3:
                    dr = FirebaseDatabase.getInstance().getReference("dishes").child("drinks").child(id);
                    dr.removeValue();
                    break;
            }


            Toast.makeText(this, "Dish has been removed", Toast.LENGTH_SHORT).show();
        }
    }

    public void editDish(int index) {
        if (master) {
            Dish d = new Dish();
            switch (choice) {
                case 1:
                    d = mainList.get(index);
                    break;
                case 2:
                    d = dessertsList.get(index);
                    break;
                case 3:
                    d = drinksList.get(index);
                    break;
            }

            Intent t = new Intent(this, Edit.class);

            //Send the arguments as separate values:
            t.putExtra("id", d.getId());
            t.putExtra("name", d.getName());
            t.putExtra("price", d.getPrice());
            t.putExtra("url", d.getUrl());
            t.putExtra("choice", choice);
            startActivity(t);
        }
    }

    public void main(View view) {
        choice = 1;
        onStart();
    }

    public void desserts(View view) {
        choice = 2;
        onStart();
    }

    public void drinks(View view) {
        choice = 3;
        onStart();
    }

    public void back(View view) {
        finish();
    }

    public void orders(View view) {
        Intent t;
        if (!master) {
            t = new Intent(this, Orders.class);
            //samim et hareshima shell dishes ba Intent.
            t.putExtra("orders", dishes);
            startActivityForResult(t, 1);
        } else {
            t = new Intent(this, Main.class);
            startActivity(t);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (!master) {
            Dish dish = null;
            switch (choice) {
                case 1:
                    dish = mainList.get(position);
                    break;
                case 2:
                    dish = dessertsList.get(position);
                    break;
                case 3:
                    dish = drinksList.get(position);
                    break;
            }
            dishes.add(dish);
            Toast.makeText(this, "Item has been added", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        if(master) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(master) {
            id = item.getItemId();
            if(id==R.id.orders) {
                Intent s = new Intent(this, OrdersList.class);
                s.putExtra("s",1);
                startActivity(s);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}