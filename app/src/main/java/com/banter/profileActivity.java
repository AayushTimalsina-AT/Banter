package com.banter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.banter.Models.Users;
import com.banter.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class profileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profileActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = binding.etStatus.getText().toString();
                String username = binding.userName.getText().toString();
                String email = binding.etEmail.getText().toString();

                HashMap<String, Object> obj = new HashMap<>();
                obj.put("userName", username);
                obj.put("about", status);

                // Check if the user logged in with Google
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                boolean isGoogleLogin = false;

                if (user != null) {
                    for (UserInfo profile : user.getProviderData()) {
                        if (profile.getProviderId().equals("google.com")) {
                            isGoogleLogin = true;
                            break;
                        }
                    }
                } else {
                    Toast.makeText(profileActivity.this, "Email Not Updated", Toast.LENGTH_SHORT).show();
                }

                // Update email only if not logged in with Google
                if (!isGoogleLogin) {
                    obj.put("email", email);
                    updateEmailOnAuthentication(email);
                }

                database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).updateChildren(obj);

                Toast.makeText(profileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        //Fetching Data from database
        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    if (users != null) {
                        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.man).into(binding.profileImage);

                        binding.userName.setText(users.getUserName());
                        binding.UserName.setText(users.getUserName());
                        binding.etStatus.setText(users.getAbout());
                        binding.About.setText(users.getAbout());
                        binding.etEmail.setText(users.getEmail());
                    }
                } else {
                    // Handle the case when the user data is not available
                    // after logout or deletion
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error condition if needed
            }
        });

        binding.addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*"); // This sets the type to only select image files
                startActivityForResult(intent, 69);
            }
        });

    }

    private void updateEmailOnAuthentication(String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Email updated successfully
                        Toast.makeText(profileActivity.this, "Email Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error updating email
                        Toast.makeText(profileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            Uri sFile = data.getData();
            binding.profileImage.setImageURI(sFile);
            final StorageReference reference = storage.getReference().child("ProfilePicture").child(auth.getUid());

            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(auth.getUid()).child("ProfilePic").setValue(uri.toString());
                            Toast.makeText(profileActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }


}
