package com.example.socce.livesaverandsafer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final int LOC_REQ_CODE = 42;
    final int PHONE_REQ_CODE = 999;
    final int SMS_REQ_CODE = 535;
    final int BLUETOOTH_ENABLE_CODE = 423;
    final String HELMET_NAME = "HELMET";
    private Map<String, String> devicesFound =  new HashMap<>();
    static boolean termination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, PHONE_REQ_CODE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_REQ_CODE);
        }

        //Test
//        GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
//                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
//                .addApi(LocationServices.API)
//                .build();
//        apiClient.isConnected();
//        LocationRequest locReq = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationListener listener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//
//            }
//        };
//        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient,locReq,listener);
//        Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
//        if(location==null){
//            // TODO requestLocationUpdates
//        }
//        double longt = location.getLongitude();
//        double langt = location.getLatitude();
//        double alt = location.getAltitude();
//        String incident = "Incident at Long(" + longt + "), Lang(" + langt + ") & alt(" + alt + ")";
//        Log.v("test", incident);
        //Test

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            Log.e("no bt","no bt");
            System.exit(0);
        }
        if(! bluetoothAdapter.isEnabled()){
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth,BLUETOOTH_ENABLE_CODE);
        }
        // Bluetooth is now enabled above
        BluetoothDevice device = null;
        try {
            String s = bluetoothActions(bluetoothAdapter);
            // TODO use string, device = bluetoothActions(bluetoothAdapter);
        }
        catch (Exception e){
            Log.e("Not found","Not found");
        }
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        if(device!=null){

        }
        // Declare emergencyOccurenceService
        while(termination){
            wakeLock.acquire();
//            Intent intent = new Intent(Intent.)
            // Get Data from BluetoothDevice
            // If data ! null, Do code here for if incident happens
            wakeLock.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case LOC_REQ_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Log.v("Denied","App needs permissions to work");
                    System.exit(-1);
                }
                break;
            }
            case PHONE_REQ_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Log.v("Denied", "App needs permissions to work");
                    System.exit(-1);
                }
                break;
            }
            case SMS_REQ_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Log.v("Denied", "App needs permissions to work");
                    System.exit(-1);
                }
                break;
            }
            default:
                break;
        }
    }

    private String bluetoothActions(BluetoothAdapter bluetoothAdapter) throws Resources.NotFoundException {
        String address = queryKnown(bluetoothAdapter);
        if(address.equals("")){
            address = discover(bluetoothAdapter);
        }
        if(address.equals("")){
            throw new Resources.NotFoundException("Device is not available!");
        }
        return address;
    }

    private String queryKnown(BluetoothAdapter bluetoothAdapter){
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : devices){
            String devName = device.getName();
            String devMacAdd = device.getAddress();
            if(devName.equals(HELMET_NAME)){
                return devMacAdd;
            }
        }
        return "";
    }

    private String discover(final BluetoothAdapter bluetoothAdapter){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver,filter);
        bluetoothAdapter.startDiscovery();
        if(devicesFound.containsKey(HELMET_NAME)){
            return devicesFound.get(HELMET_NAME);
        }
        unregisterReceiver(receiver);
        bluetoothAdapter.cancelDiscovery();
        return "";
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                devicesFound.put(deviceName,deviceHardwareAddress);
            }
        }
    };

    private void connect(BluetoothAdapter bluetoothAdapter, String address){

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
