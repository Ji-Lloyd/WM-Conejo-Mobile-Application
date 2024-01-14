package com.capstone.wmconejoelpatio;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RabbitBreedingHistory extends DrawerBaseActivity {

    ArrayList<RabbitBreedingData> rabbitBreedingDataArrayList;
    RabbitBreedingAdapter rabbitBreedingAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rabbitBreedingRecyclerView;
    DocumentReference documentRabbitReference;
    RabbitBreedingData rabbitBreedingData;

    TextInputEditText breedingSearchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_breeding_history, contentFrameLayout);

        getSupportActionBar().setTitle("Rabbit Breeding History");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        breedingSearchFilter = findViewById(R.id.textRabbitBreedingListSearchFilter);

        rabbitBreedingRecyclerView = findViewById(R.id.rabbitBreedingRecyclerView);
        rabbitBreedingRecyclerView.setHasFixedSize(true);
        rabbitBreedingRecyclerView.setLayoutManager(linearLayoutManager);

        rabbitBreedingData = new RabbitBreedingData();

        firebaseFirestore = FirebaseFirestore.getInstance();

        rabbitBreedingDataArrayList = new ArrayList<RabbitBreedingData>();

        rabbitBreedingAdapter = new RabbitBreedingAdapter(RabbitBreedingHistory.this, rabbitBreedingDataArrayList);

        rabbitBreedingRecyclerView.setAdapter(rabbitBreedingAdapter);

        try{
            search_filter();
            recycler_listener();
        }catch (Exception exception){
            System.out.println("Exception: \n" + exception.getMessage());
            Toast.makeText(this, "Exception: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sire_name_recycler_listener() {
        String breedID = breedingSearchFilter.getEditableText().toString();
        firebaseFirestore.collection("Breeding")
                .whereEqualTo("sireName",breedID)
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
                                rabbitBreedingDataArrayList.add(documentChange.getDocument().toObject(RabbitBreedingData.class));
                                rabbitBreedingAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitBreedingDataArrayList.remove(documentChange.getDocument().toObject(RabbitBreedingData.class));
                                rabbitBreedingAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void dam_name_recycler_listener() {
        String breedID = breedingSearchFilter.getEditableText().toString();
        firebaseFirestore.collection("Breeding")
                .whereEqualTo("damName",breedID)
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
                                rabbitBreedingDataArrayList.add(documentChange.getDocument().toObject(RabbitBreedingData.class));
                                rabbitBreedingAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitBreedingDataArrayList.remove(documentChange.getDocument().toObject(RabbitBreedingData.class));
                                rabbitBreedingAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void search_filter(){
        breedingSearchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String breedID = breedingSearchFilter.getEditableText().toString();
                if(breedID.equals("")){
                    rabbitBreedingDataArrayList.clear();
                    recycler_listener();
                }else{
                    documentRabbitReference = db.document("Breeding/" + breedID);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        rabbitBreedingDataArrayList.clear();
                                        rabbitBreedingData = documentSnapshot.toObject(RabbitBreedingData.class);
                                        assert rabbitBreedingData != null;
                                        rabbitBreedingData.setDocumentName(documentSnapshot.getString("documentName"));
                                        rabbitBreedingData.setSireName(documentSnapshot.getString("sireName"));
                                        rabbitBreedingData.setDamName(documentSnapshot.getString("damName"));
                                        rabbitBreedingData.setStudDate(documentSnapshot.getString("studDate"));
                                        rabbitBreedingData.setBirthDate(documentSnapshot.getString("birthDate"));
                                        rabbitBreedingData.setLitterSize(Math.toIntExact(documentSnapshot.getLong("litterSize")));

                                        rabbitBreedingDataArrayList.add(rabbitBreedingData);
                                        rabbitBreedingRecyclerView.setAdapter(rabbitBreedingAdapter);
                                    }
                                    else{
                                        rabbitBreedingDataArrayList.clear();
                                        sire_name_recycler_listener();
                                        dam_name_recycler_listener();
                                    }
                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void recycler_listener() {
        firebaseFirestore.collection("Breeding")
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
                                rabbitBreedingDataArrayList.add(documentChange.getDocument().toObject(RabbitBreedingData.class));
                                rabbitBreedingAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitBreedingDataArrayList.remove(documentChange.getDocument().toObject(RabbitBreedingData.class));
                                rabbitBreedingAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }
}