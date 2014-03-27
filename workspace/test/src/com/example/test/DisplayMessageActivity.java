package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		// Show the Up button in the action bar.
		setupActionBar();
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		  // Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(40);
	    textView.setText(message);

	    // Set the text view as the activity layout
	    setContentView(textView);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);	    // add menu items  
        MenuItem add=menu.add(0,0,0,"add");  
        MenuItem del=menu.add(0,0,0,"del");  
        //MenuItem save=menu.add(0,0,0,"save");  
        //add menu to ActionBar    
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);  
        del.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);  
        //save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);  

		return true;  
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Toast.makeText(getApplicationContext(), "android.R.id.home", 0).show();
			NavUtils.navigateUpFromSameTask(this);
			break;
        
		case R.id.set_date:  
            Toast.makeText(getApplicationContext(), "set_date", 0).show();  
		    break;  
		
		//case android.R.id.home:  
            //Intent intent = new Intent(this, MainActivity.class);  
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  
		    //                | Intent.FLAG_ACTIVITY_NEW_TASK);  
            //startActivity(intent);  
            //Toast.makeText(getApplicationContext(), "android.R.id.home", 0).show();  
		//break;  
		
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
