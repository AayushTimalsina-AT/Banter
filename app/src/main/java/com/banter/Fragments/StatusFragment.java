package com.banter.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.banter.Adapters.StatusAdapter;
import com.banter.Models.Status;
import com.banter.StatusDetailActivity;
import com.banter.databinding.FragmentStatusBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatusFragment extends Fragment {
    FragmentStatusBinding binding;
    ArrayList<Status> statusList = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth auth;

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentStatusBinding.inflate(inflater, container, false);
        database= FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        binding.shimmerLayout.startShimmerAnimation();
        StatusAdapter sAdapter = new StatusAdapter(statusList,getContext());
        binding.statusRecyclerView.setAdapter(sAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.statusRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        statusList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                Log.d("STATS-KEY", dataSnapshot.getKey());
                                Status sPost = dataSnapshot.getValue(Status.class);
                                sPost.setStatusId(dataSnapshot.getKey());
                                statusList.add(sPost);
                            }
                            binding.shimmerLayout.stopShimmerAnimation();
                            binding.shimmerLayout.setVisibility(View.INVISIBLE);
                            sAdapter.notifyDataSetChanged();
                        }
                        binding.shimmerLayout.stopShimmerAnimation();
                        binding.shimmerLayout.setVisibility(View.INVISIBLE);
                        statusList.clear();
                        // Add a placeholder status or display a message
//                        Status placeholderStatus = new Status();
//                        placeholderStatus.setStatusText("Nothing here");
//                        statusList.add(placeholderStatus);
                        sAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("StatusFragment", "DatabaseError: " + error.getMessage());
            }
        });
        binding.writingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity() , StatusDetailActivity.class);
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return binding.getRoot();
    }


}