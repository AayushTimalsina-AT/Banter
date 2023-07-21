package com.banter.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.banter.Adapters.StatusAdapter;
import com.banter.Models.Status;
import com.banter.Models.Users;
import com.banter.R;
import com.banter.SettingActivity;
import com.banter.SignInActivity;
import com.banter.SignUpActivity;
import com.banter.StatusDetailActivity;
import com.banter.databinding.FragmentChatBinding;
import com.banter.databinding.FragmentStatusBinding;
import com.banter.profileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

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

        StatusAdapter sAdapter = new StatusAdapter(statusList,getContext());
        binding.statusRecyclerView.setAdapter(sAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.statusRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            statusList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Status sPost = dataSnapshot.getValue(Status.class);
                                statusList.add(sPost);

                            }
                        }

                      sAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("StatusFragment", "DatabaseError: " + error.getMessage());
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