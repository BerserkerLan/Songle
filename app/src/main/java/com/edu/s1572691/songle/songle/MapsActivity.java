package com.edu.s1572691.songle.songle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import com.google.gson.Gson;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    InputStream inputStream;
    Spinner songNo;
    String kmlURL;
    KMLPlacemarkers placemarkers;
    ArrayList<Placemark> placeMarkersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        songNo = (Spinner) findViewById(R.id.songNo);
        final String[] listOfSongs = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18"};

        ArrayAdapter<String> songsAdapter = new ArrayAdapter<String>(MapsActivity.this, R.layout.dropdown_text_view, listOfSongs);
        songNo.setAdapter(songsAdapter);

        songNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kmlURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + listOfSongs[position] + "/map1.txt";
                new KMlTask().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        //SQLiteDatabase songsDB = openOrCreateDatabase("Songs",Context.MODE_APPEND, null);
        //songsDB.execSQL("CREATE TABLE IF NOT EXISTS CurrentSong(song VARCHAR, level VARCHAR);");
        String currentSong = "";
        String currentLevel = "";

        //songsDB.close();



        //kmlURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/01/map1.kml";

        //new KMlTask().execute();




    }

    public void getKMLStream() {

            //String kMlURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/01/map1.kml";
            URL oracle = null;
            InputStreamReader in = null;
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            BufferedReader reader = null;
            try {
                oracle = new URL(kmlURL);
                in = new InputStreamReader(oracle.openStream());
                reader = new BufferedReader(in);

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                in.close();

            } catch (MalformedURLException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }

            String xml = stringBuilder.toString();
        JSONObject jsonObject = null;
        try {
            jsonObject = XML.toJSONObject(xml);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        Gson gson = new Gson();
        placemarkers = gson.fromJson(json, KMLPlacemarkers.class);

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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


        //mMap.addMarker(new MarkerOptions().position(temp[0]).title("Current Location"));
    }
    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        connectionResult.getErrorMessage();

    }

    /*public boolean onMarkerClick(Marker marker) {

    }*/

    @Override
    public void onLocationChanged(Location location) {
        /*mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);*/

        //move map camera
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));



    }

    private class KMlTask extends AsyncTask<Void,String,KMLPlacemarkers> {

        @Override
        protected KMLPlacemarkers doInBackground(Void... voids) {

            getKMLStream();

            return placemarkers;
        }
        @Override
        protected void onPostExecute(KMLPlacemarkers mlayer) {

            mMap.clear();

            placeMarkersList = placemarkers.getKml().getDocument().getPlacemark();
            MarkerOptions markerOptions = new MarkerOptions();
            Bitmap bmp;
            String tempCoordinate = "";
            String d = "";
            String dd = "";

            for (int i = 0; i < placeMarkersList.size(); i++) {
                tempCoordinate= placeMarkersList.get(i).getPoint().getCoordinates();
                d = tempCoordinate.substring(0,tempCoordinate.indexOf(','));
                dd = tempCoordinate.substring(tempCoordinate.indexOf(',')+1,tempCoordinate.indexOf(',',tempCoordinate.indexOf(',')+1));
                try {
                    bmp = BitmapFactory.decodeStream(new URL(placemarkers.getKml().getDocument().getStyle().getIconStyle().getIcon().getHref()).openStream());
                    mMap.addMarker(markerOptions.position(new LatLng(Double.parseDouble(dd),Double.parseDouble(d)))
                            .title(placeMarkersList.get(i).getName())
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }

    }


}
