package com.capstone.wmconejoelpatio;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RabbitBreedingActivity extends DrawerBaseActivity implements DatePickerDialog.OnDateSetListener{

    Button buckSelect, doeSelect;
    Button studDate, birthDate, studResult;
    TextInputEditText litterSize, litterSurvived;
    TextInputEditText beforeStudWeight,afterStudDaysWeight;
    Button afterWeaning;
    Button submit,update,delete,clear,viewAll;
    ImageButton enabler,validate,validateResult;

    String sireName = "",sireBreed,sireOrigin,sireStatus,sireAccumulatedKits,sireSuccessRate, sireDeadKits;
    String damName = "",damBreed,damOrigin,damStatus,damAccumulatedKits,damSuccessRate, damDeadKits;
    TextView buckName,buckBreed, buckOrigin, buckStatus, buckAccumulatedKits, buckSuccessRate;
    TextView doeName,doeBreed, doeOrigin, doeStatus, doeAccumulatedKits, doeSuccessRate;

    String litterSizeString, litterSurvivedString, beforeStudWeightString, afterStudDaysWeightString, afterWeaningString, studDateString, birthDateString, studResultString;
    int DATE_DIALOG = 0;

    SireBreedersData sireBreedersData;
    DamBreedersData damBreedersData;
    RabbitEntryData entryData;
    RabbitBreedingData rabbitBreedingData;

    CollectionReference collectionRabbitReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentRabbitReference;

    String documentName;
    boolean fieldValidator;
    boolean weightLoad;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_breeding, contentFrameLayout);

        getSupportActionBar().setTitle("Add Breeding");

        rabbitBreedingData = new RabbitBreedingData();

        buckSelect = findViewById(R.id.btnBreedingBuckSelect);
        doeSelect = findViewById(R.id.btnBreedingDoeSelect);

        buckName = findViewById(R.id.textRabbitBreedingNameBuck);
        buckBreed = findViewById(R.id.textRabbitBreedingBreedBuck);
        buckOrigin = findViewById(R.id.textRabbitBreedingOriginBuck);
        buckStatus = findViewById(R.id.textRabbitBreedingStatusBuck);
        buckAccumulatedKits = findViewById(R.id.textRabbitBreedingAccumulatedKitsBuck);
        buckSuccessRate = findViewById(R.id.textRabbitBreedingSuccessRateBuck);

        doeName = findViewById(R.id.textRabbitBreedingNameDoe);
        doeBreed = findViewById(R.id.textRabbitBreedingBreedDoe);
        doeOrigin = findViewById(R.id.textRabbitBreedingOriginDoe);
        doeStatus = findViewById(R.id.textRabbitBreedingStatusDoe);
        doeAccumulatedKits = findViewById(R.id.textRabbitBreedingAccumulatedKitsDoe);
        doeSuccessRate = findViewById(R.id.textRabbitBreedingSuccessRateDoe);

        litterSize = findViewById(R.id.textBreedingLitterSize);
        litterSurvived = findViewById(R.id.textBreedingSurvivedLitterSize);

        beforeStudWeight = findViewById(R.id.textBreedingBeforeStudWeight);
        afterStudDaysWeight = findViewById(R.id.textBreedingAfterStudWeight);

        afterWeaning = findViewById(R.id.btnBreedingDateAfterWeaning);
        studDate = findViewById(R.id.btnBreedingStudDate);
        birthDate = findViewById(R.id.btnBreedingDueDate);
        studResult = findViewById(R.id.btnBreedingStudResult);

        submit = findViewById(R.id.btnBreedingSubmit);
        update = findViewById(R.id.btnBreedingUpdate);
        clear = findViewById(R.id.btnBreedingClear);
        delete = findViewById(R.id.btnBreedingDelete);
        viewAll = findViewById(R.id.btnBreedingViewAll);

        enabler = findViewById(R.id.btnBreedingValidateBreedLinageEnabler);
        validate = findViewById(R.id.btnBreedingValidateBreedLinage);

        loadingDialog = new LoadingDialog(RabbitBreedingActivity.this);

        try{
            show_update();
            breeding_data_load();
        }catch (Exception exception){
            System.err.println("Exception Breeding Load: " + exception.getMessage());
            Log.d("TAG",exception.getMessage());
        }

        try{

        }catch (Exception exception){
            System.err.println("Start Exception: " + exception.getMessage());
            Log.d("TAG", exception.getMessage());
        }

        try{
            submit_function();
            update_function();
            clear_function();
            delete_function();
            viewAll_function();
            validate.setEnabled(false);
        }catch (Exception exception){
            System.err.println("Button Exception: " + exception.getMessage());
            Log.d("TAG", exception.getMessage());
        }

        try{
            buck_select();
            doe_select();
        }catch (Exception exception){
            System.err.println("Exception: " + exception.getMessage());
            Log.d("TAG",exception.getMessage());
        }

        try{
            buck_selected_load();
            doe_selected_load();
        }catch (Exception exception){
            System.err.println("Exception Sire and Dam Selected: " + exception.getMessage());
            Log.d("TAG",exception.getMessage());
        }

        try {
            stud_date();
            birth_date();
            afterWeaning_date();
        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
        }




    }

    String successDescriptionText = "";
    @SuppressLint("SetTextI18n")
    private void showSuccessDialog(){
        ConstraintLayout successConstraintLayout = findViewById(R.id.successConstraintLayout);
        View viewSuccess = LayoutInflater.from(RabbitBreedingActivity.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = viewSuccess.findViewById(R.id.successDone);
        TextView successDescription = viewSuccess.findViewById(R.id.successDescription);
        successDescription.setText(successDescriptionText);

        AlertDialog.Builder  builder =  new AlertDialog.Builder(RabbitBreedingActivity.this);
        builder.setView(viewSuccess);
        final AlertDialog alertDialog = builder.create();

        successDone.findViewById(R.id.successDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccessDialog_update(){
        ConstraintLayout successConstraintLayout = findViewById(R.id.successConstraintLayout);
        View viewSuccess = LayoutInflater.from(RabbitBreedingActivity.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = viewSuccess.findViewById(R.id.successDone);
        TextView successDescription = viewSuccess.findViewById(R.id.successDescription);
        successDescription.setText(successDescriptionText);

        AlertDialog.Builder  builder =  new AlertDialog.Builder(RabbitBreedingActivity.this);
        builder.setView(viewSuccess);
        final AlertDialog alertDialog = builder.create();

        successDone.findViewById(R.id.successDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_function();
                alertDialog.dismiss();
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
        inflater.inflate(R.menu.tool_bar_menu_breeding,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_toolbar_list:
                startActivity(new Intent(RabbitBreedingActivity.this, RabbitBreedingHistory.class));
                break;
            case R.id.nav_toolbar_breeding_validation:
                pair_checking();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void show_update(){
        Intent getData = getIntent();
        boolean rabbitShow = getData.getBooleanExtra("rabbitShow", false);
        if(rabbitShow){
            submit.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Update Breeding");
        }
    }

    private void pair_checking(){
        test_origin_validator();
        isFieldFilled = field_validator();
        if(isFieldFilled){
            errorTitle = "Error";
            errorMessage = "Breeding is not ready!";
            showErrorDialog();
        }else{
            loadingDialog.startLoadingDialog();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean ancestry = ancestry_validator();
                    if(ancestry){
                        rabbitBreedingStatus = "Caution Inbreeding!";
                        show_dialog();
                    }else{
                        rabbitBreedingStatus = "Normal";
                        rabbitBreedingMessage = "No inbreeding will occur!";
                        show_dialog_okay();
                    }
                    loadingDialog.dismissDialog();

                }
            }, 3000);
        }
    }

    private void breeding_data_load() {
        try{
            Intent getIntent = getIntent();
            String documentName = getIntent.getStringExtra("documentName");
            buck_selected_load();
            doe_selected_load();
            documentRabbitReference = db.document("Breeding/" + documentName);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            field_getter();
                            if(documentSnapshot.exists()){

                                rabbitBreedingData = documentSnapshot.toObject(RabbitBreedingData.class);

                                String stud = rabbitBreedingData.getStudDate();
                                String birth = rabbitBreedingData.getBirthDate();
                                size = rabbitBreedingData.getLitterSize();
                                survived = rabbitBreedingData.getLitterSurvived();
                                double beforeStud = rabbitBreedingData.getWeightBeforeStud();
                                double afterStud = rabbitBreedingData.getWeightAfterStud();
                                String weaning = rabbitBreedingData.getDateAfterWeaning();
                                String studRes = rabbitBreedingData.getStudResult();


                                studDate.setText(stud);
                                if(!stud.equals("Pick Stud Date")){
                                    studDate.setEnabled(false);
                                    studResult.setEnabled(true);
                                    studResult_inquiry();
                                }



                                birthDate.setText(birth);
                                if(!birth.equals("Pick Birth Date")){
                                    birthDate.setEnabled(false);
                                    afterWeaning.setEnabled(true);
                                }

                                afterWeaning.setText(weaning);
                                if(!weaning.equals("Pick Date")){
                                    afterWeaning.setEnabled(false);
                                }

                                litterSize.setText(String.valueOf(size));
                                if(size == 0){
                                    litterSize.setEnabled(true);
                                }else{
                                    litterSize.setEnabled(false);
                                }

                                afterStudDaysWeight.setText(String.valueOf(afterStud));
                                if(afterStud == 0){
                                    afterStudDaysWeight.setEnabled(true);
                                }else{
                                    afterStudDaysWeight.setEnabled(false);
                                }



                                litterSurvived.setText(String.valueOf(survived));
                                beforeStudWeight.setText(String.valueOf(beforeStud));

                                litterSurvived.setEnabled(true);

                                buckSelect.setEnabled(false);
                                doeSelect.setEnabled(false);

                                studResult.setText(studRes);
                                if(studRes.equals("Success")){
                                    birthDate.setEnabled(true);
                                    studResult.setEnabled(false);
                                } else if (studRes.equals("Failed")) {
                                    studResult.setEnabled(false);
                                    birthDate.setEnabled(false);
                                    afterWeaning.setEnabled(false);
                                    litterSize.setEnabled(false);
                                    afterStudDaysWeight.setEnabled(false);
                                    litterSurvived.setEnabled(false);

                                }

                                Toast.makeText(RabbitBreedingActivity.this, "Information Successfully Loaded!", Toast.LENGTH_SHORT).show();
                                System.out.println("Breeding Loaded");

                            }
                        }
                    });
        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
            System.out.println("--------------------------------------------------");
            System.err.println(exception.getMessage());
            System.out.println("--------------------------------------------------");
        }
    }

    private void field_getter(){

        sireName = buckName.getText().toString();
        sireBreed = buckBreed.getText().toString();
        sireSuccessRate = buckSuccessRate.getText().toString();

        damName = doeName.getText().toString();
        damBreed = doeBreed.getText().toString();
        damSuccessRate = doeSuccessRate.getText().toString();

        litterSizeString = litterSize.getEditableText().toString();
        litterSurvivedString = litterSurvived.getEditableText().toString();

        beforeStudWeightString = beforeStudWeight.getEditableText().toString();
        afterStudDaysWeightString = afterStudDaysWeight.getEditableText().toString();

        afterWeaningString = afterWeaning.getText().toString();
        studDateString = studDate.getText().toString();
        birthDateString = birthDate.getText().toString();
        studResultString = studResult.getText().toString();


    }

    private void data_getter(){

        field_getter();

        rabbitBreedingData.setSireName(sireName);
        rabbitBreedingData.setSireBreed(sireBreed);
        rabbitBreedingData.setSireSuccessRate(sireSuccessRate);

        rabbitBreedingData.setDamName(damName);
        rabbitBreedingData.setDamBreed(damBreed);
        rabbitBreedingData.setDamSuccessRate(damSuccessRate);

        if(litterSizeString.equals("") || litterSurvivedString.equals("") || afterStudDaysWeightString.equals("")){
            Log.d("TAG","Empty");
        }else{
            rabbitBreedingData.setLitterSize(Long.parseLong(litterSizeString));
            rabbitBreedingData.setLitterSurvived(Long.parseLong(litterSurvivedString));

            rabbitBreedingData.setWeightAfterStud(Double.parseDouble(afterStudDaysWeightString));
        }

        rabbitBreedingData.setWeightBeforeStud(Double.parseDouble(beforeStudWeightString));
        rabbitBreedingData.setStudDate(studDateString);
        rabbitBreedingData.setBirthDate(birthDateString);
        rabbitBreedingData.setDateAfterWeaning(afterWeaningString);
        rabbitBreedingData.setStudResult(studResultString);

        documentName = sireName+damName+studDateString;
        rabbitBreedingData.setDocumentName(documentName);
    }

    private void recentStudDate(){

        field_getter();

        DocumentReference documentDamRabbitReferenceSuccess = db.document("DamBreeders/" +damName);
        documentDamRabbitReferenceSuccess.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            DamBreedersData damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                            assert damBreedersData != null;
                            damBreedersData.setRecentStudDate(studDateString);
                            documentDamRabbitReferenceSuccess.set(damBreedersData, SetOptions.merge());
                        }else {
                            Toast.makeText(RabbitBreedingActivity.this, "Dam success rate does not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void clear_fields(){
        buckName.setText("");
        buckBreed.setText("");
        buckOrigin.setText("");
        buckStatus.setText("");
        buckAccumulatedKits.setText("");
        buckSuccessRate.setText("");

        doeName.setText("");
        doeBreed.setText("");
        doeOrigin.setText("");
        doeStatus.setText("");
        doeAccumulatedKits.setText("");
        doeSuccessRate.setText("");

        litterSize.setText("");
        litterSurvived.setText("");
        beforeStudWeight.setText("");
        afterStudDaysWeight.setText("");

        afterWeaning.setText("Pick Date");
        studDate.setText("Pick Stud Date");
        birthDate.setText("Pick Birth Date");

        buckSelect.setEnabled(true);
        doeSelect.setEnabled(true);
        litterSize.setEnabled(true);
        afterWeaning.setEnabled(true);
        studDate.setEnabled(true);
        birthDate.setEnabled(true);
    }

    private void litter_breeders_incrementer(){

        field_getter();

        DocumentReference documentSireRabbitReference = db.document("SireBreeders/" +sireName);
        documentSireRabbitReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            sireBreedersData = documentSnapshot.toObject(SireBreedersData.class);
                            assert sireBreedersData != null;
                            if(litterSizeString.equals("")){

                            }else {
                                long litterSizeGetter = sireBreedersData.getSireAccumulatedKits();
                                long currentLitter = Long.parseLong(litterSizeString);
                                long result = litterSizeGetter + currentLitter;
                                sireBreedersData.setSireAccumulatedKits(result);
                                documentSireRabbitReference.set(sireBreedersData, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                calculate_deceased();
                                            }
                                        });
                            }
                        }
                    }
                });

        DocumentReference documentDamRabbitReference = db.document("DamBreeders/" +damName);
        documentDamRabbitReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                            if(litterSizeString.equals("")){
                            }else{
                                assert damBreedersData != null;
                                long litterSizeGetter = damBreedersData.getDamAccumulatedKits();
                                long currentLitter = Long.parseLong(litterSizeString);
                                long result = litterSizeGetter + currentLitter;
                                damBreedersData.setDamAccumulatedKits(result);
                                documentDamRabbitReference.set(damBreedersData, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //update_pregnant_status_no();
                                                calculate_deceased();
                                                //Toast.makeText(RabbitBreedingActivity.this, "Dam", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        }else {
                            Toast.makeText(RabbitBreedingActivity.this, "Dam kits does not increment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void submit_function(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    submit_show_confirmation_dialog();

                } catch (Exception exception) {
                    Toast.makeText(RabbitBreedingActivity.this, "Submit Exception: \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Exception: " + exception.getMessage());
                }
            }
        });
    }

    private void calculate_sire_success(){

        field_getter();

        DocumentReference documentSireRabbitReferenceSuccess = db.document("SireBreeders/" +sireName);
        documentSireRabbitReferenceSuccess.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){

                            SireBreedersData sireBreedersData = documentSnapshot.toObject(SireBreedersData.class);

                            assert sireBreedersData != null;
                            long litterAccumulatedKits =  sireBreedersData.getSireAccumulatedKits();
                            long litterDeceasedKitsUpdated = sireBreedersData.getSireDeathKits();


                            double upper = (double) (litterAccumulatedKits - litterDeceasedKitsUpdated) / litterAccumulatedKits;
                            long multiplier = 100;
                            long updatedResult = (long) (upper * multiplier);

                            sireBreedersData.setSireSuccessRate(updatedResult);
                            documentSireRabbitReferenceSuccess.set(sireBreedersData, SetOptions.merge());
                        }
                        else {
                            Toast.makeText(RabbitBreedingActivity.this, "Sire success rate does not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void calculate_dam_success(){

        field_getter();

        DocumentReference documentDamRabbitReferenceSuccess = db.document("DamBreeders/" +damName);
        documentDamRabbitReferenceSuccess.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            DamBreedersData damBreedersData = documentSnapshot.toObject(DamBreedersData.class);

                            assert damBreedersData != null;
                            long litterAccumulatedKits =  damBreedersData.getDamAccumulatedKits();
                            long litterDeceasedKitsUpdated = damBreedersData.getDamDeathKits();

                            double upper = (double) (litterAccumulatedKits - litterDeceasedKitsUpdated) / litterAccumulatedKits;
                            long multiplier = 100;
                            long updatedResult = (long) (upper * multiplier);

                            damBreedersData.setDamSuccessRate(updatedResult);
                            documentDamRabbitReferenceSuccess.set(damBreedersData, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //update_pregnant_status_no();
                                            if(!litterSizeString.equals("0") && !litterSizeString.equals("")){
                                                update_pregnant_status_no();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(RabbitBreedingActivity.this, "Dam success rate does not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    long size, survived;
    private void calculate_deceased(){
        try{
            field_getter();
            long litterSize = Long.parseLong(litterSizeString);
            long litterSurvived = Long.parseLong(litterSurvivedString);

            DocumentReference documentSireRabbitReference = db.document("SireBreeders/" +sireName);
            documentSireRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                SireBreedersData sireBreedersData = documentSnapshot.toObject(SireBreedersData.class);
                                if(litterSizeString.equals("") || litterSurvivedString.equals("")){
                                    System.err.println("No data");
                                }else {
                                    assert sireBreedersData != null;
                                    long litterDeceasedKits = sireBreedersData.getSireDeathKits(); // 10

                                    long currentDeceased = size - survived; // 6 & 5 = 1

                                    long updateLitterDeceasedKits = litterDeceasedKits - currentDeceased; // 10 - 1 = 9

                                    long deceased = litterSize - litterSurvived; // 6 - 3 = 3

                                    long deceasedResult = updateLitterDeceasedKits + deceased; // 9 + 3 = 12

                                    sireBreedersData.setSireDeathKits(deceasedResult);
                                    documentSireRabbitReference.set(sireBreedersData, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    System.out.println("----------------------------------------");
                                                    System.out.println("Sire Deceased Kits: " + litterDeceasedKits);
                                                    System.out.println("Breeding Litter Size: " + size);
                                                    System.out.println("Breeding Survived Sized: " + survived);
                                                    System.out.println("litterDeceasedKits + currentDeceased = " + updateLitterDeceasedKits);
                                                    System.out.println("Deceased New Input: " + litterSize + " - " + litterSurvived + " = " + deceased);
                                                    System.out.println("Updated Result (updateLitterDeceasedKits + newDeceased):" + deceasedResult);
                                                    System.out.println("----------------------------------------");
                                                    calculate_sire_success();
                                                    calculate_sire_success();
                                                }
                                            });
                                }
                            }
                            else {
                                Toast.makeText(RabbitBreedingActivity.this, "Sire success rate does not updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            DocumentReference documentDamRabbitReference = db.document("DamBreeders/" +damName);
            documentDamRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                DamBreedersData damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                                if(litterSizeString.equals("") || litterSurvivedString.equals("")){
                                    System.err.println("No data");
                                }else{
                                    assert damBreedersData != null;
                                    long litterDeceasedKits = damBreedersData.getDamDeathKits();

                                    long currentDeceased = size - survived; // 6 & 5 = 1

                                    long updateLitterDeceasedKits = litterDeceasedKits - currentDeceased; // 10 - 1 = 9

                                    long deceased = litterSize - litterSurvived; // 6 - 3 = 3

                                    long deceasedResult = updateLitterDeceasedKits + deceased; // 9 + 3 = 12

                                    damBreedersData.setDamDeathKits(deceasedResult);
                                    documentDamRabbitReference.set(damBreedersData, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    //update_pregnant_status_no();

                                                    calculate_dam_success();
                                                    //calculate_dam_success();
                                                }
                                            });
                                }
                            }else {
                                Toast.makeText(RabbitBreedingActivity.this, "Dam success rate does not updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
        }

    }

    boolean survivedChecker;
    private boolean size_survived_checker(){
        field_getter();
        long litterSize = Long.parseLong(litterSizeString);
        long litterSurvived = Long.parseLong(litterSurvivedString);
        if(litterSize < litterSurvived){
            survivedChecker = true;
            errorTitle = "Error";
            errorMessage = "Kits that survived is greater than total litter!";
            showErrorDialog();
        }else {
            survivedChecker = false;
        }
        return survivedChecker;
    }

    private void update_dam_weight(){
        damName = doeName.getText().toString();
        afterStudDaysWeightString = afterStudDaysWeight.getEditableText().toString();
        if(afterStudDaysWeightString.equals("")){
            System.out.println("Empty");
        }else{
            DocumentReference documentReference = db.document("Rabbit/" +damName);
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                assert entryData != null;
                                entryData.setWeight(Double.parseDouble(afterStudDaysWeightString));
                                documentReference.set(entryData, SetOptions.merge());
                            }
                        }
                    });
        }
    }
    private void update_function(){
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_show_confirmation_dialog();
            }
        });
    }

    private void reset_function(){
        Intent clear = new Intent(RabbitBreedingActivity.this, RabbitBreedingActivity.class);
        clear.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(clear);
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
        try {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete_show_confirmation_dialog();
                }
            });
        } catch (Exception exception) {
            Toast.makeText(RabbitBreedingActivity.this, "Submit Exception: \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("Exception: " + exception.getMessage());
        }
    }

    private void viewAll_function(){
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitBreedingActivity.this, RabbitBreedingHistory.class));
            }
        });
    }

    private boolean field_validator(){
        field_getter();
        if(sireName.trim().equals("") || damName.trim().equals("") || studDate.getText().equals("Pick Stud Date")){
            errorTitle = "Error";
            errorMessage = "Fill the prerequisite forms!";
            showErrorDialog();
            fieldValidator = true;
        }else{
            fieldValidator = false;
        }
        return fieldValidator;
    }

    boolean birthValidate;
    private boolean birth_field_validator(){
        field_getter();
        if(birthDate.getText().equals("Pick Birth Date") && !litterSizeString.trim().equals("0")){
            birthValidate = true;
            errorTitle = "Error";
            errorMessage = "Fill the birth date first before the litter size form!";
            showErrorDialog();
        }else{
            birthValidate = false;
        }
        return birthValidate;
    }

    boolean update_fieldValidator;
    private boolean update_field_validator(){
        field_getter();

        if(afterStudDaysWeightString.trim().equals("0")){
            errorTitle = "Error";
            errorMessage = "Fill the weight of doe after 10 days!";
            showErrorDialog();
            update_fieldValidator = true;
        }else{
            update_fieldValidator = false;
        }
        return update_fieldValidator;
    }

    private void buck_select(){
        buckSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(RabbitBreedingActivity.this, RabbitBreedersActivity.class);
                    intent.putExtra("rabbitGender","Buck");
                    intent.putExtra("damName",damName);
                    startActivity(intent);
                }catch (Exception exception){
                    Log.d("TAG", exception.getMessage());
                    System.out.println("--------------------------------------------------");
                    System.err.println(exception.getMessage());
                    System.out.println("--------------------------------------------------");
                }
            }
        });
    }

    private void buck_selected_load(){
        try{
            Intent getIntent = getIntent();
            sireName = getIntent.getStringExtra("sireName");

            documentRabbitReference = db.document("SireBreeders/" + sireName);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                sireBreedersData = documentSnapshot.toObject(SireBreedersData.class);
                                String name = sireBreedersData.getSireName();
                                String breed = sireBreedersData.getSireBreed();
                                String origin = sireBreedersData.getSireOrigin();
                                String status = sireBreedersData.getSireStatus();
                                long totalKits = sireBreedersData.getSireAccumulatedKits();
                                long successRate = sireBreedersData.getSireSuccessRate();

                                buckName.setText(name);
                                buckBreed.setText(breed);
                                buckOrigin.setText(origin);
                                buckStatus.setText(status);
                                buckAccumulatedKits.setText(String.valueOf(totalKits));
                                buckSuccessRate.setText(String.valueOf(successRate)+"%");

                            }
                        }
                    });
        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
            System.out.println("--------------------------------------------------");
            System.err.println(exception.getMessage());
            System.out.println("--------------------------------------------------");
        }
    }

    private void doe_select(){
        doeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(RabbitBreedingActivity.this, RabbitBreedersActivity.class);
                    intent.putExtra("rabbitGender","Doe");
                    intent.putExtra("sireName",sireName);
                    startActivity(intent);
                }catch (Exception exception){
                    Log.d("TAG", exception.getMessage());
                    System.out.println("--------------------------------------------------");
                    System.out.println(exception.getMessage());
                    System.out.println("--------------------------------------------------");
                }
            }
        });
    }

    private void doe_weight_load(){
        Intent getIntent = getIntent();
        damName = getIntent.getStringExtra("damName");
        afterStudDaysWeightString = afterStudDaysWeight.getEditableText().toString();
        documentRabbitReference = db.document("Rabbit/" + damName);
        documentRabbitReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        entryData = documentSnapshot.toObject(RabbitEntryData.class);
                        assert entryData != null;
                        double damWeight = entryData.getWeight();
                        if(studDate.getText().equals("Pick Stud Date")){
                            beforeStudWeight.setText(String.valueOf(damWeight));
                        }
                    }
                });
    }

    private void doe_selected_load(){
        try{

            Intent getIntent = getIntent();
            damName = getIntent.getStringExtra("damName");

            documentRabbitReference = db.document("DamBreeders/" + damName);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                                String name = damBreedersData.getDamName();
                                String breed = damBreedersData.getDamBreed();
                                String origin = damBreedersData.getDamOrigin();
                                String status = damBreedersData.getDamStatus();
                                long totalKits = damBreedersData.getDamAccumulatedKits();
                                long successRate = damBreedersData.getDamSuccessRate();

                                doeName.setText(name);
                                doeBreed.setText(breed);
                                doeOrigin.setText(origin);
                                doeStatus.setText(status);
                                doeAccumulatedKits.setText(String.valueOf(totalKits));
                                doeSuccessRate.setText(String.valueOf(successRate)+"%");


                                doe_weight_load();
                                /*
                                if(weightLoad){
                                    doe_weight_load();
                                }

                                 */


                            }
                        }
                    });

        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
            System.out.println("--------------------------------------------------");
            System.out.println(exception.getMessage());
            System.out.println("--------------------------------------------------");
        }
    }

    private void openDatePickerDialog(){
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "Date picker");
    }

    private void stud_date(){
        studDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DATE_DIALOG = 1;
                openDatePickerDialog();
            }
        });
    }

    private void birth_date(){
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DATE_DIALOG = 2;
                openDatePickerDialog();
            }
        });
    }

    private void afterWeaning_date(){
        afterWeaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DATE_DIALOG = 3;
                openDatePickerDialog();
            }
        });
    }

    private void studResult_inquiry(){
        studResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studResult_show_confirmation_dialog();
            }
        });
    }

    private void studResult_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        View view = LayoutInflater.from(RabbitBreedingActivity.this).inflate(
                R.layout.confirmation_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Stud Result");
        ((TextView) view.findViewById(R.id.textMessage)).setText("What is the result of this mating?");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Success");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Failed");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.question_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studResult.setText("Success");
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studResult.setText("Failed");
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    String stud,birth,weaning;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar c = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);


        if(DATE_DIALOG == 1){
            stud = sdf.format(c.getTime());
            studDate.setText(stud);
            studResult.setText("Ongoing");


            AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
            View viewBirth = LayoutInflater.from(RabbitBreedingActivity.this).inflate(
                    R.layout.confirmation_dialog,
                    (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
            );
            builder.setView(viewBirth);
            ((TextView) viewBirth.findViewById(R.id.textTitle)).setText("Expected Birth Date");
            ((TextView) viewBirth.findViewById(R.id.textMessage)).setText("Do you want to adhere in expected 30 days birth date?");
            ((Button) viewBirth.findViewById(R.id.buttonYes)).setText("Yes");
            ((Button) viewBirth.findViewById(R.id.buttonNo)).setText("Ignore");
            ((ImageView) viewBirth.findViewById(R.id.imageIcon)).setImageResource(R.drawable.question_icon);

            final AlertDialog alertDialog = builder.create();

            viewBirth.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c.add(Calendar.DAY_OF_MONTH, 30);
                    birth = sdf.format(c.getTime());
                    birthDate.setText(birth);
                    alertDialog.dismiss();
                }
            });

            viewBirth.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            if (alertDialog.getWindow() != null){
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            alertDialog.show();

        } else if(DATE_DIALOG == 2){
            birth = sdf.format(c.getTime());
            try{
                LocalDate stud_date = LocalDate.parse(studDate.getText(), DateTimeFormatter.ISO_DATE);
                LocalDate birth_date = LocalDate.parse(birth, DateTimeFormatter.ISO_DATE);
                if(stud_date.isBefore(birth_date)){
                    birthDate.setText(birth);
                }else{
                    errorTitle = "Error";
                    errorMessage = "Birthdate is not valid!";
                    showErrorDialog();
                    birthDate.setText("Pick Birth Date");
                }
            }catch (Exception exception){
                System.out.println("---------------------------");
                System.out.println("Time Exception: " + exception.getMessage());
                System.out.println("---------------------------");
            }
        } else if(DATE_DIALOG == 3){
            weaning = sdf.format(c.getTime());
            try{
                LocalDate birth_date = LocalDate.parse(birthDate.getText(), DateTimeFormatter.ISO_DATE);
                LocalDate weaning_date = LocalDate.parse(weaning, DateTimeFormatter.ISO_DATE);
                if(birth_date.isBefore(weaning_date)){
                    afterWeaning.setText(weaning);
                }else{
                    errorTitle = "Error";
                    errorMessage = "Weaning date is not valid!";
                    showErrorDialog();
                    afterWeaning.setText("Pick Date");
                }
            }catch (Exception exception){
                System.out.println("---------------------------");
                System.out.println("Time Exception: " + exception.getMessage());
                System.out.println("---------------------------");
            }

        }
    }

    String sireParentSire="sireParentSire", sireParentDam="sireParentDam", sire_parentSire_GrandParentSire="sire_parentSire_GrandParentSire", sire_parentSire_GrandParentDam="sire_parentSire_GrandParentDam", sire_parentDam_GrandParentSire="sire_parentDam_GrandParentSire", sire_parentDam_GrandParentDam="sire_parentDam_GrandParentDam";
    String damParentSire="damParentSire", damParentDam="damParentDam", dam_parentSire_GrandParentSire="dam_parentSire_GrandParentSire", dam_parentSire_GrandParentDam="dam_parentSire_GrandParentDam", dam_parentDam_GrandParentSire="dam_parentDam_GrandParentSire", dam_parentDam_GrandParentDam="dam_parentDam_GrandParentDam";

    //
    // Sire
    //sire_parentSire_GrandParentSire
    String sire_parentSire_GrandParentSire_GreatSire="sire_parentSire_GrandParentSire_GreatSire",sire_parentSire_GrandParentSire_GreatDam="sire_parentSire_GrandParentSire_GreatDam";

    //sire_parentSire_GrandParentDam
    String sire_parentSire_GrandParentDam_GreatSire="sire_parentSire_GrandParentDam_GreatSire",sire_parentSire_GrandParentDam_GreatDam="sire_parentSire_GrandParentDam_GreatDam";

    //sire_parentDam_GrandParentSire
    String sire_parentDam_GrandParentSire_GreatSire="sire_parentDam_GrandParentSire_GreatSire",sire_parentDam_GrandParentSire_GreatDam="sire_parentDam_GrandParentDam_GreatDam";

    //sire_parentDam_GrandParentDam
    String sire_parentDam_GrandParentDam_GreatSire="sire_parentDam_GrandParentDam_GreatSire", sire_parentDam_GrandParentDam_GreatDam="sire_parentDam_GrandParentDam_GreatDam";

    //
    // Dam
    //dam_parentSire_GrandParentSire
    String dam_parentSire_GrandParentSire_GreatSire="dam_parentSire_GrandParentSire_GreatSire",dam_parentSire_GrandParentSire_GreatDam="dam_parentSire_GrandParentSire_GreatDam";

    //dam_parentSire_GrandParentDam
    String dam_parentSire_GrandParentDam_GreatSire="dam_parentSire_GrandParentDam_GreatSire",dam_parentSire_GrandParentDam_GreatDam="dam_parentSire_GrandParentDam_GreatDam";

    //dam_parentDam_GrandParentSire
    String dam_parentDam_GrandParentSire_GreatSire="dam_parentDam_GrandParentSire_GreatSire",dam_parentDam_GrandParentSire_GreatDam="dam_parentDam_GrandParentSire_GreatDam";

    //dam_parentDam_GrandParentDam
    String dam_parentDam_GrandParentDam_GreatSire="dam_parentDam_GrandParentDam_GreatSire",dam_parentDam_GrandParentDam_GreatDam="dam_parentDam_GrandParentDam_GreatDam";

    boolean isFieldFilled;

    private void test_origin_validator(){
        try{
            sireName = buckName.getText().toString();
            if(sireName.equals("")){
                System.out.println("1036 error");
            }else {
                DocumentReference documentRabbitReference = db.document("Rabbit/" + sireName);
                documentRabbitReference
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                    assert entryData != null;
                                    sireParentSire = entryData.getSire();
                                    sireParentDam = entryData.getDam();

                                    sireParent();
                                }
                            }
                        });

            }

            damName = doeName.getText().toString();
            if(damName.equals("")){
                System.out.println("1055 error");
            }else {
                DocumentReference documentRabbitReference = db.document("Rabbit/" + damName);
                documentRabbitReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                    assert entryData != null;
                                    damParentSire = entryData.getSire();
                                    damParentDam = entryData.getDam();

                                    damParent();
                                }
                            }
                        });
            }

        }catch (Exception exception){
            Log.d("TAG", exception.getMessage());
            System.out.println("-----------------------------------------");
            System.out.println("Sibling Error: \n" + exception.getMessage());
            System.out.println("-----------------------------------------");
        }
    }

    private void sireParent(){
        //
        // 2nd Degree
        // Getting the grandparents (father side) of sire
        if(sireParentSire.equals("")){
            System.out.println("1036 error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + sireParentSire);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                assert entryData != null;
                                sire_parentSire_GrandParentSire = entryData.getSire();
                                sire_parentSire_GrandParentDam = entryData.getDam();

                                sire_parentSire_great_grandParents();
                            }
                        }
                    });
        }

        // Getting the grandparents (mother side) of sire
        if(sireParentDam.equals("")){
            System.out.println("1055 error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + sireParentDam);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                assert entryData != null;
                                sire_parentDam_GrandParentSire = entryData.getSire();
                                sire_parentDam_GrandParentDam = entryData.getDam();

                                sire_parentDam_great_grandParents();
                            }
                        }
                    });
        }
    }

    private void damParent(){
        // Getting the grandparents (father side) of dam
        if(damParentSire.equals("")){
            System.out.println("1036 error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + damParentSire);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                assert entryData != null;
                                dam_parentSire_GrandParentSire = entryData.getSire();
                                dam_parentSire_GrandParentDam = entryData.getDam();

                                dam_parentSire_great_grandParents();
                            }
                        }
                    });
        }

        // Getting the grandparents (mother side) of dam
        if(damParentDam.equals("")){
            System.out.println("1055 error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + damParentDam);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                assert entryData != null;
                                dam_parentDam_GrandParentSire = entryData.getSire();
                                dam_parentDam_GrandParentDam = entryData.getDam();

                                dam_parentDam_great_grandParents();
                            }
                        }
                    });

        }
    }

    // Sire - Parents - Great Grand Parents
    private void sire_parentSire_great_grandParents(){
        // --- Sire ---
        // sire_parentSire_GrandParentSire
        if(sire_parentSire_GrandParentSire.equals("")){
            System.out.println("sire_parentSire_GrandParentSire error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + sire_parentSire_GrandParentSire);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                sire_parentSire_GrandParentSire_GreatSire = entryData.getSire();
                                sire_parentSire_GrandParentSire_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }

        // sire_parentSire_GrandParentDam
        if(sire_parentSire_GrandParentDam.equals("")){
            System.out.println("sire_parentSire_GrandParentDam error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + sire_parentSire_GrandParentDam);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                sire_parentSire_GrandParentDam_GreatSire = entryData.getSire();
                                sire_parentSire_GrandParentDam_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }
    }

    private void sire_parentDam_great_grandParents(){
        // sire_parentDam_GrandParentSire
        if(sire_parentDam_GrandParentSire.equals("")){
            System.out.println("sire_parentDam_GrandParentSire error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + sire_parentDam_GrandParentSire);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                sire_parentDam_GrandParentSire_GreatSire = entryData.getSire();
                                sire_parentDam_GrandParentSire_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }

        // sire_parentDam_GrandParentDam
        if(sire_parentDam_GrandParentDam.equals("")){
            System.out.println("sire_parentDam_GrandParentDam error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + sire_parentDam_GrandParentDam);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                sire_parentDam_GrandParentDam_GreatSire = entryData.getSire();
                                sire_parentDam_GrandParentDam_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }
    }

    // Dam - Parents - Great Grand Parents
    private void dam_parentSire_great_grandParents(){
        // --- Dam ---
        // dam_parentSire_GrandParentSire
        if(dam_parentSire_GrandParentSire.equals("")){
            System.out.println("dam_parentSire_GrandParentSire error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + dam_parentSire_GrandParentSire);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                dam_parentSire_GrandParentSire_GreatSire = entryData.getSire();
                                dam_parentSire_GrandParentSire_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }

        // dam_parentSire_GrandParentDam
        if(dam_parentSire_GrandParentDam.equals("")){
            System.out.println("dam_parentSire_GrandParentDam error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + dam_parentSire_GrandParentDam);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                dam_parentSire_GrandParentDam_GreatSire = entryData.getSire();
                                dam_parentSire_GrandParentDam_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }
    }

    private void dam_parentDam_great_grandParents(){
        // dam_parentDam_GrandParentSire
        if(dam_parentDam_GrandParentSire.equals("")){
            System.out.println("dam_parentDam_GrandParentSire error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + dam_parentDam_GrandParentSire);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                dam_parentDam_GrandParentSire_GreatSire = entryData.getSire();
                                dam_parentDam_GrandParentSire_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }

        // dam_parentDam_GrandParentDam
        if(dam_parentDam_GrandParentDam.equals("")){
            System.out.println("dam_parentDam_GrandParentDam error");
        }else {
            DocumentReference documentRabbitReference = db.document("Rabbit/" + dam_parentDam_GrandParentDam);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                dam_parentDam_GrandParentDam_GreatSire = entryData.getSire();
                                dam_parentDam_GrandParentDam_GreatDam = entryData.getDam();
                            }
                        }
                    });
        }
    }

    // November 18, 2023
    boolean ancestry;
    private boolean ancestry_validator() {
        if (sireName.equals(damParentSire) || damName.equals(sireParentDam))
        {
            rabbitBreedingMessage = "1st degree separation parent inbreeding!";
            ancestry = true;
        }
        else if (sireParentSire.equals(damParentSire) || sireParentDam.equals(damParentDam))
        {
            rabbitBreedingMessage = "1st degree separation sibling inbreeding!";
            ancestry = true;
        }
        else if(sireName.equals(dam_parentSire_GrandParentSire) || sireName.equals(dam_parentDam_GrandParentSire)
                || damName.equals(sire_parentSire_GrandParentDam) || damName.equals(sire_parentDam_GrandParentDam))
        {
            rabbitBreedingMessage = "2nd degree separation grandparent inbreeding!";
            ancestry = true;
        }
        else if(sire_parentSire_GrandParentSire.equals(damParentSire) || sire_parentDam_GrandParentSire.equals(damParentSire)
                || dam_parentSire_GrandParentSire.equals(sireParentSire) || dam_parentDam_GrandParentSire.equals(sireParentSire)
                || sire_parentSire_GrandParentDam.equals(damParentDam) || sire_parentDam_GrandParentDam.equals(damParentDam)
                || dam_parentSire_GrandParentDam.equals(sireParentDam) || dam_parentDam_GrandParentDam.equals(sireParentDam))
        {
            rabbitBreedingMessage = "2nd degree separation pibling inbreeding!";
            ancestry = true;
        }
        else if(sire_parentSire_GrandParentSire.equals(dam_parentSire_GrandParentSire) || sire_parentSire_GrandParentDam.equals(dam_parentSire_GrandParentDam)
                || sire_parentDam_GrandParentSire.equals(dam_parentDam_GrandParentSire) || sire_parentDam_GrandParentDam.equals(dam_parentDam_GrandParentDam)
                || sire_parentSire_GrandParentSire.equals(dam_parentDam_GrandParentSire) || sire_parentSire_GrandParentDam.equals(dam_parentDam_GrandParentDam))
        {
            rabbitBreedingMessage = "2nd degree separation cousin inbreeding!";
            ancestry = true;
        }
        else if(sireName.equals(dam_parentDam_GrandParentDam_GreatSire) || sireName.equals(dam_parentDam_GrandParentSire_GreatSire)
                    || sireName.equals(dam_parentSire_GrandParentDam_GreatSire) || sireName.equals(dam_parentSire_GrandParentSire_GreatSire)
                    || damName.equals(sire_parentDam_GrandParentDam_GreatDam) || damName.equals(sire_parentDam_GrandParentSire_GreatDam)
                    || damName.equals(sire_parentSire_GrandParentDam_GreatDam) || damName.equals(sire_parentSire_GrandParentSire_GreatDam))
        {
            rabbitBreedingMessage = "3rd degree separation great grand parent inbreeding!";
            ancestry = true;
        }
        else if(sire_parentSire_GrandParentSire_GreatSire.equals(damParentSire) || sire_parentSire_GrandParentDam_GreatSire.equals(damParentSire)
                    || sire_parentDam_GrandParentSire_GreatSire.equals(damParentSire) || sire_parentDam_GrandParentDam_GreatSire.equals(damParentSire)
                    || dam_parentSire_GrandParentSire_GreatSire.equals(sireParentSire) || dam_parentSire_GrandParentDam_GreatSire.equals(sireParentSire)
                    || dam_parentDam_GrandParentSire_GreatSire.equals(sireParentSire) || dam_parentDam_GrandParentDam_GreatSire.equals(sireParentSire)
                    || sire_parentSire_GrandParentSire_GreatDam.equals(damParentDam) || sire_parentSire_GrandParentDam_GreatDam.equals(damParentDam)
                    || sire_parentDam_GrandParentSire_GreatDam.equals(damParentDam) || sire_parentDam_GrandParentDam_GreatDam.equals(damParentDam)
                    || dam_parentSire_GrandParentSire_GreatDam.equals(sireParentDam) || dam_parentSire_GrandParentDam_GreatDam.equals(sireParentDam)
                    || dam_parentDam_GrandParentSire_GreatDam.equals(sireParentDam) || dam_parentDam_GrandParentDam_GreatDam.equals(sireParentDam))
        {
            rabbitBreedingMessage = "3rd degree separation great pibling inbreeding!";
            ancestry = true;
        }
        else if(sire_parentSire_GrandParentSire_GreatSire.equals(dam_parentSire_GrandParentSire) || sire_parentSire_GrandParentSire_GreatDam.equals(dam_parentSire_GrandParentDam)
                || sire_parentSire_GrandParentDam_GreatSire.equals(dam_parentSire_GrandParentSire) || sire_parentSire_GrandParentDam_GreatDam.equals(dam_parentSire_GrandParentDam)
                || sire_parentDam_GrandParentSire_GreatSire.equals(dam_parentDam_GrandParentSire) || sire_parentDam_GrandParentSire_GreatDam.equals(dam_parentDam_GrandParentDam)
                || sire_parentDam_GrandParentDam_GreatSire.equals(dam_parentDam_GrandParentSire) || sire_parentDam_GrandParentDam_GreatDam.equals(dam_parentDam_GrandParentDam)
                || sire_parentSire_GrandParentSire_GreatSire.equals(dam_parentDam_GrandParentSire) || sire_parentSire_GrandParentSire_GreatDam.equals(dam_parentDam_GrandParentDam)
                || sire_parentSire_GrandParentDam_GreatSire.equals(dam_parentDam_GrandParentSire) || sire_parentSire_GrandParentDam_GreatDam.equals(dam_parentDam_GrandParentDam)
                || sire_parentDam_GrandParentSire_GreatSire.equals(dam_parentSire_GrandParentSire) || sire_parentDam_GrandParentSire_GreatDam.equals(dam_parentSire_GrandParentDam)
                || sire_parentDam_GrandParentDam_GreatSire.equals(dam_parentSire_GrandParentSire) || sire_parentDam_GrandParentDam_GreatDam.equals(dam_parentSire_GrandParentDam))
        {
            rabbitBreedingMessage = "3rd degree separation great cousin inbreeding!";
            ancestry = true;
        }
        else
        {
            Toast.makeText(this, "No Inbreeding", Toast.LENGTH_SHORT).show();
            ancestry = false;
        }

        return ancestry;
    }

    String rabbitBreedingStatus;
    String rabbitBreedingMessage;
    private void show_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        builder
                .setIcon(R.drawable.status_note_icon)
                .setTitle("Status : " + rabbitBreedingStatus )
                .setMessage(rabbitBreedingMessage)
                .setPositiveButton("Ok", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void show_dialog_okay(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        builder
                .setIcon(R.drawable.status_note_icon_okay)
                .setTitle("Status : " + rabbitBreedingStatus )
                .setMessage(rabbitBreedingMessage)
                .setPositiveButton("Ok", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void entry_pregnant_update(String value){
        if(damName.equals("")){
            System.out.println("Empty");
        }else{
            DocumentReference documentReference = db.document("Rabbit/" +damName);
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                assert entryData != null;
                                entryData.setPregnant(value);
                                documentReference.set(entryData, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private void update_pregnant_status_yes(){
        damName = doeName.getText().toString();
        if(damName.equals("")){
            System.out.println("Empty");
        }else{
            DocumentReference documentReference = db.document("DamBreeders/" +damName);
            documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            DamBreedersData damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                            assert damBreedersData != null;
                            damBreedersData.setDamPregnant("Yes");
                            documentReference.set(damBreedersData, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(RabbitBreedingActivity.this, "Dam pregnant status (Yes) has been updated! (damBreeders)", Toast.LENGTH_SHORT).show();
                                            entry_pregnant_update("Yes");
                                        }
                                    });
                        }
                    }
                });
        }
    }

    private void update_pregnant_status_no(){
        damName = doeName.getText().toString();
        if(damName.equals("")){
            System.out.println("Empty");
        }else{
            DocumentReference documentReference = db.document("DamBreeders/" +damName);
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                DamBreedersData damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                                assert damBreedersData != null;
                                damBreedersData.setDamPregnant("No");
                                documentReference.set(damBreedersData, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(RabbitBreedingActivity.this, "Dam pregnant status (No) has been updated!", Toast.LENGTH_SHORT).show();
                                            entry_pregnant_update("No");
                                        }
                                    });
                            }
                        }
                    });
        }


    }

    private void submit_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        View view = LayoutInflater.from(RabbitBreedingActivity.this).inflate(
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
                if(field_validator()){
                    showErrorDialog();
                }else {
                    data_getter();
                    documentRabbitReference = db.document("Breeding/"+documentName);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        errorTitle = "Error";
                                        errorMessage = "Breeding entry already exist";
                                        showErrorDialog();
                                    }else{
                                        documentRabbitReference.set(rabbitBreedingData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        recentStudDate();
                                                        successDescriptionText = "Rabbit breeding created";
                                                        showSuccessDialog();
                                                    }
                                                });
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
    private void update_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        View view = LayoutInflater.from(RabbitBreedingActivity.this).inflate(
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
                if(field_validator() || size_survived_checker() ||  birth_field_validator()){
                    //Toast.makeText(RabbitBreedingActivity.this, "Field the prerequisite forms!", Toast.LENGTH_SHORT).show();
                }else {
                    data_getter();
                    documentRabbitReference = db.document("Breeding/" +documentName);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        documentRabbitReference.set(rabbitBreedingData, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        if(studResult.getText().toString().equals("Success")){
                                                            update_pregnant_status_yes();
                                                            //Toast.makeText(RabbitBreedingActivity.this, "Stud success update to pregnant", Toast.LENGTH_SHORT).show();
                                                        } else if (studResult.getText().toString().equals("Failed")) {
                                                            Toast.makeText(RabbitBreedingActivity.this, "Stud failed", Toast.LENGTH_SHORT).show();
                                                        }

                                                        if(litterSize.isEnabled() && !litterSizeString.equals("0")){
                                                            //Toast.makeText(RabbitBreedingActivity.this, "Litter size increment", Toast.LENGTH_SHORT).show();
                                                            litter_breeders_incrementer();
                                                        }else if(!litterSizeString.equals("0")){
                                                            calculate_deceased();
                                                            //Toast.makeText(RabbitBreedingActivity.this, "Litter size update", Toast.LENGTH_SHORT).show();
                                                        }

                                                        if(!afterStudDaysWeightString.equals("0")){
                                                            update_dam_weight();
                                                        }
                                                        //reset_function();
                                                        successDescriptionText = "Breeding Information Updated!";
                                                        showSuccessDialog_update();
                                                    }
                                                });
                                    }else{
                                        errorTitle = "Error";
                                        errorMessage = "Breeding information does not exist";
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        View view = LayoutInflater.from(RabbitBreedingActivity.this).inflate(
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
                if(sireName.equals("") || damName.equals("")){
                    Toast.makeText(RabbitBreedingActivity.this, "No Information to delete!", Toast.LENGTH_SHORT).show();
                }else{
                    documentRabbitReference = db.document("Breeding/" +documentName);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        documentRabbitReference.delete();
                                        successDescriptionText = "Breeding Information Deleted!";
                                        showSuccessDialog();
                                        clear_fields();
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

    String errorTitle = "Error", errorMessage = "Fill the necessary form!";
    private void showErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        View view = LayoutInflater.from(RabbitBreedingActivity.this).inflate(
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitBreedingActivity.this);
        View view = LayoutInflater.from(RabbitBreedingActivity.this).inflate(
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
                reset_function();
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



}