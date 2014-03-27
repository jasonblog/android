package com.regis.two_wd_robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
   	SensorManager sensorManager;  
	Sensor accelerometer;
	TextView accelerationTextView;  
    TextView accelerationTextView_x;  
    TextView accelerationTextView_y;  
    TextView accelerationTextView_z;  
    TextView maxAccelerationTextView;  
    private Handler timer_handler;
 	double acc_x;  
   	double acc_y;  
   	double acc_z;  
      
    PowerManager pm;
    PowerManager.WakeLock wl;
    
    float currentAcceleration = 0;  
    float maxAcceleration = 0;  
    
    private static final String TAG = "2WD_ROBOT";
    private static final boolean D = true;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    // Well known SPP UUID (will *probably* map to
    // RFCOMM channel 1 (default) if not in use);
    // see comments in onResume().
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
    // ==> hardcode your server's MAC address here
    private static String address = "20:13:12:04:05:92"; //Regis-2
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accelerationTextView = (TextView)findViewById(R.id.acceleration);  
        accelerationTextView_x = (TextView)findViewById(R.id.acceleration_x);  
        accelerationTextView_y = (TextView)findViewById(R.id.acceleration_y);  
        accelerationTextView_z = (TextView)findViewById(R.id.acceleration_z);  
        maxAccelerationTextView = (TextView)findViewById(R.id.maxAcceleration);  
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);  
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  
        pm = (PowerManager)getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "2WD");
        //timer_handler = new Handler();
        //timer_handler.postDelayed(timer_runnable,250); // start Timer 0.5s
        if (D)
            Log.e(TAG, "+++ ON CREATE +++");
 
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                "Bluetooth is not available.",
                Toast.LENGTH_LONG).show();
            finish();
            return;
        }
 
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this,
                "Please enable your BT and re-run this program.",
                Toast.LENGTH_LONG).show();
            finish();
            return;
        }
 
        if (D)
            Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
    }

    //private Runnable timer_runnable = new Runnable() {
    	//public void run () {
    		//showAcc_x();
    		//showAcc_y();
    		//showAcc_z();
    		//timer_handler.postDelayed(this,250); 
    	//}
    //};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onPause(){  
        wl.release();
        sensorManager.unregisterListener(this);  
        
        if (D)
            Log.e(TAG, "- ON PAUSE -");
 
        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
            }
        }
 
        try {
            btSocket.close();
        } catch (IOException e2) {
            Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
        }
        super.onPause();  
    }  
        
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        wl.acquire(); 
        
        if (D) {
            Log.e(TAG, "+ ON RESUME +");
            Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
        }
 
        // When this returns, it will 'know' about the server,
        // via it's MAC address.
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
 
        // We need two things before we can successfully connect
        // (authentication issues aside): a MAC address, which we
        // already have, and an RFCOMM channel.
        // Because RFCOMM channels (aka ports) are limited in
        // number, Android doesn't allow you to use them directly;
        // instead you request a RFCOMM mapping based on a service
        // ID. In our case, we will use the well-known SPP Service
        // ID. This ID is in UUID (GUID to you Microsofties)
        // format. Given the UUID, Android will handle the
        // mapping for you. Generally, this will return RFCOMM 1,
        // but not always; it depends what other BlueTooth services
        // are in use on your Android device.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Socket creation failed.", e);
        }
 
        // Discovery may be going on, e.g., if you're running a
        // 'scan for devices' search from your handset's Bluetooth
        // settings, so we call cancelDiscovery(). It doesn't hurt
        // to call it, but it might hurt not to... discovery is a
        // heavyweight process; you don't want it in progress when
        // a connection attempt is made.
        mBluetoothAdapter.cancelDiscovery();
 
        // Blocking connect, for a simple client nothing else can
        // happen until a successful connection is made, so we
        // don't care if it blocks.
        try {
            btSocket.connect();
            Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG,
                    "ON RESUME: Unable to close socket during connection failure", e2);
            }
        }
 
        // Create a data stream so we can talk to server.
        if (D)
            Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");
 
        try {
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: stream creation failed.", e);
        }
 
        String message = "o";
        byte[] msgBuffer = message.getBytes();
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Exception during write.", e);
        }
    }
       
    private void showAcc_x() {
    	String s = "x="+ acc_x;
		accelerationTextView_x.setText(s);
    }

    private void showAcc_y() {
    	String s = "y="+ acc_y;
		accelerationTextView_y.setText(s);
    }

    private void showAcc_z() {
       	String s = "z="+ acc_z;
		accelerationTextView_z.setText(s);
    }

    
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }  
          
    public void onSensorChanged(SensorEvent event) {  
      	double acc_x_tmp = Math.round(event.values[0] * 100);  
       	double acc_y_tmp = Math.round(event.values[1] * 100);  
       	double acc_z_tmp = Math.round(event.values[2] * 100);  
       	
       	acc_x_tmp /= 100;
       	acc_y_tmp /= 100;
       	acc_z_tmp /= 100;
       	
       	if (Math.abs(acc_x_tmp - acc_x) > 0.1)
       		showAcc_x();
       	if (Math.abs(acc_y_tmp - acc_y) > 0.1)
       		showAcc_y();
       	if (Math.abs(acc_z_tmp - acc_z) > 0.1)
       		showAcc_z();
       	
       	acc_x = acc_x_tmp;
       	acc_y = acc_y_tmp;
       	acc_z = acc_z_tmp;
			
		//accelerationTextView_x.invalidate();  
		//accelerationTextView_y.invalidate();  
		//accelerationTextView_z.invalidate();  
    }  
}
