package com.regis.two_wd_robot;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Menu;
import android.widget.TextView;

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
        super.onPause();  
    }  
        
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        wl.acquire(); 
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
