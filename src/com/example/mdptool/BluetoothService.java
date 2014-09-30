package com.example.mdptool;

import java.util.Set;
import java.util.UUID;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothService extends Activity {
	
	static final UUID mdpUUID  =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	int REQUEST_ENABLE_BT = 1;
	int RESULT_ENABLE_BT = 0;
	BluetoothServerThread server;
	BluetoothConnectThread client;
	BluetoothAdapter mBluetoothAdapter;
	ArrayAdapter mPairedDeviceArrayAdapter;
	ArrayAdapter mAvailDeviceArrayAdapter;
	
	BroadcastReceiver mReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_service);
		
		//Configure listview for pairedDevices
		final ListView listView = (ListView) findViewById(R.id.pairedDevice);
		mPairedDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_listview,R.id.label);
		listView.setAdapter(mPairedDeviceArrayAdapter);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is unavailable on this device.", Toast.LENGTH_SHORT).show();
			finish();
		}else if(!mBluetoothAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}else{
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
			    for (BluetoothDevice device : pairedDevices) {
			        // Add the name and address to an array adapter to show in a ListView
			    	mPairedDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			    }
			}
			server = new BluetoothServerThread();
			server.start();
		}
		
		
		

		listView.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
			      Object listItem = listView.getItemAtPosition(position);
			      //server.cancel();
			      client = new BluetoothConnectThread(listItem);
			      client.start();
			   } 
			});


		
		//Configure listview for scannedDevice
		final ListView listView2 = (ListView) findViewById(R.id.scannedDevice);
		mAvailDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_listview,R.id.label);
		listView2.setAdapter(mAvailDeviceArrayAdapter);
		
		
		listView2.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
			      Object listItem = listView2.getItemAtPosition(position);
			      server.cancel();
			      client = new BluetoothConnectThread(listItem);
			      client.run();
			   } 
			});
		
		mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            // Add the name and address to an array adapter to show in a ListView
		            mAvailDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		        }
		    }
		};
		
	}
	protected void onResume(){
		super.onResume();
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
	}
	protected void onPause(){
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	public void startDiscovery(View view){
			mAvailDeviceArrayAdapter.clear();
			mBluetoothAdapter.startDiscovery();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
		if(requestCode == REQUEST_ENABLE_BT){
			if(mBluetoothAdapter.isEnabled()) {
				Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
				//start listening server
				server =  new BluetoothServerThread();
				server.start();

				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
				// If there are paired devices
				if (pairedDevices.size() > 0) {
				    // Loop through paired devices
				    for (BluetoothDevice device : pairedDevices) {
				        // Add the name and address to an array adapter to show in a ListView
				    	mPairedDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				    }
				}

			} else {  
				Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
