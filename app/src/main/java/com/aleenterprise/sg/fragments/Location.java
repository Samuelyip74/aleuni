package com.aleenterprise.sg.fragments;

import android.Manifest;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.aleenterprise.sg.R;
import com.aleenterprise.sg.services.IndoorProvider;
import com.aleenterprise.sg.services.LocationClient;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapView;


import io.indoorlocation.gps.GPSIndoorLocationProvider;
import io.mapwize.mapwizeformapbox.api.Api;
import io.mapwize.mapwizeformapbox.api.ApiCallback;
import io.mapwize.mapwizeformapbox.api.Direction;
import io.mapwize.mapwizeformapbox.api.LatLngFloor;
import io.mapwize.mapwizeformapbox.api.Place;
import io.mapwize.mapwizeformapbox.api.Venue;
import io.mapwize.mapwizeformapbox.map.MapOptions;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;
import io.mapwize.mapwizeformapbox.map.MapwizePluginFactory;


import static android.content.Context.NOTIFICATION_SERVICE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static org.altbeacon.beacon.service.BeaconService.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Location extends Fragment {
    private static final String EXTRA_COLOR = "color";

    //args
    private int color;

    //
    private MapView mapView;
    protected MapboxMap mapboxMapp;
    private MapwizePlugin mapwizePlugin;
    private GPSIndoorLocationProvider gpsIndoorLocationProvider;
    public final static int MAP_CLICK = 0;  // The user clicked on the map
    public final static int PLACE_CLICK = 1;// The user clicked on a place
    public final static int VENUE_CLICK = 2;// The user clicked on a venue
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 0;
    private static final Double initfloor = 5.0;
    protected IndoorProvider indoor;
    private String currentPlace;
    private String toPlace;

    public static Location newInstance(int color) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_COLOR, color);

        Location fragment = new Location();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null || !getArguments().containsKey(EXTRA_COLOR))
            throw new IllegalArgumentException("you should run fragment view newInstance");

        color = getArguments().getInt(EXTRA_COLOR, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        Mapbox.getInstance(getActivity(), "pk.eyJ1Ijoic2FtdWVseWlwNzQiLCJhIjoiY2pzbzhkYnExMGp5aTQ5bGlvZ21mZDdiMiJ9.XkUpg9IWHwx9uTvplPKcNg");// PASTE YOU MAPBOX API KEY HERE !!! This is a demo key. It is not allowed to use it for production. The key might change at any time without notice. Get your key by signing up at mapbox.com
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_CONTACTS}, 0);


        // Instanciate Indoor Provider
        indoor = new IndoorProvider();

        // Stellar LBS Positioning SDK
        //LocationClient loc = new LocationClient(getActivity(), indoor);
        //GeofencingClient geo = new GeofencingClient(getActivity());

        mapView = (MapView) getView().findViewById(R.id.mapView);

        MapOptions options = new MapOptions.Builder()
                //.restrictContentToOrganization("YOUR_ORGANIZATION_ID")
                //.restrictContentToVenue("YOUR_VENUE_ID")
                .centerOnVenue("5c75f1f6e2aaf80074bb978e")
                //.centerOnPlace("YOUR_PLACE_ID")
                .floor(initfloor)
                .build();
        mapwizePlugin = MapwizePluginFactory.create(mapView,options);
        mapwizePlugin.setOnDidLoadListener(new MapwizePlugin.OnDidLoadListener() {
            @Override
            public void didLoad(MapwizePlugin plugin) {
                // Mapwize plugin is loaded and ready to use.
                startLocationService();

            }


        });


        mapwizePlugin.addOnClickListener(clickEvent -> {
            switch (clickEvent.getEventType()) {
                case MAP_CLICK:
                    LatLngFloor llf = clickEvent.getLatLngFloor();
                    Log.d(TAG, "onViewCreated: MAP_CLICK " + clickEvent.getLatLngFloor().toString());
                    // Do something with LatLngFloor
                    break;
                case PLACE_CLICK:
                    Place place = clickEvent.getPlace();
                    Log.d(TAG, "onViewCreated: PLACE_CLICK " + place.toString());
                    new AlertDialog.Builder(getActivity())
                            .setTitle(place.getName().toString())
                            //.setMessage("Start Navigation")
                            .setCancelable(true)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Navigate", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever...

                                    toPlace = place.getName();
                                    wayfinding(getView());
                                    Log.d(TAG, "onViewCreated: StartNavigation " + clickEvent.getLatLngFloor().toString());

                                }
                            }).show();


                    // Do something with place
                    break;
                case VENUE_CLICK:
                    Venue venue = clickEvent.getVenue();
                    Log.d(TAG, "onViewCreated: VENUE_CLICK " + clickEvent.getLatLngFloor().toString());

                    // Do something with venue
                    break;
            }
        });



    }

    private void startLocationService() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            setupLocationProvider();
        }
    }

    private void setupLocationProvider() {
        //gpsIndoorLocationProvider = new GPSIndoorLocationProvider(getActivity());
        //basicBeaconIndoorLocationProvider = new BasicBeaconIndoorLocationProvider(getActivity(), "3d7e7161d1fc40cc3dfd9950d7812238", gpsIndoorLocationProvider);
        mapwizePlugin.setLocationProvider(indoor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupLocationProvider();

                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
    }

    public void wayfinding(View v){
        String lvenueName = mapwizePlugin.getVenue().getAlias();
        Api.getVenueWithAlias("viva_business_park", new ApiCallback<Venue>() { // harcdcoded to run on brest map

            @Override
            public void onSuccess(Venue venue) {
                Api.getPlaceWithName(toPlace, venue, new ApiCallback<Place>() { // harcoded final place = pause
                    @Override
                    public void onSuccess(Place place) {
                        //Api.getDirection(new LatLngFloor(1.3231527, 103.92172491, 5.0), place, true, new ApiCallback<Direction>() {
                        Api.getDirection(new LatLngFloor(mapwizePlugin.getUserPosition().getLatitude(), mapwizePlugin.getUserPosition().getLongitude(), 5.0), place, true, new ApiCallback<Direction>() {
                            @Override
                            public void onSuccess(Direction direction) {
                                Log.i("MainActivity", "getDirection");

                                drawWayfinding(direction);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.i("MainActivity", "error getDirection" + mapwizePlugin.getUserPosition().getLatitude());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("MainActivity", "error getPlaceWithAlias");
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i("MainActivity", "error GetVenueWithName");
            }
        });
    }

    private void drawWayfinding(final Direction dir){   // need to run in UI thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapwizePlugin.setDirection(dir);    // draw the wayfinding
            }
        });
    }



}

