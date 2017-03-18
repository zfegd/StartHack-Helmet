package com.example.socce.livesaverandsafer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.RuntimeExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final int LOC_REQ_CODE = 42;
    final int PHONE_REQ_CODE = 999;
    final int SMS_REQ_CODE = 535;
    final int BLUETOOTH_ENABLE_CODE = 423;
    final String HELMET_NAME = "HELMET";
    final int WAITING_TIME = 100;
    private Map<String, String> devicesFound =  new HashMap<>();
    static boolean termination;
    private final String SERVICE_UUID = "00005301-0000-0041-4C50-574953450000";
    private final String WRITE_UUID = "00005302-0000-0041-4C50-574953450000";
    private final String READ_UUID = "00005303-0000-0041-4C50-574953450000";

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
        BluetoothDevice device=null;
        try {
            String s = bluetoothActions(bluetoothAdapter);
            device = bluetoothAdapter.getRemoteDevice(s);
        }
        catch (Exception e){
            Log.e("Not found","Not found");
        }
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");

        BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback(){

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Log.d("1","1");
            }

            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.d("1","1");

                BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_UUID));

                BluetoothGattCharacteristic read = service.getCharacteristic(UUID.fromString(READ_UUID));
                gatt.setCharacteristicNotification(read,true);


                BluetoothGattCharacteristic write = service.getCharacteristic(UUID.fromString(WRITE_UUID));
                int i =write.getProperties();
                write.setWriteType(2);
                write.setValue("start");
                gatt.writeCharacteristic(write);
                Log.d("Done","Done");
            }

            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.d("1","1");
            }

            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d("1","1");
            }

            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d("1","1");
            }

            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d("1","1");
            }

            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d("1","1");
            }

            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                Log.d("1","1");
            }

            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.d("1","1");
            }

            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                Log.d("1","1");
            }

        };
        BluetoothGatt btGatt = null;
        if(device!=null) {
            btGatt = device.connectGatt(this, false, bluetoothGattCallback);
        }
        else {
            Log.d("Error","No Device");
            return;
        }
        try {
            btGatt.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Declare emergencyOccurenceService
        /*while(! termination){
            wakeLock.acquire();
//            Intent intent = new Intent(Intent.)
            // Get Data from BluetoothDevice
            btGatt.readCharacteristic(read);
            byte[] value = read.getValue();
            // String readPastTense = read.getStringValue(0); // Is this null?
            Log.d("Test","Test");
            // If data ! null, Do code here for if incident happens
            // Write Data to text file for research !!
            wakeLock.release();
            try {
                wait(WAITING_TIME);
            }
            catch (InterruptedException e){
                Log.e("Error","Time Interrupted");
            }
        }*/
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
