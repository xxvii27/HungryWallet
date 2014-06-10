/**
 * 
 */
package com.taste.hunfrywallet.model;

import java.io.Serializable;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * @author Huang Li and Tiffany Truong
 *
 */
@ParseClassName("Budget")
public class Budget extends ParseObject {
	private double totalBudget;
	private double remainingBudget;
	private Date endTime;
	
	public Budget(){
	}
	
	public Budget(double totalBudget)
	{
		this.setTotalBudget( totalBudget );
		this.setRemainingBudget(totalBudget);
		//this.setEndTime( getCreatedAt());
	}

	public double getTotalBudget() {
		return totalBudget;
	}
	
	public boolean setTotalBudget(double totalBudget) {
		if (totalBudget >= 0) 
		{
			put("totalBudget", totalBudget);
			this.totalBudget = totalBudget;
			return true;
		}
		else
		{
			totalBudget = 0; 
			put("totalBudget", totalBudget);
			this.totalBudget = totalBudget;
			return true;
		}
	}
	
	public double getRemainingBudget() {
		return remainingBudget;
	}
	
	@SuppressLint("UseValueOf")
	public void updateRemainingBudget(double expense) {
		put("remainingBudget", new Double(getDouble("remainingBudget") - expense));
		this.remainingBudget = getDouble("remainingBudget") - expense;
	}
	
	
	@SuppressLint("UseValueOf")
	public void setRemainingBudget(double remainingBudget){
		put("remainingBudget", new Double(remainingBudget));
		this.remainingBudget = remainingBudget;
	}
	
	public void addBill(Bill bill){
		add("Bills", bill);
		updateRemainingBudget(bill.getExpense());
		saveEventually();
	}
	
	
	public Date getStartTime() {
		return this.getCreatedAt();
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	@SuppressWarnings("deprecation")
	public void setEndTime(Date endTime) {
		endTime.setMonth(endTime.getMonth() + 1);
		put("endAt", endTime);
		this.endTime = endTime;
	}
  
  
}
