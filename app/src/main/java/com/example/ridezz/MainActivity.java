package com.example.ridezz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
	Button login;
	Button signup;

	FirebaseUser user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null){
			Intent i = new Intent(MainActivity.this, dashboard.class);
			startActivity(i);
		}
		else{
			Intent i = new Intent(MainActivity.this, home.class);
			startActivity(i);
		}

//		login = findViewById(R.id.login_btn);
//		login.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(MainActivity.this, login_activity.class);
//				startActivity(intent);
//			}
//		});
//
//		signup = findViewById(R.id.signup_btn);
//		signup.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new  Intent(MainActivity.this, signup_activity.class);
//				startActivity(intent);
//			}
//		});
	}
}
