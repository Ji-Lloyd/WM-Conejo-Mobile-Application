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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RabbitFeedingAdapter extends RecyclerView.Adapter<RabbitFeedingAdapter.RabbitViewHolder> {

    Context context;
    DocumentReference documentRabbitReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<RabbitFeedingData> rabbitArrayList;

    public RabbitFeedingAdapter(Context context, ArrayList<RabbitFeedingData> rabbitArrayList) {
        this.context = context;
        this.rabbitArrayList = rabbitArrayList;
    }

    @NonNull
    @Override
    public RabbitFeedingAdapter.RabbitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rabbit_feeding_list_holder, parent, false);
        return new RabbitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RabbitFeedingAdapter.RabbitViewHolder holder, int position) {

        RabbitFeedingData feedingData = rabbitArrayList.get(position);

        holder.id.setText(String.valueOf(feedingData.id));
        holder.name.setText(feedingData.name);
        holder.feedType.setText(feedingData.feedType);
        holder.quantity.setText(feedingData.feedQuantity);
        holder.date.setText(feedingData.feedDate);
        holder.time.setText(feedingData.feedTime);

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
                        Intent sendData = new Intent(context, FeedingNutritionActivity.class);
                        sendData.putExtra("rabbitFeedingID", holder.id.getText().toString());
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
                                Intent sendData = new Intent(context, FeedingNutritionActivity.class);
                                sendData.putExtra("rabbitFeedingID", holder.id.getText().toString());
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
                                documentRabbitReference = db.document("FeedingAndNutrition/" + holder.id.getText().toString());
                                documentRabbitReference.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    documentRabbitReference.delete();
                                                    Toast.makeText(context, "Rabbit record deleted!", Toast.LENGTH_SHORT).show();
                                                    context.startActivity(new Intent(context, RabbitFeedingHistory.class));
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

        TextView id,name,feedType,quantity,date,time;
        Button update, delete;
        public RabbitViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.rabbitFeedingListID);
            name = itemView.findViewById(R.id.rabbitFeedingListName);
            feedType = itemView.findViewById(R.id.rabbitFeedingListFeedType);
            quantity = itemView.findViewById(R.id.rabbitFeedingListFeedQuantity);
            date = itemView.findViewById(R.id.rabbitFeedingListDate);
            time = itemView.findViewById(R.id.rabbitFeedingListFeedingTime);

            update = itemView.findViewById(R.id.btnFeedingListUpdate);
            delete = itemView.findViewById(R.id.btnFeedingListDelete);

        }
    }
}
