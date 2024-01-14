package com.capstone.wmconejoelpatio;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RabbitBreedersActivity extends DrawerBaseActivity {

    ArrayList<SireBreedersData> sireBreedersDataArrayList;
    ArrayList<DamBreedersData> damBreedersDataArrayList;

    SireBreedersAdapter sireBreedersAdapter;
    DamBreedersAdapter damBreedersAdapter;
    FirebaseFirestore firebaseFirestore;
    RecyclerView rabbitBreedersRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentRabbitReference;

    SireBreedersData sireBreedersData;
    DamBreedersData damBreedersData;
    private String[] rabbitDropdown;
    private ArrayAdapter<String> rabbitAdapter;

    AutoCompleteTextView breederFilter;

    String breeder,kits,rate,status;

    TextView accumulatedKits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_breeders, contentFrameLayout);

        getSupportActionBar().setTitle("Rabbit Breeders List");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rabbitBreedersRecyclerView = findViewById(R.id.rabbitBreedersRecyclerView);
        rabbitBreedersRecyclerView.setHasFixedSize(true);
        rabbitBreedersRecyclerView.setLayoutManager(linearLayoutManager);

        sireBreedersData = new SireBreedersData();
        damBreedersData = new DamBreedersData();

        firebaseFirestore = FirebaseFirestore.getInstance();

        breederFilter = findViewById(R.id.dropDownBreeders);

        sireBreedersDataArrayList = new ArrayList<SireBreedersData>();
        damBreedersDataArrayList = new ArrayList<DamBreedersData>();

        accumulatedKits = findViewById(R.id.rabbitBreedersListAccumulatedKits);

        try{
            adapters();
            breeder_getter();
            breeder_filter();
        }catch (Exception exception){
            System.out.println("Exception: \n" + exception.getMessage());
            Toast.makeText(this, "Exception: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void breeder_getter(){
        Intent getData = getIntent();
        String rabbitGender = getData.getStringExtra("rabbitGender");
        if(rabbitGender.equals("Buck")){
            breeder = "SireBreeders";
            kits = "sireAccumulatedKits";
            rate = "sireSuccessRate";
            status = "sireStatus";
            sire_list_recycler();
        } else if (rabbitGender.equals("Doe")) {
            breeder = "DamBreeders";
            kits = "damAccumulatedKits";
            rate = "damSuccessRate";
            status = "damStatus";
            dam_list_recycler();
        }
    }

    String minusDate;
    private void test(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Subtract 182 days
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -182);

        // Get the new date
        Date newDate = calendar.getTime();

        // Format the new date
        String todayDate = sdf.format(currentDate);
        minusDate = sdf.format(newDate);
    }

    String recentStudDate;
    private void recent_stud(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Add 15 days
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 15);

        // Get the new date
        Date newDate = calendar.getTime();

        // Format the new date
        String todayDate = sdf.format(currentDate);
        recentStudDate = sdf.format(newDate);
    }

    private boolean isCoolDownPeriodMet(String lastStudDate) {
        if (lastStudDate != null) {
            // Parse the lastStudDate to Date
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date lastStud = sdf.parse(lastStudDate);

                // Check if the current date is 15 days or more after the lastStudDate
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastStud);
                calendar.add(Calendar.DAY_OF_MONTH, 15);
                Date coolDownDate = calendar.getTime();

                // Get the current date
                Date currentDate = new Date();

                return currentDate.compareTo(coolDownDate) >= 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // If lastStudDate is null, consider the cooldown period met
        return true;
    }





    private void sire_list_recycler(){
        test();
        sireBreedersAdapter = new SireBreedersAdapter(RabbitBreedersActivity.this, sireBreedersDataArrayList);

        rabbitBreedersRecyclerView.setAdapter(sireBreedersAdapter);

        firebaseFirestore.collection("SireBreeders")
                .whereEqualTo("sireStatus","Healthy")
                .whereLessThan("sireBirthDate",minusDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.d("FireStore Error: ", error.getMessage());
                            System.out.println("Error: " + error.getMessage());
                            return;
                        }

                        assert value != null;
                        for(DocumentChange documentChange : value.getDocumentChanges()){

                            if(documentChange.getType() == DocumentChange.Type.ADDED){
                                sireBreedersDataArrayList.add(documentChange.getDocument().toObject(SireBreedersData.class));
                                sireBreedersAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                sireBreedersDataArrayList.remove(documentChange.getDocument().toObject(SireBreedersData.class));
                                sireBreedersAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void dam_list_recycler(){

        test();
        recent_stud();
        damBreedersAdapter = new DamBreedersAdapter(RabbitBreedersActivity.this, damBreedersDataArrayList);

        rabbitBreedersRecyclerView.setAdapter(damBreedersAdapter);

        firebaseFirestore.collection("DamBreeders")
                .whereEqualTo("damStatus","Healthy")
                .whereNotEqualTo("damPregnant","Yes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.d("FireStore Error: ", error.getMessage());
                            System.out.println("Error: " + error.getMessage());
                            return;
                        }

                        assert value != null;

                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            DamBreedersData damData = documentChange.getDocument().toObject(DamBreedersData.class);

                            if (damData.getDamBirthDate() != null && damData.getRecentStudDate() != null &&
                                    damData.getDamBirthDate().compareTo(minusDate) < 0 &&
                                    (damData.getRecentStudDate().compareTo(recentStudDate) < 0 ||
                                            damData.getRecentStudDate().equals("") || recentStudDate == null)) {

                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    // Check if the rabbit is available based on the cool down period
                                    if (isCoolDownPeriodMet(damData.getRecentStudDate())) {
                                        damBreedersDataArrayList.add(damData);
                                        damBreedersAdapter.notifyDataSetChanged();
                                    }
                                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                    damBreedersDataArrayList.remove(damData);
                                    damBreedersAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }
                });
    }

    private void accumulatedKits_recycler_listener() {
        firebaseFirestore.collection(breeder)
                .orderBy(kits, Query.Direction.DESCENDING)
                .whereEqualTo(status,"Healthy")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.d("FireStore Error: ", error.getMessage());
                            System.out.println("Error: " + error.getMessage());
                            return;
                        }

                        assert value != null;
                        for(DocumentChange documentChange : value.getDocumentChanges()){

                            if(breeder.equals("SireBreeders")){
                                if(documentChange.getType() == DocumentChange.Type.ADDED){
                                    sireBreedersDataArrayList.add(documentChange.getDocument().toObject(SireBreedersData.class));
                                    sireBreedersAdapter.notifyDataSetChanged();
                                }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                    sireBreedersDataArrayList.remove(documentChange.getDocument().toObject(SireBreedersData.class));
                                    sireBreedersAdapter.notifyDataSetChanged();
                                }
                            }else if(breeder.equals("DamBreeders")){
                                if(documentChange.getType() == DocumentChange.Type.ADDED){
                                    damBreedersDataArrayList.add(documentChange.getDocument().toObject(DamBreedersData.class));
                                    damBreedersAdapter.notifyDataSetChanged();
                                }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                    damBreedersDataArrayList.remove(documentChange.getDocument().toObject(DamBreedersData.class));
                                    damBreedersAdapter.notifyDataSetChanged();
                                }
                            }

                        }
                    }
                });
    }

    private void successRate_recycler_listener() {
        firebaseFirestore.collection(breeder)
                .orderBy(rate, Query.Direction.DESCENDING)
                .whereEqualTo(status,"Healthy")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){
                            Log.d("FireStore Error: ", error.getMessage());
                            System.out.println("Error: " + error.getMessage());
                            return;
                        }

                        assert value != null;
                        for(DocumentChange documentChange : value.getDocumentChanges()){

                            if(breeder.equals("SireBreeders")){
                                if(documentChange.getType() == DocumentChange.Type.ADDED){
                                    sireBreedersDataArrayList.add(documentChange.getDocument().toObject(SireBreedersData.class));
                                    sireBreedersAdapter.notifyDataSetChanged();
                                }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                    sireBreedersDataArrayList.remove(documentChange.getDocument().toObject(SireBreedersData.class));
                                    sireBreedersAdapter.notifyDataSetChanged();
                                }
                            }else if(breeder.equals("DamBreeders")){
                                if(documentChange.getType() == DocumentChange.Type.ADDED){
                                    damBreedersDataArrayList.add(documentChange.getDocument().toObject(DamBreedersData.class));
                                    damBreedersAdapter.notifyDataSetChanged();
                                }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                    damBreedersDataArrayList.remove(documentChange.getDocument().toObject(DamBreedersData.class));
                                    damBreedersAdapter.notifyDataSetChanged();
                                }
                            }

                        }
                    }
                });
    }

    private void breeder_filter(){
        try{
            breederFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String click = adapterView.getItemAtPosition(i).toString();
                    if (click.equals("Accumulated Kits")) {
                        sireBreedersDataArrayList.clear();
                        damBreedersDataArrayList.clear();
                        accumulatedKits_recycler_listener();
                    }
                    else if (click.equals("Success Rate")) {
                        sireBreedersDataArrayList.clear();
                        damBreedersDataArrayList.clear();
                        successRate_recycler_listener();
                    }
                    else if (click.equals("None")) {
                        sireBreedersDataArrayList.clear();
                        damBreedersDataArrayList.clear();
                        breeder_getter();
                    }
                }
            });
        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
        }
    }
    private void adapters(){
        dropdown_gender_adapter();
        breederFilter.setAdapter(rabbitAdapter);
    }

    private void dropdown_gender_adapter() {
        rabbitDropdown = new String[]{"Accumulated Kits", "Success Rate", "None"};
        rabbitAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitDropdown);
    }
}