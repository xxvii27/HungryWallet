package com.taste.hunfrywallet;

import java.text.DecimalFormat;

import android.app.*; 
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taste.hunfrywallet.PieGraph;
import com.taste.hunfrywallet.model.Budget;

public class SummaryActivity extends Activity
{
	// Declaring text views/instance vars
	private TextView remainingText; 
	
	//Variables for text boxes
	private TextView spentLabel; 
	private TextView spent; 
	private TextView budgLabel; 
	private TextView budg; 
	
	private Budget budget; 
	private Double balance;
	private Double userBudget; 
	private PieGraph pg;
	private PieSlice spentSlice; 
	private PieSlice remainingSlice; 
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary_activity); //Set to activity 
		loadActivity(); //Load the activity
		initTypeFace();
	}
	
	/* Method to load the activity */
	public void loadActivity()
	{
		this.pg = (PieGraph)findViewById(R.id.piegraph);
		remainingSlice = new PieSlice();
		spentSlice = new PieSlice();
		spentLabel = (TextView)findViewById(R.id.textRemaining);
		budgLabel = (TextView)findViewById(R.id.textBudget);
		budg = (TextView)findViewById(R.id.budget);
		spent = (TextView)findViewById(R.id.remaining);
		remainingText = (TextView)findViewById(R.id.percentageText); 
		
		spentLabel.setText("AMOUNT SPENT");
		budgLabel.setText("ORIGINAL BUDGET");
		
		//Get user's budget object
		try {
			budget = (Budget) ParseQuery.getQuery("Budget").get((String) (ParseUser.getCurrentUser().get("BudgetID")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userBudget = budget.getDouble("totalBudget"); 
		balance = budget.getDouble("remainingBudget");
		
		
		
		//Check if budget has already been spent 
		if(balance.floatValue() < 0)
		{
			//Zero out the graph 
			remainingSlice.setValue(0);
			spentSlice.setValue(1);
			remainingSlice.setColor(Color.RED); 
			spentSlice.setColor(Color.RED);
			pg.setPadding(1); 
			pg.addSlice(remainingSlice); 
			pg.addSlice(spentSlice);
			
			spent.setTextColor(Color.RED);
			spent.setText("$ "+String.format("%.2f", userBudget.floatValue()+(-1)*balance.floatValue()));
			budg.setText("$ "+String.format("%.2f", userBudget.floatValue()));
			
			String pString = "OVER BUDGET BY\n$"+String.format("%.2f", (-1)*balance.floatValue());
			remainingText.setText(pString);
			return; 
		}
		
		
		
		remainingSlice.setColor(Color.parseColor("#fc7758"));//Set color of "remaining slice"
		remainingSlice.setValue(balance.floatValue());
		pg.addSlice(remainingSlice);

		//Log.e("graph", "VALUE OF SLICE #1: " + slice.getValue()); 
		spentSlice.setColor(Color.parseColor("#FFFFFF"));//Set color of "total spent slice"
		spentSlice.setValue(userBudget.floatValue()-balance.floatValue());
		if( spentSlice.getValue() == 0.0f)//Check for full budget bug, if found resolve with padding
		{
			pg.setPadding(1);
		}
		else
			pg.setPadding(0); //Reset padding
		
		pg.addSlice(spentSlice);
		
		//Code to manipulate percentageText
		String pString = "Remaining\n$"+String.format("%.2f", balance.floatValue());
		remainingText.setText(pString);
		
		//Update/declare text boxes		
		budg.setText("$ "+String.format("%.2f", userBudget.floatValue()));
		spent.setText("$ "+String.format("%.2f", userBudget.floatValue()-balance.floatValue()));
	}
	
	/*Method to update slice values*/
	private void updateSlices()
	{
		
		budg = (TextView)findViewById(R.id.budget);
		spent = (TextView)findViewById(R.id.remaining);
		remainingText = (TextView)findViewById(R.id.percentageText); 
		
		//Get user's budget object
		try {
			budget = (Budget) ParseQuery.getQuery("Budget").get((String) (ParseUser.getCurrentUser().get("BudgetID")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		userBudget = budget.getDouble("totalBudget"); 
		balance = budget.getDouble("remainingBudget");
		
		
		//Check if amount spend is beyond budget
		if(balance.floatValue() < 0)
		{
			//Zero out the graph 
			remainingSlice.setValue(0);
			spentSlice.setValue(1);
			remainingSlice.setColor(Color.RED); 
			spentSlice.setColor(Color.RED); 
			pg.setPadding(1); 
			
			spent.setTextColor(Color.RED);
			spent.setText("$ "+String.format("%.2f", userBudget.floatValue()+(-1)*balance.floatValue()));
			budg.setText("$ "+String.format("%.2f", userBudget.floatValue()));
			
			String pString = "OVER BUDGET BY\n$"+String.format("%.2f", (-1)*balance.floatValue());
			remainingText.setText(pString);
			return; 
		}
				
		
		this.remainingSlice.setValue(balance.floatValue()); //Update "remaining"'
		this.spentSlice.setValue(userBudget.floatValue()-balance.floatValue()); //Update "spent"
		
		if( spentSlice.getValue() == 0.0f)//Check for full budget bug, if found resolve with padding
		{
			pg.setPadding(1);
		}
		else
			pg.setPadding(0); //Reset padding
		
		
		//Code to manipulate percentageText
		String pString = "Remaining\n$"+String.format("%.2f", balance.floatValue());
		remainingText.setText(pString);
		
		
		
		//Update/declare text boxes
		budg.setText("$ "+String.format("%.2f", userBudget.floatValue()));
		
		spent.setText("$ "+String.format("%.2f", userBudget.floatValue()- balance.floatValue()));
	}
	
	/*Method called upon returning to tab, updates graph*/
	public void onResume()
	{
		super.onResume();
		updateSlices(); 
		pg.invalidate();
	}
	
	// CINDY ADDED THIS TO CHANGE THE FONTS
			private void initTypeFace() {

				Typeface roboto = Typeface.createFromAsset(getAssets(),
						"fonts/Roboto-Light.ttf");

				TextView bud = (TextView) findViewById(R.id.budget);
				bud.setTypeface(roboto);
				
				TextView textbud = (TextView) findViewById(R.id.textBudget);
				textbud.setTypeface(roboto);
				
				TextView rem = (TextView) findViewById(R.id.remaining);
				rem.setTypeface(roboto);
				
				TextView textrem = (TextView) findViewById(R.id.textRemaining);
				textrem.setTypeface(roboto);
				
				TextView pt= (TextView) findViewById(R.id.percentageText);
				pt.setTypeface(roboto);
			
				

			}
			
			// END OF CINDY'S STUFF
	
}
