package com.example.mdptool;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

public class MainActivity extends Activity {

    protected static final int MESSAGE_READ = 1;
    private static BluetoothSocket mSocket;
    public static ConnectedThread ct;
    public static Handler mHandler;
    TextView receivedMsg;
    BroadcastReceiver bluetoothReceiver;
    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

    RecognitionListener speechRecognition;
    SpeechRecognizer speechRecognizer;
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    GestureDetector gDetector;
    SimpleOnGestureListener gestureListener;
    boolean tiltStatus, gestureStatus = false;

    MapDescriptor mapDesc = new MapDescriptor();
    Maze myMaze;
    SurfaceHolder holder;
    ToggleButton toggle;
    Button updateMap;

    static Object bluetoothItem;

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
        myMaze = (Maze) findViewById(R.id.maze);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String string = new String(readBuf, 0, msg.arg1);
                        if (string.contains("STAT")) {
                            char stat = string.charAt(4);
                            Log.d("char", "" + stat);
                            String robotCommand = Integer.toBinaryString((int) stat);
                            int toPad = 6 - robotCommand.length();
                            for (int i = 0; i < toPad; i++) {
                                robotCommand = "0" + robotCommand;
                            }
                            Log.d("Message", robotCommand);
                            updateStatus(robotCommand);

                        } else if (string.contains("GRID")) {
                            Log.d("decode", string);
                            mapDesc.decode(string.substring(4), toggle.isChecked());
                        } else {
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
                } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    Toast.makeText(MainActivity.this, "Bluetooth Disconnected", Toast.LENGTH_SHORT).show();
                    ct.cancel();
                    BluetoothConnectThread server = new BluetoothConnectThread(bluetoothItem);
                    server.start();
                }
            }
        };

        speechRecognition = new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "Error on Speech", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                // TODO Auto-generated method stub
                List<String> list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals("up")) {
                        sendUp(null);
                        break;
                    } else if (list.get(i).equals("down")) {
                        sendDown(null);
                        break;
                    } else if (list.get(i).equals("left")) {
                        sendLeft(null);
                        break;
                    } else if (list.get(i).equals("right")) {
                        sendRight(null);
                        break;
                    }
                }
                Toast.makeText(MainActivity.this, "Command not recognised", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBeginningOfSpeech() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onEndOfSpeech() {
                // TODO Auto-generated method stub

            }
        };

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                if (Math.abs(x) > Math.abs(y)) {
                    if (x < -2) {
                        sendRight(null);
                    }
                    if (x > 2) {
                        sendLeft(null);
                    }
                } else {
                    if (y < -2) {
                        sendUp(null);
                    }
                    if (y > 2) {
                        sendDown(null);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }

        };
        gestureListener = new SimpleOnGestureListener() {

            public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX, float velocityY) {

                if ((start.getRawY() - finish.getRawY()) > 0 && -100 < (start.getRawX() - finish.getRawX()) && 100 > (start.getRawX() - finish.getRawX())) {
                    sendUp(null);
                } else if ((start.getRawY() - finish.getRawY()) < 0 && -100 < (start.getRawX() - finish.getRawX()) && 100 > (start.getRawX() - finish.getRawX())) {
                    sendDown(null);
                } else if ((start.getRawX() - finish.getRawX()) < 0 && -100 < (start.getRawY() - finish.getRawY()) && 100 > (start.getRawY() - finish.getRawY())) {
                    sendRight(null);
                } else if ((start.getRawX() - finish.getRawX()) > 0 && -100 < (start.getRawY() - finish.getRawY()) && 100 > (start.getRawY() - finish.getRawY())) {
                    sendLeft(null);
                }
                return true;
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);
        registerReceiver(bluetoothReceiver, filter1);

        if (tiltStatus)
            sensorManager.registerListener(sensorEventListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        if (gestureStatus) {
            gDetector = new GestureDetector(null, gestureListener);
        }
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothReceiver);
        sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        gDetector = null;
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

    public static void manageConnectedSocket(BluetoothSocket mmSocket) {
        mSocket = mmSocket;
        ct = new ConnectedThread(mSocket, mHandler);
        ct.run();
    }


    public void toggleStart (View view) {
        boolean status = ((ToggleButton) view).isChecked();
        if (status) {
            if (ct != null) {
                myMaze.startMaze(mapDesc);
                toggle.setVisibility(View.VISIBLE);
                ct.write((Helper.convert(Config.START_EXPLORE)).getBytes());
            } else {
                Toast.makeText(this, R.string.no_device_msg , Toast.LENGTH_SHORT).show();
                ((ToggleButton) view).setChecked(false);
            }
         }
    }

    /*
    public void startBtn(View view) {
        myMaze.startMaze(mapDesc);
        toggle.setVisibility(View.VISIBLE);
        ct.write((Helper.convert(Config.START_EXPLORE)).getBytes());
    }
    */

    public void startPreferencePage(View view) {
        Intent newIntent = new Intent(this, PreferencePage.class);
        this.startActivity(newIntent);
    }

    public void startCoordinatePage(View view) {
        Intent newIntent1 = new Intent(this, SendCoordinates.class);
        this.startActivity(newIntent1);
    }
    public static byte[] concat(byte[] a, byte[] b) {
        int lengthA = a.length;
        int lengthB = b.length;
        byte[] result = new byte[lengthA + lengthB];
        System.arraycopy(a, 0, result, 0, lengthA);
        System.arraycopy(b, 0, result, lengthA, lengthB);
        return result;
    }

    public static byte[] formatHelper(byte[] msg) {
        return concat("h".getBytes(), msg);
    }


    public void sendUp(View view) {
        if (ct != null)
            ct.write((Helper.convert(Config.UP)).getBytes());
        else
            Toast.makeText(this, R.string.no_device_msg, Toast.LENGTH_SHORT).show();
    }

    public void sendDown(View view) {
        if (ct != null)
            ct.write((Helper.convert(Config.DOWN)).getBytes());
        else
            Toast.makeText(this, R.string.no_device_msg, Toast.LENGTH_SHORT).show();
    }

    public void sendLeft(View view) {
        if (ct != null)
            ct.write((Helper.convert(Config.LEFT)).getBytes());
        else
            Toast.makeText(this, R.string.no_device_msg, Toast.LENGTH_SHORT).show();
    }

    public void sendRight(View view) {
        if (ct != null)
            ct.write((Helper.convert(Config.RIGHT)).getBytes());
        else
            Toast.makeText(this, R.string.no_device_msg, Toast.LENGTH_SHORT).show();
    }

    public void sendF1(View view) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.mdptool", Context.MODE_PRIVATE);
        ct.write(prefs.getString(PreferencePage.F1, "").getBytes());
    }

    public void sendF2(View view) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.mdptool", Context.MODE_PRIVATE);
        ct.write(prefs.getString(PreferencePage.F2, "").getBytes());
    }

    public void toggleMap(View view) {

        boolean status = ((ToggleButton) view).isChecked();
        if (status) {
            updateMap.setVisibility(View.INVISIBLE);
        } else {
            updateMap.setVisibility(View.VISIBLE);
        }
    }

    public void updateMap(View view) {
        mapDesc.updateValue();
    }

    public void turnOnVoice(View view) {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizer.setRecognitionListener(speechRecognition);
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, MainActivity.this.getPackageName());
        speechRecognizer.startListening(speechRecognizerIntent);

        Toast.makeText(this, "Speech ready", Toast.LENGTH_SHORT).show();

    }

    public void turnTilt(View view) {

        tiltStatus = ((ToggleButton) view).isChecked();
        if (tiltStatus) {
            sensorManager.registerListener(sensorEventListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        }

    }

    public void turnGesture(View view) {
        gestureStatus = ((ToggleButton) view).isChecked();
        if (gestureStatus) {
            gDetector = new GestureDetector(null, gestureListener);
        } else {
            gDetector = null;
        }
    }

    public static void setBluetoothObject(Object item) {
        bluetoothItem = item;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (gestureStatus)
            return gDetector.onTouchEvent(me);
        else
            return true;

    }

    private void updateStatus(String msg) {
        int mode = Integer.parseInt(msg.substring(0, 2));
        int movement = Integer.parseInt(msg.substring(2, 4));
        String status = "";
        switch (mode) {
            case 00:
                status = "Stop: ";
                break;
            case 01:
                status = "Explore: ";
                break;
            case 10:
                status = "Shortest Path: ";
                break;
            case 11:
                status = "Calibrate: ";
                break;
        }
        switch (movement) {
            case 00:
                status = status + "Turning 180";
                break;
            case 01:
                status = status + "Turning Right";
                break;
            case 10:
                status = status + "Turning Left";
                break;
            case 11:
                status = status + "Moving Forward";
                break;
        }
        receivedMsg.setText(status);
    }

}
