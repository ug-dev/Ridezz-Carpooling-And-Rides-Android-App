package com.example.ridezz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import javax.annotation.Nonnull;

public class forgot_password_activity extends AppCompatActivity {
	ImageView back;
	ProgressBar progressBar;

	FirebaseAuth mAuth;

	EditText email;
    private final static String TAG = "forgot_password";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password_activity);
		mAuth = FirebaseAuth.getInstance();
		progressBar = findViewById(R.id.forgot_progressBar);

		email = findViewById(R.id.tb_first);
		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);

				if (!TextUtils.isEmpty(email.getText().toString().trim())) {
					mAuth.sendPasswordResetEmail(email.getText().toString().trim())
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@Nonnull Task<Void> task) {
									if (task.isSuccessful()) {
										progressBar.setVisibility(View.GONE);
										Log.d(TAG, "Email sent.");
										Toast.makeText(forgot_password_activity.this, "Password Reset link is sent to your Email", Toast.LENGTH_SHORT).show();
									}
								}
							});
				} else {
					Toast.makeText(forgot_password_activity.this, "Enter Email!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		back = findViewById(R.id.back_btn);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(forgot_password_activity.this, login_activity.class);
				startActivity(intent);
			}
		});
	}
}
