package com.banter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PermissionRequestActivity extends AppCompatActivity {
    Button btnNext;
    private static final String Storage_permission = Manifest.permission.READ_MEDIA_IMAGES;
    private static final String Notification_Permission = Manifest.permission.POST_NOTIFICATIONS;
    private static final int REQ_CODE = 36;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);

        btnNext = findViewById(R.id.btnNext);
        if (ContextCompat.checkSelfPermission(this, Storage_permission) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Notification_Permission) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PermissionRequestActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPermissionDialog();
                }
            });

        }
        }

//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPermissionDialog();
//            }
//        });
//
//    }

    private void showPermissionDialog() {
        if (ContextCompat.checkSelfPermission(this, Storage_permission) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Notification_Permission) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PermissionRequestActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Storage_permission, Notification_Permission}, REQ_CODE);
//            Toast.makeText(this, "Permission Deny", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(PermissionRequestActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(PermissionRequestActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            showPermissionDialog();
        }

    }
}