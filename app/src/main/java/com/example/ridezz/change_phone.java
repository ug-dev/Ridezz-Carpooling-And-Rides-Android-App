package com.example.ridezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

public class change_phone extends AppCompatActivity {

    EditText phone;
    private View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        phone = findViewById(R.id.new_num);
        final HashMap<String,Object> phoneNum = new HashMap<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        findViewById(R.id.btn_new_phone_num).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum.put("phone_num",phone.getText().toString().trim());
                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.set(phoneNum, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent i = new Intent(change_phone.this,dashboard.class);
                                startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        contextView = findViewById(android.R.id.content);
                        Snackbar.make(contextView, "Something Went Wrong, Please Try again later", Snackbar.LENGTH_SHORT).show();
                    }
                });;
            }
        });
    }
}
