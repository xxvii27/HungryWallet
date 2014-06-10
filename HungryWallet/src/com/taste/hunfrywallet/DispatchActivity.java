/**
 * 
 */
package com.taste.hunfrywallet;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taste.hunfrywallet.model.Budget;

/**
 * @author Heim201
 *
 */
// TODO: create XML for this activity in order for it to display first
public class DispatchActivity extends Activity {

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    // Check if there is current user info
	    if (ParseUser.getCurrentUser() != null) {	    	
	      startActivity(new Intent(this, MainActivity.class));
	      
	    } else {
	      // Start and intent for the logged out activity
	      startActivity(new Intent(this, SignUpOrLoginActivity.class));
	    }
	  }
}
