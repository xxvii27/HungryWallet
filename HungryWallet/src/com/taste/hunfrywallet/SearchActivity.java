package com.taste.hunfrywallet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.parse.ParseUser;
import com.taste.hunfrywallet.Yelp;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;

public class SearchActivity extends Activity implements 
GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener {
	
	// Used to gather user's current location
	LocationClient mCurrentLocationClient;
	
	// Constants used to transfer search details to next activity in Intent
	public final static String SEARCH_TERM_MESSAGE = "com.taste.hunfrywallet.SEARCH_TERM";
	public final static String SEARCH_TYPE = "com.taste.hunfrywallet.SEARCH_TYPE";
    public final static String SEARCH_RESULTS_MESSAGE = "com.taste.hunfrywallet.SEARCH_RESULTS";
    
    private SeekBar seekBar;
    private TextView seekView;
    private Button logOut;
    private AlertDialog.Builder alert;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_search);
		initTypeFace();
		// Show the Up button in the action bar.
		// setupActionBar();
		
		// Seekbar
		seekBar = (SeekBar)findViewById(R.id.seekBar1);
		seekView = (TextView)findViewById(R.id.textView1);
		seekView.setText("$$$");
		seekBar.setProgress(2);
		seekBar.setOnSeekBarChangeListener(
				new OnSeekBarChangeListener() {
					int progress = 0;
					@Override
					public void onProgressChanged(SeekBar seekBar, 
							int progresValue, boolean fromUser) {
						progress = progresValue;
						// Display the value in textview
						switch(progress){
						
						   case 0 : seekView.setText("$");
						   break;
						   case 1 : seekView.setText("$$");
						   break;
						   case 2:  seekView.setText("$$$");
						   break;
						   case 3:  seekView.setText("$$$$");
						   break;	
						   case 4:  seekView.setText("$$$$$");
						   break;
						   default: seekView.setText("$$$");
						   break;			
						
						}
						
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// Do something here, 
						// if you want to do anything at the start of
						// touching the seekbar
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// Display the value in textview
					}
		});
		
		mCurrentLocationClient = new LocationClient(this, this, this);	
	}
	
	/*
     * Called when the Activity becomes visible.
     */
	@Override
    protected void onStart() {
		super.onStart();
        // Connect the client.
		mCurrentLocationClient.connect();	
		
		//Getting logout Button
		logOut = (Button) findViewById(R.id.logout_button);
		/* Create the alert dialog for the LogOut Confirmation */
		alert = new AlertDialog.Builder(this);
        alert.setTitle("Log Out");
        alert.setMessage("Are you sure you want to log out?");
        alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
 
            }
        });
 
        alert.setNegativeButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	ParseUser.logOut();
                        startActivity(new Intent (getApplicationContext(), SignUpOrLoginActivity.class));
                    }
                });
		
		logOut.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
		       alert.create().show();
			}	

		});	
	}
	
	/*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mCurrentLocationClient.disconnect();
        super.onStop();
    }
    
	

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/* Thread used to perform a Search - by preference or by price range
	 * 
	 * If search option is by preference, the Yelp API is used
	 * If search option is by price range, the Google Places API is used
	 *  and results are returned based on the price_levels of restaurants.
	 */
	private class MyAsyncTask extends AsyncTask<String,Void,Void>{
		
		String typeOfSearch;
		
		public MyAsyncTask(String typeOfSearch){
			this.typeOfSearch = typeOfSearch;
		}

		@Override
		protected Void doInBackground(String... args) {
			String response = "";
			
		    //System.out.println("THE USER WANTS TO FIND PLACES TO EAT "+ args[0]);
		  //GET LOCATION NOW
		    double latitude;
		    double longitude;
		    try{
		    	Location loc = mCurrentLocationClient.getLastLocation();
		    	latitude = loc.getLatitude();
		    	longitude = loc.getLongitude();
		    }
		    catch(Throwable e){
		    	latitude = 32.8810;
		    	longitude = 117.2380;
		    	
		    }
		    //System.out.println("Latitude is: " + latitude);
			//System.out.println("Longitude: " + longitude);

			
			if(typeOfSearch.equals("yelp")){
				String consumerKey = "JmCEPESnmAdI--_rPRtojg";
			    String consumerSecret = "Ss8vkoOTJTkwTW-TosiiDUIlgMg";
			    String token = "uDoa1Gkuapkc6YIcUn_fv7uN8PHGM7rb";
			    String tokenSecret = "CKzZFC8figPjQI3NQYI0P7MwOjc";
			    
			    Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
				response = yelp.search(args[0], latitude, longitude);
				
			}
			else{
				
				//Build URL for Google API request
				StringBuilder urlString = new StringBuilder();
		        urlString.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		        urlString.append("location=").append(latitude + "," + longitude);
		        urlString.append("&sensor=").append("true");
		        //urlString.append("&name=").append(params[1]);
		        urlString.append("&rankby=").append("distance");
		        urlString.append("&keyword=").append(args[0].trim());
		        urlString.append("&minprice=").append(seekBar.getProgress());
		        urlString.append("&maxprice=").append(seekBar.getProgress());
		        
		        urlString.append("&key=").append("AIzaSyA63EmMpjRs3wJd_9SU-QsuCw-C7AKA5Qs");
		        
		        HttpURLConnection urlConnection = null;
		        URL url = null;
		        //JSONObject object = null;

		        try
		        {
					url = new URL(urlString.toString());
		            
		        	urlConnection = (HttpURLConnection) url.openConnection();
		        	urlConnection.setRequestMethod("GET");
		        	urlConnection.setDoOutput(true);
		        	urlConnection.setDoInput(true);
		        	urlConnection.connect();
		            InputStream inStream = null;
		            
		            
		            
		            // problem code
		            inStream = urlConnection.getInputStream();
		            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
		            
		            String temp = "";
		            while ((temp = bReader.readLine()) != null){
		    	        //System.out.println("The response is: " + response);
		            	response += temp;
		        	}
		            //response = response.trim();
		            bReader.close();
		            inStream.close();
		            urlConnection.disconnect();
		            //object = (JSONObject) new JSONTokener(response).nextValue();
		        }

		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
				
			}//end else
			

	        //print something extra if there was a Search by Price
			toPassSearchResults(typeOfSearch, args[0], response);
			 return null;
		} 
		
	}//end private class MyAsyncTask
	
	public void sendPrefMessage(View view) {
        // Do something in response to button
	    EditText editText = (EditText) findViewById(R.id.edit_foodsearch);
    	String message = editText.getText().toString();
        new MyAsyncTask("yelp").execute(message,null, null);
		//Toast.makeText(this, "Yelp request complete", Toast.LENGTH_LONG).show();

    }
	
	public void sendPriceMessage(View view) {
        // Do something in response to button
	
		EditText editText = (EditText) findViewById(R.id.edit_foodsearch);
    	String message = editText.getText().toString();
    	
    	//Initiates Google places request
    	new MyAsyncTask("google").execute(message,null, null);
       // Toast.makeText(this, "Google request complete", Toast.LENGTH_LONG).show();
    
    }
	
	public void toPassSearchResults(String searchType, String searchTerm, String jsonResult){
		Intent intent = new Intent(this, DisplaySearchResultsActivity.class);
    	intent.putExtra(SEARCH_TERM_MESSAGE, searchTerm);
    	intent.putExtra(SEARCH_TYPE, searchType);
    	intent.putExtra(SEARCH_RESULTS_MESSAGE, jsonResult);
    	startActivity(intent);	
	}
		
	

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Please make sure your location or GPS services are enabled and try again.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		
	}

	@Override
	public void onDisconnected() {
		
	}
	
	  private void initTypeFace(){
		 
		  Typeface journal = Typeface.createFromAsset(getAssets(), "fonts/journal-webfont.ttf");
	      Typeface effra = Typeface.createFromAsset(getAssets(), "fonts/Effra-Regular.ttf");
	      Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
	      TextView searchBar = (TextView)findViewById(R.id.search_bar);
	      searchBar.setTypeface(journal);
	      
	      
	      TextView edit = (TextView)findViewById(R.id.edit_foodsearch);
	      edit.setTypeface(effra);
	      
	      TextView greet = (TextView)findViewById(R.id.greeting);
	      greet.setTypeface(effra);
	      
	      TextView searchPref = (TextView)findViewById(R.id.search_by_pref);
	      searchPref.setTypeface(roboto);
	      
	      TextView searchPrice = (TextView)findViewById(R.id.search_by_price);
	      searchPrice.setTypeface(roboto);
	      
	      
	  }

}
