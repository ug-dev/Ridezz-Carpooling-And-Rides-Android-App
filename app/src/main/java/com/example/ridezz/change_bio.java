package com.example.ridezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class change_bio extends AppCompatActivity {

    TextView bio,current_bio;
    private View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bio);


        bio = findViewById(R.id.bio);
        current_bio = findViewById(R.id.current_bio);


        final Map<String, Object> data = new HashMap<>();
        final Map<String, Object> getData = new HashMap<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("bio") != null) {
                    getData.put("bio",documentSnapshot.get("bio"));
                    current_bio.setText(Objects.requireNonNull(getData.get("bio")).toString());
                }
            }
        });


        findViewById(R.id.btn_change_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = bio.getText().toString().trim();
                data.put("bio", temp);
                docRef.set(data,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent i = new Intent(change_bio.this,dashboard.class);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        contextView = findViewById(android.R.id.content);
                        Snackbar.make(contextView, "Something Went Wrong, Please Try again later", Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}
