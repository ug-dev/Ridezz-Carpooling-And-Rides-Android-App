package com.example.ridezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        Intent i = getIntent();
        if (Objects.equals(i.getStringExtra("Selected"), "offer")){
            openFragment(offer_ride.newInstance("", ""));
            bottomNavigationView.getMenu().findItem(R.id.menu_offer).setChecked(true);
        }else if (Objects.equals(i.getStringExtra("Selected"), "search")){
            openFragment(search.newInstance("", ""));
            bottomNavigationView.getMenu().findItem(R.id.menu_serach).setChecked(true);
        }
        else{
            openFragment(search.newInstance("", ""));
        }

    }

public void openFragment(Fragment fragment) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.container, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
}

BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.menu_your_ridezz:
//                        openFragment(your_ridezz.newInstance("", ""));
//                        return true;
                    case R.id.menu_serach:
                        openFragment(search.newInstance("", ""));
                        return true;
                    case R.id.menu_offer:
                        openFragment(offer_ride.newInstance("", ""));
                        return true;
//                    case R.id.menu_inbox:
//                        openFragment(inbox.newInstance("", ""));
//                        return true;
                    case R.id.menu_profile:
                        openFragment(profile.newInstance("", ""));
                        return true;
                }
                return false;
            }
        };
}
