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
import com.google.android.gms.tasks.RuntimeExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int WAITING_TIME = 1000;
    final int LOC_REQ_CODE = 42;
    final int PHONE_REQ_CODE = 999;
    final int SMS_REQ_CODE = 535;
    static boolean termination;
    private final String judgeNo = "tel:+4915120774296";

    private List<String> volvoChallenge = new ArrayList<>();
    private List<String> generalChallenge = new ArrayList<>();

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

        myDatagramReceiver = new MyDatagramReceiver();
        myDatagramReceiver.start();
        try {
            myDatagramReceiver.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(myDatagramReceiver.getMessage().equals("hello")){
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage("tel:+972522821752",null,"Incident at St Gallen University",null,null);
            manager.sendTextMessage("tel:+447936619937",null,"Incident at St Gallen University",null,null);
            manager.sendTextMessage("tel:+972545877122",null,"Incident at St Gallen University",null,null);
            manager.sendTextMessage("tel:+972504392880",null,"Incident at St Gallen University",null,null);

            Intent calling = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+972522821752"));
            startActivity(calling);

            System.exit(0);
        }

//        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
//        EmergencyOccurenceService eos = new EmergencyOccurenceService();
//        wakeLock.acquire();
//        while(! termination){
//            // Get Data from BluetoothDevice
//            if(dataAffirmative){
//                eos.onHandleIntent(new Intent("Accident"));
//            }
//            try {
//                wait(WAITING_TIME);
//            }
//            catch (InterruptedException e){
//                Log.e("Error","Time Interrupted");
//            }
//            if(Math.random()>0.87){
//                dataAffirmative = true;
//            }
//        }
//        wakeLock.release();
    }

    private MyDatagramReceiver myDatagramReceiver = null;

    protected void onResume() {
        super.onResume();
//        myDatagramReceiver = new MyDatagramReceiver();
//        myDatagramReceiver.start();
//        try {
//            myDatagramReceiver.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if(myDatagramReceiver.getMessage().equals("Hello")){
//            SmsManager manager = SmsManager.getDefault();
//            manager.sendTextMessage("+972545877122",null,"incident",null,null);
//            Log.v("Sent","Success");
//        }
    }

    protected void onPause() {
        super.onPause();
        myDatagramReceiver.kill();
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


}
