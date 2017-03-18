package com.example.socce.livesaverandsafer;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

class MyDatagramReceiver extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage;

    public void run() {
        String message="";
        byte[] lmessage = new byte[6];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

        try {
            DatagramSocket socket = new DatagramSocket(1234);

            while(! message.equals("hello")) {
                socket.receive(packet);
                message = new String(lmessage, 0, packet.getLength());
                message = message.trim();
                Log.v(message,message);
                //add a try, wait -> ensure not killed by onpause
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        lastMessage = message;
    }

    public void kill() {
        bKeepRunning = false;
    }

    public String getMessage() {
        return lastMessage;
    }
}
