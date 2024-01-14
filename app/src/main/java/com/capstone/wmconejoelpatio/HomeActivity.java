package com.capstone.wmconejoelpatio;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HomeActivity extends DrawerBaseActivity {

    ImageButton qrCode,list,management, dashboard;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRabbitReference;
    DocumentReference documentRabbitReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);

        getSupportActionBar().setTitle("Home");

        qrCode = findViewById(R.id.btnQRCodeScanner);
        list = findViewById(R.id.btnRabbitList);
        management = findViewById(R.id.btnRabbitManagement);
        dashboard = findViewById(R.id.btnDashboard);

        try{
            scanner_function(); // Straight QR Code Scanner
            list_function();
            management_function();
            exit_function();
        }catch (Exception ex){
            Toast.makeText(this, "onCreate Error: \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void list_function() {
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RabbitListsActivity.class));
            }
        });
    }

    private void management_function() {
        management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RabbitManagementActivity.class));
            }
        });
    }

    private void exit_function(){
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    startActivity(new Intent(HomeActivity.this, RabbitDashboardActivity.class));
                }catch (Exception ex){
                    Toast.makeText(HomeActivity.this, "Error: " + ex.toString(), Toast.LENGTH_SHORT).show();
                    System.out.println("Error: " + ex.toString());
                }
            }
        });
    }


    private void scanner_function() {
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.setPrompt("Scan a QR Code");
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            String contents = result.getContents();
            if(contents != null){
                documentRabbitReference = db.document("Rabbit/" + contents);
                documentRabbitReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            Toast.makeText(HomeActivity.this, result.getContents(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(HomeActivity.this, RabbitManagementActivity.class);
                                            intent.putExtra("rabbitName",result.getContents());
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(HomeActivity.this, "Rabbit does not exist", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
            }
            else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


}