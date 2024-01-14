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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RabbitFeedingHistory extends DrawerBaseActivity {

    ArrayList<RabbitFeedingData> rabbitFeedingDataArrayList;
    RabbitFeedingAdapter rabbitFeedingAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rabbitFeedingRecyclerView;
    DocumentReference documentRabbitReference;
    RabbitFeedingData rabbitFeedingData;
    TextInputEditText searchFeedFilter;
    CollectionReference collectionRabbitReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_feeding_history, contentFrameLayout);

        getSupportActionBar().setTitle("Feeding History");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        rabbitFeedingRecyclerView = findViewById(R.id.rabbitFeedRecyclerView);
        rabbitFeedingRecyclerView.setHasFixedSize(true);
        rabbitFeedingRecyclerView.setLayoutManager(linearLayoutManager);

        rabbitFeedingData = new RabbitFeedingData();

        firebaseFirestore = FirebaseFirestore.getInstance();

        rabbitFeedingDataArrayList = new ArrayList<RabbitFeedingData>();

        rabbitFeedingAdapter = new RabbitFeedingAdapter(RabbitFeedingHistory.this, rabbitFeedingDataArrayList);

        rabbitFeedingRecyclerView.setAdapter(rabbitFeedingAdapter);

        searchFeedFilter = findViewById(R.id.textRabbitFeedingListSearchFilter);

        try{
            recycler_listener();
            search_function();
        }catch (Exception exception){
            System.out.println("Exception: \n" + exception.getMessage());
            Toast.makeText(this, "Exception: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void name_recycler_listener() {
        String feedID =searchFeedFilter.getEditableText().toString();
        firebaseFirestore.collection("FeedingAndNutrition")
                .whereEqualTo("name",feedID)
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
                                rabbitFeedingDataArrayList.add(documentChange.getDocument().toObject(RabbitFeedingData.class));
                                rabbitFeedingAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitFeedingDataArrayList.remove(documentChange.getDocument().toObject(RabbitFeedingData.class));
                                rabbitFeedingAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void search_function() {
        searchFeedFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String feedID =searchFeedFilter.getEditableText().toString();
                try {
                    if(feedID.equals("")){
                        rabbitFeedingDataArrayList.clear();
                        recycler_listener();
                    }else {
                        documentRabbitReference = db.document("FeedingAndNutrition/" + feedID);
                        documentRabbitReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            rabbitFeedingDataArrayList.clear();
                                            rabbitFeedingData = documentSnapshot.toObject(RabbitFeedingData.class);
                                            assert rabbitFeedingData != null;
                                            rabbitFeedingData.setId(Math.toIntExact(documentSnapshot.getLong("id")));
                                            rabbitFeedingData.setName((documentSnapshot.getString("name")));
                                            rabbitFeedingData.setFeedType((documentSnapshot.getString("feedType")));
                                            rabbitFeedingData.setFeedQuantity((documentSnapshot.getString("feedQuantity")));
                                            rabbitFeedingData.setFeedDate((documentSnapshot.getString("feedDate")));
                                            rabbitFeedingData.setFeedTime((documentSnapshot.getString("feedTime")));

                                            rabbitFeedingDataArrayList.add(rabbitFeedingData);
                                            rabbitFeedingRecyclerView.setAdapter(rabbitFeedingAdapter);
                                        }
                                        else{
                                            rabbitFeedingDataArrayList.clear();
                                            name_recycler_listener();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.d("Feeding filter error : " , exception.getMessage());
                                        System.out.println(exception.getMessage());
                                    }
                                });
                    }
                }catch (Exception exception){
                    Log.d("Feeding filter error : " , exception.getMessage());
                    System.out.println(exception.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void recycler_listener() {
        firebaseFirestore.collection("FeedingAndNutrition")
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
                                rabbitFeedingDataArrayList.add(documentChange.getDocument().toObject(RabbitFeedingData.class));
                                rabbitFeedingAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitFeedingDataArrayList.remove(documentChange.getDocument().toObject(RabbitFeedingData.class));
                                rabbitFeedingAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }
}