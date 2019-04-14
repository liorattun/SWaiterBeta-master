/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.liorattun.swaiterbeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class OrdersList extends Activity {
    public static final int NUMBER_OF_ORDERS = 8;
    ListView[] o;
    int i;
    List<Dish>[] lists;
    DatabaseReference ref;
    DishListOrders[] adapters;
    String ph,smsText;
    Intent t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);


        o = new ListView[NUMBER_OF_ORDERS];

        o[0] = (ListView) findViewById(R.id.o1);
        o[1] = (ListView) findViewById(R.id.o2);
        o[2] = (ListView) findViewById(R.id.o3);
        o[3] = (ListView) findViewById(R.id.o4);
        o[4] = (ListView) findViewById(R.id.o5);
        o[5] = (ListView) findViewById(R.id.o6);
        o[6] = (ListView) findViewById(R.id.o7);
        o[7] = (ListView) findViewById(R.id.o8);



            o[0].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    i=0;
                    refresh();
                }
            });

           o[1].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                i=1;
                refresh();
            }
           });

          o[2].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                i=2;
                refresh();
            }
           });

          o[3].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
               i=3;
                refresh();
            }
        });

          o[4].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
               i=4;
                refresh();
            }
          });

          o[5].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
               i=5;
                refresh();
            }
          });

          o[6].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                i=6;
                refresh();
            }
          });

        o[7].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                i=7;
                refresh();
            }
        });


        lists = new ArrayList[NUMBER_OF_ORDERS];
        adapters = new DishListOrders[NUMBER_OF_ORDERS];

        for ( i = 0; i < NUMBER_OF_ORDERS; i++) {
            lists[i] = new ArrayList<>();
        }

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Clear all the lists.
                for (int i = 0; i < NUMBER_OF_ORDERS; i++)
                    lists[i].clear();
                //Get each list into its dedicated list.
                int i = 0;
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    if (i < NUMBER_OF_ORDERS) {
                        GenericTypeIndicator<ArrayList<Dish>> t = new GenericTypeIndicator<ArrayList<Dish>>() {
                        };

                        if (orderSnapshot.getValue(t) == null) {
                            lists[i] = new ArrayList<>();
                        } else
                            lists[i] = orderSnapshot.getValue(t);

                        adapters[i] = new DishListOrders(OrdersList.this, lists[i]);
                        o[i].setAdapter(adapters[i]);
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //send sms
    public void sms() {
        smsText = "Your order has been prepared and will arrive at your desk in a few minutes. enjoy your meal!";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(ph, null, smsText, null, null);

    }

    //Deleting Prepared Orders
    public void refresh(){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int x=0;
                for (DataSnapshot df:dataSnapshot.child("orders").getChildren()) {
                    if (x==i){
                        String name=df.getKey();
                        ref.child("orders").child(name).removeValue();
                        Toast.makeText(OrdersList.this,name,Toast.LENGTH_LONG).show();
                        ph = dataSnapshot.child("phones").child(name).getValue(String.class);
                        ref.child("phones").child(name).removeValue();
                        sms();
                        //bdikat kelet;
                    }
                    x++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

