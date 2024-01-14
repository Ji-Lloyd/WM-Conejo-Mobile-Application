package com.capstone.wmconejoelpatio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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

public class RabbitHealthHistory extends DrawerBaseActivity {

    ArrayList<RabbitHealthData> rabbitHealthDataArrayList;
    RabbitHealthAdapter rabbitHealthAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputEditText search;
    RecyclerView rabbitHealthRecyclerView;
    DocumentReference documentRabbitReference;
    RabbitHealthData rabbitHealthData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_health_history, contentFrameLayout);

        getSupportActionBar().setTitle("Rabbit Health History");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rabbitHealthRecyclerView = findViewById(R.id.rabbitHealthRecyclerView);
        rabbitHealthRecyclerView.setHasFixedSize(true);
        rabbitHealthRecyclerView.setLayoutManager(linearLayoutManager);

        rabbitHealthData = new RabbitHealthData();

        firebaseFirestore = FirebaseFirestore.getInstance();

        rabbitHealthDataArrayList = new ArrayList<RabbitHealthData>();

        rabbitHealthAdapter = new RabbitHealthAdapter(RabbitHealthHistory.this, rabbitHealthDataArrayList);

        rabbitHealthRecyclerView.setAdapter(rabbitHealthAdapter);

        search = findViewById(R.id.textRabbitHealthListSearchFilter);

        try{
            recycler_listener();
            search_function();
        }catch (Exception exception){
            System.out.println("Exception: \n" + exception.getMessage());
            Toast.makeText(this, "Exception: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void name_recycler_listener() {
        String id = search.getEditableText().toString();
        firebaseFirestore.collection("HealthAdministration")
                .whereEqualTo("name",id)
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
                                rabbitHealthDataArrayList.add(documentChange.getDocument().toObject(RabbitHealthData.class));
                                rabbitHealthAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitHealthDataArrayList.remove(documentChange.getDocument().toObject(RabbitHealthData.class));
                                rabbitHealthAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void disease_recycler_listener() {
        String id = search.getEditableText().toString();
        firebaseFirestore.collection("HealthAdministration")
                .whereEqualTo("disease",id)
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
                                rabbitHealthDataArrayList.add(documentChange.getDocument().toObject(RabbitHealthData.class));
                                rabbitHealthAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitHealthDataArrayList.remove(documentChange.getDocument().toObject(RabbitHealthData.class));
                                rabbitHealthAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void search_function() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String id = search.getEditableText().toString();
                try{
                    if(id.equals("")){
                        rabbitHealthDataArrayList.clear();
                        recycler_listener();
                    }else{

                        documentRabbitReference = db.document("HealthAdministration/"+id);
                        documentRabbitReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            rabbitHealthDataArrayList.clear();
                                            rabbitHealthData = documentSnapshot.toObject(RabbitHealthData.class);
                                            assert rabbitHealthData != null;
                                            rabbitHealthData.setId(documentSnapshot.getLong("id"));
                                            rabbitHealthData.setName(documentSnapshot.getString("name"));
                                            rabbitHealthData.setDisease(documentSnapshot.getString("disease"));
                                            rabbitHealthData.setVaccination(documentSnapshot.getString("vaccination"));
                                            rabbitHealthData.setNotes(documentSnapshot.getString("notes"));
                                            rabbitHealthData.setDate(documentSnapshot.getString("date"));

                                            rabbitHealthDataArrayList.add(rabbitHealthData);
                                            rabbitHealthRecyclerView.setAdapter(rabbitHealthAdapter);
                                        }else{
                                            rabbitHealthDataArrayList.clear();
                                            name_recycler_listener();
                                            disease_recycler_listener();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });



                    }
                }catch (Exception exception){
                    Log.d("TAG", exception.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void recycler_listener() {
        firebaseFirestore.collection("HealthAdministration")
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
                                rabbitHealthDataArrayList.add(documentChange.getDocument().toObject(RabbitHealthData.class));
                                rabbitHealthAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitHealthDataArrayList.remove(documentChange.getDocument().toObject(RabbitHealthData.class));
                                rabbitHealthAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }
}