package com.example.mdptool;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
import android.widget.Toast;

public class MainActivity extends Activity {
	
	protected static final int MESSAGE_READ = 1;
	private static BluetoothSocket mSocket;
	public static ConnectedThread ct;
	public static Handler mHandler;
	
	Maze myMaze;
	SurfaceHolder holder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myMaze =(Maze) findViewById(R.id.maze);
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
			  switch (msg.what) {
			     case MESSAGE_READ:	byte[] readBuf = (byte[]) msg.obj;
			    	 				String string = new String(readBuf);
			    	 				Log.d("bluetooth", string.toString());
			     					break;
			  }
			}
		};
	}
	@Override
	protected void onResume(){
		super.onResume();
	}
	protected void onPause(){
		super.onPause();
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
		myMaze.startMaze();
	}
}
