package com.capstone.wmconejoelpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.material.textfield.TextInputLayout;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FeedingNutritionActivity extends DrawerBaseActivity implements TimePickerDialog.OnTimeSetListener {

    String[] feedSelection,feedType,feedQuantity,feedSchedule;
    ArrayAdapter<String> feedSelectAdapter,feedTypeAdapter,feedQuantityAdapter,feedScheduleAdapter;
    AutoCompleteTextView dropdownFeedSelect,dropdownFeedType,dropdownFeedQuantity,dropdownFeedSchedule;
    TextInputLayout feedSelectLayout;
    TextInputEditText rabbitFeedName,textCurrentWeight,textGoalWeight,textRabbitID;
    Button btnDatePicker,submit,update,clear,delete,viewAll,generate,validate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRabbitReference;
    DocumentReference documentRabbitReference;
    RabbitFeedingData feedingData;
    String currentDate,feedTime;
    String category,name,feedingType,feedingQuantity,feedingSchedule;
    String feedingID,currentWeight,goalWeight;
    int id_counter = 0;
    String loadedID;
    String feedingCollectionTitle = "FeedingAndNutrition/";
    Calendar calendar;

    TextInputLayout layoutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_feeding_nutrition, contentFrameLayout);

        getSupportActionBar().setTitle("Add Nutrition");

        layoutName = findViewById(R.id.layoutName);

        rabbitFeedName = findViewById(R.id.textFeedRabbitName);
        textRabbitID = findViewById(R.id.textFeedRabbitID);
        textCurrentWeight = findViewById(R.id.textFeedRabbitCurrentWeight);
        textGoalWeight = findViewById(R.id.textFeedRabbitGoalWeight);

        dropdownFeedSelect = findViewById(R.id.dropDownCategory);
        dropdownFeedType = findViewById(R.id.dropDownFeedType);
        dropdownFeedQuantity = findViewById(R.id.dropDownFeedQuantity);
        dropdownFeedSchedule = findViewById(R.id.dropdownFeedSchedule);

        btnDatePicker = findViewById(R.id.btnFeedDatePicker);
        submit = findViewById(R.id.btnFeedSubmit);
        update = findViewById(R.id.btnFeedUpdate);
        clear = findViewById(R.id.btnFeedClear);
        delete = findViewById(R.id.btnFeedDelete);
        viewAll = findViewById(R.id.btnFeedViewAll);
        validate = findViewById(R.id.btnFeedRabbitValidate);
        generate = findViewById(R.id.btnFeedRabbitGenerateID);

        feedingData = new RabbitFeedingData();

        loadingDialog = new LoadingDialog(FeedingNutritionActivity.this);

        show_update();

        try {
            Intent getData = getIntent();
            rabbitFeedName.setText(getData.getStringExtra("rabbitName"));
            String name = rabbitFeedName.getEditableText().toString();
            if(getData.getStringExtra("rabbitName").equals(name)){
                documentRabbitReference = db.document("Rabbit/" + getData.getStringExtra("rabbitName"));
                documentRabbitReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                    assert entryData != null;
                                    String feedFor = entryData.getGender();
                                    dropdownFeedSelect.setText(feedFor,false);
                                    rabbitFeedName.setEnabled(false);
                                    dropdownFeedSelect.setEnabled(false);
                                }
                            }
                        });
            }else{
                rabbitFeedName.setEnabled(true);
            }
        } catch (Exception exception) {
            System.out.println("Getting Intent Data Exception: " + exception.getMessage());
        }

        try {
            Intent getData = getIntent();
            loadedID = getData.getStringExtra("rabbitFeedingID");
            recycler_loaded_data();
            textRabbitID.setEnabled(false);
            rabbitFeedName.setEnabled(false);
            generate.setEnabled(false);
            textCurrentWeight.setEnabled(false);
            dropdownFeedSelect.setEnabled(false);
            dropdownFeedType.setEnabled(false);
            dropdownFeedSchedule.setEnabled(false);
            btnDatePicker.setEnabled(false);
        }catch (Exception exception){
            Log.d("Error : " , exception.getMessage());
        }

        try{
            submit_function();
            update_function();
            delete_function();
            clear_field_function();
            view_feed_history_function();
            validate_function();
        }catch (Exception exception){
            Toast.makeText(this, "Function Exception: \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try{
            adapters();
            feed_quantity_click();
            custom_feed_schedule();
        }catch (Exception exception){
            Toast.makeText(this, "Error : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("Error: \n" + exception.getMessage());
        }

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                document_counter();
            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");

            }
        });

    }

    String successDescriptionText = "";
    @SuppressLint("SetTextI18n")
    private void showSuccessDialog(){
        ConstraintLayout successConstraintLayout = findViewById(R.id.successConstraintLayout);
        View viewSuccess = LayoutInflater.from(FeedingNutritionActivity.this).inflate(R.layout.success_dialog, successConstraintLayout);
        Button successDone = viewSuccess.findViewById(R.id.successDone);
        TextView successDescription = viewSuccess.findViewById(R.id.successDescription);
        successDescription.setText(successDescriptionText);

        AlertDialog.Builder  builder =  new AlertDialog.Builder(FeedingNutritionActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar_menu_nutrition,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_toolbar_retrieve_nutrition_document:
                try{
                    name = rabbitFeedName.getEditableText().toString();
                    if(name.trim().equals("")){
                        errorTitle = "Error";
                        errorMessage = "Fill the rabbit name!";
                        showErrorDialog();
                    }else{
                        rabbit_info();
                    }
                }catch (Exception error){
                    Log.d("TAG", error.getMessage());
                }
                break;
            case R.id.nav_toolbar_list:
                startActivity(new Intent(FeedingNutritionActivity.this, RabbitFeedingHistory.class));
                break;
            case R.id.nav_toolbar_advice:
                name = rabbitFeedName.getEditableText().toString();
                currentWeight = textCurrentWeight.getEditableText().toString();
                goalWeight = textGoalWeight.getEditableText().toString();
                feedingQuantity = dropdownFeedQuantity.getEditableText().toString();
                if(name.equals("") || currentWeight.equals("") || goalWeight.equals("") || feedingQuantity.equals("")){
                    showErrorDialog();
                }else{
                    nutrition_advisor();
                }
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
            getSupportActionBar().setTitle("Update Nutrition");
        }
    }

    private void recycler_loaded_data(){
        if (loadedID.equals("")) {
            System.out.println("Data is not loaded");
        } else {
            documentRabbitReference = db.document(feedingCollectionTitle +loadedID);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            feedingData = documentSnapshot.toObject(RabbitFeedingData.class);
                            assert feedingData != null;
                            String id = String.valueOf(feedingData.getId());
                            String feedFor = feedingData.getFeedFor();
                            String name = feedingData.getName();
                            String cWeight = String.valueOf(feedingData.getCurrentWeight());
                            String gWeight = String.valueOf(feedingData.getGoalWeight());
                            String feedingType = feedingData.getFeedType();
                            String feedingQuantity = feedingData.getFeedQuantity();
                            String sched = feedingData.getFeedSchedule();
                            String schedTime = feedingData.getFeedTime();
                            String schedDate = feedingData.getFeedDate();

                            textRabbitID.setText(id);
                            dropdownFeedSelect.setText(feedFor,false);
                            rabbitFeedName.setText(name);
                            textCurrentWeight.setText(cWeight);
                            textGoalWeight.setText(gWeight);
                            dropdownFeedType.setText(feedingType,false);
                            dropdownFeedQuantity.setText(feedingQuantity,false);
                            dropdownFeedSchedule.setText(sched,false);
                            btnDatePicker.setText(schedTime);
                            feedTime = schedTime;
                            currentDate = schedDate;



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Data retrieval error: \n", e.getMessage());
                        }
                    });
        }
    }

    private void document_counter(){

        Query query = db.collection("FeedingAndNutrition")
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

        /*
        collectionRabbitReference = db.collection(feedingCollectionTitle);
        collectionRabbitReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            id_counter = 100 + task.getResult().size() + 5;
                            textRabbitID.setText(String.valueOf(id_counter));
                        }else {
                            System.out.println("Error : " + task.getException());
                            Toast.makeText(FeedingNutritionActivity.this, "ID unsuccessfully generated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

         */
    }
    private void data_getter(){

        try {
            feedingID = textRabbitID.getEditableText().toString();
            name = rabbitFeedName.getEditableText().toString();
            currentWeight = textCurrentWeight.getEditableText().toString();
            goalWeight = textGoalWeight.getEditableText().toString();

            category = dropdownFeedSelect.getEditableText().toString();
            feedingType = dropdownFeedType.getEditableText().toString();
            feedingQuantity = dropdownFeedQuantity.getEditableText().toString();
            feedingSchedule = dropdownFeedSchedule.getEditableText().toString();

            //current_date();

            feedingData.setFeedFor(category);
            feedingData.setName(name);
            if (currentWeight.equals("") || goalWeight.equals("") || feedingID.equals("")) {
                showErrorDialog();
            } else {
                id_counter = Integer.parseInt(textRabbitID.getEditableText().toString());
                feedingData.setId(id_counter);
                feedingData.setCurrentWeight(Double.parseDouble(currentWeight));
                feedingData.setGoalWeight(Double.parseDouble(goalWeight));
            }
            feedingData.setFeedType(feedingType);
            feedingData.setFeedQuantity(feedingQuantity);
            feedingData.setFeedSchedule("Now");

            feedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        }
        catch (Exception ex){

        }
        //feedingData.setFeedDate(currentDate);
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

    @SuppressLint("SetTextI18n")
    private void clear_data(){
        textRabbitID.setText("");
        rabbitFeedName.setText("");
        textCurrentWeight.setText("");
        textGoalWeight.setText("");

        dropdownFeedSelect.setText(null);
        dropdownFeedSchedule.setText(null);
        dropdownFeedQuantity.setText(null);
        dropdownFeedType.setText(null);

        dropdownFeedSelect.setFocusable(false);
        dropdownFeedSchedule.setFocusable(false);
        dropdownFeedQuantity.setFocusable(false);
        dropdownFeedType.setFocusable(false);

        btnDatePicker.setText("Schedule");

        generate.setEnabled(true);
        rabbitFeedName.setEnabled(true);
        textCurrentWeight.setEnabled(true);
        dropdownFeedSelect.setEnabled(true);
        dropdownFeedType.setEnabled(true);
        dropdownFeedSchedule.setEnabled(true);

        feedTime = "";
    }
    private void clear_field_function(){
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

    private void view_feed_history_function(){
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedingNutritionActivity.this, RabbitFeedingHistory.class));
            }
        });
    }

    private void adapters(){

        dropdown_feed_adapter();
        dropdown_feed_type_adapter();
        dropdown_feed_quantity_adapter();
        dropdown_feed_schedule_adapter();


        dropdownFeedSelect.setAdapter(feedSelectAdapter);
        dropdownFeedType.setAdapter(feedTypeAdapter);
        dropdownFeedQuantity.setAdapter(feedQuantityAdapter);
        dropdownFeedSchedule.setAdapter(feedScheduleAdapter);
    }
    private void dropdown_feed_adapter(){
        feedSelection = new String[] {"Buck","Doe"};
        feedSelectAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, feedSelection);
    }

    private void dropdown_feed_quantity_adapter(){
        feedQuantity = new String[] {"50","100","150","200","250","Custom"};
        feedQuantityAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, feedQuantity);
    }

    private void dropdown_feed_type_adapter(){
        feedType = new String[] {"Mommi (Lactating Pellets)","Premium (Optimax Growth)","Fibrellet (Maintenance)"};
        feedTypeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout, feedType);
    }

    private void dropdown_feed_schedule_adapter(){
        feedSchedule = new String[] {"Now"};
        feedScheduleAdapter = new ArrayAdapter<>(this, R.layout.dropdown_layout,feedSchedule);
    }

    private void feed_quantity_click(){
        dropdownFeedQuantity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String click = adapterView.getItemAtPosition(i).toString();
                if(click.equals("Custom")){
                    dropdownFeedQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                    dropdownFeedQuantity.setText("0",false);
                }
            }
        });
    }

    private void current_date(){
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = dateFormat.format(calendar);
    }

    private void custom_feed_schedule(){
        dropdownFeedSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String click = adapterView.getItemAtPosition(i).toString();
                if(click.equals("Later")){
                    btnDatePicker.setEnabled(true);
                    btnDatePicker.setText("Pick Time");
                } else if (click.equals("Now")) {
                    btnDatePicker.setEnabled(false);
                    feedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                    btnDatePicker.setText(feedTime);
                }
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        current_date();

        /*
        String am_pm = "";
        int hour = 0;

        if(hourOfDay > 12){
            hour = hourOfDay - 12;
            am_pm = " PM";
        } else if (hourOfDay == 0) {
            hour = 12;
            am_pm = " AM";
        } else if (hourOfDay == 12) {
            hour = 12;
            am_pm = " PM";
        } else if(hourOfDay < 12){
            hour = hourOfDay;
            am_pm = " AM";
        }


        feedTime = hour + ":" + minute + am_pm;
        btnDatePicker.setText(feedTime);
        System.out.println("Later : " + feedTime);

         */
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND,0);
        String sched = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        feedTime = sched;
        btnDatePicker.setText(sched);



    }

    private void startAlarm(Calendar c){

        try{
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, FeedingAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent,0);
            if(c.before(Calendar.getInstance())){
                c.add(Calendar.DATE,1);
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pendingIntent);
        }catch (Exception exception){
            System.err.println("Starting Alarm Exception: " + exception.getMessage());
            Log.d("TAG", exception.getMessage());
        }
    }

    String rabbitStatus,pregnant;
    double weight;
    private void validate_function(){
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = rabbitFeedName.getEditableText().toString();
                if(name.equals("")){
                    Toast.makeText(FeedingNutritionActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                }else{
                    documentRabbitReference = db.document("Rabbit/" + name);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                        assert entryData != null;
                                        String status = entryData.getStatus();
                                        if(status.equals("Deceased")){
                                            errorTitle = "Error";
                                            errorMessage = "Rabbit is already dead!";
                                            showErrorDialog();
                                        }else{
                                            weight = entryData.getWeight();
                                            String feedForType = entryData.getGender();
                                            pregnant = entryData.getPregnant();

                                            textCurrentWeight.setText(String.valueOf(weight));
                                            rabbitStatus = status;
                                            dropdownFeedSelect.setText(feedForType);
                                            textCurrentWeight.setEnabled(false);
                                            dropdownFeedSelect.setEnabled(false);
                                            //Toast.makeText(FeedingNutritionActivity.this, "", Toast.LENGTH_SHORT).show();
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
        });
    }

    private void rabbit_info(){
        name = rabbitFeedName.getEditableText().toString();
        if(name.equals("")){
            //Toast.makeText(FeedingNutritionActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
        }else{
            documentRabbitReference = db.document("Rabbit/" + name);
            documentRabbitReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                RabbitEntryData entryData = documentSnapshot.toObject(RabbitEntryData.class);
                                String status = entryData.getStatus();
                                if(status.equals("Deceased")){
                                    errorTitle = "Error";
                                    errorMessage = "Rabbit is already dead!";
                                    showErrorDialog();
                                }
                                else
                                {
                                    weight = entryData.getWeight();
                                    String feedForType = entryData.getGender();
                                    pregnant = entryData.getPregnant();

                                    textCurrentWeight.setText(String.valueOf(weight));
                                    rabbitStatus = status;
                                    dropdownFeedSelect.setText(feedForType);
                                    document_counter();
                                    textCurrentWeight.setEnabled(false);
                                    dropdownFeedSelect.setEnabled(false);
                                }

                                //status_advisor();
                            }else {
                                errorTitle = "Error";
                                errorMessage = "Rabbit does not exist!";
                                showErrorDialog();
                            }
                        }
                    });
        }
    }

    String statusMessage;
    private void status_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedingNutritionActivity.this);
        builder
                .setIcon(R.drawable.nutrition_advice_icon)
                .setTitle("Rabbit Status : " + rabbitStatus )
                .setMessage(statusMessage)
                .setPositiveButton("Ok", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void submit_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedingNutritionActivity.this);
        View view = LayoutInflater.from(FeedingNutritionActivity.this).inflate(
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
                data_getter();
                if(name.equals("") || currentWeight.equals("") || goalWeight.equals("") || category.equals("") || feedingType.equals("") ||
                        feedingQuantity.equals("")){
                    showErrorDialog();
                }else{
                    DocumentReference rabbitExistChecker = db.document("Rabbit/"+name);
                    rabbitExistChecker.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        document_counter();
                                        documentRabbitReference = db.document(feedingCollectionTitle+id_counter);
                                        documentRabbitReference.get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if(documentSnapshot.exists()){
                                                            errorTitle = "Error";
                                                            errorMessage = "Data already exist!";
                                                            showErrorDialog();
                                                        }else{
                                                            current_date();
                                                            feedingData.setFeedTime(feedTime);
                                                            feedingData.setFeedDate(currentDate);
                                                            documentRabbitReference.set(feedingData)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            successDescriptionText = "Rabbit nutrition created!";
                                                                            showSuccessDialog();
                                                                            startAlarm(calendar);
                                                                            clear_data();
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
                                                    }
                                                });
                                    }

                                    else{
                                        errorTitle = "Error";
                                        errorMessage = "Rabbit does not exist to feed!";
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
    private void update_show_confirmation_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedingNutritionActivity.this);
        View view = LayoutInflater.from(FeedingNutritionActivity.this).inflate(
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
                if(name.equals("") || currentWeight.equals("") || goalWeight.equals("") || category.equals("") || feedingType.equals("") ||
                        feedingQuantity.equals("") || feedingSchedule.equals("") || feedTime.equals("")){
                    showErrorDialog();
                }else{
                    documentRabbitReference = db.document(feedingCollectionTitle+feedingID);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        documentRabbitReference.set(feedingData, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        successDescriptionText = "Nutrition Information  Updated!";
                                                        showSuccessDialog();
                                                        clear_data();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedingNutritionActivity.this);
        View view = LayoutInflater.from(FeedingNutritionActivity.this).inflate(
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
                feedingID = textRabbitID.getEditableText().toString();
                if(feedingID.equals("")){
                    Toast.makeText(FeedingNutritionActivity.this, "There is no ID input to delete", Toast.LENGTH_SHORT).show();
                }else{
                    documentRabbitReference = db.document(feedingCollectionTitle+feedingID);
                    documentRabbitReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        documentRabbitReference.delete();
                                        clear_data();
                                        successDescriptionText = "Nutrition Information  Deleted!";
                                        showSuccessDialog();
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
                                    //Toast.makeText(FeedingNutritionActivity.this, "Deletion error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedingNutritionActivity.this);
        View view = LayoutInflater.from(FeedingNutritionActivity.this).inflate(
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedingNutritionActivity.this);
        View view = LayoutInflater.from(FeedingNutritionActivity.this).inflate(
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


    private String stringURLEndPoint = "https://api.openai.com/v1/chat/completions";
    private String stringAPIKey = "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";
    private String stringOutput = "";

    public String rabbitNutritionAdvice;
    LoadingDialog loadingDialog;
    public void nutrition_advisor(){

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

            goalWeight = textGoalWeight.getEditableText().toString();
            feedingQuantity = dropdownFeedQuantity.getEditableText().toString();
            if(pregnant.equals("Yes")){
                rabbitNutritionAdvice = String.format("In three sentences what can be nutrition advice for pregnant rabbit that has current weight of %skg and has a goal weight of %skg that will intake %s grams of pellet feed?",weight,goalWeight,feedingQuantity);
                System.out.println(String.format("In three sentences what can be nutrition advice for pregnant rabbit that has current weight of %skg and has a goal weight of %skg?",weight,goalWeight));
            }else if(rabbitStatus.equals("Healthy")){
                rabbitNutritionAdvice = String.format("In three sentences what can be nutrition advice for healthy rabbit that has current weight of %skg and has a goal weight of %skg that will intake %s grams of pellet feed?",weight,goalWeight,feedingQuantity);
                System.out.println(String.format("In three sentences what can be nutrition advice for healthy rabbit that has current weight of %skg and has a goal weight of %skg that will intake %s grams of pellet feed?",weight,goalWeight,feedingQuantity));
            }else if(rabbitStatus.equals("Ill")){
                rabbitNutritionAdvice = String.format("In three sentences what can be nutrition advice for Ill rabbit that has current weight of %skg and has a goal weight of %skg that will intake %s grams of pellet feed?",weight,goalWeight,feedingQuantity);
                System.out.println(String.format("In three sentences what can be nutrition advice for Ill rabbit that has current weight of %skg and has a goal weight of %skg?",weight,goalWeight));
            }else if(rabbitStatus.equals("Deceased")){
                rabbitNutritionAdvice = "The rabbit is deceased. Feeding preparation is unnecessary!";
            }

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

                //stringOutput = stringOutput + stringText;
                statusMessage = stringOutput + stringText;
                status_dialog();
                //textView.setText(stringOutput);
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