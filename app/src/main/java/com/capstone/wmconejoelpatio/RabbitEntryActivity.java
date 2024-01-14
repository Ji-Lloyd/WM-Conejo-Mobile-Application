package com.capstone.wmconejoelpatio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RabbitEntryActivity extends DrawerBaseActivity implements DatePickerDialog.OnDateSetListener{

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView qrImage, entryRabbitImage;
    Bitmap bitmap;
    TextInputLayout pregnantLayout;
    String[] rabbitBreeds, rabbitGender, rabbitStatus, rabbitOrigin, rabbitPregnant;
    ArrayAdapter<String> breedAdapter, genderAdapter, statusAdapter, originAdapter, pregnantAdapter;
    AutoCompleteTextView dropdownBreed, dropdownGender, dropdownStatus, dropdownPregnantStatus, dropdownOrigin, birthDate;
    Button submit, update, clear, viewAll, delete;
    TextInputEditText sire, dam, name, tattoo, weight;
    String rabbitEntrySire, rabbitEntryDam, rabbitEntryName, rabbitEntryBreed, rabbitEntryGender, rabbitEntryOrigin, rabbitEntryStatus, rabbitEntryPregnant, rabbitEntryTattoo, rabbitEntryWeight, rabbitEntryBirthDate;
    String imageLink = "QR_Code_Images/";
    String rabbitImageLink = "Rabbit_Images/";
    RabbitEntryData entryData;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<Void> collectionRabbitReference;

    CollectionReference collectionReference;

    DocumentReference documentRabbitReference;
    StorageReference storageRabbitReference;
    String qr_uri;
    String qr_name = "";
    boolean sireValidator,damValidator,fieldValidator;
    SireBreedersData sireBreedersData;
    DamBreedersData damBreedersData;

    boolean rabbitShow = false;

    SwitchCompat importSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is used for the DrawerBaseActivity java file
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_entry, contentFrameLayout);

        getSupportActionBar().setTitle("Add Rabbit");

        entryData = new RabbitEntryData();

        // ImageView
        qrImage = findViewById(R.id.imageRabbitCode);
        entryRabbitImage = findViewById(R.id.imageRabbit);

        // TextInputLayout
        pregnantLayout = findViewById(R.id.layoutPregnant);

        // Dropdowns
        dropdownBreed = findViewById(R.id.dropDownBreed);
        dropdownOrigin = findViewById(R.id.dropdownOrigin);
        dropdownGender = findViewById(R.id.dropDownGender);
        dropdownPregnantStatus = findViewById(R.id.dropdownPregnant);
        dropdownStatus = findViewById(R.id.dropdownStatus);

        importSwitch = findViewById(R.id.switchImport);

        // TextInputEditTexts
        sire = findViewById(R.id.textEntryRabbitSire);
        dam = findViewById(R.id.textEntryRabbitDam);
        name = findViewById(R.id.textEntryRabbitName);
        tattoo = findViewById(R.id.textEntryRabbitTattoo);
        weight = findViewById(R.id.textEntryRabbitWeight);

        // Buttons
        submit = findViewById(R.id.btnEntrySubmit);
        update = findViewById(R.id.btnEntryUpdate);
        clear = findViewById(R.id.btnEntryClear);
        delete = findViewById(R.id.btnEntryDelete);
        viewAll = findViewById(R.id.btnEntryViewAll);
        birthDate = findViewById(R.id.btnEntryBirthDate);

        loadingDialog = new LoadingDialog(RabbitEntryActivity.this);

        gender_click_select();

        // Getting the rabbit name from another activity
        try {
            Intent getData = getIntent();
            qr_name = getData.getStringExtra("rabbitName");
            qr_load_information();
            sire.setEnabled(false);
            dam.setEnabled(false);
            name.setEnabled(false);
            dropdownBreed.setEnabled(false);
            dropdownOrigin.setEnabled(false);
            dropdownGender.setEnabled(false);
            birthDate.setEnabled(false);
            importSwitch.setVisibility(View.GONE);
            show_update();
            //birthDate.setEnabled(false);
        } catch (Exception exception) {
            System.out.println("Getting Intent Data Exception: " + exception.getMessage());
        }


        // Functions
        try {
            qr_code_provider_name();
            submit_function();
            update_function();
            clear_function();
            delete_function();
            viewAll_function();
            birthDate_function();
            image_rabbit_select();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }


        // Auxiliary
        adapters();
        gender_click();
    }

    private void deceased_status(){
        rabbitEntryStatus = dropdownStatus.getEditableText().toString();
        if(rabbitEntryStatus.equals("Deceased")){
            update.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar_menu_entry,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_toolbar_list:
                startActivity(new Intent(RabbitEntryActivity.this, RabbitEntryListActivity.class));
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
            deceased_status();
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            downloadQrImage();
            retrieveImage();
            entryRabbitImage.setEnabled(false);
            getSupportActionBar().setTitle("Update Rabbit");

        }
    }

    private void retrieveImage() {

        if (qr_name.equals("")) {
            System.out.println("Data is not loaded from QR Scan");
        } else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(rabbitImageLink+qr_name);
            try{
                File localFile = File.createTempFile("tempfile",".jpeg");
                storageReference.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                entryRabbitImage.setImageBitmap(bitmap);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                errorTitle = "Error";
                                errorMessage = e.getMessage();
                                //showErrorDialog();
                                entryRabbitImage.setImageResource(R.drawable.rabbit_icon);
                            }
                        });
            }catch (Exception exception){
                Log.d("TAG", exception.getMessage());
            }
        }

    }


    // String getter method
    private void string_getter() {

        rabbitEntrySire = sire.getEditableText().toString();
        rabbitEntryDam = dam.getEditableText().toString();
        rabbitEntryTattoo = tattoo.getEditableText().toString();
        rabbitEntryWeight = weight.getEditableText().toString();

        rabbitEntryName = name.getEditableText().toString();
        rabbitEntryBreed = dropdownBreed.getEditableText().toString();
        rabbitEntryGender = dropdownGender.getEditableText().toString();
        rabbitEntryOrigin = dropdownOrigin.getEditableText().toString();
        rabbitEntryPregnant = dropdownPregnantStatus.getEditableText().toString();
        rabbitEntryStatus = dropdownStatus.getEditableText().toString();
        rabbitEntryBirthDate = birthDate.getEditableText().toString();

        // If the client chooses male gender this condition will be trigger as the pregnant dropdown will not pop up in the screen
        if (rabbitEntryPregnant.equals("")) {
            rabbitEntryPregnant = "N/A";
        }else if(rabbitEntryGender.equals("Buck")){
            rabbitEntryPregnant = "N/A";
        }


        entryData.setSire(rabbitEntrySire);
        entryData.setDam(rabbitEntryDam);
        entryData.setName(rabbitEntryName);
        entryData.setBreed(rabbitEntryBreed);
        entryData.setOrigin(rabbitEntryOrigin);
        entryData.setGender(rabbitEntryGender);
        entryData.setPregnant(rabbitEntryPregnant);
        entryData.setStatus(rabbitEntryStatus);
        entryData.setTattoo(rabbitEntryTattoo);
        entryData.setBirthDate(rabbitEntryBirthDate);

        if(rabbitEntryWeight.equals("")){
            //Toast.makeText(this, "Fill the necessary form!!", Toast.LENGTH_SHORT).show();
        }else{
            entryData.setWeight(Double.parseDouble(rabbitEntryWeight));
        }

    }

    private void qr_load_information() {
        try{

        }catch (Exception exception){
            System.out.println("--------------------------------------------------");
            System.out.println("Rabbit Retrieving exception: \n" + exception.getMessage());
            System.out.println("--------------------------------------------------");
        }

        if (qr_name.equals("")) {
            System.out.println("Data is not loaded from QR Scan");
        } else {
            string_getter();
            documentRabbitReference = db.document("Rabbit/" + qr_name);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                String rSire = entryData.getSire();
                                String rDam = entryData.getDam();
                                String rName = entryData.getName();
                                String rTattoo = entryData.getTattoo();
                                String breed = entryData.getBreed();
                                String origin = entryData.getOrigin();
                                String gender = entryData.getGender();
                                String pregnant = entryData.getPregnant();
                                String status = entryData.getStatus();
                                double rWeight = entryData.getWeight();
                                String birth = entryData.getBirthDate();

                                sire.setText(rSire);
                                dam.setText(rDam);
                                name.setText(rName);
                                tattoo.setText(rTattoo);
                                weight.setText(String.valueOf(rWeight));
                                dropdownBreed.setText(breed, false);
                                dropdownOrigin.setText(origin, false);
                                dropdownGender.setText(gender, false);
                                dropdownPregnantStatus.setText(pregnant, false);
                                dropdownStatus.setText(status, false);
                                birthDate.setText(birth, false);

                                if (!rTattoo.equals("none")) {
                                    tattoo.setEnabled(false);
                                }

                                if(gender.equals("Doe")){
                                    pregnantLayout.setVisibility(View.VISIBLE);
                                }

                                if(status.equals("Deceased")){
                                    update.setEnabled(false);
                                }

                            }
                        }
                    });
        }
    }

    private boolean sire_validator(){
        rabbitEntrySire = sire.getEditableText().toString();
        Query sireQuery = db.collection("Rabbit")
                .whereEqualTo("name",rabbitEntrySire)
                .whereEqualTo("gender", "Buck");


        sireQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if(!querySnapshot.isEmpty()){
                            sireValidator = true;
                        }else{
                            sireValidator = false;
                        }
                    }
                });

        return sireValidator;
    }

    private boolean dam_validator(){
        rabbitEntryDam = dam.getEditableText().toString();
        Query damQuery = db.collection("Rabbit")
                .whereEqualTo("name",rabbitEntryDam)
                .whereEqualTo("gender", "Doe");


        damQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if(!querySnapshot.isEmpty()){
                            damValidator = true;
                        }else{
                            damValidator = false;
                        }
                    }
                });

        return damValidator;
    }

    private boolean field_validator(){
        string_getter();
        if (rabbitEntryName.trim().equals("") || rabbitEntryBreed.trim().equals("") || rabbitEntryDam.trim().equals("") || rabbitEntryGender.trim().equals("") ||
                rabbitEntryOrigin.trim().equals("") || rabbitEntryPregnant.trim().equals("") || rabbitEntrySire.trim().equals("") || rabbitEntryStatus.trim().equals("") ||
                rabbitEntryTattoo.trim().equals("") || rabbitEntryWeight.trim().equals("") || rabbitEntryBirthDate.trim().equals("")) {
            fieldValidator = true;
        } else {
            fieldValidator = false;
        }
        return fieldValidator;
    }

    private void breeding_info_adder(){
        rabbitEntryGender = dropdownGender.getEditableText().toString();

        rabbitEntryName = name.getEditableText().toString();
        rabbitEntryBreed = dropdownBreed.getEditableText().toString();
        rabbitEntryOrigin = dropdownOrigin.getEditableText().toString();
        rabbitEntryStatus = dropdownStatus.getEditableText().toString();
        rabbitEntryPregnant = dropdownPregnantStatus.getEditableText().toString();
        rabbitEntryBirthDate = birthDate.getEditableText().toString();

        //SireBreedersData sireBreedersData = new SireBreedersData();

        if(rabbitEntryGender.equals("Buck")){
            DocumentReference documentRabbitReference = db.document("SireBreeders/" + rabbitEntryName);
            documentRabbitReference
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                SireBreedersData sireBreedersData = documentSnapshot.toObject(SireBreedersData.class);
                                assert sireBreedersData != null;
                                sireBreedersData.setSireName(rabbitEntryName);
                                sireBreedersData.setSireBreed(rabbitEntryBreed);
                                sireBreedersData.setSireOrigin(rabbitEntryOrigin);
                                sireBreedersData.setSireStatus(rabbitEntryStatus);
                                sireBreedersData.setSireBirthDate(rabbitEntryBirthDate);
                                documentRabbitReference.set(sireBreedersData, SetOptions.merge());
                            }
                            else {
                                SireBreedersData sireBreedersData = new SireBreedersData();
                                sireBreedersData.setSireName(rabbitEntryName);
                                sireBreedersData.setSireBreed(rabbitEntryBreed);
                                sireBreedersData.setSireOrigin(rabbitEntryOrigin);
                                sireBreedersData.setSireStatus(rabbitEntryStatus);
                                sireBreedersData.setSireBirthDate(rabbitEntryBirthDate);
                                documentRabbitReference.set(sireBreedersData, SetOptions.merge());
                            }

                        }
                    });
        } else if (rabbitEntryGender.equals("Doe")) {
            DocumentReference documentRabbitReference = db.document("DamBreeders/" + rabbitEntryName);
            documentRabbitReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            DamBreedersData damBreedersData = documentSnapshot.toObject(DamBreedersData.class);
                            assert damBreedersData != null;
                            damBreedersData.setDamName(rabbitEntryName);
                            damBreedersData.setDamBreed(rabbitEntryBreed);
                            damBreedersData.setDamOrigin(rabbitEntryOrigin);
                            damBreedersData.setDamStatus(rabbitEntryStatus);
                            damBreedersData.setDamPregnant(rabbitEntryPregnant);
                            damBreedersData.setDamBirthDate(rabbitEntryBirthDate);
                            documentRabbitReference.set(damBreedersData, SetOptions.merge());
                        }else{
                            DamBreedersData damBreedersData = new DamBreedersData();
                            damBreedersData.setDamName(rabbitEntryName);
                            damBreedersData.setDamBreed(rabbitEntryBreed);
                            damBreedersData.setDamOrigin(rabbitEntryOrigin);
                            damBreedersData.setDamStatus(rabbitEntryStatus);
                            damBreedersData.setDamPregnant(rabbitEntryPregnant);
                            damBreedersData.setDamBirthDate(rabbitEntryBirthDate);
                            damBreedersData.setRecentStudDate("");
                            documentRabbitReference.set(damBreedersData, SetOptions.merge());
                        }
                    }
                });
        }
    }

    String successDescriptionText = "";
    private void showSuccessDialog(){
        ConstraintLayout successConstraintLayout = findViewById(R.id.successConstraintLayout);
        View viewSuccess = LayoutInflater.from(RabbitEntryActivity.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = viewSuccess.findViewById(R.id.successDone);
        TextView successDescription = viewSuccess.findViewById(R.id.successDescription);
        successDescription.setText(successDescriptionText);

        AlertDialog.Builder  builder =  new AlertDialog.Builder(RabbitEntryActivity.this);
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

    private void showSuccessDialog_qr(){
        ConstraintLayout successConstraintLayout = findViewById(R.id.successConstraintLayout);
        View viewSuccess = LayoutInflater.from(RabbitEntryActivity.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = viewSuccess.findViewById(R.id.successDone);
        TextView successDescription = viewSuccess.findViewById(R.id.successDescription);
        successDescription.setText(successDescriptionText);

        AlertDialog.Builder  builder =  new AlertDialog.Builder(RabbitEntryActivity.this);
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

    private void showSuccessDialog_update_form(){
        ConstraintLayout successConstraintLayout = findViewById(R.id.successConstraintLayout);
        View viewSuccess = LayoutInflater.from(RabbitEntryActivity.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = viewSuccess.findViewById(R.id.successDone);
        TextView successDescription = viewSuccess.findViewById(R.id.successDescription);
        successDescription.setText(successDescriptionText);

        AlertDialog.Builder  builder =  new AlertDialog.Builder(RabbitEntryActivity.this);
        builder.setView(viewSuccess);
        final AlertDialog alertDialog = builder.create();

        successDone.findViewById(R.id.successDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitEntryActivity.this, RabbitEntryActivity.class));
                alertDialog.dismiss();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    // Methods
    private void submit_function() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    submit_show_confirmation_dialog();
                } catch (Exception exception) {
                    Toast.makeText(RabbitEntryActivity.this, "Submit Exception: \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Exception: " + exception.getMessage());
                }
            }
        });
    }

    private void update_setter() {
        string_getter();
        documentRabbitReference = db.document("Rabbit/" + rabbitEntryName);
        documentRabbitReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            RabbitEntryData data = documentSnapshot.toObject(RabbitEntryData.class);
                            assert data != null;
                            String url = data.getQrCode();
                            String imageUrl = data.getPhoto();
                            entryData.setQrCode(url);
                            entryData.setPhoto(imageUrl);
                            documentRabbitReference.set(entryData, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            breeding_info_adder();
                                            successDescriptionText = "Rabbit Information Updated!";
                                            showSuccessDialog_update_form();
                                            //clear_data();

                                        }
                                    });
                        } else {
                            errorTitle = "Error";
                            errorMessage = "There is no existing rabbit record to update!";
                            showErrorDialog();
                        }
                    }
                });
    }

    private void update_function() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_show_confirmation_dialog();
            }
        });
    }

    private void clear_data(){
        sire.setText("");
        dam.setText("");
        name.setText("");
        tattoo.setText("");
        weight.setText("");

        dropdownBreed.setText(null);
        dropdownGender.setText(null);
        dropdownStatus.setText(null);
        dropdownOrigin.setText(null);
        dropdownPregnantStatus.setText(null);
        birthDate.setText(null);

        dropdownBreed.setFocusable(false);
        dropdownGender.setFocusable(false);
        dropdownStatus.setFocusable(false);
        dropdownOrigin.setFocusable(false);
        dropdownPregnantStatus.setFocusable(false);
        birthDate.setFocusable(false);

        pregnantLayout.setVisibility(View.GONE);

        qrImage.setImageBitmap(null);
        entryRabbitImage.setImageResource(R.drawable.insert_image);

        name.setEnabled(true);
        tattoo.setEnabled(true);
        sire.setEnabled(true);
        dam.setEnabled(true);
        dropdownBreed.setEnabled(true);
        dropdownOrigin.setEnabled(true);
        dropdownGender.setEnabled(true);
        birthDate.setEnabled(true);
        importSwitch.setChecked(false);
        importSwitch.setVisibility(View.VISIBLE);
    }

    private void clear_function() {
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_show_confirmation_dialog();
            }
        });
    }


    private void cascade_delete_feeding_health(){
        Query query = db.collection("FeedingAndNutrition")
                .whereEqualTo("name",rabbitEntryName);


        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                           {
                               collectionRabbitReference = db.collection("FeedingAndNutrition").document(documentSnapshot.getId()).delete();
                           }
                       }
                    }
                });

        Query queryHealth = db.collection("HealthAdministration")
                .whereEqualTo("name",rabbitEntryName);


        queryHealth.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                            {
                                collectionRabbitReference = db.collection("HealthAdministration").document(documentSnapshot.getId()).delete();
                            }
                        }
                    }
                });
    }

    private void cascade_delete_breeding(){
        documentRabbitReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            documentRabbitReference.delete();
                            Toast.makeText(RabbitEntryActivity.this, "Deletion cascaded!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                        System.err.println("Error: " + e.getMessage());
                    }
                });
    }

    private void cascade_delete(){
        rabbitEntryName = name.getEditableText().toString();
        rabbitEntryGender = dropdownGender.getEditableText().toString();

        if(rabbitEntryGender.equals("Buck")){
            documentRabbitReference = db.document("SireBreeders/" + rabbitEntryName);
            cascade_delete_breeding();

        } else if (rabbitEntryGender.equals("Doe")) {
            documentRabbitReference = db.document("DamBreeders/" + rabbitEntryName);
            cascade_delete_breeding();
        }

        cascade_delete_feeding_health();
    }

    private void delete_function() {
        try {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete_show_confirmation_dialog();
                }
            });
        } catch (Exception exception) {
            Toast.makeText(RabbitEntryActivity.this, "Submit Exception: \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("Exception: " + exception.getMessage());
        }
    }

    private void viewAll_function() {
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitEntryActivity.this, RabbitEntryListActivity.class));
            }
        });
    }

    // Private method that will encode the given string value to qr code into the bitmap
    private void qr_generator() {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(name.getEditableText().toString(), BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException writerException) {
            throw new RuntimeException(writerException);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    String currentDate;
    private void current_date(){
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = dateFormat.format(calendar);
    }

    private void data_setter() {
        current_date();
        entryData.setEntryDate(currentDate);
        documentRabbitReference.set(entryData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        rabbit_image_converter();
                        breeding_info_adder();
                        successDescriptionText = "Rabbit entry created!";
                        showSuccessDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorTitle = "Error";
                        errorMessage = e.getMessage();
                        showErrorDialog();
                    }
                });
    }

    private void qr_image_converter() {
        string_getter();
        storageRabbitReference = FirebaseStorage.getInstance().getReference(imageLink + rabbitEntryName);

        // Converting bitmap to byte[]
        qrImage.setDrawingCacheEnabled(true);
        qrImage.buildDrawingCache();
        Bitmap bitmap = qrImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Uploading the byte[] to firebase storage
        storageRabbitReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRabbitReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Binding the uploaded qr code url to string variable
                                qr_uri = uri.toString();
                                System.out.println(qr_uri);

                                // Will trigger if any of the field is left empty
                                if (field_validator()) {
                                    showErrorDialog();
                                } else {
                                    // ID == Rabbit Name
                                    documentRabbitReference = db.document("Rabbit/" + rabbitEntryName);
                                    documentRabbitReference.get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    entryData.setQrCode(qr_uri);
                                                    if (documentSnapshot.exists()) {
                                                        errorTitle = "Error";
                                                        errorMessage = "Rabbit Already Exist!";
                                                        showErrorDialog();
                                                    } else {
                                                        if (rabbitEntryTattoo.equals("none".toLowerCase())) {
                                                            if (pregnantLayout.getVisibility() == View.VISIBLE) {
                                                                if (rabbitEntryPregnant.trim().equals("N/A")) {
                                                                    showErrorDialog();
                                                                } else {
                                                                    data_setter();
                                                                }
                                                            } else {
                                                                data_setter();
                                                            }
                                                        } else {
                                                            CollectionReference tattooChecker = db.collection("Rabbit");
                                                            tattooChecker
                                                                    .whereEqualTo("tattoo", rabbitEntryTattoo)
                                                                    .get()
                                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot querySnapshot) {
                                                                            if (querySnapshot.isEmpty()) {
                                                                                if (pregnantLayout.getVisibility() == View.VISIBLE) {
                                                                                    if (rabbitEntryPregnant.trim().equals("N/A")) {
                                                                                        showErrorDialog();
                                                                                    } else {
                                                                                        data_setter();
                                                                                    }
                                                                                } else {
                                                                                    data_setter();
                                                                                }
                                                                            } else {
                                                                                errorTitle = "Error";
                                                                                errorMessage = "Ear tattoo already exist!";
                                                                                showErrorDialog();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                });


    }

    private void qr_code_provider_name() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //qr_generator();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                qr_generator();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void birthDate_function(){
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date picker");
            }
        });
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar c = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        String dateOfBirth = sdf.format(c.getTime());
        birthDate.setText(dateOfBirth);


    }

    // Method that encapsulates all adapter methods
    private void adapters() {
        dropdown_breed_adapter();
        dropdown_gender_adapter();
        dropdown_status_adapter();
        dropdown_origin_adapter();
        dropdown_pregnant_adapter();

        dropdownBreed.setAdapter(breedAdapter);
        dropdownGender.setAdapter(genderAdapter);
        dropdownStatus.setAdapter(statusAdapter);
        dropdownOrigin.setAdapter(originAdapter);
        dropdownPregnantStatus.setAdapter(pregnantAdapter);

    }

    private void gender_click() {
        dropdownGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String click = adapterView.getItemAtPosition(i).toString();
                if (click.equals("Doe")) {
                    pregnantLayout.setVisibility(View.VISIBLE);
                } else if (click.equals("Buck")) {
                    pregnantLayout.setVisibility(View.GONE);
                    rabbitEntryPregnant = "N/A";
                }
            }
        });
    }

    private void gender_click_select(){
        rabbitEntryGender = dropdownGender.getEditableText().toString();
        if(rabbitEntryGender.equals("Doe")){
            pregnantLayout.setVisibility(View.VISIBLE);
        }
    }


    // Adapter Methods
    private void dropdown_breed_adapter() {
        rabbitBreeds = new String[]{"New Zealand", "Californian", "Flemish Giant", "PS Line", "Hyla Optima", "Hyla Plus", "German Giant", "Holland Lop"};
        breedAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitBreeds);
    }

    private void dropdown_origin_adapter() {
        rabbitOrigin = new String[]{"Island Born", "Indonesia","WM Conejo"};
        originAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitOrigin);
    }

    private void dropdown_gender_adapter() {
        rabbitGender = new String[]{"Buck", "Doe"};
        genderAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitGender);
    }

    private void dropdown_status_adapter() {
        rabbitStatus = new String[]{"Healthy", "Ill", "Deceased"};
        statusAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitStatus);
    }

    private void dropdown_pregnant_adapter() {
        rabbitPregnant = new String[]{"Yes", "No"};
        pregnantAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, rabbitPregnant);
    }

    private void update_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitEntryActivity.this);
        View view = LayoutInflater.from(RabbitEntryActivity.this).inflate(
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
                //alertDialog.dismiss();
                string_getter();
                // Will trigger if any of the field is left empty
                if (field_validator()) {
                    showErrorDialog();
                } else {
                    // ID == Rabbit Name
                    if (rabbitEntryTattoo.equals("none".toLowerCase())) {
                        if (pregnantLayout.getVisibility() == View.VISIBLE) {
                            if (rabbitEntryPregnant.trim().equals("N/A")) {
                                showErrorDialog();
                            } else {
                                update_setter();
                            }
                        } else {
                            update_setter();
                        }
                    } else {
                        CollectionReference tattooChecker = db.collection("Rabbit");
                        tattooChecker
                                .whereEqualTo("tattoo", rabbitEntryTattoo)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        if (!querySnapshot.isEmpty()) {
                                            if (pregnantLayout.getVisibility() == View.VISIBLE) {
                                                if (rabbitEntryPregnant.trim().equals("N/A")) {
                                                    showErrorDialog();
                                                } else {
                                                    update_setter();
                                                }
                                            } else {
                                                update_setter();
                                            }
                                        } else {
                                            errorTitle = "Error";
                                            errorMessage = "Ear tattoo already exist!";
                                            showErrorDialog();
                                        }
                                    }
                                });
                    }

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

    LoadingDialog loadingDialog;
    private void submit_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitEntryActivity.this);
        View view = LayoutInflater.from(RabbitEntryActivity.this).inflate(
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
                for (int count = 0 ; count < 2; count++){
                    sire_validator();
                    dam_validator();
                }
                loadingDialog.startLoadingDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(importSwitch.isChecked()) {
                            qr_image_converter();
                        }else if(!importSwitch.isChecked()){
                            if (sire_validator() && dam_validator()) {
                                qr_image_converter();
                            } else {
                                errorTitle = "Error";
                                errorMessage = "Parent rabbits does not exist within database!";
                                showErrorDialog();
                            }
                        }
                        loadingDialog.dismissDialog();
                    }
                },3000);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitEntryActivity.this);
        View view = LayoutInflater.from(RabbitEntryActivity.this).inflate(
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
                rabbitEntryName = name.getEditableText().toString();
                if(rabbitEntryName.equals("".trim())){
                    errorTitle = "Error";
                    errorMessage = "No rabbit input to delete!";
                    showErrorDialog();
                }else{
                    string_getter();
                    documentRabbitReference = db.document("Rabbit/" + rabbitEntryName);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        string_getter();
                                        storageRabbitReference = FirebaseStorage.getInstance().getReference(imageLink + rabbitEntryName);

                                        storageRabbitReference.delete();
                                        documentRabbitReference.delete();
                                        cascade_delete();
                                        //Toast.makeText(RabbitEntryActivity.this, "Rabbit Entry Deleted!", Toast.LENGTH_SHORT).show();
                                        successDescriptionText = "Rabbit Entry Deleted!";
                                        showSuccessDialog_update_form();
                                        clear_data();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    errorTitle = "Error";
                                    errorMessage = e.getMessage();
                                    showErrorDialog();
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

    private void clear_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitEntryActivity.this);
        View view = LayoutInflater.from(RabbitEntryActivity.this).inflate(
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

    private void template_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitEntryActivity.this);
        View view = LayoutInflater.from(RabbitEntryActivity.this).inflate(
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RabbitEntryActivity.this);
        View view = LayoutInflater.from(RabbitEntryActivity.this).inflate(
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

    private void downloadQrImage(){

        qrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RabbitEntryActivity.this);
                View view1 = LayoutInflater.from(RabbitEntryActivity.this).inflate(
                        R.layout.confirmation_dialog,
                        (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
                );
                builder.setView(view1);
                ((TextView) view1.findViewById(R.id.textTitle)).setText("Download QR Code");
                ((TextView) view1.findViewById(R.id.textMessage)).setText("Are you sure?");
                ((Button) view1.findViewById(R.id.buttonYes)).setText("Yes");
                ((Button) view1.findViewById(R.id.buttonNo)).setText("Cancel");
                ((ImageView) view1.findViewById(R.id.imageIcon)).setImageResource(R.drawable.download_icon);

                final AlertDialog alertDialog = builder.create();

                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {

                            BitmapDrawable draw = (BitmapDrawable) qrImage.getDrawable();
                            Bitmap bitmap = draw.getBitmap();

                            FileOutputStream outStream = null;
                            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            File dir = new File(file.getAbsolutePath() + "/Rabbit QR");
                            dir.mkdirs();

                            rabbitEntryName = name.getText().toString();
                            @SuppressLint("DefaultLocale")
                            //String fileName = String.format("%d.png", rabbitEntryName);
                            File outFile = new File(dir, rabbitEntryName+".png");
                            outStream = new FileOutputStream(outFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                            outStream.flush();
                            outStream.close();
                            successDescriptionText = "Rabbit QR Code downloaded!";
                            showSuccessDialog_qr();
                        }catch (Exception exception){
                            Log.d("TAG",""+exception.getMessage());
                        }

                        alertDialog.dismiss();
                    }
                });

                view1.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
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
        });

    }

    private void image_rabbit_select(){
        entryRabbitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(RabbitEntryActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    Uri rabbitImageUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        rabbitImageUri = data.getData();
        entryRabbitImage.setImageURI(rabbitImageUri);
    }

    String stringRabbitImageUri;
    private void rabbit_image_converter() {
        string_getter();
        StorageReference storageRabbitImageReference = FirebaseStorage.getInstance().getReference(rabbitImageLink + rabbitEntryName);

        // Converting bitmap to byte[]
        entryRabbitImage.setDrawingCacheEnabled(true);
        entryRabbitImage.buildDrawingCache();
        Bitmap bitmap = entryRabbitImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Uploading the byte[] to firebase storage
        storageRabbitImageReference.putBytes(data)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRabbitImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            stringRabbitImageUri = uri.toString();
                            System.out.println(stringRabbitImageUri);
                            documentRabbitReference = db.document("Rabbit/" + rabbitEntryName);
                            documentRabbitReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        RabbitEntryData data = documentSnapshot.toObject(RabbitEntryData.class);
                                        String qr = data.getQrCode();
                                        System.out.println(qr);
                                        entryData.setQrCode(qr);
                                        entryData.setPhoto(stringRabbitImageUri);
                                        documentRabbitReference.set(entryData, SetOptions.merge());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        errorTitle = "Error";
                                        errorMessage = e.getMessage();
                                        showErrorDialog();
                                    }
                                });
                        }
                    });

                }


            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    errorTitle = "Error";
                    errorMessage = e.getMessage();
                    //showErrorDialog();
                }
            });
    }
}