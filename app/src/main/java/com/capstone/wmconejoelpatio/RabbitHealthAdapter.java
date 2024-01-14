package com.capstone.wmconejoelpatio;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RabbitHealthAdapter extends RecyclerView.Adapter<RabbitHealthAdapter.RabbitViewHolder> {

    Context context;
    DocumentReference documentRabbitReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRabbitReference;
    ArrayList<RabbitHealthData> rabbitArrayList;

    public RabbitHealthAdapter(Context context, ArrayList<RabbitHealthData> rabbitArrayList) {
        this.context = context;
        this.rabbitArrayList = rabbitArrayList;
    }

    @NonNull
    @Override
    public RabbitHealthAdapter.RabbitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rabbit_health_list_holder, parent, false);
        return new RabbitHealthAdapter.RabbitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RabbitHealthAdapter.RabbitViewHolder holder, int position) {

        RabbitHealthData healthData = rabbitArrayList.get(position);
        holder.id.setText(String.valueOf(healthData.getId()));
        holder.name.setText(healthData.getName());
        holder.disease.setText(healthData.getDisease());
        holder.vaccination.setText(healthData.getVaccination());
        holder.notes.setText(healthData.getNotes());
        holder.date.setText(healthData.getDate());

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
                        Intent sendData = new Intent(context, RabbitHealthActivity.class);
                        sendData.putExtra("rabbitHealthID", holder.id.getText().toString());
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
                                Intent sendData = new Intent(context, RabbitHealthActivity.class);
                                sendData.putExtra("rabbitHealthID", holder.id.getText().toString());
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
                                documentRabbitReference = db.document("HealthAdministration/" + holder.id.getText().toString());
                                documentRabbitReference.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    documentRabbitReference.delete();
                                                    Toast.makeText(context, "Rabbit record deleted!", Toast.LENGTH_SHORT).show();
                                                    context.startActivity(new Intent(context, RabbitHealthHistory.class));
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

        TextView id,name,disease,vaccination,notes,date;
        Button update,delete;
        public RabbitViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.rabbitHealthListID);
            name = itemView.findViewById(R.id.rabbitHealthListName);
            disease = itemView.findViewById(R.id.rabbitHealthListDisease);
            vaccination = itemView.findViewById(R.id.rabbitHealthListVaccination);
            notes = itemView.findViewById(R.id.rabbitHealthListNotes);
            date = itemView.findViewById(R.id.rabbitHealthListDate);

            update = itemView.findViewById(R.id.btnHealthListUpdate);
            delete = itemView.findViewById(R.id.btnHealthListDelete);
        }
    }
}
