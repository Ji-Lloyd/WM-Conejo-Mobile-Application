package com.capstone.wmconejoelpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class RabbitManagementActivity extends DrawerBaseActivity {

    TextView name;

    ImageButton entry,breeding,feeding,health;

    DocumentReference documentRabbitReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_management, contentFrameLayout);

        getSupportActionBar().setTitle("Rabbit Management");

        name = findViewById(R.id.tvRabbitName);

        entry = findViewById(R.id.btnRabbitEntry);
        breeding = findViewById(R.id.btnBreedingManagement);
        feeding = findViewById(R.id.btnFeedingManagement);
        health = findViewById(R.id.btnHealthManagement);


        try{
            Intent getIntent = getIntent();
            String rabbitName = getIntent.getStringExtra("rabbitName");
            name.setText(rabbitName);
        }catch (Exception exception){
            Log.d("TAG",exception.getMessage());
        }

        try{
            entry_function();
            breeding_function();
            feeding_function();
            health_function();
        }catch (Exception ex){
            Toast.makeText(this, "onCreate Error: \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println(ex.getMessage());
        }
    }


    private void entry_function(){
        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent getIntent = getIntent();
                    String rabbitName = getIntent.getStringExtra("rabbitName");
                    Intent intent = new Intent(RabbitManagementActivity.this, RabbitEntryActivity.class);
                    intent.putExtra("rabbitName", rabbitName);
                    intent.putExtra("rabbitShow",true);
                    startActivity(intent);
                }catch (Exception exception){
                    System.err.println("Exception: " + exception.getMessage());
                }
            }
        });
    }

    String rabbitGender;
    private void breeding_function() {
        breeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent getIntent = getIntent();
                    String rabbitName = getIntent.getStringExtra("rabbitName");
                    documentRabbitReference = db.document("Rabbit/" + rabbitName);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        RabbitEntryData data = documentSnapshot.toObject(RabbitEntryData.class);
                                        String gender = data.getGender();
                                        if(gender.equals("Buck")){
                                            Intent intent = new Intent(RabbitManagementActivity.this, RabbitBreedingActivity.class);
                                            intent.putExtra("sireName", rabbitName);
                                            startActivity(intent);
                                        }else if(gender.equals("Doe")){
                                            Intent intent = new Intent(RabbitManagementActivity.this, RabbitBreedingActivity.class);
                                            intent.putExtra("damName", rabbitName);
                                            startActivity(intent);
                                        }
                                    }else {
                                        startActivity(new Intent(RabbitManagementActivity.this, RabbitBreedingActivity.class));
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    startActivity(new Intent(RabbitManagementActivity.this, RabbitBreedingActivity.class));
                                }
                            });

                }catch (Exception exception){
                    System.err.println("Exception: " + exception.getMessage());
                }
            }
        });
    }

    private void feeding_function(){
        feeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent getIntent = getIntent();
                    String rabbitName = getIntent.getStringExtra("rabbitName");
                    Intent intent = new Intent(RabbitManagementActivity.this, FeedingNutritionActivity.class);
                    intent.putExtra("rabbitName", rabbitName);
                    startActivity(intent);
                }catch (Exception exception){
                    System.err.println("Exception: " + exception.getMessage());
                }
            }
        });
    }

    private void health_function(){
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent getIntent = getIntent();
                    String rabbitName = getIntent.getStringExtra("rabbitName");
                    Intent intent = new Intent(RabbitManagementActivity.this, RabbitHealthActivity.class);
                    intent.putExtra("rabbitName", rabbitName);
                    startActivity(intent);
                }catch (Exception exception){
                    System.err.println("Exception: " + exception.getMessage());
                }
            }
        });
    }




}