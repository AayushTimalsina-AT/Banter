package com.banter.Adapters;

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

    ArrayList<Users> List;
    private ArrayList<Users> userListFull;
    Context context;
    EncryptDecryptHelper encryptDecryptHelper = new EncryptDecryptHelper();

    public UserAdapter(ArrayList<Users> list, Context context) {
        this.List = list;
        this.context = context;
        this.userListFull = new ArrayList<>(list); // Copy the original list
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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String encryptedData = snapshot1.getValue(String.class);
                        try {
                            // Decrypt the encrypted data
                            String decryptedData = encryptDecryptHelper.decrypt(encryptedData);
                            // Convert the decrypted JSON data to MessageModel object
                            Messages model = GsonUtils.convertFromJson(decryptedData, Messages.class);
                            holder.lastMessage.setText(model.getMessage());
                            holder.time.setText(TimeAgo.using(model.getTimestamp()));
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
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    @Override
    public  Filter getFilter() {
        return null;
    }
     Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Users> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // If the search query is empty, show the original list
                filteredList.addAll(userListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Users user : userListFull) {
                    if (user.getUserName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
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
