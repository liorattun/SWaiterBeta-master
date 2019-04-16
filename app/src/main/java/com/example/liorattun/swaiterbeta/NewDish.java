package com.example.liorattun.swaiterbeta;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewDish extends AppCompatActivity {

    static final int PICK_IMAGE_REQUEST = 1;
    DatabaseReference df, d1, d2, d3;
    StorageReference ref;
    EditText name, price;
    RadioButton main, desserts, drinks;
    RadioGroup group;
    ImageView img;
    Uri imageUri;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dish);

        df=FirebaseDatabase.getInstance().getReference("dishes");
        ref=FirebaseStorage.getInstance().getReference("images");
        d1=df.child("main");
        d2=df.child("desserts");
        d3=df.child("drinks");

        name = (EditText) findViewById(R.id.name);
        price = (EditText) findViewById(R.id.price);
        img = (ImageView) findViewById(R.id.img);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        group = (RadioGroup) findViewById(R.id.group);
        main = (RadioButton) findViewById(R.id.main);
        desserts = (RadioButton) findViewById(R.id.desserts);
        drinks = (RadioButton) findViewById(R.id.drinks);

        //Ask the user to grant necessary permissions.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };

            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
    }

    //Check for permissions.
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void push(View view) {
        final String n = name.getText().toString();
        final String p = price.getText().toString();

        if (!n.isEmpty() && !p.isEmpty() && !p.equals(".") && (main.isChecked() || desserts.isChecked() || drinks.isChecked()) && imageUri != null)
        {
            final String id;
            if (main.isChecked())
                id = d1.push().getKey();
            else if (desserts.isChecked())
                id = d2.push().getKey();
            else
                id = d3.push().getKey();

            ref.child(id).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Dish d = new Dish(id, n, Double.parseDouble(p), uri.toString());

                            if (main.isChecked()) {
                                d1.child(id).setValue(d);
                            }
                            else if (desserts.isChecked()) {
                                d2.child(id).setValue(d);
                            }
                            else {
                                d3.child(id).setValue(d);
                            }

                            Toast.makeText(NewDish.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            progressBar.setProgress(0);

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewDish.this, "Push failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress  = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });
        }
        else
            Toast.makeText(this, "You didn't enter all the information", Toast.LENGTH_SHORT).show();
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

    public void ba(View view) {
        Intent t= new Intent(this,Menu.class);
        startActivity(t);

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main2,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if (id==R.id.credits){
            Intent s = new Intent(this, Credits.class);
            s.putExtra("s",1);
            startActivity(s);
        }
        return super.onOptionsItemSelected(item);
    }
}
