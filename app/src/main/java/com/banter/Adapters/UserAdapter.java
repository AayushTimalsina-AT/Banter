package com.banter.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.recyclerview.widget.RecyclerView;

import com.banter.ChatDetailActivity;
import com.banter.Models.Messages;
import com.banter.Models.Users;
import com.banter.R;
import com.banter.Utils.EncryptDecryptHelper;
import com.banter.Utils.GsonUtils;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    ArrayList<Users> List = new ArrayList<>();
    private ArrayList<Users> userListFull = new ArrayList<>();
    Context context;
    String lmessage;


    EncryptDecryptHelper encryptDecryptHelper = new EncryptDecryptHelper();

    public UserAdapter(ArrayList<Users> list, Context context) {
        this.List.clear();
        this.List.addAll(list);
        this.context = context;
        this.userListFull.clear();
        this.userListFull.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_show, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = List.get(position);
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.man).into(holder.profile);
        holder.userName.setText(users.getUserName());


        FirebaseDatabase.getInstance().getReference().child("Chats").
                child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                DataSnapshot lastMessageSnapshot = snapshot.getChildren().iterator().next();
                                String encryptedData = lastMessageSnapshot.getValue(String.class);
                                try {
                                    // Decrypt the encrypted data
                                    String decryptedData = encryptDecryptHelper.decrypt(encryptedData);
                                    // Convert the decrypted JSON data to MessageModel object
                                    Messages model = GsonUtils.convertFromJson(decryptedData, Messages.class);
                                    holder.lastMessage.setText(model.getMessage());
                                    holder.time.setText(TimeAgo.using(model.getTimestamp()));
                                    notify();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("profilePic", users.getProfilePic());
                intent.putExtra("userName", users.getUserName());
                intent.putExtra("FcmToken", users.getFCMToken());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            ArrayList<Users> filteredList = new ArrayList<>();
            if (keyword.toString().isEmpty()) {
                // If the search query is empty, show the original list
                filteredList.addAll(userListFull);
            } else {
                for (Users sUser : userListFull) {
                    if (sUser.getUserName().toString().toLowerCase().contains(keyword.toString().toLowerCase())) {
                        filteredList.add(sUser);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List.clear();
            List.addAll((ArrayList<Users>) results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView userName, lastMessage, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            time = itemView.findViewById(R.id.time);
        }
    }
}