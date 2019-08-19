package com.example.istiaque.bustracking;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class passanger extends FragmentActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener {


    GoogleMap mMap;

    double Lat, Long;

    private Marker driverMarker;
    private SupportMapFragment mapFragment;
    private Location location;

    private String Busname;
    private String busname;

    private String key;

    private DatabaseReference mref;

    Map<String,Integer> map = new HashMap<String, Integer>();
    List<Marker> markerList = new ArrayList<Marker>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passanger);

        permissionrequest();

        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.bus_arrays,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner.setPrompt("Choose a Bus");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.1778, 89.1801), 18));


    }

    public void permissionrequest(){
        if (ActivityCompat.checkSelfPermission(passanger.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(passanger.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(passanger.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mref = FirebaseDatabase.getInstance().getReference().child("DriversLocation");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot driversnapshot : dataSnapshot.getChildren()){
                    Driverinfo driverinfo = driversnapshot.getValue(Driverinfo.class);
                    key = driverinfo.getKey();
                    Lat = driverinfo.getLatitude();
                    Long = driverinfo.getLongitude();
                    Busname = driverinfo.getBusname();
                    //Toast.makeText(passanger.this,"Ami" + key + " " + Lat + "_" + Long + " " +  busname,Toast.LENGTH_SHORT).show();
                    if(map.containsKey(key)){
                        setmarker(1);
                    }
                    else{
                        setmarker(0);
                        //Toast.makeText(passanger.this,"I'm Done",Toast.LENGTH_SHORT).show();
                        map.put(key,1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return;
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        busname = adapterView.getItemAtPosition(position).toString();

        //Toast.makeText(this,"Its Busname " + busname,Toast.LENGTH_SHORT).show();
        mref = FirebaseDatabase.getInstance().getReference().child("DriversLocation");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot driversnapshot : dataSnapshot.getChildren()){
                    Driverinfo driverinfo = driversnapshot.getValue(Driverinfo.class);
                    key = driverinfo.getKey();
                    Lat = driverinfo.getLatitude();
                    Long = driverinfo.getLongitude();
                    Busname = driverinfo.getBusname();
                    //Toast.makeText(passanger.this,"Ami" + key + " " + Lat + "_" + Long + " " +  busname,Toast.LENGTH_SHORT).show();
                    if(Busname.equals(busname)){
                        if(map.containsKey(key)){
                            setmarker(1);
                        }
                        else{
                            setmarker(0);
                            //Toast.makeText(passanger.this,"I'm Done",Toast.LENGTH_SHORT).show();
                            map.put(key,1);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setmarker(int typ)
    {
        LatLng marker = new LatLng(Lat, Long);
        if(typ == 1){
            for(int i = 0;i < markerList.size();i++) {
                Marker tempmarker = markerList.get(i);
                if (tempmarker.getTag().equals(key)) {
                    if((tempmarker.getPosition().latitude - Lat) != 0 || (tempmarker.getPosition().longitude - Long) != 0){
                        driverMarker = tempmarker;
                        animateCar(marker);
                        //Toast.makeText(passanger.this,"New " + driverMarker.getPosition().latitude + "_" + driverMarker.getPosition().longitude,Toast.LENGTH_SHORT).show();
                        markerList.set(i,driverMarker);
                    }
                    /*boolean contains = mMap.getProjection()
                            .getVisibleRegion()
                            .latLngBounds
                            .contains(marker);
                    if (!contains) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker));
                    }*/
                    break;
                }
            }
        }
        else {
            driverMarker = mMap.addMarker(new MarkerOptions().position(marker).flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
            driverMarker.setTag(key);
            driverMarker.setTitle(Busname);
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 18));
            markerList.add(driverMarker);
        }
    }

    private void animateCar(LatLng destination) {
        final LatLng startPosition = driverMarker.getPosition();
        final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);

        final float startrotation = driverMarker.getRotation();

        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000); // duration 5 seconds
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float v = animation.getAnimatedFraction();
                    LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                    driverMarker.setPosition(newPosition);
                    driverMarker.setRotation(computeRotation(v,startrotation,getBearing(startPosition,endPosition)));
                    //Toast.makeText(passanger.this,"Asi to",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(passanger.this,startPosition.latitude + "_" + startPosition.longitude + " " + endPosition.latitude + "_" + endPosition.longitude,Toast.LENGTH_SHORT).show();
                    //driverMarker.setTag(key);
                } catch (Exception ex) {
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.start();
    }

    private static float computeRotation(float fraction,float start,float end){
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }
}
