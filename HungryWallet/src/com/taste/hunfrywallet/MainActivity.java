package com.taste.hunfrywallet;

import java.util.ArrayList;
import java.util.Date;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taste.hunfrywallet.model.Bill;
import com.taste.hunfrywallet.model.Budget;
import com.taste.hunfrywallet.model.Restaurant;
import com.taste.hunfrywallet.model.User;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends TabActivity {

	public static final int DAYS_30 = 2592000;

	private static ParseUser parseUser;
	
	public final static String BUDGET_OBJECT = "com.taste.hunfrywallet.BUDGET_OBJECT";
	
	private TabHost tabHost;
	private TabSpec record, analysis, search;
	private static final int tabPadding = 15;
	private int selectedGray;
	
	private Budget userBudget; 
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Create User
		parseUser = ParseUser.getCurrentUser();
		
	      Budget budget;
			// Start an intent for the logged in activity
	    	try {
				budget = (Budget) ParseQuery.getQuery("Budget").get((String) (ParseUser.getCurrentUser().get("BudgetID")));
				
				Date now = new Date();
				Date created = new Date(budget.getDate("created").toString());
				long time = (now.getTime() - created.getTime()) / 1000;
				if(time >= DAYS_30){
					startActivity(new Intent (this, SubmitBudgetActivity.class));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		//Setting up Tabs
	    tabHost=getTabHost();

		//Record Tab
	    record=tabHost.newTabSpec("Tab 1");
		record.setIndicator("RECORDS");
		Intent recordIntent = new Intent(getApplicationContext(), RecordActivity.class);
		record.setIndicator("", getResources().getDrawable(R.drawable.icon_record_tab));
		record.setContent(recordIntent);

		//Analysis Tab
		analysis=tabHost.newTabSpec("Tab 2");
		analysis.setIndicator("ANALYSIS");
		Intent analysisIntent = new Intent(getApplicationContext(), AnalysisActivity.class);
		analysis.setIndicator("", getResources().getDrawable(R.drawable.icon_analysis_tab));
		analysis.setContent(analysisIntent);

		//Search Tab
		search=tabHost.newTabSpec("Tab 3");
		search.setIndicator("SEARCH");
		Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
		search.setIndicator("" , getResources().getDrawable(R.drawable.icon_search_tab));
		search.setContent(searchIntent);

		//Adding to host
		tabHost.addTab(record);
		tabHost.addTab(analysis);
		tabHost.addTab(search);
		
		selectedGray = Color.rgb(221,221,221);
		//int selectedGray = Color.rgb(146, 146, 146);
		
		tabHost.setCurrentTab(1);
		tabHost.setMinimumHeight(120);
		
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedGray);
		
		for(int i = 0; i<tabHost.getTabWidget().getChildCount(); i++){
			
			tabHost.getTabWidget().getChildAt(i).setPadding(tabPadding, tabPadding, tabPadding,tabPadding); 
			if(i!=tabHost.getCurrentTab())
			   tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
			
		
		}
		
		
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			//Listener (start activities here)
			//Fragment can be used instead of activities
			//but need ViewPager and Pager adapter combined
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				//if tab 1 (record) selected
				
				if(tabId.equals("Tab 1"))
				{
				
					tabHost.setCurrentTab(0);
					tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedGray);
					tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);
					tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);
					
				}
				//if tab2 (analysis) selected
				else if(tabId.equals("Tab 2")){
					tabHost.setCurrentTab(1);
					tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedGray);
					tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
					tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);
				}
				//if tab3 (search selected)
				else{
					tabHost.setCurrentTab(2);
					tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedGray);
					tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
					tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);

					
				}			
			}
		});
    }
	
}
