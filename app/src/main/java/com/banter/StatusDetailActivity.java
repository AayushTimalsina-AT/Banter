package com.banter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.banter.Models.Status;
import com.banter.Models.Users;
import com.banter.databinding.ActivityStatusDetailBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class StatusDetailActivity extends AppCompatActivity {
    ActivityStatusDetailBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String username, profileImage;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        binding.backArrowId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatusDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Fetching User Detail  from data base
        database.getReference()
                .child("Users")
                .child(Objects.requireNonNull(auth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users users = snapshot.getValue(Users.class);
                            if (users != null) {
//
                                if ( users.getProfilePic() != null && !users.getProfilePic().trim().isEmpty()  ) {
                                    profileImage = users.getProfilePic();
                                } else {

                                }
                                username = users.getUserName();
                            }
                        } else {
                            Intent intent = new Intent(StatusDetailActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error condition if needed
                    }
                });

        binding.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String statusText = binding.editTextStatus.getText().toString();
                long timeInMillis = System.currentTimeMillis();
                Status status = new Status( profileImage,username, statusText,timeInMillis);
                database.getReference().child("Status").push().setValue(status); // Set the status object instead of status1
                Toast.makeText(StatusDetailActivity.this, "Status Upload Successfully", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
