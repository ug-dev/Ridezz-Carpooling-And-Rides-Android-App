package com.example.ridezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_activity extends AppCompatActivity {
	TextView signup, forgot;
	ProgressBar progressBar;

	private final static String TAG = "login";

	EditText email,password;

	FirebaseAuth mAuth;
	FirebaseUser user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_activity);
		mAuth = FirebaseAuth.getInstance();

		progressBar = findViewById(R.id.login_progressBar);

		email = findViewById(R.id.tb_first);
		password = findViewById(R.id.tb_last);

		signup = findViewById(R.id.login_text);
		signup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(login_activity.this, signup_activity.class);
				startActivity(intent);
			}
		});


		forgot = findViewById(R.id.forgot_text);
		forgot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(login_activity.this, forgot_password_activity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.btn_signin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (email.getText().toString().matches("")){
					Toast.makeText(login_activity.this, "Please Enter email", Toast.LENGTH_SHORT).show();
				}else{
					if (password.getText().toString().matches("")){
					Toast.makeText(login_activity.this, "Please Enter password", Toast.LENGTH_SHORT).show();
				}else{
					signInWithEmailAndPassword();
				}
				}
			}
		});
	}

	private void signInWithEmailAndPassword() {
		if (!email.getText().toString().matches("") && !password.getText().toString().matches("")){
			progressBar.setVisibility(View.VISIBLE);

			mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
					.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> task) {
							if (task.isSuccessful()) {
								// Sign in success, update UI with the signed-in user's information
								Log.d(TAG, "signInWithEmail:success");
								FirebaseUser user = mAuth.getCurrentUser();
								updateUI(user);
								if (user.isEmailVerified()) {
									Intent i = new Intent(login_activity.this, dashboard.class);
									startActivity(i);
									finish();
									progressBar.setVisibility(View.GONE);
								} else {
									FirebaseAuth.getInstance().signOut();
									Toast.makeText(login_activity.this, "Please Verify your email", Toast.LENGTH_SHORT).show();
									progressBar.setVisibility(View.GONE);
								}
							} else {
								// If sign in fails, display a message to the user.
								Log.w(TAG, "signInWithEmail:failure", task.getException());
								Toast.makeText(login_activity.this, "Please Enter Vaild Email/Password",
										Toast.LENGTH_SHORT).show();
								updateUI(null);
								progressBar.setVisibility(View.GONE);
							}

							// ...
						}
					});
		}else{
			Toast.makeText(login_activity.this, "Please Enter details", Toast.LENGTH_SHORT).show();
		}
	}

	private void updateUI(FirebaseUser u) {
		user = u;
	}
}
