package com.capstone.wmconejoelpatio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class RabbitListsActivity extends DrawerBaseActivity {

    ImageButton entryList,feedingList,healthList,breedingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_rabbit_lists, contentFrameLayout);

        getSupportActionBar().setTitle("Rabbit Records");

        entryList = findViewById(R.id.btnRabbitEntryList);
        feedingList = findViewById(R.id.btnFeedingManagementList);
        healthList = findViewById(R.id.btnHealthManagementList);
        breedingList = findViewById(R.id.btnBreedingManagementList);

        entry_list();
        feeding_list();
        health_list();
        breeding_list();

    }

    private void breeding_list() {
        breedingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitListsActivity.this, RabbitBreedingHistory.class));
            }
        });
    }

    private void health_list() {
        healthList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitListsActivity.this, RabbitHealthHistory.class));
            }
        });
    }

    private void feeding_list() {
        feedingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitListsActivity.this, RabbitFeedingHistory.class));
            }
        });
    }

    private void entry_list() {
        entryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RabbitListsActivity.this, RabbitEntryListActivity.class));
            }
        });
    }
}