//package com.example.socce.livesaverandsafer;
//
//import android.app.Activity;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.telephony.SmsManager;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//
//public class Locator implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
//
//    private GoogleApiClient apiClient;
//    private Location mLocation;
//
//    public Locator() {
//        if(apiClient==null){
//            apiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//    }
//
//    protected void locate() {
//        apiClient.connect();
//        LocationRequest locReq = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationListener listener = this;
//        Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
//        if(location==null){
//            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient,locReq,listener);
//            location.setLongitude(46.4992509);
//            location.setLatitude(9.8287628);
//        }
//        double longt = location.getLongitude();
//        double langt = location.getLatitude();
//        double alt = location.getAltitude();
//        String incident = "Incident at Long(" + longt + "), Lang(" + langt + ") & alt(" + alt + ")";
//        }
//
//    public Location getmLocation() {
//        return mLocation;
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLocation = location;
//    }
//}
