package com.example.ridezz;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoLocation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class offer_ride extends Fragment {


    Button pick_up_location,drop_off_location,submit_offer_ride;
    TextView dateTV,timeTV,numOfPassanger,vehicleNumber;
    Date dateToOffer;
    Boolean RideOffered = false;
    private FirebaseUser user;
    private GeoPoint geoPointPickUpLocation,geoPointDropOffLocation;
    private String dateToOfferString;
    private String timeToOfferString;

    public offer_ride() {
        // Required empty public constructor
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    static offer_ride newInstance(String param1, String param2) {
        offer_ride fragment = new offer_ride();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        pick_up_location = view.findViewById(R.id.tb_search_1);
        drop_off_location = view.findViewById(R.id.tb_search_2);
        submit_offer_ride = view.findViewById(R.id.submit_offer_ride);
        numOfPassanger = view.findViewById(R.id.numOfPassanger);
        vehicleNumber = view.findViewById(R.id.vehicleNumber);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        pick_up_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),offer_ride_map.class);
                i.putExtra("text_field","offer_pick_up_location");
                startActivity(i);
            }
        });


        drop_off_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),offer_ride_map.class);
                i.putExtra("text_field","offer_drop_off_location");
                startActivity(i);
            }
        });


        dateTV = view.findViewById(R.id.offer_select_date);
        timeTV = view.findViewById(R.id.offer_select_time);

        view.findViewById(R.id.offer_select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

        view.findViewById(R.id.offer_select_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeButton();
            }
        });



        db.collection("ride_live").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    RideOffered = true;
                    submit_offer_ride.setText("Cancel");
                    numOfPassanger.setText(String.valueOf(documentSnapshot.get("num_of_passanger")));
                    dateTV.setText(documentSnapshot.getString("date"));
                    timeTV.setText(documentSnapshot.getString("time"));
                    vehicleNumber.setText(documentSnapshot.getString("vehicle_number"));
                    submit_offer_ride.setEnabled(true);
                }else{
                    db.collection("ride_found_live").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                Toast.makeText(getActivity(), "you Already Found ride", Toast.LENGTH_SHORT).show();
                            }else{
                                RideOffered = false;
                                pick_up_location.setEnabled(true);
                                drop_off_location.setEnabled(true);
                                numOfPassanger.setEnabled(true);
                                vehicleNumber.setEnabled(true);
                                dateTV.setEnabled(true);
                                timeTV.setEnabled(true);
                                submit_offer_ride.setEnabled(true);
                            }
                        }
                    });

                }
            }
        });



        final HashMap<String,Object> data = new HashMap<>();
        db.collection("users").document(user.getUid())
                .collection("information")
                .document("offer_location_text_field").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.get("offer_pick_up_location") != null) {
                        String temp = String.valueOf(documentSnapshot.get("offer_pick_up_location"));
                        pick_up_location.setText(temp);
                    }
                    if (documentSnapshot.get("offer_drop_off_location") != null) {
                        String temp = String.valueOf(documentSnapshot.get("offer_drop_off_location"));
                        drop_off_location.setText(temp);
                    }
                }
            }
        });
        view.findViewById(R.id.submit_offer_ride).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RideOffered) {
                    RideOffered = false;
                    pick_up_location.setEnabled(true);
                    drop_off_location.setEnabled(true);
                    timeTV.setEnabled(true);
                    dateTV.setEnabled(true);
                    numOfPassanger.setEnabled(true);
                    vehicleNumber.setEnabled(true);
                    submit_offer_ride.setText("Submit");

                    db.collection("ride_live").document(user.getUid()).delete();
                    db.collection("ride_live_pick_up_location").document(user.getUid()).delete();
                    db.collection("ride_live_drop_off_location").document(user.getUid()).delete();
                } else {
                    if (pick_up_location.getText() == null || drop_off_location.getText() == null || numOfPassanger.getText() == null || vehicleNumber.getText() == null || dateTV.getText() == null || timeTV.getText() == null) {
                        Toast.makeText(getActivity(), "Please Insert All information", Toast.LENGTH_SHORT).show();
                    } else {
                        final Boolean[] phoneNumEntered = {false};
                        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    if (documentSnapshot.get("phone_num") != null){
                                        phoneNumEntered[0] = true;
                                    }else {
                                        Toast.makeText(getActivity(), "Please Enter Phone Number In Profile", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (phoneNumEntered[0]){
                                    if (pick_up_location.getText() != null && drop_off_location.getText() != null && dateTV.getText() != null && timeTV.getText() != null) {
                                        final HashMap<String, Object> data_offer = new HashMap<>();
                                        db.collection("users").document(user.getUid())
                                                .collection("information")
                                                .document("offer_location_text_field").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {


                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
//                                data_offer.put("pick_up_location", documentSnapshot.getGeoPoint("offer_pick_up_location"));
//                                data_offer.put("drop_off_location", documentSnapshot.get("offer_drop_off_location"));
//                                data_offer.put("date", dateTV.getText());
//                                data_offer.put("time", timeTV.getText());
                                                    data_offer.put("num_of_passanger", Integer.parseInt(String.valueOf(numOfPassanger.getText())));
                                                    data_offer.put("vehicle_number", vehicleNumber.getText().toString());
                                                    data_offer.put("date", dateToOfferString);
                                                    data_offer.put("time", timeToOfferString);
                                                    geoPointPickUpLocation = documentSnapshot.getGeoPoint("offer_pick_up_location");
                                                    geoPointDropOffLocation = documentSnapshot.getGeoPoint("offer_drop_off_location");
                                                }
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                db.collection("ride_live").document(user.getUid()).set(data_offer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(), "Ride Offered Successfully", Toast.LENGTH_SHORT).show();
                                                        RideOffered = true;
                                                        pick_up_location.setEnabled(false);
                                                        drop_off_location.setEnabled(false);
                                                        timeTV.setEnabled(false);
                                                        dateTV.setEnabled(false);
                                                        numOfPassanger.setEnabled(false);
                                                        vehicleNumber.setEnabled(false);
                                                        submit_offer_ride.setText("Cancel");

                                                    }
                                                });
                                                CollectionReference pick_up_location_collection = FirebaseFirestore.getInstance().collection("ride_live_pick_up_location");
                                                GeoFirestore geoFirestorePickUpLocation = new GeoFirestore(pick_up_location_collection);
                                                GeoFirestore.CompletionCallback CompletionCallbackcallBack = new GeoFirestore.CompletionCallback() {
                                                    @Override
                                                    public void onComplete(Exception e) {
                                                        Toast.makeText(getActivity(), e != null ? e.getMessage() : "Ride Offered Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                };
                                                geoFirestorePickUpLocation.setLocation(user.getUid(), new GeoPoint(geoPointPickUpLocation.getLatitude(), geoPointPickUpLocation.getLongitude()), CompletionCallbackcallBack);

                                                CollectionReference drop_of_location_collection = FirebaseFirestore.getInstance().collection("ride_live_drop_off_location");
                                                GeoFirestore geoFirestoreDropOffLocation = new GeoFirestore(drop_of_location_collection);
                                                geoFirestoreDropOffLocation.setLocation(user.getUid(), new GeoPoint(geoPointDropOffLocation.getLatitude(), geoPointDropOffLocation.getLongitude()));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                                }
                            }
                        });

                    }
                }
            }
        });
    }
    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        final int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("yyyy-MM-dd", calendar1).toString();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateToOffer = sdf.parse(year+"-"+month+"-"+date);
                    sdf.setTimeZone(TimeZone.getTimeZone("IST"));
                    dateToOfferString = DateFormat.format("yyyy-MM-dd", calendar1).toString();
                } catch (Exception e) {
                    Toast.makeText(getActivity(),  e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                dateTV.setText(dateText);
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void handleTimeButton() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.i("TIME", "onTimeSet: " + hour + minute);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);
                timeToOfferString = DateFormat.format("h:mm a", calendar1).toString();
//                String[] timeTemp = timeToOfferString.split(" ");
//                if (timeTemp[1].matches("PM")){
//                    timeToOfferString = timeToOfferString.replace("PM","AM");
//                }else{
//                    timeToOfferString = timeToOfferString.replace("AM","PM");
//                }
                timeTV.setText(timeToOfferString);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();

    }


}
