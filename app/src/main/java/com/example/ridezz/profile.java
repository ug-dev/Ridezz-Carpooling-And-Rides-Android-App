package com.example.ridezz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;


public class profile extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tab1,tab2;
    public PagerAdapter pagerAdapter;

    Activity referenceActivity;
    View parentHolder;

    private TextView DisplayPhone,DisplayEmail,DisplayBio;

    FirebaseAuth mAuth;
    private FirebaseUser user;
    private String TAG = "detail";

    private TextView address;

    TextView signout;
    public profile() {
        // Required empty public constructor
    }
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    static profile newInstance(String param1, String param2) {
        profile fragment = new profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        referenceActivity = getActivity();
        parentHolder = inflater.inflate(R.layout.fragment_profile, container,
                false);

//        signout = (TextView) parentHolder.findViewById(R.id.logOut);

//        signout.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent i = new Intent(profile.this,home.class);
//
//            }
//        });
        return parentHolder;
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DisplayEmail = view.findViewById(R.id.DisplayEmail);
        DisplayPhone = view.findViewById((R.id.DisplayPhone));
        DisplayBio = view.findViewById((R.id.BIO));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("address") != null) {
                    data temp = documentSnapshot.toObject(data.class);
                    if (temp.getEmail() != null) {
                        DisplayEmail.setText(temp.getEmail());
                    }
                    if (temp.getPhone_num() != null) {
                        DisplayPhone.setText(temp.getPhone_num());
                    }
                    HashMap<String,Object> getData = new HashMap<>();
                    getData.put("bio",documentSnapshot.get("bio"));
                    if (documentSnapshot.get("bio") != null) {
                        DisplayBio.setText(Objects.requireNonNull(getData.get("bio")).toString());
                    }
                }
            }
        });
        view.findViewById(R.id.BIO).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),change_bio.class);
                startActivity(i);
            }
        });
        DisplayPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),change_phone.class);
                startActivity(i);
            }
        });

        view.findViewById(R.id.change_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),change_address.class);
                startActivity(i);
            }
        });
        TextView change_pwd = view.findViewById(R.id.change_pwd);
        change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),change_password.class);
                startActivity(i);
            }
        });

        view.findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(),home.class);
                startActivity(i);
            }
        });


//        tabLayout = view.findViewById(R.id.tabLayout);
//
//
//        tab1 = view.findViewById(R.id.detailTab);
//        tab2 = view.findViewById(R.id.accountTab);
//        viewPager = view.findViewById(R.id.viewPager);
//        pagerAdapter = new PageAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setOffscreenPageLimit(2);
//        viewPager.setAdapter(pagerAdapter);
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//
//                if (tab.getPosition() == 0){
//                    pagerAdapter.notifyDataSetChanged();
//                }
//                else if(tab.getPosition() == 1){
//                    pagerAdapter.notifyDataSetChanged();
//                }
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

}
