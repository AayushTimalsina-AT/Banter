package com.banter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.banter.Models.Users;
import com.banter.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog= new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're Creating Your Account");
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,options);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.etUserName.getText().toString().isEmpty()){
                    binding.etPassword.setError("Enter Your UserName");
                    return;
                }
                if(binding.etEmail.getText().toString().isEmpty()){
                    binding.etEmail.setError("Enter Your Email");
                    return;
                }
                if(binding.etPassword.getText().toString().isEmpty()){
                    binding.etPassword.setError("Enter Your Password");
                    return;
                }
                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Users user= new Users(binding.etUserName.getText().toString(),binding.etEmail.getText().toString(),
                                            binding.etPassword.getText().toString());
                                    String id= Objects.requireNonNull(task.getResult().getUser()).getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    Toast.makeText(SignUpActivity.this, "User Create Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent= new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
           });
        binding.tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });
    }
    private final int RC_SIGN_IN = 69;
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "FirebaseAuthWithGoogle:" + account.getIdToken());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
//                throw new RuntimeException(e);
                Log.w("TAg", "Google Sign in Failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilePic(user.getPhotoUrl().toString());
                            users.setEmail(user.getEmail());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);


                            Intent intent = new Intent(SignUpActivity.this , MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignUpActivity.this, "Sign In with Google", Toast.LENGTH_SHORT).show();
//                            updateUI(user);
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });

    }
}