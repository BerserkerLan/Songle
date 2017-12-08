package com.edu.s1572691.songle.songle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
        //Initialize first level and song no matter if user has already finished it
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
        placemarkers = gson.fromJson(json, KMLPlacemarkers.class); //Parses the Placemarkers to a Class

        //Places all collected words for that song in the ArrayList words
        placeMarkersList = placemarkers.getKml().getDocument().getPlacemark();
        //Begin Database usage
        wordsDatabase = openOrCreateDatabase("Words",Context.MODE_PRIVATE,null);
        wordsDatabase.execSQL("CREATE TABLE IF NOT EXISTS tab" + currentSongNo + "(wposs VARCHAR)");
        Cursor resultWords = wordsDatabase.rawQuery("SELECT * FROM tab" + currentSongNo,null);
        resultWords.moveToFirst();
        String word;
        words = new ArrayList<>();
        words.clear();
        long count = DatabaseUtils.longForQuery(wordsDatabase, "SELECT COUNT(*) FROM tab"  + currentSongNo,null);
        //Places all collected words for that song in the ArrayList words
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
        //End of database usage
        numOfMarkers = placeMarkersList.size();
    }
    public void parseXML()  {
        //Parses the XML for getting the number of songs in the XML
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Checks if User has permission for GPS

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
    }
    //Method that returns true if user is close enough to collect the marker from the current location
    public boolean closeEnoughToCollect(LatLng markerLocation) {
        double yourLat = mMap.getMyLocation().getLatitude();
        double yourLong = mMap.getMyLocation().getLongitude();
        double mLat = markerLocation.latitude;
        double mLong = markerLocation.longitude;

        return (distance(yourLat, yourLong, mLat, mLong) < 0.03);
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
    public void onBackPressed() {
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
        finish();
    }

    //Async Task to parse the placemarkers and place them on a map
    private class KMlTask extends AsyncTask<Void,String,KMLPlacemarkers> {

        @Override
        protected KMLPlacemarkers doInBackground(Void... voids) {

            getKMLStream();

            return placemarkers;
        }
        @Override
        protected void onPostExecute(KMLPlacemarkers mlayer) {
            //Clear any previous Placemarkers
            mMap.clear();
            String tempCoordinate;
            String d;
            String dd;
            Bitmap bmp;
            MarkerOptions markerOptions = new MarkerOptions();


            for (int i = 0; i < placeMarkersList.size(); i++) {
                //Only load the placemarkers of the words the user has not yet collected
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
            //Action for pickup marker
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    //Adds it into the database if user is close enough, and removes the marker
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
    //Returns true if there is internet connection
    public boolean hasInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
    //Toast to display when there is no internet
    public void noInternetToast() {
        Toast.makeText(getApplicationContext(), "Please connect to the internet to play the game",Toast.LENGTH_LONG).show();
    }

    //Task to parse the XML
    private class XMLTask extends AsyncTask<Void,String,SongParse> {

        @Override
        protected SongParse doInBackground(Void... voids) {
            parseXML();
            return theSongs;
        }
        @Override
        protected void onPostExecute(SongParse song) {

            //Sets up the spinners
            final String[] listOfSongs = new String[theSongs.getSong().getSong().length];
            for (int i = 0; i < listOfSongs.length; i++) {
                if (i <9) {
                    listOfSongs[i] =  "0" + (i+1);
                }
                else {
                    listOfSongs[i] = "" + (i+1);
                }
            }

            ArrayAdapter<String> songsAdapter = new ArrayAdapter<String>(MapsActivity.this, R.layout.dropdown_text_view, listOfSongs);
            songNo.setAdapter(songsAdapter);

            final String[] listOfLevels = {"1","2","3","4","5"};
            ArrayAdapter<String> levelsAdapter = new ArrayAdapter<String>(MapsActivity.this,R.layout.dropdown_text_view,listOfLevels);
            levelNo.setAdapter(levelsAdapter);

            levelNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!hasInternet()) {
                        noInternetToast();
                    }
                    else {
                        if (!currentLevel.equals(listOfLevels[position])) { //Only execute a new task if user changes Level
                            currentLevel = listOfLevels[position];
                            new KMlTask().execute();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            songNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!hasInternet()) {
                        noInternetToast();
                    }
                    else {
                        currentSongNo = listOfSongs[position];
                        new KMlTask().execute();
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }


}
