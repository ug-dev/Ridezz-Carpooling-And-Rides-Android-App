package com.example.ridezz;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.CookieHandler;
import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class detail extends Fragment {

    private TextView DisplayPhone,DisplayEmail,DisplayBio;

    FirebaseAuth mAuth;
    private FirebaseUser user;
    private String TAG = "detail";

    public detail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
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
                    DisplayEmail.setText(temp.getEmail());
                    DisplayPhone.setText(temp.getPhone_num());
                    HashMap<String,Object> getData = new HashMap<>();
                    getData.put("bio",documentSnapshot.get("bio"));
                    DisplayBio.setText(Objects.requireNonNull(getData.get("bio")).toString());
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
    }
}
