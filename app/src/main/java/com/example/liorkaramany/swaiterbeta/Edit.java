package com.example.liorkaramany.swaiterbeta;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Edit extends AppCompatActivity {

    static final int PICK_IMAGE_REQUEST = 1;
    ProgressBar progressBar;
    EditText name, price;
    DatabaseReference df;
    StorageReference ref;
    Intent gt;

    String id;
    String n;
    String url;
    double p;
    int choice;
    ImageView img;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        name = (EditText) findViewById(R.id.name);
        price = (EditText) findViewById(R.id.price);
        img = (ImageView) findViewById(R.id.img);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        gt = getIntent();
        id = gt.getStringExtra("id");
        n = gt.getStringExtra("name");
        p = gt.getDoubleExtra("price", 0);
        choice = gt.getIntExtra("choice", 1);
        url = gt.getStringExtra("url");

        switch (choice)
        {
            case 1: df = FirebaseDatabase.getInstance().getReference("dishes").child("main").child(id); break;
            case 2: df = FirebaseDatabase.getInstance().getReference("dishes").child("desserts").child(id); break;
            case 3: df = FirebaseDatabase.getInstance().getReference("dishes").child("drinks").child(id); break;
        }

        ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        Picasso.get().load(url).into(img);


        name.setText(n);
        price.setText(""+p);

    }

    public void save(View view) {

        if (!name.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !price.getText().toString().equals(".")) {

            n = name.getText().toString();
            p = Double.parseDouble(price.getText().toString());

            Dish d = new Dish(id, n, p, url);

            df.setValue(d);

            if (imageUri != null) {

                ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Dish d = new Dish(id, n, p, uri.toString());
                                df.setValue(d);

                                progressBar.setProgress(0);

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Edit.this, "Push failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
            }

            finish();
        }
        else
            Toast.makeText(this, "You didn't enter all the information", Toast.LENGTH_SHORT).show();

    }

    public void cancel(View view) {
        finish();
    }

    public void select(View view) {
        Intent t = new Intent();
        t.setType("image/*");
        t.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(t, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }

    }
}
