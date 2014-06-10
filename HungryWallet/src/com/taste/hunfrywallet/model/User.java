/**
 * 
 */
package com.taste.hunfrywallet.model;

import java.util.Date;

import com.parse.ParseUser;

/**
 * @author Huang Li and Tiffany Truong and Jessica Ly
 *
 */
public class User {
	
	public static String getRestaurantsID() {
		return (String) ParseUser.getCurrentUser().get("Restaurant");
	}
	
	public static void setRestaurants(String restaurantsID) {
		ParseUser.getCurrentUser().put("Restaurants", restaurantsID);
	}
	
	public static String getCurrentBudget() {
		return (String) ParseUser.getCurrentUser().get("Budget");
	}
	
	public static void setCurrentBudgetID(String currentBudgetID) {
		ParseUser.getCurrentUser().put("BudgetID", currentBudgetID);
	}
	
	public static String getUsername() {
		return ParseUser.getCurrentUser().getUsername();
	}
	
	public static void setUsername(String username) {
		ParseUser.getCurrentUser().setUsername(username);
	}
	
	
	public static String getEmail() {
		return ParseUser.getCurrentUser().getEmail();
	}
	
	public static void setEmail(String email) {
		ParseUser.getCurrentUser().setEmail(email);
	}
	
	public static String getFacebookID() {
		return (String) ParseUser.getCurrentUser().get("facebookID");
	}
	
	public static void setFacebookID(String facebookID) {
		ParseUser.getCurrentUser().put("facebookID", facebookID);
	}
	
	public static Date getStartTime() {
		return ParseUser.getCurrentUser().getCreatedAt();
	}
	
	public static Date getEndTime() {
		return ParseUser.getCurrentUser().getUpdatedAt();
	}
	
	public static String getID() {
		return ParseUser.getCurrentUser().getObjectId();
	}
}
