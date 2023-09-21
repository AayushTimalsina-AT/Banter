package com.banter.Utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StatusDeleteWorker extends Worker {

    public StatusDeleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Perform the status deletion task here
        deleteOldStatuses();
        return Result.success();
    }

    private void deleteOldStatuses() {
        long currentTimeInMillis = System.currentTimeMillis();
        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("Status");
        Query oldStatusesQuery = statusRef.orderByChild("timestamp").endAt(currentTimeInMillis - twentyFourHoursInMillis);

        oldStatusesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot statusSnapshot : snapshot.getChildren()) {
                    statusSnapshot.getRef().removeValue();
                    StatusDeleteWorker.this.notify();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error condition if needed
            }
        });
    }
}
