package com.taste.hunfrywallet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taste.hunfrywallet.model.Bill;
import com.taste.hunfrywallet.model.Budget;
import com.taste.hunfrywallet.model.Restaurant;
import com.taste.hunfrywallet.model.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RecordActivity extends ListActivity {

	private Button logOut;
	private AlertDialog.Builder alert;
	private AlertDialog.Builder edit;
	private Button add_bill;
	private ListView billList;
	private ArrayList<String> billsInfo;
	private ArrayAdapter<String> billsAdapter;
	private EditText et;
	private TextView balance_text;

	private List<ParseObject> userRestaurants;
	private String restaurant;
	private double expense;



	private Budget userBudget;
	private List<ParseObject> userBills;

	private static final int tabPadding = 15;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_activity);
		initTypeFace();

		// Connect activity to the layout components
		add_bill = (Button) findViewById(R.id.button1);
		billList = (ListView) findViewById(android.R.id.list);
		//et = (EditText) findViewById(R.id.editText1);
		balance_text = (TextView) findViewById(R.id.balance_amount);

		try {
			userBudget = (Budget) ParseQuery.getQuery("Budget").get(
					(String) (ParseUser.getCurrentUser().get("BudgetID")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Double balance = userBudget.getDouble("remainingBudget");
		balance_text.setText(String.format("%.2f", balance.floatValue()));

		int bgColor = Color.rgb(247, 242, 235);
		billList.setBackgroundColor(Color.WHITE);

		//Getting logout Button
		logOut = (Button) findViewById(R.id.logout_button);

		add_bill.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent submitIntent = new Intent(getApplicationContext(),
						SubmitBillActivity.class);
				startActivity(submitIntent);
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
				ParseUser.logOut();
				startActivity(new Intent (getApplicationContext(), SignUpOrLoginActivity.class));
			}
		});

		logOut.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				alert.create().show();
			}	

		});	

		billList.setLongClickable(true);
		registerForContextMenu(billList);


	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_view_menu, menu);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final int pos = info.position; 
		switch (item.getItemId()) {
			//delete item
			case R.id.remove_item:
				removeBill(pos);
				return true;
		    //edit item
			case R.id.edit_item:
				editBill(pos);
				return true;
		}
		
		return false;
	}
	

	private void editBill(int pos){
		final Bill temp = (Bill) userBills.get(userBills.size()-1-pos);
		userBudget.updateRemainingBudget(temp.getDouble("expense"), 1);
		edit = new AlertDialog.Builder(this);
		edit.setTitle("Edit");
		final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_edit_bill, null);
		edit.setView(dialogView);
		edit.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				double amount = Double.parseDouble(((EditText) dialogView.findViewById(R.id.edit_amount)).getText().toString());
				String rest = ((EditText) dialogView.findViewById(R.id.edit_rest)).getText().toString();
				temp.setExpense(amount);
				temp.setRestaurantName(rest);
				try {
					temp.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					temp.saveEventually();
				}
				userBudget.updateRemainingBudget(amount, 0);
				try {
					userBudget.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				expense = temp.getDouble("expense");
				restaurant = temp.getString("restaurant");
				updateRestaurant();
				buildListView();
				Double balance = userBudget.getDouble("remainingBudget");
				balance_text.setText(String.format("%.2f", balance.floatValue()));
			}
			
		});

		edit.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {}  });
		edit.create().show();
	}
	
	private void updateRestaurant() {
		//if not queried yet
		if(userRestaurants == null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
			query.whereEqualTo("user", User.getID());
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> restList, ParseException e) {
					if (e == null) {
						userRestaurants = restList;
						// Search for Restaurant
						Restaurant tmpRest;
						for (int i = 0; i < userRestaurants.size(); i++) {
							tmpRest = (Restaurant) userRestaurants.get(i);
							if (restaurant.equals(tmpRest.get("name"))) {

				                tmpRest.setRestaurantName(restaurant);

								tmpRest.saveEventually();
								return;
							}
						}
					} else {
						Log.d("restaurant", "Error: " + e.getMessage());
					}
				}
			});
		}
		else{
			// Search for Restaurant
			Restaurant tmpRest;
			for (int i = 0; i < userRestaurants.size(); i++) {
				tmpRest = (Restaurant) userRestaurants.get(i);
				if (restaurant.equals(tmpRest.get("name"))) {

					tmpRest.setRestaurantName(restaurant);

					tmpRest.saveEventually();
					return;
				}
			}
		}

	}

	private void removeBill(int pos){
		Bill temp = (Bill) userBills.get(userBills.size()-1-pos);
		expense = temp.getDouble("expense");
		restaurant = temp.getString("restaurant");
		userBills.remove(userBills.size()-1-pos);
		degenerateRestaurant();
		userBudget.deleteBill(temp);
		buildListView();
		Double balance = userBudget.getDouble("remainingBudget");
		balance_text.setText(String.format("%.2f", balance.floatValue()));
	}
	
	private void degenerateRestaurant() {
		//if not queried yet
		if(userRestaurants == null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
			query.whereEqualTo("user", User.getID());
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> restList, ParseException e) {
					if (e == null) {
						userRestaurants = restList;
						// Search for Restaurant
						Restaurant tmpRest;
						for (int i = 0; i < userRestaurants.size(); i++) {
							tmpRest = (Restaurant) userRestaurants.get(i);
							if (restaurant.equals(tmpRest.get("name"))) {

								tmpRest.decTotalExpense(expense);
								tmpRest.decNumberOfVisits();

								tmpRest.saveEventually();
								return;
							}
						}
					} else {
						Log.d("restaurant", "Error: " + e.getMessage());
					}
				}
			});
		}
		else{
			// Search for Restaurant
			Restaurant tmpRest;
			for (int i = 0; i < userRestaurants.size(); i++) {
				tmpRest = (Restaurant) userRestaurants.get(i);
				if (restaurant.equals(tmpRest.get("name"))) {

                    tmpRest.decTotalExpense(expense);
					tmpRest.decNumberOfVisits();

					tmpRest.saveEventually();
					return;
				}
			}
		}

	}

	protected void onResume() {
		super.onResume();

		try {
			userBudget = (Budget) ParseQuery.getQuery("Budget").get(
					(String) (ParseUser.getCurrentUser().get("BudgetID")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Double balance = userBudget.getDouble("remainingBudget");
		balance_text.setText(String.format("%.2f", balance.floatValue()));

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Bill");
		query.whereEqualTo("BudgetID", userBudget.getObjectId());
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> billList, ParseException e) {
				if (e == null) {
					userBills = billList;
					buildListView();
				} else {
					Log.d("bills", "Error: " + e.getMessage());
				}
			}
		});

	}

	private void initTypeFace() {
		Typeface journal = Typeface.createFromAsset(getAssets(),
				"fonts/journal-webfont.ttf");
		Typeface effra = Typeface.createFromAsset(getAssets(),
				"fonts/Effra-Regular.ttf");

		Typeface sugarcube = Typeface.createFromAsset(getAssets(),
				"fonts/SugarcubesRegular.ttf");
		Typeface roboto = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");
		TextView balance = (TextView) findViewById(R.id.TextView04);
		balance.setTypeface(roboto);

		TextView titleText = (TextView) findViewById(R.id.TextView01);
		titleText.setTypeface(journal);
		titleText.setTextSize(34);

		TextView balanceamt = (TextView) findViewById(R.id.balance_amount);
		balanceamt.setTypeface(roboto);

		Button bt = (Button)findViewById(R.id.button1);
		bt.setTypeface(roboto);
	}

	private void buildListView() {
		String billStats;
		Bill tmpBill;

		billsInfo = new ArrayList<String>();

		// Format Bills list
		for (int i = userBills.size()-1; i >= 0; i--) {
			tmpBill = (Bill) userBills.get(i);
			billStats = new SimpleDateFormat("MM/dd").format(tmpBill
					.getCreatedAt());
			billStats += "\t \t \t $";
			billStats += String.format("%.2f", ((Double) tmpBill.getDouble("expense")).floatValue());
			billStats += "\t \t \t ";
			billStats += (String) tmpBill.get("restaurant");
			billsInfo.add(billStats);
		}

		billList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, billsInfo){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				int bkground = Color.rgb(247, 243, 235);
				Typeface roboto = Typeface.createFromAsset(getAssets(),
						"fonts/Roboto-Light.ttf");
				Typeface effra = Typeface.createFromAsset(getAssets(),
						"fonts/LANENAR_.ttf");
				TextView textView = (TextView) super.getView(position, convertView, parent);
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(18);
				textView.setTypeface(roboto);
				textView.setBackgroundColor(bkground);


				return textView;
			}
		});

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
