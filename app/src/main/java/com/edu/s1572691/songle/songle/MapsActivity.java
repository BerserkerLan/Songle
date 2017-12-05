package com.edu.s1572691.songle.songle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Spinner songNo;
    Spinner levelNo;
    String kmlURL;
    String songURL;
    SQLiteDatabase wordsDatabase;
    String currentSongNo;
    ArrayList<String> words;
    int numOfMarkers;
    SongParse theSongs;
    KMLPlacemarkers placemarkers;
    String currentLevel;
    ArrayList<Placemark> placeMarkersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        songURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.txt";
        currentLevel="1";
        currentSongNo="01";
        songNo = (Spinner) findViewById(R.id.songNo);
        levelNo = (Spinner) findViewById(R.id.levelNo);
        new XMLTask().execute();
    }

    public void getKMLStream() {
        kmlURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + currentSongNo + "/map" + currentLevel + ".txt";
            URL oracle;
            InputStreamReader in;
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            BufferedReader reader;
            try {
                oracle = new URL(kmlURL);
                in = new InputStreamReader(oracle.openStream());
                reader = new BufferedReader(in);

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                in.close();

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
        assert jsonObject != null;
        String json = jsonObject.toString();
        Gson gson = new Gson();
        placemarkers = gson.fromJson(json, KMLPlacemarkers.class);
        placeMarkersList = placemarkers.getKml().getDocument().getPlacemark();
        wordsDatabase = openOrCreateDatabase("Words",Context.MODE_PRIVATE,null);
        wordsDatabase.execSQL("CREATE TABLE IF NOT EXISTS tab" + currentSongNo + "(wposs VARCHAR)");
        Cursor resultWords = wordsDatabase.rawQuery("SELECT * FROM tab" + currentSongNo,null);
        resultWords.moveToFirst();
        String word;
        words = new ArrayList<>();
        words.clear();
        long count = DatabaseUtils.longForQuery(wordsDatabase, "SELECT COUNT(*) FROM tab"  + currentSongNo,null);
        if (count>0) {
            word = resultWords.getString(resultWords.getColumnIndex("wposs"));
            words.add(word);
        }
        while (resultWords.moveToNext()) {
            word = resultWords.getString(resultWords.getColumnIndex("wposs"));
            words.add(word);
        }
        resultWords.close();
        wordsDatabase.close();
        numOfMarkers = placeMarkersList.size();
    }
    public void parseXML()  {
        URL oracle;
        InputStreamReader in;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader;
        try {
            oracle = new URL(songURL);
            in = new InputStreamReader(oracle.openStream());
            reader = new BufferedReader(in);

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();

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
        assert jsonObject != null;
        String json = jsonObject.toString();
        Gson gson = new Gson();
        theSongs = gson.fromJson(json, SongParse.class);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
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
        //Place current location marker
       /* LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);*/

        //move map camera
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));



    }
    public boolean closeEnoughToCollect(LatLng markerLocation) {
        double yourLat = mMap.getMyLocation().getLatitude();
        double yourLong = mMap.getMyLocation().getLongitude();
        double mLat = markerLocation.latitude;
        double mLong = markerLocation.longitude;

        return (distance(yourLat, yourLong, mLat, mLong) < 0.01);
    }

    //Method return distance b/w two points on map in KM
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
            String tempCoordinate;
            String d;
            String dd;
            Bitmap bmp;
            MarkerOptions markerOptions = new MarkerOptions();


            for (int i = 0; i < placeMarkersList.size(); i++) {
                if (!words.contains(placeMarkersList.get(i).getName())){
                    tempCoordinate = placeMarkersList.get(i).getPoint().getCoordinates();
                    d = tempCoordinate.substring(0, tempCoordinate.indexOf(','));
                    dd = tempCoordinate.substring(tempCoordinate.indexOf(',') + 1, tempCoordinate.indexOf(',', tempCoordinate.indexOf(',') + 1));
                    try {
                        bmp = BitmapFactory.decodeStream(new URL("http://maps.google.com/mapfiles/kml/paddle/wht-blank.png").openStream());
                        mMap.addMarker(markerOptions.position(new LatLng(Double.parseDouble(dd), Double.parseDouble(d)))
                                .title(placeMarkersList.get(i).getName())
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    if (closeEnoughToCollect(marker.getPosition())) {
                        marker.remove();
                        Toast.makeText(getApplicationContext(), "Collected " + marker.getTitle(), Toast.LENGTH_SHORT).show();
                        wordsDatabase = openOrCreateDatabase("Words", Context.MODE_PRIVATE, null);
                        wordsDatabase.execSQL("CREATE TABLE IF NOT EXISTS tab" + currentSongNo + "(wposs VARCHAR)");
                        wordsDatabase.execSQL("INSERT INTO tab" + currentSongNo + "(wposs) VALUES('" + marker.getTitle() + "');");
                        words.add(marker.getTitle());
                        wordsDatabase.close();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Not close enough to pick up this word!",Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });


        }

    }

    private class XMLTask extends AsyncTask<Void,String,SongParse> {

        @Override
        protected SongParse doInBackground(Void... voids) {
            parseXML();
            return theSongs;
        }
        @Override
        protected void onPostExecute(SongParse song) {

            final String[] listOfSongs = new String[theSongs.getSong().getSong().length];
            for (int i = 0; i < listOfSongs.length; i++) {
                if (i <9) {
                    listOfSongs[i] =  "0" + (i+1);
                }
                else {
                    listOfSongs[i] = "" + (i+1);
                }
            }

            ArrayAdapter<String> songsAdapter = new ArrayAdapter<>(MapsActivity.this, R.layout.dropdown_text_view, listOfSongs);
            songNo.setAdapter(songsAdapter);

            final String[] listOfLevels = {"1","2","3","4","5"};
            ArrayAdapter<String> levelsAdapter = new ArrayAdapter<>(MapsActivity.this,R.layout.dropdown_text_view,listOfLevels);
            levelNo.setAdapter(levelsAdapter);

            levelNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!currentLevel.equals(listOfLevels[position])) {
                        currentLevel = listOfLevels[position];
                        new KMlTask().execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            songNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentSongNo = listOfSongs[position];
                        new KMlTask().execute();


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }


}
