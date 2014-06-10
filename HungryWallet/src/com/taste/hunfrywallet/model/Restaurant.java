/**
 * 
 */
package com.taste.hunfrywallet.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * @author Huang Li and Tiffany Truong and Aieswarya Manicka
 *
 */
@ParseClassName("Restaurant")
public class Restaurant extends ParseObject{
	
	private String restaurantName;
	// private String category;
	private double totalExpense;
	private int numberOfVisits;
	private String phoneNumber;
	private String address;
	private Integer price_level;
	private String url;
	
	
	
	public Restaurant() {
		
	}
	
	public Restaurant( String restaurantName, double expense, String userID ) {
		this.setRestaurantName( restaurantName );
		this.addTotalExpense( expense );
		this.incNumberOfVisits();
		this.setUserID(userID);
	}
	
	/*
	 * Constructor used for restaurant search results
	 */
	public Restaurant (String restaurantName, String phoneNumber, String address, Integer price_level, String url ){
		this.setRestaurantName(restaurantName);
		this.setPhoneNumber(phoneNumber);
		this.setAddress(address);
		this.price_level = price_level;
		this.setUrl(url);
		
	}
	
	private void setUserID(String userID) {
		put("user", userID);
	}

	public void modifyRestaurant( String restaurantName, double newSpent ) {
		this.setRestaurantName( restaurantName );
		this.addTotalSpent( newSpent );
	}
	
	public void addTotalSpent(double newSpent) {
		totalExpense = getDouble("totalExpense") + newSpent;
		put("totalExpense", totalExpense);
	}
	
	public String getRestaurantName() {
		return restaurantName;
	}
	
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
		put("name", restaurantName);
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setPriceLevel(Integer price_level){
		this.price_level = price_level;
	}
	
	public Integer getPriceLevel(){ 
		return price_level;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getUrl(){
		return url;
	}
	
	public double getTotalExpense() {
		return totalExpense;
	}
	
	public boolean addTotalExpense(double expense) {
		if ( expense >= 0 ) {
			this.totalExpense = getDouble("totalExpense") + expense;
			put("totalExpense", totalExpense);
			return true;
		}
		else
			return false;
	}
	
	public int getNumberOfVisits() {
		return numberOfVisits;
	}
	
	public void incNumberOfVisits() {
		numberOfVisits = getInt("visits") + 1;
		put("visits", numberOfVisits);
	}	
	
	public void decNumberOfVisits() {
		this.numberOfVisits -= 1;
	}	
}
