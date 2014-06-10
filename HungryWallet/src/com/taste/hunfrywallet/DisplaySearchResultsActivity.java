package com.taste.hunfrywallet;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.taste.hunfrywallet.model.Restaurant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;

public class DisplaySearchResultsActivity extends ListActivity {

	/* Used to display the search results */
	private ListView listView;
	/* ArrayList gets populated with restaurant names from API query */
	private ArrayList<String> restaurantNamesArr;
	private ArrayList<Restaurant> restaurantResults;
	/* Used to attach an array to the listView */
	private ArrayAdapter<String> adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_search_results);
		initTypeFace();

		// Show the Up button in the action bar.
		// setupActionBar();

		Intent intent = getIntent();
		String message = intent
				.getStringExtra(SearchActivity.SEARCH_TERM_MESSAGE);
		TextView searchByPrefTerm = (TextView) findViewById(R.id.searchbypref_term);
		searchByPrefTerm.setText("Search results for: \"" + message + " \"");

		// Change title of view based on type of search
		String searchType = intent.getStringExtra(SearchActivity.SEARCH_TYPE);
		String jsonString = intent
				.getStringExtra(SearchActivity.SEARCH_RESULTS_MESSAGE);
		// Call for doInBackground() in MyAsyncTask to be executed
		new MyAsyncTask(searchType).execute(jsonString);
	}

	/* This thread takes care of parsing the search results for the info we want */
	private class MyAsyncTask extends AsyncTask<String, Void, Void> {
		String typeOfSearch;

		public MyAsyncTask(String searchType) {
			typeOfSearch = searchType;
		}

		@Override
		protected Void doInBackground(String... args) {
			restaurantNamesArr = new ArrayList<String>();
			restaurantResults = new ArrayList<Restaurant>();

			/* Info we query */
			String name;
			String phoneNumber;
			String address = null;
			String price_level;
			String url;
			
			

			if (typeOfSearch.equals("yelp")) {
				//System.out.println("Yelp API search!");

				// Holds Key Value pairs from a JSON source
				JSONObject jsonObject;
				try {

					/*
					 * From Yelp, we are gathering the the restaurant name,
					 * their phone number, their address, their url,
					 */

					// Get the root JSONObject
					jsonObject = new JSONObject(args[0]);

					// Get the JSON object named businesses
					JSONArray businessList = jsonObject
							.getJSONArray("businesses");

					boolean isInList = false;
					
					for (int i = 0; i < businessList.length(); i++) {
						for (int p = 0; p < restaurantNamesArr.size(); p++) {
							// Check for doubles that are already in the list
							if ((restaurantNamesArr.get(p))
									.equals((businessList.getJSONObject(i))
											.getString("name"))) {
								isInList = true;
								break;
							}
						}
						if (!isInList) {
							name = (businessList.getJSONObject(i))
									.getString("name");
							phoneNumber = (businessList.getJSONObject(i))
									.getString("display_phone");
							url = (businessList.getJSONObject(i))
									.getString("url");

							// getting location which is super nester
							JSONObject locationJSONObj = new JSONObject(
									(businessList.getJSONObject(i))
											.getString("location"));
							JSONArray locArray = locationJSONObj
									.getJSONArray("display_address");

							StringBuilder buildLoc;
							if (locArray.get(0) != null) {
								buildLoc = new StringBuilder(
										(String) locArray.get(0));
								for (int index = 1; index < locArray.length(); index++) {
									buildLoc.append(" " + locArray.get(index));
								}
								address = buildLoc.toString();
							}

							restaurantNamesArr.add(name);
							restaurantResults.add(new Restaurant(name,
									phoneNumber, address, null, url));
							/*System.out.println("Adding "
									+ restaurantNamesArr.size()
									+ "th rest to array: " + name);
							System.out.println("It's phone is: " + phoneNumber);
							System.out.println("It's address is: " + address);*/
						}

					}

					/*for (int i = 0; i < restaurantNamesArr.size(); i++) {
						System.out.println(restaurantNamesArr.get(i));
					}*/

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					/*System.out
							.println("No results found for your current location.");*/
					e.printStackTrace();
				}
			}// end if
			else {
				// GOOGLE PLACES API USED FOR SEARCH BY PRICE
				// Holds Key Value pairs from a JSON source
				//System.out.println("Google API call!");
				JSONObject jsonObject;
				try {

					// Get the root JSONObject
					jsonObject = new JSONObject(args[0]);

					String returnCode = jsonObject.getString("status");

					/* ERROR CODE HANDLING FOR GOOGLE PLACES API */

					if (returnCode == "ZERO_RESULTS") {
						// display no results found message
						//System.out.println("Zero results for this search.");						
						return null;

					} else if (returnCode == "OVER_QUERY_LIMIT") {
						// display error that Hungry Wallet is experience heavy
						// usage so it's temporarily down
						return null;
					} else if (returnCode == "REQUEST_DENIED"
							|| returnCode == "INVALID_REQUEST") {
						// display some generic error screen
						return null;
					} else {
						// it's ok?

						// Check to see if it is a restaurant or not
						boolean isRestaurant = false;

						// Get the JSON object named query
						JSONArray businessList = jsonObject
								.getJSONArray("results");

						// JSONArray placeType;

						for (int i = 0; i < businessList.length(); i++) {
							boolean isInList = false;
							for (int p = 0; p < restaurantNamesArr.size(); p++) {
								// Check for doubles that are already in the
								// list
								if ((restaurantNamesArr.get(p))
										.equals((businessList.getJSONObject(i))
												.getString("name"))) {
									isInList = true;
									break;
								}
							}
							// If it hasn't been added yet, add it to the list
							if (!isInList) {
								name = (businessList.getJSONObject(i))
										.getString("name");
								address = (businessList.getJSONObject(i))
										.getString("vicinity");
								price_level = (businessList.getJSONObject(i))
										.getString("price_level");

								restaurantNamesArr.add(name);
								restaurantResults.add(new Restaurant(name,
										null, address, Integer
												.parseInt(price_level), null));

								/*System.out.println("Adding "
										+ restaurantNamesArr.size()
										+ "the rest to array: " + name + " at "
										+ address);*/
							}
						}

						/*for (int i = 0; i < restaurantNamesArr.size(); i++) {
							System.out.println(restaurantNamesArr.get(i));
						}*/
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// System.out.println("Size of the names array: " +
			// restaurantNamesArr.size());
			return null;

		}// end doInBackground

		/*
		 * After the search has occurred and results array is populated, we want
		 * to display the results using a ListView on the screen. We change the
		 * color of the text in the TextViews of the the ListView to be black
		 * because it is white by default
		 */
		protected void onPostExecute(Void result) {
			listView = (ListView) getListView();
			// listView = (ListView) findViewById(R.id.list);

			// set up ArrayAdapter to use the custom layout for each search
			// result
			if(restaurantNamesArr.size() == 0){
				((TextView)findViewById(R.id.searchbypref_term)).setText("No results found.\n" +   "Please check your location services settings or search other related terms.");
				((TextView)findViewById(R.id.searchbypref_term)).setGravity(0x01);
				((TextView)findViewById(R.id.searchbypref_term)).setTextSize(25);
				return;
			}
			
			if (restaurantNamesArr != null) {
				listView.setAdapter(adapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_list_item_1, restaurantNamesArr) {

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {

						View view = super
								.getView(position, convertView, parent);
						TextView text = (TextView) view
								.findViewById(android.R.id.text1);
						text.setTextColor(Color.BLACK);
						
						//Cindy added this to change the font
						Typeface roboto = Typeface.createFromAsset(getAssets(),
								"fonts/Roboto-Light.ttf");
						text.setTypeface(roboto);
						text.setTextColor(Color.BLACK);
						text.setBackgroundColor(Color.WHITE);
						
						// end of cindy's stuff

						return view;
					}// end getView function

				}); // end setAdapter call
			}// end if
		}// end onPostExecute function

	}// end internal AsyncTask class

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
		getMenuInflater().inflate(R.menu.display_search_results, menu);
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

	// TODO: REFACTOR THIS METHOD
	public void onListItemClick(ListView lv, View v, int position, long id) {

		Restaurant restObj = restaurantResults.get(position);
		super.onListItemClick(lv, v, position, id);
		/*System.out.println("Name of result clicked was: "
				+ restaurantNamesArr.get(position));*/

		// Pop up a dialog with more info
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater inflater = this.getLayoutInflater();
		View dialogLayout;
		if (restObj.getPriceLevel() == null)
			dialogLayout = inflater
					.inflate(R.layout.dialog_search_result, null);
		else {
			dialogLayout = inflater
					.inflate(R.layout.dialog_google_result, null);
		}
		builder.setView(dialogLayout);
		builder.setTitle(restaurantNamesArr.get(position));

		// Yelp
		if (restObj.getPriceLevel() == null) {

			// address
			if (restObj.getAddress() != null) {
				TextView phoneView = (TextView) dialogLayout
						.findViewById(R.id.resultPhone);
				phoneView.setText("Phone: " + restObj.getPhoneNumber());
			} else {
				TextView phoneView = (TextView) dialogLayout
						.findViewById(R.id.resultPhone);
				phoneView.setText("Phone: Not Available");
			}

			// address
			if (restObj.getAddress() != null) {
				TextView addressView = (TextView) dialogLayout
						.findViewById(R.id.resultAddress);
				addressView.setText("Address: " + restObj.getAddress());
			} else {
				TextView addressView = (TextView) dialogLayout
						.findViewById(R.id.resultAddress);
				addressView.setText("Address: Not Available");
			}

			// url
			if (restObj.getUrl() != null) {
				TextView urlView = (TextView) dialogLayout
						.findViewById(R.id.resultUrl);
				urlView.setText("Url: " + restObj.getUrl());
			} else {
				TextView urlView = (TextView) dialogLayout
						.findViewById(R.id.resultUrl);
				urlView.setText("Url: Not Available");
			}

		} else {
			
			builder.setMessage("Price Scale: " + (restObj.getPriceLevel() + 1)
					+ " dollar signs");

			// address
			if (restObj.getAddress() != null) {
				TextView addressView = (TextView) dialogLayout
						.findViewById(R.id.resultAddress);
				addressView.setText("Address: " + restObj.getAddress());
			} else {
				TextView addressView = (TextView) dialogLayout
						.findViewById(R.id.resultAddress);
				addressView.setText("Address: Not Available");
			}

		}

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	
	private void initTypeFace() {
		Typeface journal = Typeface.createFromAsset(getAssets(),
				"fonts/journal-webfont.ttf");
		Typeface effra = Typeface.createFromAsset(getAssets(),
				"fonts/Effra-Regular.ttf");

		Typeface sugarcube = Typeface.createFromAsset(getAssets(),
				"fonts/SugarcubesRegular.ttf");
		Typeface roboto = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");

		TextView titleText = (TextView) findViewById(R.id.TextView01);
		titleText.setTypeface(journal);
		titleText.setTextSize(34);
		
		
		TextView resmessage = (TextView) findViewById(R.id.searchbypref_term);
		resmessage.setTypeface(effra);

	}
}



