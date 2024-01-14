package com.capstone.wmconejoelpatio;

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

public class RabbitBreedingAdapter extends RecyclerView.Adapter<RabbitBreedingAdapter.RabbitViewHolder>{

    Context context;
    DocumentReference documentRabbitReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<RabbitBreedingData> rabbitBreedingDataArrayList;

    public RabbitBreedingAdapter(Context context, ArrayList<RabbitBreedingData> rabbitBreedingDataArrayList) {
        this.context = context;
        this.rabbitBreedingDataArrayList = rabbitBreedingDataArrayList;
    }

    @NonNull
    @Override
    public RabbitBreedingAdapter.RabbitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rabbit_breeding_list_holder, parent, false);
        return new RabbitBreedingAdapter.RabbitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RabbitBreedingAdapter.RabbitViewHolder holder, int position) {

        RabbitBreedingData sireBreedersData = rabbitBreedingDataArrayList.get(position);

        holder.documentName.setText(sireBreedersData.documentName);
        holder.sireName.setText(sireBreedersData.sireName);
        holder.damName.setText(sireBreedersData.damName);
        holder.studDate.setText(sireBreedersData.studDate);
        holder.birthDate.setText(sireBreedersData.birthDate);
        holder.litterSize.setText(String.valueOf(sireBreedersData.litterSize));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(
                        R.layout.confirmation_dialog, (ConstraintLayout) holder.itemView.findViewById(R.id.layoutDialogContainer)
                );
                builder.setView(view1);
                ((TextView) view1.findViewById(R.id.textTitle)).setText("Update: " + holder.documentName.getText());
                ((TextView) view1.findViewById(R.id.textMessage)).setText("Are you sure?");
                ((Button) view1.findViewById(R.id.buttonYes)).setText("Yes");
                ((Button) view1.findViewById(R.id.buttonNo)).setText("Cancel");
                ((ImageView) view1.findViewById(R.id.imageIcon)).setImageResource(R.drawable.update_icon);

                final AlertDialog alertDialog = builder.create();

                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sendData = new Intent(context, RabbitBreedingActivity.class);
                        sendData.putExtra("sireName", holder.sireName.getText().toString());
                        sendData.putExtra("damName", holder.damName.getText().toString());
                        sendData.putExtra("documentName", holder.documentName.getText().toString());
                        sendData.putExtra("rabbitShow",true);

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

                                Intent sendData = new Intent(context, RabbitBreedingActivity.class);
                                sendData.putExtra("sireName", holder.sireName.getText().toString());
                                sendData.putExtra("damName", holder.damName.getText().toString());
                                sendData.putExtra("documentName", holder.documentName.getText().toString());
                                sendData.putExtra("rabbitShow",true);

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
        return rabbitBreedingDataArrayList.size();
    }

    public static class RabbitViewHolder extends RecyclerView.ViewHolder {

        TextView documentName,sireName,damName,studDate,birthDate,litterSize;
        Button select;
        public RabbitViewHolder(@NonNull View itemView) {
            super(itemView);

            documentName = itemView.findViewById(R.id.rabbitBreedingListDocumentName);
            sireName = itemView.findViewById(R.id.rabbitBreedingListSireName);
            damName = itemView.findViewById(R.id.rabbitBreedingListDamName);
            studDate = itemView.findViewById(R.id.rabbitBreedingListStudDate);
            birthDate = itemView.findViewById(R.id.rabbitBreedingListBirthDate);
            litterSize = itemView.findViewById(R.id.rabbitBreedingListLitterSize);

            select = itemView.findViewById(R.id.btnBreedingListSelect);
        }
    }
}
