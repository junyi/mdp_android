package com.example.mdptool;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	protected static final int MESSAGE_READ = 1;
	private static BluetoothSocket mSocket;
	public static ConnectedThread ct;
	public static Handler mHandler;
	TextView receivedMsg;
	BroadcastReceiver bluetoothReceiver;
	IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
	
	MapDescriptor mapDesc = new MapDescriptor();
	Maze myMaze;
	SurfaceHolder holder;
	ToggleButton toggle;
	Button updateMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		toggle = (ToggleButton) findViewById(R.id.toggleMap);
		toggle.setChecked(true);
		toggle.setVisibility(View.INVISIBLE);
		updateMap = (Button) findViewById(R.id.autoManual);
		updateMap.setVisibility(View.INVISIBLE);
		
		receivedMsg = (TextView) findViewById(R.id.receivedMsg);
		myMaze =(Maze) findViewById(R.id.maze);
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
			  switch (msg.what) {
			     case MESSAGE_READ:	byte[] readBuf = (byte[]) msg.obj;
			    	 				String string = new String(readBuf,0,msg.arg1);
			    	 				if(string.contains("STAT")){
			    	 					receivedMsg.setText(string.substring(4));
			    	 				}else if(string.contains("GRID")){
			    	 					mapDesc.decode(string.substring(4),toggle.isChecked());
			    	 				}else{
			    	 					Toast.makeText(MainActivity.this, "WRONG COMMAND", Toast.LENGTH_LONG).show();
			    	 				}	    	 				
			     					break;
			  }
			}
		};
		
		bluetoothReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
		        	Toast.makeText(MainActivity.this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
		        }else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
		        	Toast.makeText(MainActivity.this, "Bluetooth Disconnected", Toast.LENGTH_SHORT).show();
		        	ct.cancel();
		        	BluetoothServerThread server= new BluetoothServerThread();
					server.start();
		        }
		    }
		};
	}
	@Override
	protected void onResume(){
		super.onResume();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(bluetoothReceiver, filter);
		registerReceiver(bluetoothReceiver, filter1);
		
	}
	protected void onPause(){
		super.onPause();
		unregisterReceiver(bluetoothReceiver);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_connect:
	            Intent newIntent = new Intent(this, BluetoothService.class);
	            this.startActivity(newIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public static void manageConnectedSocket(BluetoothSocket mmSocket){
		mSocket = mmSocket;
		ct =  new ConnectedThread(mSocket,mHandler);
		ct.run();
	}
	public void startBtn(View view){
		myMaze.startMaze(mapDesc);
		toggle.setVisibility(View.VISIBLE);
	}
	public void startPreferencePage(View view){
		Intent newIntent = new Intent(this, PreferencePage.class);
        this.startActivity(newIntent);
	}
	public void sendUp(View view){
		String up = "w";
		ct.write(up.getBytes());
	}
	public void sendDown(View view){
		String down = "s";
		ct.write(down.getBytes());
	}
	public void sendLeft(View view){
		String left = "a";
		ct.write(left.getBytes());
	}
	public void sendRight(View view){
		String right = "d";
		ct.write(right.getBytes());
	}
	public void toggleMap(View view){
		
		boolean status = ((ToggleButton) view).isChecked();
		if (status){
			updateMap.setVisibility(View.INVISIBLE);
		}else{
			updateMap.setVisibility(View.VISIBLE);
		}
	}
	public void updateMap(View view){
		mapDesc.updateValue();
	}
	
}
