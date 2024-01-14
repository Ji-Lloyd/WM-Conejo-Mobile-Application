package com.capstone.wmconejoelpatio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DrawerBaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<Void> collectionRabbitReference;
    DocumentReference documentRabbitReference;
    StorageReference storageRabbitReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.dashboardBottomAppNavigation);

        floatingActionButton = findViewById(R.id.drawerFloatingActionButton);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#5F96FB"));
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"));
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_dashboard:
                        setTitle("Dashboard");
                        Intent dashboard = new Intent(getApplicationContext(), RabbitDashboardActivity.class);
                        startActivity(dashboard);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_home:
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(home);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_qr:
                        try{
                            scanner_function();
                        }catch (Exception ex){
                            Toast.makeText(DrawerBaseActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(ex.getMessage());
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_rabbit_list:
                        Intent list = new Intent(getApplicationContext(), RabbitListsActivity.class);
                        startActivity(list);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_entry:
                        Intent entry = new Intent(getApplicationContext(), RabbitEntryActivity.class);
                        startActivity(entry);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_breeding:
                        Intent breeding = new Intent(getApplicationContext(), RabbitBreedingActivity.class);
                        startActivity(breeding);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_feeding:
                        Intent feeding = new Intent(getApplicationContext(), FeedingNutritionActivity.class);
                        startActivity(feeding);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_health:
                        Intent health = new Intent(getApplicationContext(), RabbitHealthActivity.class);
                        startActivity(health);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_schedule:
                        Intent schedule = new Intent(getApplicationContext(), FeedingScheduleActivity.class);
                        startActivity(schedule);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_exit:
                        try{
                            Intent exit = new Intent(getApplicationContext(), MainActivity.class);
                            exit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(exit);
                            drawerLayout.closeDrawers();
                        }catch (Exception ex){
                            Toast.makeText(getApplicationContext(), "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println("Error: " + ex.getMessage());
                        }
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navigation_clicked();

    }


    private void navigation_clicked(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_bottom_entry:
                        Intent entry = new Intent(getApplicationContext(), RabbitEntryActivity.class);
                        item.setChecked(true);
                        startActivity(entry);
                        break;
                    case R.id.nav_bottom_feeding:
                        Intent feed = new Intent(getApplicationContext(), FeedingNutritionActivity.class);
                        item.setChecked(true);
                        startActivity(feed);
                        break;
                    case R.id.nav_bottom_floating:
                        item.setChecked(true);
                        scanner_function();
                        break;
                    case R.id.nav_bottom_health:
                        Intent health = new Intent(getApplicationContext(), RabbitHealthActivity.class);
                        item.setChecked(true);
                        startActivity(health);
                        break;
                    case R.id.nav_bottom_breeding:
                        Intent breeding = new Intent(getApplicationContext(), RabbitBreedingActivity.class);
                        item.setChecked(true);
                        startActivity(breeding);
                        break;
                }
                return true;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanner_function();
            }
        });
    }
    private void scanner_function() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.setPrompt("Scan a QR Code");
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }


    RabbitEntryData entryData;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            String contents = result.getContents();
            if(contents != null){
                String qrContent = result.getContents();
                documentRabbitReference = db.document("Rabbit/" + qrContent);
                documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            RabbitEntryData rabbitEntryData = documentSnapshot.toObject(RabbitEntryData.class);
                            if(documentSnapshot.exists()){
                                String status = rabbitEntryData.getStatus();
                                if(!status.equals("Deceased")){
                                    Intent intent = new Intent(getApplicationContext(), RabbitManagementActivity.class);
                                    intent.putExtra("rabbitName",qrContent);
                                    startActivity(intent);
                                    Toast.makeText(DrawerBaseActivity.this, qrContent, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(DrawerBaseActivity.this, "Rabbit is deceased", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(DrawerBaseActivity.this, "Rabbit QR Does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



            }
            else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }


}