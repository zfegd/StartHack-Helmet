package com.example.socce.livesaverandsafer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    final int locRequestCode = 42;
    final int phoneReqCode = 999;
    final int smsRequestCode = 535;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locRequestCode);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, phoneReqCode);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, smsRequestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case locRequestCode: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Log.v("Denied","App needs permissions to work");
                    System.exit(-1);
                }
                break;
            }
            case phoneReqCode: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Log.v("Denied", "App needs permissions to work");
                    System.exit(-1);
                }
                break;
            }
            case smsRequestCode: {
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
