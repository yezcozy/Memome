package com.mad.memome.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.mad.memome.R;

import java.util.List;

/*import com.google.android.gms.maps.GeoPoint;*/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng myPosition;
    double latitude ;
Location getlocation;
    // Getting longitude of the current location
    double longitude ;
    String location;
    LatLng latLng;
    GeoPoint gg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();

        location = intent.getStringExtra("Location_value");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gg=getLocationFromAddress(location);
        latitude=gg.getLatitudeE6();
        longitude=gg.getLongitudeE6();

     latLng = new LatLng(latitude, longitude);

        myPosition = new LatLng(latitude, longitude);
    }

    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 ;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Log.d("Loc","content: "+ address.get(0).toString());
            Log.d("Loc","number: "+ address.size());
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
           Log.d("Loc","latitude: "+ location.getLatitude());
            Log.d("Loc","longtitude: "+ location.getLongitude());
            p1 = new GeoPoint( (int)(location.getLatitude()),
                    (int) (location.getLongitude()));
            Log.d("Loc","11latitude: "+ (int)(location.getLatitude() * 1E6));
            Log.d("Loc","11longtitude: "+ (int) (location.getLongitude() * 1E6));
            return p1;
        }catch (Exception e){}
return null;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        mMap.addMarker(new MarkerOptions().position(myPosition).title("Location: "+location));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Getting Current Location


    /*    if (location != null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            myPosition = new LatLng(latitude, longitude);

            googleMap.addMarker(new MarkerOptions().position(myPosition).title("Start"));
        }*/

       /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}
