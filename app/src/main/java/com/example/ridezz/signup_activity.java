package com.example.ridezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class signup_activity extends AppCompatActivity {
	ProgressBar progressBar;
	TextView login_text;
	EditText phoneno;
	EditText first;
	EditText last;
	EditText email;
	EditText password;
	String GenderSelected = "Male";
	private final static String TAG = "SingUp Activity";
	FirebaseAuth mAuth;
	private FirebaseUser user;
	Spinner spinner;
	String[] gender;
	View contextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_activity);
		contextView = findViewById(android.R.id.content);
		gender = new String[]{"Male", "Female", "Others"};

		progressBar = findViewById(R.id.signUp_progressBar);
		mAuth = FirebaseAuth.getInstance();
		spinner = findViewById(R.id.spinner);
		phoneno= findViewById(R.id.tb_phoneno);
		first=findViewById(R.id.tb_first);
		last=findViewById(R.id.tb_last);
		email=findViewById(R.id.tb_email);
		password=findViewById(R.id.tb_pass);
		login_text = findViewById(R.id.login_text);
		//Array Adapter For Spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.gender, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		//Get Gender From Spinner
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				GenderSelected = gender[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		//SignUp Btn Listener
		findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (first.getText().toString().trim().matches("")){
					Snackbar.make(contextView, R.string.first_name_error, Snackbar.LENGTH_SHORT).show();
				}else if(last.getText().toString().trim().matches("")){
					Snackbar.make(contextView, R.string.last_name_error, Snackbar.LENGTH_SHORT).show();
				}else if(email.getText().toString().trim().matches("")){
					Snackbar.make(contextView, R.string.email_error, Snackbar.LENGTH_SHORT).show();
				}else if(password.getText().toString().trim().matches("")){
					Snackbar.make(contextView, R.string.pwd_error, Snackbar.LENGTH_SHORT).show();
				}else if(phoneno.getText().toString().trim().matches("")){
					Snackbar.make(contextView, R.string.phone_error, Snackbar.LENGTH_SHORT).show();
				}else if(password.getText().toString().trim().length()<=7){
					Snackbar.make(contextView, R.string.pwd_length_error, Snackbar.LENGTH_SHORT).show();
				}else if(!isValidEmail(email.getText().toString().trim())){
					Snackbar.make(contextView, R.string.email_invalid_error, Snackbar.LENGTH_SHORT).show();
				}else {
					createUserWithEmailAndPassword();
				}
			}
		});
		//Already a user TextView Listener
		findViewById(R.id.login_text).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(signup_activity.this, login_activity.class);
				startActivity(i);
			}
		});


	}
	public static boolean isValidEmail(CharSequence target) {
		return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
	}
	//Creates User Account with Email And Password
	private void createUserWithEmailAndPassword() {
		progressBar.setVisibility(View.VISIBLE);

		mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "createUserWithEmail:success");
							//Gets Logged in user Current User
							final FirebaseUser user = mAuth.getCurrentUser();
							final String UID = user.getUid();
							updateUI(user);
							user.sendEmailVerification()
									.addOnCompleteListener(new OnCompleteListener<Void>() {
										@Override
										public void onComplete(@NonNull Task<Void> task) {
											if (task.isSuccessful()) {
												Log.d(TAG, "Email sent.");
												String pN = "+91" + phoneno.getText().toString().trim();
												String F,L,E;
												F = first.getText().toString().trim();
												L = last.getText().toString().trim();
												E = email.getText().toString().trim();
												data d = new data(F,L,E,pN,GenderSelected);
												FirebaseFirestore db = FirebaseFirestore.getInstance();
												db.collection("users").document(UID).set(d).addOnSuccessListener(new OnSuccessListener<Void>() {
													@Override
													public void onSuccess(Void aVoid) {
														Snackbar.make(contextView,R.string.account_created, Snackbar.LENGTH_SHORT).show();
														FirebaseAuth.getInstance().signOut();
														Intent i = new Intent(signup_activity.this, MainActivity.class);
														startActivity(i);
														finish();
														progressBar.setVisibility(View.GONE);
													}
												}).addOnFailureListener(new OnFailureListener() {
													@Override
													public void onFailure(@NonNull Exception e) {
														user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
															@Override
															public void onSuccess(Void aVoid) {
																Snackbar.make(contextView,R.string.something_wrong, Snackbar.LENGTH_SHORT).show();
																Intent i = new Intent(signup_activity.this, MainActivity.class);
																startActivity(i);
																progressBar.setVisibility(View.GONE);
															}
														});
													}
												});

//
											}
										}
									});

						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "createUserWithEmail:failure", task.getException());
							Toast.makeText(signup_activity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
							updateUI(null);
							progressBar.setVisibility(View.GONE);
						}

						// ...
					}
				});
	}

	@Override
	public void onStart() {
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
		updateUI(currentUser);
	}

	private void updateUI(FirebaseUser currentUser) {
		user = currentUser;
	}
}
