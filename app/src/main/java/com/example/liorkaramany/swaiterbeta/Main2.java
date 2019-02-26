package com.example.liorkaramany.swaiterbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main2 extends AppCompatActivity {
    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void press1(View view) {
        num=1;
        Intent t=new Intent(this, Menu.class) ;
        t.putExtra("num",num);
        startActivity(t);
    }

    public void press2(View view) {
        num=2;
        Intent t=new Intent(this, Menu.class) ;
        t.putExtra("num",num);
        startActivity(t);
    }


}
