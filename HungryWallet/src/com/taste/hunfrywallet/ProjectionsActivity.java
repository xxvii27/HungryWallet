package com.taste.hunfrywallet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taste.hunfrywallet.model.Budget;
import com.taste.hunfrywallet.model.Restaurant;
import com.taste.hunfrywallet.model.User;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ProjectionsActivity extends Activity {

	private ListView restList;
	private ArrayList<String> restInfo;
	private ArrayAdapter<String> billsAdapter;
	private List<ParseObject> userRestaurants;
	private TextView balance_text;
	private Budget userBudget;

	
	public ProjectionsActivity() {
		// TODO Auto-generated constructor stub
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_projections);
		
		// CHANGE FONT
		Typeface roboto = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");
		
        TextView balance = (TextView)findViewById(R.id.TextView04);
        balance.setTypeface(roboto);
        
        TextView amt = (TextView)findViewById(R.id.balance_amount);
        amt.setTypeface(roboto);
        
        // END CHANGE FONT
		
		restList = (ListView) findViewById(R.id.listView1);
		balance_text = (TextView) findViewById(R.id.balance_amount);
	}
	
	protected void onResume(){
		super.onResume();
		
		try {
			userBudget = (Budget) ParseQuery.getQuery("Budget").get(
					(String) (ParseUser.getCurrentUser().get("BudgetID")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Double balance = userBudget.getDouble("remainingBudget");
		balance_text.setText(String.format("%.2f", balance.floatValue()));
		
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
		query.whereEqualTo("user", User.getID());
		query.addDescendingOrder("visits");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> restList, ParseException e) {
				if (e == null) {
					userRestaurants = restList;
					if(userRestaurants != null)
						buildListView();
				} else {
					Log.d("restaurant", "Error: " + e.getMessage());
				}
			}
		});

	}
	
	private void buildListView() {
		String restStats;
		Restaurant tmpRest;
		int visitsLeft = 0;
		double averageSpent = 0;
		
		restInfo = new ArrayList<String>();

		// Format Restaurants list
		for (int i = 0; i< userRestaurants.size(); i++) {
			tmpRest = (Restaurant) userRestaurants.get(i);
			averageSpent = tmpRest.getDouble("totalExpense") / tmpRest.getInt("visits");
			visitsLeft =(int) (userBudget.getDouble("remainingBudget") / averageSpent);
			if(visitsLeft < 0)
				visitsLeft = 0;
			restStats = tmpRest.getString("name");
			restStats = restStats + "\n" + "you can eat here " + visitsLeft + " more times";
			restInfo.add(restStats);
		}
		
		restList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, restInfo){
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		    	
		    	int bkground = Color.rgb(247, 243, 235);
				Typeface roboto = Typeface.createFromAsset(getAssets(),
						"fonts/Roboto-Light.ttf");
				Typeface effra = Typeface.createFromAsset(getAssets(),
						"fonts/LANENAR_.ttf");
		        TextView textView = (TextView) super.getView(position, convertView, parent);
		        textView.setTextColor(Color.BLACK);
		        textView.setTextSize(18);
		        textView.setTypeface(roboto);
		        textView.setBackgroundColor(bkground);
		        textView.setPadding(20, 30, 20, 30);
		        
		        
		        
		          
		        return textView;
		    }
		});
		
	}
}
