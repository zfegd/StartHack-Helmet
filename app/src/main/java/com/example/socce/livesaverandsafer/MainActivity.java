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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

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
        try {
            bluetoothActions(bluetoothAdapter);
        }
        catch (Exception e){
            Log.e("Not found","Not found");
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

    private void bluetoothActions(BluetoothAdapter bluetoothAdapter) throws Resources.NotFoundException {
        String address = queryKnown(bluetoothAdapter);
        if(address.equals("")){
            address = discover(bluetoothAdapter);
        }
        if(address.equals("")){
            throw new Resources.NotFoundException("Device is not available!");
        }
        //Connect time
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
//        try {
//            wait(1300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(devicesFound.containsKey(HELMET_NAME)){
            return devicesFound.get(HELMET_NAME);
        }
        unregisterReceiver(receiver);
        return "";
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
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

    private void connect(BluetoothAdapter bluetoothAdapter){

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
