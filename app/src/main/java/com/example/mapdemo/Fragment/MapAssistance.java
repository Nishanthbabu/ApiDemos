package com.example.mapdemo.Fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapdemo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapAssistance extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationSource.OnLocationChangedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient = null;
    LocationRequest locationRequest = null;
    private MarkerOptions markerOptionsPoint1=null;
    private MarkerOptions markerOptionsPoint2=null;

    public TextView pointer = null;         ///// pointer.. to set locqation... click lisner is there for this
    public LinearLayout locationlayout = null;
    public ImageView buttonNavigate = null;
    private TextView textViewDistance=null;
    private TextView textViewTime=null;
    private LatLng latLngGlobalA=null;
    private LatLng latLngGlobalB=null;
    private SupportMapFragment mapFragment=null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_assistance, container, false);
        buttonNavigate = (ImageView) view.findViewById(R.id.navigateButton);
        textViewDistance=(TextView)view.findViewById(R.id.map_assistance_distance);
        textViewTime=(TextView)view.findViewById(R.id.map_assistance_time);
        textViewDistance.setText("please wait...");
        textViewTime.setText("please wait...");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_frag);
        if (mapFragment!=null)
        {
            mapFragment.getMapAsync(this);

        }
        googleApiClient = new GoogleApiClient.Builder(getActivity()) //Specify which Apis should attempt to connect
                .addApi(LocationServices.API)    /// here i use location service for map
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this) //Registers a listener to receive connection events from this GoogleApiClient.
                .addOnConnectionFailedListener(this) //Adds a listener to register to receive connection failed events from this GoogleApiClient.
                .build();  //Builds a new GoogleApiClient object for communicating with the Google APIs.
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Please give location permission", Toast.LENGTH_SHORT).show();
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
//        //////////// change here with asyncTask classssss.....
//        int a = 0;
//        Bundle bundle = getArguments();
//        if (a == 0) {////////////// order picked up
//            new FetchAddress().execute(bundle.getString("ORDER_ID") + "-" + "1");  //// 1 means order is picked up
//
//        } else {
//            /////////////////// order not p[icked up
//            new FetchAddress().execute(bundle.getString("ORDER_ID") + "-" + "0");  //// 0 means order is not picked up
//        }
//        Bundle bundle=getArguments();
//        System.out.println("kkkkkkkkk-------ORDER_ID---"+bundle.getString("ORDER_ID"));
//        new checkOrderStatus().execute(bundle.getString("ORDER_ID"));
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latLngGlobalA!=null && latLngGlobalB!=null)
                {
                    String uri = String.format(Locale.ENGLISH,
                            "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", latLngGlobalA.latitude,
                            latLngGlobalA.longitude, "Starting Point", latLngGlobalB.latitude, latLngGlobalB.longitude, "Ending Point");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Map is not ready.... .", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), ""+connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {


    }



    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

















}
