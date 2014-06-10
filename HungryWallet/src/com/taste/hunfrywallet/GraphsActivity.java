package com.taste.hunfrywallet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.kenyang.piechart.PieChart;
import net.kenyang.piechart.PieChart.OnSelectedListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dacer.androidcharts.LineView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taste.hunfrywallet.model.Budget;
import com.taste.hunfrywallet.model.LegendAdapter;
import com.taste.hunfrywallet.model.User;

public class GraphsActivity extends ActionBarActivity {

	private ExpandableHeightGridView gridView;
	
    private List<ParseObject> userRestaurants;
	private Budget userBudget;
	private List<ParseObject> userBills;
	private ListView billList;
	private ArrayList<String> billsInfo;
	private Double expenseOfBill;

    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
    }
    
    
    @Override
    protected void onResume() {
    	super.onResume();
    	setContentView(R.layout.graphs_activity);
    	
    	initTypeFace();
        // query for total expense
        try {
			userBudget = (Budget) ParseQuery.getQuery("Budget").get(
					(String) (ParseUser.getCurrentUser().get("BudgetID")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        // query a list of restaurants
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
		query.whereEqualTo("user", User.getID());
		// sort the list of restaurants by total expenses (most to least)
		query.addDescendingOrder("totalExpense");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> restList, ParseException e) {
				if (e == null) {
					
					double totalBudget = userBudget.getDouble("totalBudget");
					double remainingBudget = userBudget.getDouble("remainingBudget");
					double totalExpense = 0;
					
					if(remainingBudget < 0) {
						totalExpense = totalBudget + (-1) * remainingBudget;
					}
					else {
						totalExpense = totalBudget - remainingBudget;
					}
			
					userRestaurants = restList;
					
					ArrayList<String> restNames = new ArrayList<String>();
					ArrayList<Double> restExpenses = new ArrayList<Double>();
					
					for(int i = 0; i < userRestaurants.size(); ++i) {
					  restNames.add(userRestaurants.get(i).getString("name"));
					  restExpenses.add(userRestaurants.get(i).getDouble("totalExpense"));
					}
					
					displayPieChart(totalExpense, restNames, restExpenses);
				}
				else {
					
				}
			}
			});
		
		
		query = ParseQuery.getQuery("Bill");
		query.whereEqualTo("BudgetID", userBudget.getObjectId());
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> billList, ParseException e) {
				if (e == null) {
					userBills = billList;

					buildLineView();

				}
				else {
					Log.d("bills", "Error: " + e.getMessage());
				}
			}
		});

    }

    	


	private void displayPieChart(double totalExpense, ArrayList<String> restName, ArrayList<Double> restExpenses) {
		int numberOfRests = restName.size();
		if(numberOfRests <= 0) {
			// display image "You haven't eat at restaurants yet!"
		}
		else if(numberOfRests < 5) {
			// get the list of restaurants for display
			final String[] restList = new String[numberOfRests];

			for(int i = 0; i < numberOfRests; ++i) {
				restList[i] = restName.get(i);
			}

			// calculate each slice of the pie
			final ArrayList<Float> alPercentage = new ArrayList<Float>();
			int percent = 0;
			int others = 100;
			
			for(int i = 0; i < numberOfRests - 1; ++i) {
				percent = (int) ((restExpenses.get(i) / totalExpense) * 100.00);
				alPercentage.add((float) percent);
				others -= percent;
			}
	
			alPercentage.add((float) others);


			// display pie graph
		    final PieChart pie = (PieChart) findViewById(R.id.pieChart);
		    
		    try {
		    	// setting data
		    	pie.setAdapter(alPercentage);

		    	// setting a listener           	  
		    	pie.setOnSelectedListener(new OnSelectedListener() {
		    		public void onSelected(int iSelectedIndex) {
		    			float fPercent = alPercentage.get(iSelectedIndex);
		        	      int percent = (int)fPercent;
		                  Toast.makeText(getApplicationContext(), 
		                                 restList[(iSelectedIndex )] + " - " + percent + "%", 
		                                 Toast.LENGTH_SHORT).show();

		    		}
		    	});  

		     } catch (Exception e) {
		          if (e.getMessage().equals(PieChart.ERROR_NOT_EQUAL_TO_100)){
		            Log.e("kenyang","percentage is not equal to 100");
		          }
		     }

		    // display legend
			 gridView = (ExpandableHeightGridView) findViewById(R.id.gridView1);
			 
			 gridView.setExpanded(true);

			 ArrayList<Integer> colors = pie.getColor();
			 
			 gridView.setAdapter(new LegendAdapter(this, android.R.layout.simple_list_item_1,
					 									restList, colors));


			 gridView.setOnItemClickListener(new OnItemClickListener() {
				 public void onItemClick(AdapterView<?> parent, View v,
						 									int position, long id) {
					 float fPercent = alPercentage.get(position);
	        	      int percent = (int)fPercent;
					 Toast.makeText(getApplicationContext(), 
							        ((TextView) v).getText() + " - " + percent + "%", 
							   	    Toast.LENGTH_SHORT).show();
			 }

		     });

		}
		else {
			// get the list of restaurants for display
			final String[] restList = new String[6];
			for(int i = 0; i < 5; ++i) {
				restList[i] = restName.get(i);
			}
			restList[5] = new String("Others"); 
			
			// calculate each slice of the pie
			final ArrayList<Float> alPercentage = new ArrayList<Float>();
			int percent = 0;
			int others = 100;
			
			for(int i = 0; i < 5; ++i) {
				percent = (int)((restExpenses.get(i) / totalExpense) * 100);
				alPercentage.add((float) percent);
				others -= percent;
			}
			alPercentage.add((float) others);
			
			// display pie graph
	        final PieChart pie = (PieChart) findViewById(R.id.pieChart);
	        
	        try {
	        	  // setting data
	        	  pie.setAdapter(alPercentage);
	
	        	  // setting a listener           	  
	        	  pie.setOnSelectedListener(new OnSelectedListener() {
	        		  
	        	    public void onSelected(int iSelectedIndex) {
	        	      float fPercent = alPercentage.get(iSelectedIndex);
	        	      int percent = (int)fPercent;
	                  Toast.makeText(getApplicationContext(), 
	                		         restList[(iSelectedIndex )] + " - " + percent + "%", 
	                		         Toast.LENGTH_SHORT).show();
	        	    }
	        	    
	        	  });  
	        	} catch (Exception e) {
	        	  if (e.getMessage().equals(PieChart.ERROR_NOT_EQUAL_TO_100)){
	        	    Log.e("kenyang","percentage is not equal to 100");
	        	  }
	        	}
	        
	        
	        // display legend
			gridView = (ExpandableHeightGridView) findViewById(R.id.gridView1);
			
			gridView.setExpanded(true);
			
			ArrayList<Integer> colors = pie.getColor();
	 
			gridView.setAdapter(new LegendAdapter(this, android.R.layout.simple_list_item_1,
					restList, colors));
			
			gridView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
					float fPercent = alPercentage.get(position);
	        	      int percent = (int)fPercent;
				   Toast.makeText(getApplicationContext(),
				               	  ((TextView) v).getText() + " - " + percent + "%", 
				       	          Toast.LENGTH_SHORT).show();
				 }
		    });
		}
	}
	


	private void buildLineView()
	{
		// build list of bills (not accumulated yet)
		ArrayList<Double> expensesPerDay = new ArrayList<Double>();
		
		// list of strings for the bottom of the graph
		ArrayList<String> strList = new ArrayList<String>();

		// Get the days for the period of the analysis
		String dateStarted = userBudget.getCreatedAt().toString();
		String firstDay = dateStarted.substring(8,10);  // Get only the day
		String firstMonth = dateStarted.substring(4,7); // Get month
		int maxDays = 31;
		
		// Find the max number of days depending on the month
		if(firstMonth == "Apr" || firstMonth == "Jun" || firstMonth == "Sep" || firstMonth == "Nov")
		{
			maxDays = 30;
		}
		
		else if(firstMonth == "Feb")
		{
			maxDays = 28;
		}

		
		Integer day = Integer.parseInt(firstDay);
		for(int i = 0; i < 30; i++)
		{
			String stringDay = day.toString();
			strList.add(stringDay);
			if(day == maxDays)
			{
				day = 1;
			}
			else
			{
				day++;
			}
		}
		
		// Get the number day of this day.
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_MONTH);
		
		// Find out how many days have passed since the start.
		int numOfDaysSinceStarted = 0;
		for(int index = 0; index < 30; index++)
		{
			if(strList.get(index) == Integer.toString(today))
			{
				numOfDaysSinceStarted = index + 1;
			}
		}
		
		// If it contains, then go through bills and compare, 
		// and then if date > day we want, then add 0. else, 
		for(int j = 0; j < numOfDaysSinceStarted; j++)
		{
			expenseOfBill = 0.0;
			
			String dayOnGraph = strList.get(j);
			String appendedDay = "0" + strList.get(j);  // For checking singular numbers

			// Add the bills for each day
			for(int k = 0; k < userBills.size(); k++)
			{
				// Get day of the bill
				String dateOfBill = (userBills.get(k)).getCreatedAt().toString();
				String dayOfBill = dateOfBill.substring(8,10);  
				
				// If the days match, add it to the expense
				if(dayOfBill.equals(dayOnGraph) || dayOfBill.equals(appendedDay))
				{
					expenseOfBill += userBills.get(k).getDouble("expense");
				}
			}
			// Add to the list of data for each day
			expensesPerDay.add(expenseOfBill);
		}

		
		// Pass the data to the library
		ArrayList<ArrayList<Double>> dataLists = new ArrayList<ArrayList<Double>>();
		dataLists.add(expensesPerDay);
		
		LineView lineView = (LineView)findViewById(R.id.line_view);
		lineView.setDrawDotLine(false); //optional
		lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
		Log.d("bills", "Size of strList: " +  strList.size()); 
		lineView.setBottomTextList(strList);
		lineView.setDataList(dataLists);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initTypeFace() {

		Typeface roboto = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");

		TextView heading1 = (TextView) findViewById(R.id.textView1);
		heading1.setTypeface(roboto);
		TextView heading2 = (TextView) findViewById(R.id.textView2);
		heading2.setTypeface(roboto);

	}

}
