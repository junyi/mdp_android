package com.example.mdptool;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class BluetoothServerThread extends Thread{
    private final BluetoothServerSocket mmServerSocket;
	
	
	private BluetoothAdapter mBluetoothAdapter;
	public BluetoothServerThread(){
		// Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("MDPAndroidController", BluetoothService.mdpUUID);
        } catch (IOException e) {
            Log.d("MDPBluetooth", "GG LIAO!");
        }
        mmServerSocket = tmp;

	}
	public void run() {
		Log.d("THREAD","Started");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
            	Log.d("THREAD","Stop");
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
            	MainActivity.setBluetoothObject(socket.getRemoteDevice());
                MainActivity.manageConnectedSocket(socket);
            	Log.d("THREAD","connected");
                try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            }
        }
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }

}
