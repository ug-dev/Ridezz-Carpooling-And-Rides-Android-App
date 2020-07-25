package com.example.ridezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nonnull;

public class change_password extends AppCompatActivity {

    EditText pwd,confPwd;

    FirebaseUser user;

    View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        contextView = findViewById(android.R.id.content);
        user = FirebaseAuth.getInstance().getCurrentUser();

        pwd = findViewById(R.id.newpassword);
        confPwd = findViewById(R.id.confirmpassword);

        findViewById(R.id.btn_update_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwd.getText().toString().trim().matches("") || confPwd.getText().toString().trim().matches("")){
                    Snackbar.make(contextView,R.string.pwd_change_empty_filed,Snackbar.LENGTH_SHORT).show();
                }else {
                    if (pwd.getText().toString().trim().equals(confPwd.getText().toString().trim())) {
                        String temp = confPwd.getText().toString().trim();
                        user.updatePassword(temp)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent i = new Intent(change_password.this, dashboard.class);
                                            startActivity(i);
                                            Log.d("change_password", "User password updated.");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@Nonnull Exception e) {
                                Toast.makeText(change_password.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent i = new Intent(change_password.this,home.class);
                                startActivity(i);
                            }
                        });
                    } else {
                        Snackbar.make(contextView, R.string.pwd_doent_match, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
