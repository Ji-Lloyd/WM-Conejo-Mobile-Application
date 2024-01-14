package com.capstone.wmconejoelpatio;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

public class RabbitEntryListActivity extends DrawerBaseActivity {

    ArrayList<RabbitEntryData> rabbitEntryDataArrayList;
    RabbitEntryAdapter rabbitEntryAdapter;
    FirebaseFirestore firebaseFirestore;
    RecyclerView rabbitEntryRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputEditText searchFilter;
    DocumentReference documentRabbitReference;
    RabbitEntryData rabbitEntryData;
    private String[] rabbitBreeds,rabbitOrigin, rabbitGender;
    private ArrayAdapter<String> breedAdapter, originAdapter,genderAdapter;

    AutoCompleteTextView dropdownBreed, dropdownGender;

    Button combine,clearFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_entry_list, contentFrameLayout);

        getSupportActionBar().setTitle("Rabbit Entry List");

        searchFilter = findViewById(R.id.textRabbitListSearchFilter);
        combine = findViewById(R.id.btnEntryListCombine);
        clearFilter = findViewById(R.id.btnEntryListClearFilter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rabbitEntryRecyclerView = findViewById(R.id.rabbitEntryRecyclerView);
        rabbitEntryRecyclerView.setHasFixedSize(true);
        rabbitEntryRecyclerView.setLayoutManager(linearLayoutManager);

        rabbitEntryData = new RabbitEntryData();

        firebaseFirestore = FirebaseFirestore.getInstance();

        rabbitEntryDataArrayList = new ArrayList<RabbitEntryData>();

        rabbitEntryAdapter = new RabbitEntryAdapter(RabbitEntryListActivity.this, rabbitEntryDataArrayList);

        rabbitEntryRecyclerView.setAdapter(rabbitEntryAdapter);

        dropdownGender = findViewById(R.id.dropDownEntryGenderList);
        dropdownBreed = findViewById(R.id.dropDownEntryBreedList);

        try{
            search_function();
            adapters();
            all_recycler_listener();
            gender_filter();
            breed_filter();
            combine_function();
            clear_filter_function();
        }catch (Exception exception){
            System.out.println("Exception: \n" + exception.getMessage());
            Toast.makeText(this, "Exception: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void search_function(){
        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = searchFilter.getEditableText().toString();
                try{
                    if(name.equals("")){
                        rabbitEntryDataArrayList.clear();
                        all_recycler_listener();
                    }else {
                        documentRabbitReference = db.document("Rabbit/"+name);
                        documentRabbitReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){

                                            rabbitEntryDataArrayList.clear();
                                            rabbitEntryData = documentSnapshot.toObject(RabbitEntryData.class);
                                            assert rabbitEntryData != null;
                                            rabbitEntryData.setName(documentSnapshot.getString("name"));
                                            rabbitEntryData.setTattoo(documentSnapshot.getString("tattoo"));
                                            rabbitEntryData.setBreed(documentSnapshot.getString("breed"));
                                            rabbitEntryData.setOrigin(documentSnapshot.getString("origin"));
                                            rabbitEntryData.setGender(documentSnapshot.getString("gender"));
                                            rabbitEntryData.setPregnant(documentSnapshot.getString("pregnant"));
                                            rabbitEntryData.setStatus(documentSnapshot.getString("status"));

                                            rabbitEntryDataArrayList.add(rabbitEntryData);
                                            rabbitEntryRecyclerView.setAdapter(rabbitEntryAdapter);
                                        }
                                    }
                                });
                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void combine_function(){
        combine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rabbitEntryDataArrayList.clear();
                two_recycler_listener();
            }
        });
    }

    private void clear_filter_function(){
        clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropdownBreed.setText(null);
                dropdownGender.setText(null);
                dropdownBreed.setFocusable(false);
                dropdownGender.setFocusable(false);

                all_recycler_listener();
            }
        });
    }

    private void all_recycler_listener() {
        firebaseFirestore.collection("Rabbit")
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
                                rabbitEntryDataArrayList.add(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitEntryDataArrayList.remove(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    String gender;
    private void gender_filter(){
        dropdownGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String click = adapterView.getItemAtPosition(i).toString();
                if (click.equals("Doe")) {
                    gender = "Doe";
                    rabbitEntryDataArrayList.clear();
                    gender_recycler_listener();
                } else if (click.equals("Buck")) {
                    rabbitEntryDataArrayList.clear();
                    gender = "Buck";
                    gender_recycler_listener();
                } else if (click.equals("All")) {
                    rabbitEntryDataArrayList.clear();
                    all_recycler_listener();
                }
            }
        });
    }
    private void gender_recycler_listener() {
        firebaseFirestore.collection("Rabbit")
                .whereEqualTo("gender",gender)
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
                                rabbitEntryDataArrayList.add(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitEntryDataArrayList.remove(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    String breed;
    private void breed_filter(){
        dropdownBreed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String click = adapterView.getItemAtPosition(i).toString();
                if (click.equals("New Zealand")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "New Zealand";
                    breed_recycler_listener();
                } else if (click.equals("Californian")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "Californian";
                    breed_recycler_listener();
                } else if (click.equals("Flemish Giant")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "Flemish Giant";
                    breed_recycler_listener();
                }else if (click.equals("PS Line")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "PS Line";
                    breed_recycler_listener();
                }else if (click.equals("Hyla Optima")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "Hyla Optima";
                    breed_recycler_listener();
                }else if (click.equals("Hyla Plus")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "Hyla Plus";
                    breed_recycler_listener();
                }else if (click.equals("German Giant")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "German Giant";
                    breed_recycler_listener();
                }else if (click.equals("Holland Lop")) {
                    rabbitEntryDataArrayList.clear();
                    breed = "Holland Lop";
                    breed_recycler_listener();
                }
            }
        });
    }
    private void breed_recycler_listener() {
        firebaseFirestore.collection("Rabbit")
                .whereEqualTo("breed", breed)
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
                                rabbitEntryDataArrayList.add(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitEntryDataArrayList.remove(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }


    private void two_recycler_listener() {
        firebaseFirestore.collection("Rabbit")
                .whereEqualTo("gender",gender)
                .whereEqualTo("breed",breed)
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
                                rabbitEntryDataArrayList.add(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                                rabbitEntryDataArrayList.remove(documentChange.getDocument().toObject(RabbitEntryData.class));
                                rabbitEntryAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void adapters() {
        dropdown_breed_adapter();
        dropdown_gender_adapter();

        dropdownBreed.setAdapter(breedAdapter);
        dropdownGender.setAdapter(genderAdapter);

    }
    private void dropdown_breed_adapter() {
        rabbitBreeds = new String[]{"New Zealand", "Californian", "Flemish Giant", "PS Line", "Hyla Optima", "Hyla Plus", "German Giant", "Holland Lop"};
        breedAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitBreeds);
    }

    private void dropdown_gender_adapter() {
        rabbitGender = new String[]{"Buck", "Doe", "All"};
        genderAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitGender);
    }
}