/**
 * 
 */
package com.taste.hunfrywallet;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.taste.hunfrywallet.model.Bill;
import com.taste.hunfrywallet.model.Budget;
import com.taste.hunfrywallet.model.Restaurant;

import android.app.Application;

/**
 * @author Heim201
 *
 */
public class HungryWalletApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		

		ParseObject.registerSubclass(Budget.class);
		ParseObject.registerSubclass(Bill.class);
		ParseObject.registerSubclass(Restaurant.class);

		Parse.initialize(this, "JVDKYrHLhsmzhVYrW0Z4JoEZrOOp7vD4ZrnXjZg8", "SZA0qpbjS6ahP9pZOmuGmESBUjshLeQSaN83sBzW");
		
		/*ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true); */
	}
	
}
