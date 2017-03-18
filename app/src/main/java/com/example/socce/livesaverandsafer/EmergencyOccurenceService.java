package com.example.socce.livesaverandsafer;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class EmergencyOccurenceService extends IntentService {

    private GoogleApiClient apiClient;
    private final String judgeNo = "tel:+491512"; //TODO

    public EmergencyOccurenceService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO below if there is an incident
        if(apiClient==null){
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
        }
        apiClient.connect();
        LocationRequest locReq = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        };
        // TODO request Location Updates
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient,locReq,listener);
        Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        double longt = location.getLongitude();
        double langt = location.getLatitude();
        double alt = location.getAltitude();
        String incident = "Incident at Long(" + longt + "), Lang(" + langt + ") & alt(" + alt + ")";

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage("+972545877122",null,incident,null,null);

        Intent calling = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+972545877122"));
        startActivity(calling);
        MainActivity.termination = false;
        }


}
