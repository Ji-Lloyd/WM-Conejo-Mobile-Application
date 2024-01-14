package com.capstone.wmconejoelpatio;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SireBreedersAdapter extends RecyclerView.Adapter<SireBreedersAdapter.SireViewHolder> {

    Context context;
    ArrayList<SireBreedersData> sireBreedersDataArrayList;

    public SireBreedersAdapter(Context context, ArrayList<SireBreedersData> sireBreedersDataArrayList) {
        this.context = context;
        this.sireBreedersDataArrayList = sireBreedersDataArrayList;
    }

    @NonNull
    @Override
    public SireBreedersAdapter.SireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rabbit_breeders_list_holder, parent, false);
        return new SireBreedersAdapter.SireViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SireBreedersAdapter.SireViewHolder holder, int position) {
        SireBreedersData sireBreedersData = sireBreedersDataArrayList.get(position);

        holder.sireName.setText(sireBreedersData.sireName);
        holder.sireBreed.setText(sireBreedersData.sireBreed);
        holder.sireOrigin.setText(sireBreedersData.sireOrigin);
        holder.sireStatus.setText(sireBreedersData.sireStatus);
        holder.sireAccumulatedKits.setText(String.valueOf(sireBreedersData.sireAccumulatedKits));
        holder.sireSuccessRate.setText(String.valueOf(sireBreedersData.sireSuccessRate));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(
                        R.layout.confirmation_dialog, (ConstraintLayout) holder.itemView.findViewById(R.id.layoutDialogContainer)
                );
                builder.setView(view1);
                ((TextView) view1.findViewById(R.id.textTitle)).setText("Select: " + holder.sireName.getText());
                ((TextView) view1.findViewById(R.id.textMessage)).setText("Are you sure?");
                ((Button) view1.findViewById(R.id.buttonYes)).setText("Yes");
                ((Button) view1.findViewById(R.id.buttonNo)).setText("Cancel");
                ((ImageView) view1.findViewById(R.id.imageIcon)).setImageResource(R.drawable.update_icon);

                final AlertDialog alertDialog = builder.create();

                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent getIntent = ((Activity)context).getIntent();
                        String damName = getIntent.getStringExtra("damName");
                        String damBreed = getIntent.getStringExtra("damBreed");

                        Intent sendData = new Intent(context, RabbitBreedingActivity.class);
                        sendData.putExtra("sireName", holder.sireName.getText().toString());

                        sendData.putExtra("damName", damName);
                        context.startActivity(sendData);
                        ((Activity)context).finish();
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

        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setIcon(R.drawable.update_icon)
                        .setTitle("Select")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent getIntent = ((Activity)context).getIntent();
                                String damName = getIntent.getStringExtra("damName");
                                String damBreed = getIntent.getStringExtra("damBreed");

                                Intent sendData = new Intent(context, RabbitBreedingActivity.class);
                                sendData.putExtra("sireName", holder.sireName.getText().toString());

                                sendData.putExtra("damName", damName);
                                context.startActivity(sendData);
                                ((Activity)context).finish();
                            }
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return sireBreedersDataArrayList.size();
    }

    public static class SireViewHolder extends RecyclerView.ViewHolder {

        TextView sireName, sireBreed, sireOrigin, sireStatus, sireAccumulatedKits, sireSuccessRate;

        Button select;
        public SireViewHolder(@NonNull View itemView) {
            super(itemView);

            sireName = itemView.findViewById(R.id.rabbitBreedersListName);
            sireBreed = itemView.findViewById(R.id.rabbitBreedersListBreed);
            sireOrigin = itemView.findViewById(R.id.rabbitBreedersListOrigin);
            sireStatus = itemView.findViewById(R.id.rabbitBreedersListStatus);
            sireAccumulatedKits = itemView.findViewById(R.id.rabbitBreedersListAccumulatedKits);
            sireSuccessRate = itemView.findViewById(R.id.rabbitBreedersListSuccessRate);

            select = itemView.findViewById(R.id.btnBreederListSelect);
        }
    }
}
