package com.capstone.wmconejoelpatio;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCodeActivity extends DrawerBaseActivity {

    Button scanner,generator;
    TextView scannerResult;
    EditText codeText;
    ImageView codeImageViewer;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_qr_code, contentFrameLayout);

        scanner = findViewById(R.id.btnScanner);
        generator = findViewById(R.id.btnScanner);
        scannerResult = findViewById(R.id.tvScannerResult);

        codeText = findViewById(R.id.etCodeGenerator);
        codeImageViewer = findViewById(R.id.codeImage);

        //scanner_function();
        code_generator();

    }

    private void code_generator() {
        generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter writer = new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix = writer.encode(codeText.getText().toString(), BarcodeFormat.QR_CODE, 300, 300);
                    BarcodeEncoder encoder = new BarcodeEncoder();

                    Bitmap bitmap = encoder.createBitmap(bitMatrix);

                    codeImageViewer.setImageBitmap(bitmap);

                }catch (WriterException writerException){
                    throw new RuntimeException(writerException);
                }catch (Exception exception){
                    System.out.println(exception.getMessage());
                }
            }
        });
    }


    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            String contents = result.getContents();
            if(contents != null){
                scannerResult.setText(result.getContents());
            }
            else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

     */


}