package com.taste.hunfrywallet;

import java.text.DecimalFormat;

import android.app.*; 
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taste.hunfrywallet.PieGraph;
import com.taste.hunfrywallet.model.Bill;
import com.taste.hunfrywallet.model.Budget;

public class AnalysisActivity extends TabActivity
{
	private Button logOut;
    private AlertDialog.Builder alert;
	private TabHost tabHost;
	private TabSpec summary, graph, projections;
	private static final int tabPadding = 15;
	private int selectedRed = Color.rgb(219, 81, 72);
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysis_activity);
		initTypeFace();
		
		//Getting logout Button
		logOut = (Button) findViewById(R.id.logout_button);
		
		//Setting up Tabs
	    tabHost=getTabHost();
	    
	    //projections Tab
  		projections=tabHost.newTabSpec("Tab 1");
  		projections.setIndicator("Projections");
  		Intent prjctionIntent = new Intent(getApplicationContext(), ProjectionsActivity.class);
  		projections.setContent(prjctionIntent);

		//summary Tab
	    summary=tabHost.newTabSpec("Tab 2");
		summary.setIndicator("Summary");
		Intent summaryIntent = new Intent(getApplicationContext(), SummaryActivity.class);
		summary.setContent(summaryIntent);

		//graph Tab
		graph=tabHost.newTabSpec("Tab 3");
		graph.setIndicator("Graphs");
		Intent graphIntent = new Intent(getApplicationContext(), GraphsActivity.class);
		graph.setContent(graphIntent);

		//Adding to host
		tabHost.addTab(projections);
		tabHost.addTab(summary);
		tabHost.addTab(graph);
		
		
		int tabBackground = Color.rgb(161, 31, 20);
		//int selectedGray = Color.rgb(146, 146, 146);
		
		tabHost.setCurrentTab(1);
		
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedRed);
		
		for(int i = 0; i<tabHost.getTabWidget().getChildCount(); i++){
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 80;
			tabHost.getTabWidget().getChildAt(i).setPadding(tabPadding, tabPadding, tabPadding,tabPadding); 
			if(i!=tabHost.getCurrentTab())
			   tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
			
		
		}
		
		// END OF TAB COLORS
		
		
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
					tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedRed);
					tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);
					tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);
					
				}
				//if tab2 (analysis) selected
				else if(tabId.equals("Tab 2")){
					
					tabHost.setCurrentTab(1);
					tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedRed);
					tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
					tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);
				}
				//if tab3 (search selected)
				else{
					
					tabHost.setCurrentTab(2);
					tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(selectedRed);
					tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
					tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);

					
				}			
			}
		});
		
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
                    	finish();
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
	
	// CINDY ADDED THIS TO CHANGE THE FONTS
		private void initTypeFace() {

			Typeface didot = Typeface.createFromAsset(getAssets(),
					"fonts/didot_italic.ttf");
			Typeface effra = Typeface.createFromAsset(getAssets(),
					"fonts/Effra-Regular.ttf");
			Typeface sugarcube = Typeface.createFromAsset(getAssets(),
					"fonts/SugarcubesRegular.ttf");
			Typeface journal = Typeface.createFromAsset(getAssets(),
					"fonts/journal-webfont.ttf");

			TextView tnb = (TextView) findViewById(R.id.projections_banner);
			tnb.setTypeface(journal);
			tnb.setTextSize(33);


		}
		
		// END OF CINDY'S STUFF
}
