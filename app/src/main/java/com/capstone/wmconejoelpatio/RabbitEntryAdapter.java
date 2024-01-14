package com.capstone.wmconejoelpatio;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RabbitEntryAdapter extends RecyclerView.Adapter<RabbitEntryAdapter.RabbitViewHolder> {

    Context context;
    DocumentReference documentRabbitReference;
    StorageReference storageRabbitReference;
    String imageLink = "QR_Code_Images/";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRabbitReference;
    ArrayList<RabbitEntryData> rabbitArrayList;


    public RabbitEntryAdapter(Context context, ArrayList<RabbitEntryData> rabbitArrayList) {
        this.context = context;
        this.rabbitArrayList = rabbitArrayList;
    }

    @NonNull
    @Override
    public RabbitEntryAdapter.RabbitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rabbit_list_holder, parent, false);
        return new RabbitViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull RabbitEntryAdapter.RabbitViewHolder holder, int position) {

        RabbitEntryData entryData = rabbitArrayList.get(position);

        holder.name.setText(entryData.name);
        holder.tattoo.setText(entryData.tattoo);
        holder.breed.setText(entryData.breed);
        holder.origin.setText(entryData.origin);
        holder.gender.setText(entryData.gender);
        holder.pregnant.setText(entryData.pregnant);
        holder.status.setText(entryData.status);

        Glide.with(holder.rabbitImage.getContext())
                        .load(entryData.getPhoto())
                        .placeholder(R.drawable.wm_icon)
                        .circleCrop()
                        .error(R.drawable.rabbit_icon)
                        .into(holder.rabbitImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(
                        R.layout.confirmation_dialog, (ConstraintLayout) holder.itemView.findViewById(R.id.layoutDialogContainer)
                );
                builder.setView(view1);
                ((TextView) view1.findViewById(R.id.textTitle)).setText("Update: " + holder.name.getText());
                ((TextView) view1.findViewById(R.id.textMessage)).setText("Are you sure?");
                ((Button) view1.findViewById(R.id.buttonYes)).setText("Yes");
                ((Button) view1.findViewById(R.id.buttonNo)).setText("Cancel");
                ((ImageView) view1.findViewById(R.id.imageIcon)).setImageResource(R.drawable.update_icon);

                final AlertDialog alertDialog = builder.create();

                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sendData = new Intent(context, RabbitEntryActivity.class);
                        sendData.putExtra("rabbitName", holder.name.getText().toString());
                        sendData.putExtra("rabbitShow",true);
                        context.startActivity(sendData);
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

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setIcon(R.drawable.update_icon)
                        .setTitle("Update")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent sendData = new Intent(context, RabbitEntryActivity.class);
                                sendData.putExtra("rabbitName", holder.name.getText().toString());
                                sendData.putExtra("rabbitShow",true);
                                context.startActivity(sendData);
                            }
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure?")
                        .setIcon(R.drawable.delete_icon)
                        .setTitle("Delete")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                documentRabbitReference = db.document("Rabbit/" + holder.name.getText().toString());
                                documentRabbitReference.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    storageRabbitReference = FirebaseStorage.getInstance().getReference(imageLink + holder.name.getText().toString());
                                                    storageRabbitReference.delete();
                                                    documentRabbitReference.delete();
                                                    Toast.makeText(context, "Rabbit record deleted!", Toast.LENGTH_SHORT).show();
                                                    context.startActivity(new Intent(context, RabbitEntryListActivity.class));
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Deletion error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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
        return rabbitArrayList.size();
    }


    public static class RabbitViewHolder extends RecyclerView.ViewHolder {

        TextView name, tattoo, breed, origin, gender, pregnant, status;
        CircleImageView rabbitImage;
        Button update, delete;

        public RabbitViewHolder(@NonNull View itemView) {
            super(itemView);

            rabbitImage = itemView.findViewById(R.id.rabbitEntryListImage);

            name = itemView.findViewById(R.id.rabbitEntryListName);
            tattoo = itemView.findViewById(R.id.rabbitEntryListEarTattoo);
            breed = itemView.findViewById(R.id.rabbitEntryListBreed);
            origin = itemView.findViewById(R.id.rabbitEntryListOrigin);
            gender = itemView.findViewById(R.id.rabbitEntryListGender);
            pregnant = itemView.findViewById(R.id.rabbitEntryListPregnant);
            status = itemView.findViewById(R.id.rabbitEntryListStatus);


            update = itemView.findViewById(R.id.btnHolderUpdate);
            delete = itemView.findViewById(R.id.btnHolderDelete);

        }
    }


}
