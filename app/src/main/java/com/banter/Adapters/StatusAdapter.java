package com.banter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banter.Models.Status;
import com.banter.R;
import com.banter.Models.Users;
import com.github.marlonlom.utilities.timeago.TimeAgo;
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
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Status status = statusList.get(position);
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
