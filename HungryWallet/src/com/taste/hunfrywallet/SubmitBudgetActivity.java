package com.taste.hunfrywallet;

import java.util.Date;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.taste.hunfrywallet.model.Bill;
import com.taste.hunfrywallet.model.Budget;
import com.taste.hunfrywallet.model.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitBudgetActivity extends Activity {

	private Button save;
	private EditText amount;
	private AlertDialog.Builder alert;
	
	private Budget userBudget;
	private Bill userBill;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_budget);

		initTypeFace();
		
		 if (ParseUser.getCurrentUser() == null) {
		      // Start an intent for the logged in activity
			 finish();
		   } 
		
		save = (Button) findViewById(R.id.submit_budget);
		amount = (EditText) findViewById(R.id.budget_amount);
		
		/* Create the alert dialog for the Bill Confirmation */
		alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm Budget");
		alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		alert.setNegativeButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Double totalBudget =  Double.parseDouble(amount.getText().toString());
						userBudget = new Budget(totalBudget);
						userBudget.put("created", new Date());					
						try {
							userBudget.save();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							userBudget.saveEventually();
						}
						
						User.setCurrentBudgetID(userBudget.getObjectId());
						
						try {
							ParseUser.getCurrentUser().save();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							ParseUser.getCurrentUser().saveEventually();
						}
						
						Intent intent = new Intent(SubmitBudgetActivity.this, DispatchActivity.class);
			            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(intent);
					}
				});

		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				boolean validationError = false;
				StringBuilder validationErrorMessage = new StringBuilder(
						getResources().getString(R.string.error_intro));
				if (isEmpty(amount)) {
					validationError = true;
					validationErrorMessage.append("Please enter a dollar amount");
				}
				else if(Float.parseFloat(amount.getText().toString()) < 0)
				{
					validationError = true;
					validationErrorMessage.append(getResources().getString(
							R.string.error_negative_budget));
				}
				// validationErrorMessage.append(getResources().getString(R.string.error_end));

				// If there is a validation error, display the error
				if (validationError) {
					Toast.makeText(SubmitBudgetActivity.this,
							validationErrorMessage.toString(),
							Toast.LENGTH_LONG).show();
					return;
				}
				
				alert.setMessage("Are you sure you want to start a budget of $"
						+ amount.getText().toString() +"?");

				alert.create().show();
			}
			
		});

	}
	
	private void initTypeFace() {

	      
	      Typeface didot = Typeface.createFromAsset(getAssets(), "fonts/didot_italic.ttf");
	      Typeface effra = Typeface.createFromAsset(getAssets(), "fonts/Effra-Regular.ttf");
	      Typeface sugarcube = Typeface.createFromAsset(getAssets(), "fonts/SugarcubesRegular.ttf");
	      Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
	      
	      TextView newBud = (TextView)findViewById(R.id.newBud);
	      newBud.setTypeface(didot);
	      
	      TextView setBud = (TextView)findViewById(R.id.setBud);
	      setBud.setTypeface(roboto);
	      TextView bud_amt = (TextView)findViewById(R.id.budget_amount);
	      bud_amt.setTypeface(effra);
	      
	      TextView submitbud = (TextView)findViewById(R.id.submit_budget);
	      submitbud.setTypeface(sugarcube);

	       
	  }

	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

}
