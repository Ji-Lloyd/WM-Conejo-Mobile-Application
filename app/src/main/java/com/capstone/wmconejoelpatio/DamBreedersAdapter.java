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

import java.util.ArrayList;

public class DamBreedersAdapter extends RecyclerView.Adapter<DamBreedersAdapter.DamViewHolder> {

    Context context;
    ArrayList<DamBreedersData> damBreedersDataArrayList;

    public DamBreedersAdapter(Context context, ArrayList<DamBreedersData> damBreedersDataArrayList) {
        this.context = context;
        this.damBreedersDataArrayList = damBreedersDataArrayList;
    }

    @NonNull
    @Override
    public DamBreedersAdapter.DamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rabbit_breeders_list_holder, parent, false);
        return new DamBreedersAdapter.DamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DamBreedersAdapter.DamViewHolder holder, int position) {
        DamBreedersData damBreedersData = damBreedersDataArrayList.get(position);

        holder.damName.setText(damBreedersData.damName);
        holder.damBreed.setText(damBreedersData.damBreed);
        holder.damOrigin.setText(damBreedersData.damOrigin);
        holder.damStatus.setText(damBreedersData.damStatus);
        holder.damAccumulatedKits.setText(String.valueOf(damBreedersData.damAccumulatedKits));
        holder.damSuccessRate.setText(String.valueOf(damBreedersData.damSuccessRate));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(
                        R.layout.confirmation_dialog, (ConstraintLayout) holder.itemView.findViewById(R.id.layoutDialogContainer)
                );
                builder.setView(view1);
                ((TextView) view1.findViewById(R.id.textTitle)).setText("Select: " + holder.damName.getText());
                ((TextView) view1.findViewById(R.id.textMessage)).setText("Are you sure?");
                ((Button) view1.findViewById(R.id.buttonYes)).setText("Yes");
                ((Button) view1.findViewById(R.id.buttonNo)).setText("Cancel");
                ((ImageView) view1.findViewById(R.id.imageIcon)).setImageResource(R.drawable.update_icon);

                final AlertDialog alertDialog = builder.create();

                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent getIntent = ((Activity)context).getIntent();
                        String sireName = getIntent.getStringExtra("sireName");

                        Intent sendData = new Intent(context, RabbitBreedingActivity.class);
                        sendData.putExtra("damName", holder.damName.getText().toString());

                        sendData.putExtra("sireName", sireName);
                        context.startActivity(sendData);
                        //((Activity)context).startActivityForResult(sendData,RESULT_OK);
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
                                String sireName = getIntent.getStringExtra("sireName");

                                Intent sendData = new Intent(context, RabbitBreedingActivity.class);
                                sendData.putExtra("damName", holder.damName.getText().toString());

                                sendData.putExtra("sireName", sireName);
                                context.startActivity(sendData);
                                //((Activity)context).startActivityForResult(sendData,RESULT_OK);
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
        return damBreedersDataArrayList.size();
    }

    public static class DamViewHolder extends RecyclerView.ViewHolder {

        TextView damName, damBreed, damOrigin, damStatus, damAccumulatedKits, damSuccessRate;

        Button select;
        public DamViewHolder(@NonNull View itemView) {
            super(itemView);

            damName = itemView.findViewById(R.id.rabbitBreedersListName);
            damBreed = itemView.findViewById(R.id.rabbitBreedersListBreed);
            damOrigin = itemView.findViewById(R.id.rabbitBreedersListOrigin);
            damStatus = itemView.findViewById(R.id.rabbitBreedersListStatus);
            damAccumulatedKits = itemView.findViewById(R.id.rabbitBreedersListAccumulatedKits);
            damSuccessRate = itemView.findViewById(R.id.rabbitBreedersListSuccessRate);

            select = itemView.findViewById(R.id.btnBreederListSelect);
        }
    }
}
