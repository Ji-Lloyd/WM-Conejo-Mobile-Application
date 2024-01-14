package com.capstone.wmconejoelpatio;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import io.grpc.ExperimentalApi;
import io.grpc.ManagedChannel;
import io.grpc.NameResolver;

public class RabbitDashboardActivity extends DrawerBaseActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRabbitReference;
    DocumentReference documentRabbitReference;

    TextView buckCount, doeCount, rabbitCount, kitsCount, rabbitDeceasedCount, kitsDeceasedCount;

    TextView totalDoe, pregnant, notPregnant;

    TextView topBuck, topDoe, topTest;

    TextView newZealandBreed, californianBreed, flemishGiantBreed, psLineBreed, hylaOptimaBreed, hylaPlusBreed, germanGiantBreed, hollandLopBreed;

    long newZealand = 0,californian = 0,flemishGiant = 0,psLine = 0, hylaOptima = 0, hylaPlus = 0, germanGiant = 0, hollandLop = 0;

    long doe = 0;

    BottomNavigationView bottomNavigationView;

    TextView buckRow1Name,buckRow2Name,buckRow3Name,buckRow4Name,buckRow5Name;
    TextView buckRow1SuccessRate, buckRow2SuccessRate, buckRow3SuccessRate, buckRow4SuccessRate, buckRow5SuccessRate;

    TextView doeRow1Name,doeRow2Name,doeRow3Name,doeRow4Name,doeRow5Name;
    TextView doeRow1SuccessRate, doeRow2SuccessRate, doeRow3SuccessRate, doeRow4SuccessRate, doeRow5SuccessRate;

    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_dashboard, contentFrameLayout);

        getSupportActionBar().setTitle("Dashboard");

        refreshLayout = findViewById(R.id.refreshSwipeLayout);

        buckCount = findViewById(R.id.tvBuckCount);
        doeCount = findViewById(R.id.tvDoeCount);

        buckRow1Name = findViewById(R.id.buckRow1Name);
        buckRow2Name = findViewById(R.id.buckRow2Name);
        buckRow3Name = findViewById(R.id.buckRow3Name);
        buckRow4Name = findViewById(R.id.buckRow4Name);
        buckRow5Name = findViewById(R.id.buckRow5Name);

        buckRow1SuccessRate = findViewById(R.id.buckRow1SuccessRate);
        buckRow2SuccessRate = findViewById(R.id.buckRow2SuccessRate);
        buckRow3SuccessRate = findViewById(R.id.buckRow3SuccessRate);
        buckRow4SuccessRate = findViewById(R.id.buckRow4SuccessRate);
        buckRow5SuccessRate = findViewById(R.id.buckRow5SuccessRate);

        doeRow1Name = findViewById(R.id.doeRow1Name);
        doeRow2Name = findViewById(R.id.doeRow2Name);
        doeRow3Name = findViewById(R.id.doeRow3Name);
        doeRow4Name = findViewById(R.id.doeRow4Name);
        doeRow5Name = findViewById(R.id.doeRow5Name);

        doeRow1SuccessRate = findViewById(R.id.doeRow1SuccessRate);
        doeRow2SuccessRate = findViewById(R.id.doeRow2SuccessRate);
        doeRow3SuccessRate = findViewById(R.id.doeRow3SuccessRate);
        doeRow4SuccessRate = findViewById(R.id.doeRow4SuccessRate);
        doeRow5SuccessRate = findViewById(R.id.doeRow5SuccessRate);


        rabbitCount = findViewById(R.id.tvTotalRabbit);
        kitsCount = findViewById(R.id.tvTotalKits);

        rabbitDeceasedCount = findViewById(R.id.tvRabbitDeceasedTotal);
        kitsDeceasedCount = findViewById(R.id.tvKitsDeceasedTotal);

        totalDoe = findViewById(R.id.tvTotalDoe);
        pregnant = findViewById(R.id.tvPregnantTotal);
        notPregnant = findViewById(R.id.tvNotPregnantTotal);

        newZealandBreed = findViewById(R.id.tvNewZealandBreed);
        californianBreed = findViewById(R.id.tvCalifornianBreed);
        flemishGiantBreed = findViewById(R.id.tvFlemishGiantBreed);
        psLineBreed = findViewById(R.id.tvPsLineBreed);

        hylaOptimaBreed = findViewById(R.id.tvHylaOptimaBreed);
        hylaPlusBreed = findViewById(R.id.tvHylaPlusBreed);
        germanGiantBreed = findViewById(R.id.tvGermanGiantBreed);
        hollandLopBreed = findViewById(R.id.tvHollandLopBreed);

        bottomNavigationView = findViewById(R.id.dashboardBottomAppNavigation);


        try{
            dashboard_functions();
            refresh_function();
        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
            System.err.println("Dashboard count exception: " + exception.getMessage());
        }
    }

    private void dashboard_functions(){
        buck_counter();
        doe_counter();
        counter_all_rabbit();
        rabbit_deceased_counter();

        newZealand_counter();
        californian_counter();
        flemishGiant_counter();
        psLine_counter();
        hylaOptima_counter();
        hylaPlus_counter();
        germanGiant_counter();
        hollandLop_counter();
        pregnant_counter();

        //top_buck_breeders_counter();
        //top_doe_breeders_counter();
        accumulated_kits_counter();
        deceased_kits_counter();
    }

    private void refresh_function(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dashboard_functions();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void buck_counter() {
        Query query = db.collection("Rabbit")
                .whereEqualTo("gender","Buck")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    buckCount.setText(Long.toString(snapshot.getCount()));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());

                }
            }
        });
    }

    private void doe_counter() {
        Query query = db.collection("Rabbit")
                                .whereEqualTo("gender","Doe")
                                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    doe = snapshot.getCount();
                    doeCount.setText(Long.toString(snapshot.getCount()));
                    totalDoe.setText("Doe: " + Long.toString(snapshot.getCount()));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void counter_all_rabbit(){
        Query query = db.collection("Rabbit")
                .whereNotEqualTo("status","Deceased");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    rabbitCount.setText(Long.toString(snapshot.getCount()));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }


    private void rabbit_deceased_counter() {
        Query query = db.collection("Rabbit")
                .whereEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    rabbitDeceasedCount.setText("Deceased Rabbit: " + Long.toString(snapshot.getCount()));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void pregnant_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("pregnant","Yes")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Pregnant Count: " + snapshot.getCount());
                    pregnant.setText("Pregnant: " + Long.toString(snapshot.getCount()));
                    long nPregnant = doe - snapshot.getCount();
                    notPregnant.setText("Not Pregnant: " + Long.toString(nPregnant));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void newZealand_counter(){

        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","New Zealand")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    newZealand = snapshot.getCount();
                    newZealandBreed.setText(Long.toString(newZealand));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });




    }

    private void californian_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","Californian")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    californian = snapshot.getCount();
                    californianBreed.setText(Long.toString(californian));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void flemishGiant_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","Flemish Giant")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    flemishGiant = snapshot.getCount();
                    flemishGiantBreed.setText(Long.toString(flemishGiant));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void psLine_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","PS Line")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    psLine = snapshot.getCount();
                    psLineBreed.setText(Long.toString(psLine));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void hylaOptima_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","Hyla Optima")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    hylaOptima = snapshot.getCount();
                    hylaOptimaBreed.setText(Long.toString(hylaOptima));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void hylaPlus_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","Hyla Plus")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    hylaPlus = snapshot.getCount();
                    hylaPlusBreed.setText(Long.toString(hylaPlus));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void germanGiant_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","German Giant")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    germanGiant = snapshot.getCount();
                    germanGiantBreed.setText(Long.toString(germanGiant));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void hollandLop_counter(){
        Query query = db.collection("Rabbit")
                .whereEqualTo("breed","Holland Lop")
                .whereNotEqualTo("status","Deceased");
        //Query query = db.collection("Rabbit");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                    hollandLop = snapshot.getCount();
                    hollandLopBreed.setText(Long.toString(hollandLop));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }


    private void top_buck_breeders_counter() {
        Query query =  db.collection("SireBreeders")
                .orderBy("sireSuccessRate", Query.Direction.DESCENDING)
                .limit(5);

        query.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    int counter = 1;
                    for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                        SireBreedersData sireBreedersData = queryDocumentSnapshot.toObject(SireBreedersData.class);
                        String name = sireBreedersData.getSireName();
                        long successRate = sireBreedersData.getSireSuccessRate();

                        if(counter == 1){
                            buckRow1Name.setText(name);
                            buckRow1SuccessRate.setText(String.valueOf(successRate)+"%");
                        } else if (counter == 2) {
                            buckRow2Name.setText(name);
                            buckRow2SuccessRate.setText(String.valueOf(successRate)+"%");
                        }
                        else if (counter == 3) {
                            buckRow3Name.setText(name);
                            buckRow3SuccessRate.setText(String.valueOf(successRate)+"%");
                        }
                        else if (counter == 4) {
                            buckRow4Name.setText(name);
                            buckRow4SuccessRate.setText(String.valueOf(successRate)+"%");
                        }
                        else if (counter == 5) {
                            buckRow5Name.setText(name);
                            buckRow5SuccessRate.setText(String.valueOf(successRate)+"%");
                        }
                        counter++;
                    }

                }
            });
    }

    private void top_doe_breeders_counter() {
        Query query =  db.collection("DamBreeders")
                .orderBy("damSuccessRate", Query.Direction.DESCENDING)
                .limit(5);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        int counter = 1;
                        for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                            DamBreedersData damBreedersData = queryDocumentSnapshot.toObject(DamBreedersData.class);
                            String name = damBreedersData.getDamName();
                            long successRate = damBreedersData.getDamSuccessRate();

                            if(counter == 1){
                                doeRow1Name.setText(name);
                                doeRow1SuccessRate.setText(String.valueOf(successRate)+"%");
                            } else if (counter == 2) {
                                doeRow2Name.setText(name);
                                doeRow2SuccessRate.setText(String.valueOf(successRate)+"%");
                            }
                            else if (counter == 3) {
                                doeRow3Name.setText(name);
                                doeRow3SuccessRate.setText(String.valueOf(successRate)+"%");
                            }
                            else if (counter == 4) {
                                doeRow4Name.setText(name);
                                doeRow4SuccessRate.setText(String.valueOf(successRate)+"%");
                            }
                            else if (counter == 5) {
                                doeRow5Name.setText(name);
                                doeRow5SuccessRate.setText(String.valueOf(successRate)+"%");
                            }
                            counter++;
                        }

                    }
                });
    }

    private void accumulated_kits_counter(){
        Query query =  db.collection("Breeding");

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        long totalLitters = 0;
                        long totalSurvivedKits = 0;
                        for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                            RabbitBreedingData rabbitBreedingData = queryDocumentSnapshot.toObject(RabbitBreedingData.class);
                            long accumulatedKits = rabbitBreedingData.getLitterSize();
                            long survivedKits = rabbitBreedingData.getLitterSurvived();

                            totalLitters = totalLitters + accumulatedKits;
                            totalSurvivedKits =  totalSurvivedKits  +survivedKits;
                        }


                        kitsCount.setText(String.valueOf(totalSurvivedKits));
                    }
                });
    }

    private void deceased_kits_counter(){
        Query query =  db.collection("Breeding");

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        long totalLitters = 0;
                        long totalSurvivedLitters = 0;
                        for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                            RabbitBreedingData rabbitBreedingData = queryDocumentSnapshot.toObject(RabbitBreedingData.class);
                            long accumulatedKits = rabbitBreedingData.getLitterSize();
                            long survivedKits = rabbitBreedingData.getLitterSurvived();

                            totalLitters = totalLitters + accumulatedKits;
                            totalSurvivedLitters = totalSurvivedLitters + survivedKits;
                        }

                        long result = totalLitters - totalSurvivedLitters;

                        kitsDeceasedCount.setText("Deceased Kits: " + String.valueOf(result));
                    }
                });
    }
}