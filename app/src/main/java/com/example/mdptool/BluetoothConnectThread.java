package com.example.mdptool;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothConnectThread extends Thread {
	private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
 
    public BluetoothConnectThread(Object object) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String macid = object.toString();
        mmDevice = mBluetoothAdapter.getRemoteDevice(object.toString().substring(object.toString().length()-17)) ;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = mmDevice.createRfcommSocketToServiceRecord(BluetoothService.mdpUUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
        Log.d("client", "discovery cancel");
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
        	Log.d("client", "attempt to connect");
           mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
        	Log.d("client", "cannot connect");
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        if (mmSocket != null) {
            // Do work to manage the connection (in a separate thread)
        	Log.d("client", "connected");
            MainActivity.manageConnectedSocket(mmSocket);
        }
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }


}
