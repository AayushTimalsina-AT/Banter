package com.banter;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.banter.Adapters.ChatAdapter;
import com.banter.Models.Messages;
import com.banter.Models.Users;
import com.banter.Utils.EncryptDecryptHelper;
import com.banter.Utils.GsonUtils;
import com.banter.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    Context context;
    String FCMToken;
    private static final String CHANNEL_ID = "1000";
    EncryptDecryptHelper encryptDecryptHelper = new EncryptDecryptHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        binding.send.setEnabled(false);

        createNotificationChannel();
        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }


        final String senderId = auth.getUid();
        FCMToken = getIntent().getStringExtra("FcmToken");
        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");


        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.man).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final ArrayList<Messages> messages = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messages, this, receiveId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiveId;
        final String receiverRoom = receiveId + senderId;

        // Message Fetching from Database
        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String encryptedData = snapshot1.getValue(String.class);
                            try {
                                // Decrypt the encrypted data
                                String decryptedData = encryptDecryptHelper.decrypt(encryptedData);
                                // Convert the decrypted JSON data to MessageModel object
                                Messages model = GsonUtils.convertFromJson(decryptedData, Messages.class);
                                model.setMessageId(snapshot1.getKey());
                                messages.add(model);
                                Log.d("MESSAGESTEST", "onDataChange: " + messages);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        if (messages.size() > 0) {
                            binding.chatRecyclerView.scrollToPosition(messages.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = binding.btMessage.getText().toString();

                final Messages model = new Messages(senderId, message);
                model.setTimestamp(System.currentTimeMillis());
                binding.btMessage.setText("");
                // Encryption
                try {
                    // Convert the model object to JSON
                    String modelJson = GsonUtils.convertToJson(model);
                    // Encrypt the model data
                    String encryptedData = encryptDecryptHelper.encrypt(modelJson);
                    // Message store in Database
                    database.getReference().child("Chats")
                            .child(senderRoom)
                            .push()
                            .setValue(encryptedData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference().child("Chats")
                                            .child(receiverRoom)
                                            .push()
                                            .setValue(encryptedData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // Data sent successfully
//                                                    updateRecentChat( senderRoom,receiverRoom,senderId,model);
                                                    sendNotification(message);
                                                }
                                            });
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.btMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the button if the user has typed something
                binding.send.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed in this case
            }
        });
        binding.menuChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ChatDetailActivity.this, view);
        popupMenu.inflate(R.menu.chat_menu); // The menu resource file for the popup menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks here
                int id = item.getItemId();
                if (id == R.id.Delete) {
                    deleteAllChats();
                    return true;

                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void deleteAllChats() {
        final String senderId = auth.getUid();
        String receiveId = getIntent().getStringExtra("userId");
        final String senderRoom = senderId + receiveId;
        final String receiverRoom = receiveId + senderId;

        // Delete messages from sender's room
        database.getReference().child("Chats")
                .child(senderRoom)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Messages deleted from sender's room, now delete messages from receiver's room
                        Intent intetn = new Intent(ChatDetailActivity.this, MainActivity.class);
                        startActivity(intetn);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to delete messages from sender's room
                    }
                });

    }

    private void createNotificationChannel() {
        // Create a notification channel (for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Chats",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotificationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Notifications");
        builder.setMessage("Allow Banter to send you notification ?");

        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open app settings to allow notification permissions
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the case where the user cancels the permission request
                Toast.makeText(context, "Permission Denied ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private boolean areNotificationsEnabled() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    void sendNotification(String message) {
        database.getReference().child("Users")
                .child(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            Users currentUser = task.getResult().getValue(Users.class);
                            try {
                                JSONObject jsonObject = new JSONObject();


                                JSONObject NotificationObject = new JSONObject();

                                assert currentUser != null;
                                NotificationObject.put("title", currentUser.getUserName());
                                NotificationObject.put("body", message);
//                        NotificationObject.put("icon", "man");

                                JSONObject DataObject = new JSONObject();
                                DataObject.put("receiveId", currentUser.getUserId());

                                jsonObject.put("notification", NotificationObject);
                                jsonObject.put("data", DataObject);
                                jsonObject.put("to", FCMToken);

                                callApi(jsonObject);


                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                });


    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        String ServerKey = "AAAAkzNnNwo:APA91bHBkQUKJL5gXJ5_07xKTwYrFPu5nyG1GXrTQgB8ZYq-ye8LG0oqwV5laLh9juci4frOyzJdFYsok3iwTik8jQiFRGKmKocKD_W3NoLjlWeykj26l79zm-DEyh2BxG_7h9rgW6QF";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + ServerKey)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("APICALL", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("APICALL", "onResponse: " + response.toString());
            }
        });

    }

}
