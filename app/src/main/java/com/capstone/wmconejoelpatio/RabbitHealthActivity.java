package com.capstone.wmconejoelpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RabbitHealthActivity extends DrawerBaseActivity {

    String[] healthAdministration, healthDisease, healthVaccination;
    ArrayAdapter<String> administrationAdapter, diseaseAdapter, vaccinationAdapter;
    AutoCompleteTextView dropdownHealthAdministrationFor, dropdownDiseaseOutbreak, dropdownVaccination;
    TextInputEditText textRabbitID,textRabbitName,textRabbitNotes,textRabbitStatus;
    String currentDate,rabbitID,rabbitName,rabbitNotes,rabbitAdministration,rabbitDisease,rabbitVaccination;
    Button submit,update,clear,delete,viewAll,generate;
    RabbitHealthData healthData;
    long id_counter = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRabbitReference;
    DocumentReference documentRabbitReference;
    String healthCollectionTitle = "HealthAdministration/";
    String loadedID;
    boolean genderValidator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_health, contentFrameLayout);

        getSupportActionBar().setTitle("Administration");

        healthData = new RabbitHealthData();

        dropdownHealthAdministrationFor = findViewById(R.id.dropDownHealthAdministrationCategory);
        dropdownDiseaseOutbreak = findViewById(R.id.dropDownHealthDiseaseOutbreaks);
        dropdownVaccination = findViewById(R.id.dropDownHealthVaccineCategory);

        textRabbitID = findViewById(R.id.textHealthRabbitID);
        textRabbitName = findViewById(R.id.textHealthRabbitName);
        textRabbitNotes = findViewById(R.id.textHealthRabbitNotes);
        textRabbitStatus = findViewById(R.id.textHealthRabbitStatus);

        submit = findViewById(R.id.btnHealthSubmit);
        update = findViewById(R.id.btnHealthUpdate);
        clear = findViewById(R.id.btnHealthClear);
        delete = findViewById(R.id.btnHealthDelete);
        viewAll = findViewById(R.id.btnHealthViewAll);
        generate = findViewById(R.id.btnHealthRabbitGenerateID);

        loadingDialog = new LoadingDialog(RabbitHealthActivity.this);

        show_update();

        try {
            Intent getData = getIntent();
            textRabbitName.setText(getData.getStringExtra("rabbitName"));
            String name = textRabbitName.getEditableText().toString();
            if(getData.getStringExtra("rabbitName").equals(name)){
                documentRabbitReference = db.document("Rabbit/" + getData.getStringExtra("rabbitName"));
                documentRabbitReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()) {
                                            RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                            assert entryData != null;
                                            String adminFor = entryData.getGender();
                                            String rabbitStatus = entryData.getStatus();
                                            textRabbitStatus.setText("Ill");
                                            dropdownHealthAdministrationFor.setText(adminFor,false);
                                            textRabbitName.setEnabled(false);
                                            dropdownHealthAdministrationFor.setEnabled(false);
                                        }
                                    }
                                });

            }else{
                textRabbitName.setEnabled(true);
            }
        } catch (Exception exception) {
            System.out.println("Getting Intent Data Exception: " + exception.getMessage());
        }

        try{
            submit_function();
            update_function();
            clear_function();
            delete_function();
            viewAll_function();
        }catch (Exception exception){
            System.err.println("Button function error: " + exception.getMessage());
            Log.d("TAG", exception.getMessage());
        }

        try{
            Intent getData = getIntent();
            loadedID = getData.getStringExtra("rabbitHealthID");
            recycler_loaded_data();
            textRabbitID.setEnabled(false);
            textRabbitName.setEnabled(false);
            dropdownHealthAdministrationFor.setEnabled(false);
            dropdownDiseaseOutbreak.setEnabled(false);
            generate.setEnabled(false);
        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
            System.err.println("Recyclerview Update exception: " + exception.getMessage());
        }

        try{
            counter_generator();
            adapters();
        }catch (Exception exception){
            System.err.println("Auxiliary function error: " + exception.getMessage());
            Log.d("TAG", exception.getMessage());
        }

    }

    String successDescriptionText = "";
    @SuppressLint("SetTextI18n")
    private void showSuccessDialog(){
        ConstraintLayout successConstraintLayout = findViewById(R.id.successConstraintLayout);
        View viewSuccess = LayoutInflater.from(RabbitHealthActivity.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = viewSuccess.findViewById(R.id.successDone);
        TextView successDescription = viewSuccess.findViewById(R.id.successDescription);
        successDescription.setText(successDescriptionText);

        AlertDialog.Builder  builder =  new AlertDialog.Builder(RabbitHealthActivity.this);
        builder.setView(viewSuccess);
        final AlertDialog alertDialog = builder.create();

        successDone.findViewById(R.id.successDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                clear_data();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar_menu_health,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_toolbar_list:
                startActivity(new Intent(RabbitHealthActivity.this, RabbitHealthHistory.class));
                break;
            case R.id.nav_toolbar_advice:
                rabbitName = textRabbitName.getEditableText().toString();
                rabbitDisease = dropdownDiseaseOutbreak.getEditableText().toString();
                rabbitVaccination = dropdownVaccination.getEditableText().toString();
                if(rabbitName.equals("") || rabbitDisease.equals("") || rabbitVaccination.equals("")){
                    showErrorDialog();
                }else{
                    health_advisor();
                }
                break;
            case R.id.nav_toolbar_retrieve_health_document:
                rabbitName = textRabbitName.getEditableText().toString();
                if(rabbitName.equals("")){
                    errorTitle = "Error";
                    errorMessage = "Fill rabbit name!";
                    showErrorDialog();
                }else{
                    rabbit_info();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    String status;
    private void rabbit_info(){
        rabbitName = textRabbitName.getEditableText().toString();
        if(rabbitName.equals("")){
        }else{
            documentRabbitReference = db.document("Rabbit/" + rabbitName);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                status = entryData.getStatus();
                                if(status.equals("Deceased")){
                                    errorTitle = "Error";
                                    errorMessage = "Rabbit is already dead!";
                                    showErrorDialog();
                                }else{
                                    String administrationType = entryData.getGender();
                                    textRabbitStatus.setText(status);
                                    dropdownHealthAdministrationFor.setText(administrationType);
                                    document_counter();
                                }

                            }else {
                                errorTitle = "Error";
                                errorMessage = "Rabbit does not exist!";
                                showErrorDialog();
                            }
                        }
                    });
        }
    }

    private void show_update(){
        Intent getData = getIntent();
        boolean rabbitShow = getData.getBooleanExtra("rabbitShow", false);
        if(rabbitShow){
            submit.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Update Health");
        }
    }

    private void recycler_loaded_data(){
        if (loadedID.equals("")) {
            System.out.println("Data is not loaded");
        } else {
            documentRabbitReference = db.document(healthCollectionTitle + loadedID);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            healthData = documentSnapshot.toObject(RabbitHealthData.class);
                            assert healthData != null;
                            String id = String.valueOf(healthData.getId());
                            String administration = healthData.getHealthAdministration();
                            String name = healthData.getName();
                            String disease = healthData.getDisease();
                            String vaccine = healthData.getVaccination();
                            String notes = healthData.getNotes();
                            String dateCreated = healthData.getDate();


                            textRabbitID.setText(id);
                            textRabbitName.setText(name);
                            textRabbitNotes.setText(notes);
                            textRabbitStatus.setText("Ill");

                            dropdownHealthAdministrationFor.setText(administration,false);
                            dropdownDiseaseOutbreak.setText(disease,false);
                            dropdownVaccination.setText(vaccine,false);
                            currentDate = dateCreated;
                        }
                    });
        }
    }

    private void document_counter(){
        Query query = db.collection("HealthAdministration")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        id_counter = Integer.parseInt(documentSnapshot.getId()) + 1;
                        textRabbitID.setText(String.valueOf(id_counter));
                    }

                }
            }
        });
    }

    private void counter_generator(){
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                document_counter();
            }
        });
    }

    private void data_getter(){

        rabbitID = textRabbitID.getEditableText().toString();
        rabbitName = textRabbitName.getEditableText().toString();
        rabbitNotes = textRabbitNotes.getEditableText().toString();

        rabbitAdministration = dropdownHealthAdministrationFor.getEditableText().toString();
        rabbitDisease = dropdownDiseaseOutbreak.getEditableText().toString();
        rabbitVaccination = dropdownVaccination.getEditableText().toString();



        if(rabbitID.equals("")){
            showErrorDialog();
        }else{
            id_counter = Integer.parseInt(textRabbitID.getEditableText().toString());
            healthData.setId(id_counter);
        }

        healthData.setName(rabbitName);
        healthData.setNotes(rabbitNotes);
        healthData.setHealthAdministration(rabbitAdministration);
        healthData.setDisease(rabbitDisease);
        healthData.setVaccination(rabbitVaccination);


    }

    private boolean validation(){
        rabbitName = textRabbitName.getEditableText().toString();
        rabbitAdministration = dropdownHealthAdministrationFor.getEditableText().toString();
        Query genderQuery = db.collection("Rabbit")
                .whereEqualTo("name",rabbitName)
                .whereEqualTo("gender", rabbitAdministration)
                .whereNotEqualTo("status","Deceased");


        genderQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(!querySnapshot.isEmpty()){
                    genderValidator = true;
                }else{
                    genderValidator = false;
                }
            }
        });
        return genderValidator;
    }

    private void status_ill_update(){
        DocumentReference rabbitStatusUpdate = db.document("Rabbit/"+rabbitName);
        rabbitStatusUpdate
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        RabbitEntryData rabbitEntryData = documentSnapshot.toObject(RabbitEntryData.class);
                        assert rabbitEntryData != null;
                        rabbitEntryData.setStatus("Ill");
                        rabbitStatusUpdate.set(rabbitEntryData, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        status_ill_update_breeders();
                                    }
                                });

                    }
                });
    }

    private void status_ill_update_breeders(){
        rabbitName = textRabbitName.getEditableText().toString();
        rabbitAdministration = dropdownHealthAdministrationFor.getEditableText().toString();
        if(rabbitAdministration.equals("Buck")){
            DocumentReference rabbitStatusUpdate = db.document("SireBreeders/"+rabbitName);
            rabbitStatusUpdate
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            SireBreedersData sireBreedersData = documentSnapshot.toObject(SireBreedersData.class);
                            assert sireBreedersData != null;
                            sireBreedersData.setSireStatus("Ill");
                            rabbitStatusUpdate.set(sireBreedersData, SetOptions.merge());
                            //Toast.makeText(RabbitHealthActivity.this, "Sire Breeder status update", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (rabbitAdministration.equals("Doe")) {
            DocumentReference rabbitStatusUpdate = db.document("DamBreeders/"+rabbitName);
            rabbitStatusUpdate
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            DamBreedersData damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                            assert damBreedersData != null;
                            damBreedersData.setDamStatus("Ill");
                            rabbitStatusUpdate.set(damBreedersData, SetOptions.merge());
                            //Toast.makeText(RabbitHealthActivity.this, "Dam Breeder status update", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void submit_data(){
        data_getter();
        if(rabbitID.equals("") || rabbitName.equals("") || rabbitAdministration.equals("")
                || rabbitDisease.equals("") || rabbitVaccination.equals("")){
            showErrorDialog();
        }else{
            DocumentReference rabbitExistChecker = db.document("Rabbit/"+rabbitName);
            rabbitExistChecker
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            //document_counter();
                            documentRabbitReference = db.document(healthCollectionTitle+id_counter);
                            documentRabbitReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            errorTitle = "Error";
                                            errorMessage = "Document already exists!";
                                            showErrorDialog();
                                        }else{
                                            current_date();
                                            healthData.setDate(currentDate);
                                            documentRabbitReference.set(healthData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        status_ill_update();
                                                        successDescriptionText = "Rabbit health entry added";
                                                        showSuccessDialog();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RabbitHealthActivity.this, "Data creation failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", e.getMessage());
                                        System.err.println("Submit exception: " + e.getMessage());
                                    }
                                });
                        }else{
                            errorTitle = "Error";
                            errorMessage = "Rabbit does not exist!";
                            showErrorDialog();
                        }
                    }
                });

        }
    }
    private void submit_function(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit_show_confirmation_dialog();
            }
        });
    }

    private void update_function(){
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_show_confirmation_dialog();
            }
        });
    }

    private void clear_data(){
        textRabbitID.setText("");
        textRabbitName.setText("");
        textRabbitNotes.setText("");
        textRabbitStatus.setText("");
        dropdownHealthAdministrationFor.setText("",false);
        dropdownDiseaseOutbreak.setText("",false);
        dropdownVaccination.setText("",false);
        textRabbitName.setEnabled(true);
        dropdownHealthAdministrationFor.setEnabled(true);
        generate.setEnabled(true);
    }

    private void clear_function(){
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_show_confirmation_dialog();
            }
        });
    }

    private void delete_function(){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_show_confirmation_dialog();
            }
        });
    }

    private void viewAll_function(){
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitHealthActivity.this, RabbitHealthHistory.class));
            }
        });
    }

    private void current_date(){
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = dateFormat.format(calendar);
    }

    private void adapters(){
        dropdown_health_administration();
        dropdown_health_disease();
        dropdown_health_vaccination();

        dropdownHealthAdministrationFor.setAdapter(administrationAdapter);
        dropdownDiseaseOutbreak.setAdapter(diseaseAdapter);
        dropdownVaccination.setAdapter(vaccinationAdapter);
    }

    private void dropdown_health_administration(){
        healthAdministration = new String[] {"Buck","Doe"};
        administrationAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, healthAdministration);
    }

    private void dropdown_health_disease(){
        healthDisease = new String[] {"Mange","Bloating","Diarrhea","Mastitis","Sore Hack","Cold","Halak"};
        diseaseAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, healthDisease);
    }

    private void dropdown_health_vaccination(){
        healthVaccination = new String[] {"Ivermectin (Anti-Parasitic)","Vitamin ADE","Vitamin B Complex","Virgin Coconut Oil"};
        vaccinationAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, healthVaccination);
    }

    private void submit_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitHealthActivity.this);
        View view = LayoutInflater.from(RabbitHealthActivity.this).inflate(
                R.layout.confirmation_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Submit");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Are you sure?");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancel");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.submit_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                boolean valid = validation();
                if(valid){
                    submit_data();
                }
                */
                submit_data();
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private void update_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitHealthActivity.this);
        View view = LayoutInflater.from(RabbitHealthActivity.this).inflate(
                R.layout.confirmation_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Update");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Are you sure?");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancel");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.update_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data_getter();
                if(rabbitID.equals("") || rabbitName.equals("") || rabbitNotes.equals("") || rabbitAdministration.equals("")
                        || rabbitDisease.equals("") || rabbitVaccination.equals("")){
                    showErrorDialog();
                }else{
                    documentRabbitReference = db.document(healthCollectionTitle+rabbitID);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        documentRabbitReference.set(healthData, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        successDescriptionText = "Health Information Updated!";
                                                        showSuccessDialog();
                                                        clear_data();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RabbitHealthActivity.this, "Health Update Failed!" , Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        errorTitle = "Error";
                                        errorMessage = "There is no existing rabbit record to update";
                                        showErrorDialog();
                                    }
                                }
                            });
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private void delete_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitHealthActivity.this);
        View view = LayoutInflater.from(RabbitHealthActivity.this).inflate(
                R.layout.confirmation_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Delete");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Are you sure?");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancel");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.delete_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rabbitID = textRabbitID.getEditableText().toString();
                if(rabbitID.equals("")){
                    Toast.makeText(RabbitHealthActivity.this, "There is no ID input to delete", Toast.LENGTH_SHORT).show();
                }else{
                    documentRabbitReference = db.document(healthCollectionTitle+rabbitID);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        documentRabbitReference.delete();
                                        successDescriptionText = "Health Information Deleted!";
                                        showSuccessDialog();
                                        clear_data();
                                    }else{
                                        errorTitle = "Error";
                                        errorMessage = "No existing record!";
                                        showErrorDialog();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RabbitHealthActivity.this, "Deletion error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    String errorTitle = "Error", errorMessage = "Fill the necessary form!";
    @SuppressLint("SetTextI18n")
    private void showErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitHealthActivity.this);
        View view = LayoutInflater.from(RabbitHealthActivity.this).inflate(
                R.layout.layout_error_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );

        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(errorTitle);
        ((TextView) view.findViewById(R.id.textMessage)).setText(errorMessage);
        ((Button) view.findViewById(R.id.buttonAction)).setText("Okay");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.error_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void clear_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitHealthActivity.this);
        View view = LayoutInflater.from(RabbitHealthActivity.this).inflate(
                R.layout.confirmation_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Clear fields");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Are you sure?");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancel");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.clear_field_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_data();
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private final String stringURLEndPoint = "https://api.openai.com/v1/chat/completions";
    private final String stringAPIKey = "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";
    private String stringOutput = "";

    public String rabbitNutritionAdvice;
    LoadingDialog loadingDialog;
    public void health_advisor(){

        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        }, 5000);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            rabbitAdministration = dropdownHealthAdministrationFor.getEditableText().toString();
            rabbitDisease = dropdownDiseaseOutbreak.getEditableText().toString();
            //rabbitVaccination = dropdownVaccination.getEditableText().toString();

            rabbitNutritionAdvice = String.format("In three sentences what can be the health advice for a %s rabbit that is %s and has %s?",rabbitAdministration,status,rabbitDisease);

            JSONArray jsonArrayMessage = new JSONArray();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("role", "user");
            jsonObjectMessage.put("content", rabbitNutritionAdvice);
            jsonArrayMessage.put(jsonObjectMessage);

            jsonObject.put("messages", jsonArrayMessage);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                stringURLEndPoint, jsonObject, new Response.Listener< JSONObject > () {
            @Override
            public void onResponse(JSONObject response) {

                String stringText = null;
                try {
                    stringText = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                stringOutput = stringOutput + stringText;

                textRabbitNotes.setText(stringOutput);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Authorization", "Bearer " + stringAPIKey);
                mapHeader.put("Content-Type", "application/json");

                return mapHeader;
            }

            @Override
            protected Response <JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        int intTimeoutPeriod = 60000; // 60 seconds timeout duration defined
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }
}