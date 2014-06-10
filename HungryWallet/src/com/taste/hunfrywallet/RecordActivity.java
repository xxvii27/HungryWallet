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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RecordActivity extends Activity {

	private Button logOut;
    private AlertDialog.Builder alert;
	private Button add_bill;
	private ListView billList;
	private ArrayList<String> billsInfo;
	private ArrayAdapter<String> billsAdapter;
	private EditText et;
	private TextView balance_text;

	private Budget userBudget;
	private List<ParseObject> userBills;

	private static final int tabPadding = 15;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_activity);
		initTypeFace();

		// Connect activity to the layout components
		add_bill = (Button) findViewById(R.id.button1);
		billList = (ListView) findViewById(R.id.listView1);
		//et = (EditText) findViewById(R.id.editText1);
		balance_text = (TextView) findViewById(R.id.balance_amount);

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
