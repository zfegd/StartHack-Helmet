package com.example.socce.livesaverandsafer;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class EmergencyOccurenceService extends IntentService {

    public EmergencyOccurenceService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {



        // DO below if there is an incident


        SmsManager manager = SmsManager.getDefault();

        manager.sendTextMessage("+972545877122",null,"Insert Message with GPS Here",null,null);
        //Send SMS with GPS

        Intent calling = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+972545877122"));
        startActivity(calling);

        }
}
