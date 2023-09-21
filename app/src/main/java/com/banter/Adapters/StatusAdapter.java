package com.banter.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banter.Models.Status;
import com.banter.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.viewHolder> {
    ArrayList<Status> statusList;
    Context context;

    public StatusAdapter(ArrayList<Status> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.status_show, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        Status status = statusList.get(position);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Check if the current user is the owner of the status
                if (currentUserIsOwnerOfStatus(status)) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to permanently delete the selected message?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference messageRef = database.getReference()
                                            .child("Status")
                                            .child(status.getStatusId());
                                    Log.d("STATUSID", status.getStatusId());
                                    messageRef.removeValue()
                                            .addOnSuccessListener(aVoid -> {
                                                // Message deletion successful
                                                Log.d("MessageDeleted", "Status deleted successfully");
                                                statusList.remove(position); // Remove the message from the list
                                                notifyItemRemoved(position);
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle any errors that occurred during deletion
                                                Log.e("MessageDeletionError", "Error deleting message: " + e.getMessage());
                                            });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    // Display a message indicating that the user doesn't have permission to delete the status
                    Toast.makeText(context, "You don't have permission to delete this status", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            private boolean currentUserIsOwnerOfStatus(Status status) {
                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String statusUser = status.getUserId();
                if (currentUser.equals(statusUser)) {
                    return true;
                }

                return false;
            }

        });



        Picasso.get().load(status.getProfilePic()).placeholder(R.drawable.man).into(holder.profilePic);
        holder.userName.setText(status.getUserName());
        holder.status.setText(status.getStatus());
        String text = TimeAgo.using(status.getTimestamp());
        holder.time.setText(text);

    }

    @Override
    public int getItemCount() {

        return statusList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView userName, status, time;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.Profile_pic);
            userName = itemView.findViewById(R.id.tvUserName);
            status = itemView.findViewById(R.id.tvStatus);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}
