package com.banter;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.banter.databinding.ActivitySettingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        binding.Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, profileActivity.class);
                startActivity(intent);
            }
        });
        binding.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this, "You just click the About us.", Toast.LENGTH_SHORT).show();
            }
        });
        String ID = auth.getUid();
        binding.deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference messageRef = database.getReference()
                        .child("Users")
                        .child(ID);
                messageRef.removeValue()
                        .addOnSuccessListener(aVoid -> {

                            FirebaseUser user = auth.getCurrentUser();
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(SettingActivity.this, SignUpActivity.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(SettingActivity.this, "Your Account Delete Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors that occurred during deletion
                            Log.e("MessageDeletionError", "Error deleting message: " + e.getMessage());
                        });
            }
        });


    }

}
