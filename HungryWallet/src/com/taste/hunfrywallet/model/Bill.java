/**
 * 
 */
package com.taste.hunfrywallet.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;


/**
 * @author Huang Li and Tiffany Truong and Jessica Ly
 *
 */

@ParseClassName("Bill")
public class Bill extends ParseObject{
	private String restaurantName;
	private double expense;
	private String visitTime;
	
	public Bill()
	{
		
	}

	public Bill(String someRestaurantName, double expense, String budgetID){//, String visitTime) {
		super("Bill");
		this.setRestaurantName(someRestaurantName);
		if( expense < 0) //Sanitize for negative bill
			expense = 0; 
		this.setExpense( expense );
		this.attachBudget(budgetID);
		//this.setVisitTime( visitTime );
	}	
	
	public void setRestaurantName(String restaurantName){
		this.restaurantName = restaurantName;
		put("restaurant", restaurantName);
	}
	
	public String getRestaurantName() {
		return restaurantName;
	}
	
	public double getExpense() {
		return expense;
	}

	public boolean setExpense(double expense) {
		if ( expense >= 0 ) {
			this.expense = expense;
			put("expense", expense);
			return true;
		}
		else
		{
			expense = 0; 
			this.expense = expense;
			put("expense", expense);
			return true;
		}
	}

	public String getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(String visitTime) {
		if ( !visitTime.equals("") ){
			this.visitTime = visitTime;
		}
	}
	
	public void attachBudget(String budgetID){
		put("BudgetID", budgetID);
	}

	
//	public void modifyBill(double expense, String visitTime) {
//	this.setExpense( expense );
//	this.setVisitTime( visitTime );
//}
}
