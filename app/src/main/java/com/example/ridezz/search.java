package com.example.ridezz;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class search extends Fragment {

    private Button pick_up_location,drop_off_location,submit_search_ride,declineRide,acceptRide;
    private TextView dateTV,timeTV,driverName,driverNum,detailText,vehicleNumSearch,driverBio;
    private String dateToSearchString,dateToSearchTemp;
    private FirebaseUser user;
    private GeoPoint geoPointPickUpLocationRider,geoPointDropOffLocationRider;
    private Boolean driverPickUpLocationMatch = false;
    private String driverId;
    private GeoPoint geoPointDropOffLocationDriver;
    private String timeToSearchString,timeToSearchTemp;
    private Date dateToSearch;
    private EditText numOfPassangerSearch;
    private ImageView imageViewFound;
    private boolean RideFound = false;

    public search() {
        // Required empty public constructor
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    static search newInstance(String param1, String param2) {
        search fragment = new search();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dateTV = view.findViewById(R.id.dateandtime);
        timeTV = view.findViewById(R.id.datetime);
        numOfPassangerSearch = view.findViewById(R.id.numOfPassangerSearch);
        submit_search_ride = view.findViewById(R.id.submit_search_ride);
        user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        pick_up_location = view.findViewById(R.id.tb_search_1);
        drop_off_location = view.findViewById(R.id.tb_search_2);
        imageViewFound = view.findViewById(R.id.imageViewFound);
        driverName = view.findViewById(R.id.driverName);
        driverNum = view.findViewById(R.id.driverNum);
        detailText = view.findViewById(R.id.detailText);
        declineRide = view.findViewById(R.id.decilneRide);
        acceptRide = view.findViewById(R.id.acceptRide);
        vehicleNumSearch = view.findViewById(R.id.vehicleNumSearch);
        driverBio = view.findViewById(R.id.driverBio);

        pick_up_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),search_ride_map.class);
                i.putExtra("text_field","search_pick_up_location");
                startActivity(i);
            }
        });
        drop_off_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),search_ride_map.class);
                i.putExtra("text_field","search_drop_off_location");
                startActivity(i);
            }
        });
        view.findViewById(R.id.dateandtime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });
        view.findViewById(R.id.datetime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeButton();
            }
        });

        db.collection("ride_live").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    Toast.makeText(getActivity(), "You Already offered ride", Toast.LENGTH_SHORT).show();
                }else{
                            db.collection("ride_found_live").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                driverId = documentSnapshot.getString("driver_id");
                                final String[] vehicleNumber = new String[1];
                                final int[] numOfPassanger = new int[1];
                                db.collection("ride_live").document(driverId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String dateToOfferString = documentSnapshot.getString("date");
                                            String timeToOfferString = documentSnapshot.getString("time");
                                            numOfPassanger[0] = Integer.parseInt(String.valueOf(Objects.requireNonNull(documentSnapshot.get("num_of_passanger"))));
                                            vehicleNumber[0] = documentSnapshot.getString("vehicle_number");
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        db.collection("users").document(driverId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()){
                                                    String driverNameFirst = documentSnapshot.getString("first_name");
                                                    String driverNameLast = documentSnapshot.getString("last_name");
                                                    String driverNumber = documentSnapshot.getString("phone_num");

                                                    if (documentSnapshot.getString("bio") != null) {
                                                        String bio = String.valueOf(documentSnapshot.get("bio"));
                                                        driverBio.setText("Bio:" + String.valueOf(bio));
                                                        driverBio.setVisibility(View.VISIBLE);
                                                    }

                                                    driverName.setText("Name: "+driverNameFirst+" "+driverNameLast);
                                                    driverNum.setText("Number: "+driverNumber);
                                                    vehicleNumSearch.setText("Vehicle Number: "+ vehicleNumber[0]);
                                                    driverName.setVisibility(View.VISIBLE);
                                                    driverNum.setVisibility(View.VISIBLE);
                                                    detailText.setVisibility(View.VISIBLE);
                                                    declineRide.setVisibility(View.VISIBLE);
                                                    acceptRide.setVisibility(View.VISIBLE);
                                                    vehicleNumSearch.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                                    }
                                });

                                submit_search_ride.setEnabled(true);
                                submit_search_ride.setText("Cancel");
                                RideFound = true;
                            }else{
                                pick_up_location.setEnabled(true);
                                drop_off_location.setEnabled(true);
                                dateTV.setEnabled(true);
                                timeTV.setEnabled(true);
                                numOfPassangerSearch.setEnabled(true);
                                submit_search_ride.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });

        final HashMap<String,Object> data = new HashMap<>();
        db.collection("users").document(user.getUid())
                .collection("information")
                .document("search_location_text_field").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.get("search_pick_up_location") != null) {
                        String temp = String.valueOf(documentSnapshot.get("search_pick_up_location"));
                        pick_up_location.setText(temp);
                    }
                    if (documentSnapshot.get("search_drop_off_location") != null) {
                        String temp = String.valueOf(documentSnapshot.get("search_drop_off_location"));
                        drop_off_location.setText(temp);
                    }
                }
            }
        });
        view.findViewById(R.id.submit_search_ride).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RideFound) {
                    pick_up_location.setEnabled(true);
                    drop_off_location.setEnabled(true);
                    timeTV.setEnabled(true);
                    dateTV.setEnabled(true);
                    numOfPassangerSearch.setEnabled(true);
                    submit_search_ride.setText("Search");
                    imageViewFound.setVisibility(View.INVISIBLE);
                    RideFound = false;
                    driverName.setVisibility(View.INVISIBLE);
                    driverNum.setVisibility(View.INVISIBLE);
                    detailText.setVisibility(View.INVISIBLE);
                    declineRide.setVisibility(View.INVISIBLE);
                    acceptRide.setVisibility(View.INVISIBLE);
                    vehicleNumSearch.setVisibility(View.INVISIBLE);
                    driverBio.setVisibility(View.INVISIBLE);


                    db.collection("ride_found_live").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                }else{
                final HashMap<String, Object> temp = new HashMap<>();
                dateToSearchTemp = dateToSearchString;
                timeToSearchTemp = timeToSearchString;
                db.collection("users").document(user.getUid())
                        .collection("information")
                        .document("search_location_text_field").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            geoPointPickUpLocationRider = documentSnapshot.getGeoPoint("search_pick_up_location");
                            geoPointDropOffLocationRider = documentSnapshot.getGeoPoint("search_drop_off_location");
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CollectionReference docRefPickUp = FirebaseFirestore.getInstance().collection("ride_live_pick_up_location");
                        GeoFirestore geoFirestorePickUp = new GeoFirestore(docRefPickUp);
                        GeoQuery geoQueryPickUp = geoFirestorePickUp.queryAtLocation(new GeoPoint(geoPointPickUpLocationRider.getLatitude(), geoPointPickUpLocationRider.getLongitude()), 1);
                        geoQueryPickUp.removeAllListeners();


                        geoQueryPickUp.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String s, GeoPoint geoPoint) {
                                if (!driverPickUpLocationMatch) {
                                    driverPickUpLocationMatch = true;
                                    driverId = s;
//                                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onKeyExited(String s) {

                            }

                            @Override
                            public void onKeyMoved(String s, GeoPoint geoPoint) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                if (!driverPickUpLocationMatch) {
                                    Toast.makeText(getActivity(), "Ride Not Found", Toast.LENGTH_SHORT).show();
                                }
                                if (driverPickUpLocationMatch) {
                                    DocumentReference docRefDropOff = FirebaseFirestore.getInstance().collection("ride_live_drop_off_location").document(driverId);
                                    docRefDropOff.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                geoPointDropOffLocationDriver = documentSnapshot.getGeoPoint("l");
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Location dropOffLocationDriver = new Location("driver_drop_off_location");
                                            dropOffLocationDriver.setLatitude(geoPointDropOffLocationDriver.getLatitude());
                                            dropOffLocationDriver.setLongitude(geoPointDropOffLocationDriver.getLongitude());

                                            Location dropOffLocationRider = new Location("rider_drop_off_location");
                                            dropOffLocationDriver.setLatitude(geoPointDropOffLocationRider.getLatitude());
                                            dropOffLocationDriver.setLongitude(geoPointDropOffLocationRider.getLongitude());
//                                            double distance = dropOffLocationDriver.distanceTo(dropOffLocationRider);
//                                            if (distance <= 1000){
//                                                Toast.makeText(getActivity(), "Ride Found", Toast.LENGTH_SHORT).show();
//                                            }else{
//                                                Toast.makeText(getActivity(), "Ride Not Found", Toast.LENGTH_SHORT).show();
//                                            }
                                            float[] distance = new float[1];
                                            Location.distanceBetween(geoPointDropOffLocationDriver.getLatitude(), geoPointDropOffLocationDriver.getLongitude(), geoPointDropOffLocationRider.getLatitude(), geoPointDropOffLocationRider.getLongitude(), distance);
                                            if (distance[0] <= 1000) {
                                                DocumentReference docRefDriver = FirebaseFirestore.getInstance().collection("ride_live").document(driverId);
                                                docRefDriver.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            String dateToOfferString = documentSnapshot.getString("date");
                                                            String timeToOfferString = documentSnapshot.getString("time");
                                                            int numOfPassanger = Integer.parseInt(String.valueOf(Objects.requireNonNull(documentSnapshot.get("num_of_passanger"))));
                                                            final String vehicleNumber = documentSnapshot.getString("vehicle_number");

                                                            SimpleDateFormat sdfSearch = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                                                            try {
                                                                dateToSearch = sdfSearch.parse(dateToSearchTemp.trim() + " " + timeToSearchTemp.trim());
                                                                sdfSearch.setTimeZone(TimeZone.getTimeZone("IST"));
                                                                sdfSearch.format(dateToSearch);
                                                            } catch (ParseException e) {
                                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }

                                                            SimpleDateFormat sdfOffer = new SimpleDateFormat("yyyy-MM-dd h:mm a");

                                                            try {
                                                                Date dateToOffer = sdfOffer.parse(dateToOfferString + " " + timeToOfferString);
                                                                sdfOffer.setTimeZone(TimeZone.getTimeZone("IST"));
                                                                sdfOffer.format(dateToOffer);
//                                                                Toast.makeText(getActivity(), sdf.format(dateToOffer), Toast.LENGTH_SHORT).show();
//                                                                Toast.makeText(getActivity(), dateToSearchTemp, Toast.LENGTH_SHORT).show();

//                                                                Toast.makeText(getActivity(), String.valueOf(dateToOffer.compareTo(dateToSearch))+" "+dateToSearch.toString()+" "+dateToOffer.toString(), Toast.LENGTH_SHORT).show();

                                                                if (dateToOffer.after(dateToSearch) || dateToOffer.compareTo(dateToSearch) == 0) {
                                                                    if (numOfPassanger >= Integer.parseInt(numOfPassangerSearch.getText().toString().trim())) {
                                                                        pick_up_location.setEnabled(false);
                                                                        drop_off_location.setEnabled(false);
                                                                        timeTV.setEnabled(false);
                                                                        dateTV.setEnabled(false);
                                                                        numOfPassangerSearch.setEnabled(false);
                                                                        submit_search_ride.setText("Cancel");
                                                                        imageViewFound.setVisibility(View.VISIBLE);
                                                                        RideFound = true;

                                                                        final HashMap<String, Object> driverIdMap = new HashMap<>();
                                                                        driverIdMap.put("driver_id",driverId);
                                                                        db.collection("ride_found_live").document(user.getUid()).set(driverIdMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                            }
                                                                        });

                                                                        db.collection("users").document(driverId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                if (documentSnapshot.exists()){
                                                                                    String driverNameFirst = documentSnapshot.getString("first_name");
                                                                                    String driverNameLast = documentSnapshot.getString("last_name");
                                                                                    String driverNumber = documentSnapshot.getString("phone_num");

                                                                                    if (documentSnapshot.getString("bio") != null) {
                                                                                        String bio = String.valueOf(documentSnapshot.get("bio"));
                                                                                        driverBio.setText("Bio:" + String.valueOf(bio));
                                                                                        driverBio.setVisibility(View.VISIBLE);
                                                                                    }

                                                                                    driverName.setText("Name: "+driverNameFirst+" "+driverNameLast);
                                                                                    driverNum.setText("Number: "+driverNumber);
                                                                                    vehicleNumSearch.setText("Vehicle Number: "+vehicleNumber);
                                                                                    driverName.setVisibility(View.VISIBLE);
                                                                                    driverNum.setVisibility(View.VISIBLE);
                                                                                    detailText.setVisibility(View.VISIBLE);
                                                                                    declineRide.setVisibility(View.VISIBLE);
                                                                                    acceptRide.setVisibility(View.VISIBLE);
                                                                                    vehicleNumSearch.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }
                                                                        });

                                                                        Toast.makeText(getActivity(), "driver found", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(getActivity(), "Ride Not Found", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getActivity(), "Ride Not Found", Toast.LENGTH_SHORT).show();
                                                                }

                                                            } catch (ParseException e) {
                                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    }
                                                });
//                                                Toast.makeText(getActivity(), driverId, Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(getActivity(), "Ride Not Found", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onGeoQueryError(Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        }
        });
    }
    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("yyyy-MM-dd", calendar1).toString();

//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                sdf.setTimeZone(TimeZone.getTimeZone("IST"));
//                try {
//                    dateToSearch = sdf.parse(year+"-"+month+"-"+date);
//                } catch (ParseException e) {
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }

//                dateToSearchString = year+"-"+month+"-"+date;
                dateToSearchString = DateFormat.format("yyyy-MM-dd", calendar1).toString();
                Toast.makeText(getActivity(), dateToSearchString, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), is24HourFormat ? "1":"0", Toast.LENGTH_SHORT).show();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.i("TIME", "onTimeSet: " + hour + minute);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);
                timeToSearchString = DateFormat.format("h:mm a", calendar1).toString();
//                String[] timeTemp = timeToSearchString.split(" ");
//                if (timeTemp[1].matches("PM")){
//                   timeToSearchString = timeToSearchString.replace("PM","AM");
//                }else{
//                    timeToSearchString = timeToSearchString.replace("AM","PM");
//                }
                Toast.makeText(getActivity(), timeToSearchString, Toast.LENGTH_SHORT).show();
                timeTV.setText(timeToSearchString);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();

    }


}
