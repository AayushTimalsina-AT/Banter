package com.banter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.banter.Models.Status;
import com.banter.Models.Users;
import com.banter.Utils.StatusDeleteWorker;
import com.banter.databinding.ActivityStatusDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
                finish();
            }
        });

        // Fetching User Detail from the database
        database.getReference()
                .child("Users")
                .child(Objects.requireNonNull(auth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users users = snapshot.getValue(Users.class);
                            if (users != null) {
                                if (users.getProfilePic() != null && !users.getProfilePic().trim().isEmpty()) {
                                    profileImage = users.getProfilePic();
                                } else {
                                    // Set a default profile image if not available
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
                Status status = new Status(profileImage, username, statusText, timeInMillis);
                database.getReference().child("Status").push().setValue(status); // Set the status object instead of status1
                Toast.makeText(StatusDetailActivity.this, "Status Upload Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Schedule the periodic task to delete old statuses
//        scheduleStatusDeletionTask();
    }

    private void scheduleStatusDeletionTask() {
        // Schedule the task to run every 24 hours
        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(StatusDeleteWorker.class, 24, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
