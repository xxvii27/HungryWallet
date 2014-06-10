package com.taste.hunfrywallet;
 
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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class SubmitBillActivity extends Activity {
 
    private AutoCompleteTextView restaurantName;
    private EditText billAmount;
    private Button submitButton;
    private TextView balance;
    private Button cancelButton;
    private AlertDialog.Builder alert;
 
     
    private Budget userBudget;
    private List<ParseObject> userRestaurants;
    private Bill newBill;
    private double expense;
    private String restaurant;
    private static ArrayList<String> restnames;
 
 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_bills);
        initTypeFace();
 
        restaurantName = (AutoCompleteTextView) findViewById(R.id.autocomplete_rest);
        billAmount = (EditText) findViewById(R.id.amount_edit);
        submitButton = (Button) findViewById(R.id.submit_button);
        balance = (TextView) findViewById(R.id.balance);
        cancelButton = (Button) findViewById(R.id.cancel_button);
         
        restnames = new ArrayList<String>();
     
        try {
            userBudget = (Budget) ParseQuery.getQuery("Budget").get(
                    (String) (ParseUser.getCurrentUser().get("BudgetID")));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
        Double remainingBudget = (Double) userBudget
                .getDouble("remainingBudget");
        balance.setText("Balance " + String.format("%.2f", remainingBudget.floatValue()));
 
        cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
 
        /* Create the alert dialog for the Bill Confirmation */
        alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm Bill");
        alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
 
            }
        });
 
        alert.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newBill = new Bill(restaurant, expense, userBudget
                                .getObjectId());
                        userBudget.addBill(newBill);
 
                        generateRestaurant();
 
                        try {
                            userBudget.save();
                        } catch (ParseException e) {
                            userBudget.saveEventually();
                        }
                         
                        finish();
                    }
                });
 
        submitButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
 
                boolean validationError = false;
                StringBuilder validationErrorMessage = new StringBuilder(
                        getResources().getString(R.string.error_intro));
                if (isEmpty(restaurantName)) {
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(
                            R.string.error_blank_restaurant));
                }
                if (isEmpty(billAmount)) {
                    if (validationError) {
                        validationErrorMessage.append(getResources().getString(
                                R.string.error_join));
                    }
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(
                            R.string.error_blank_amount));
                }
                else if(Float.parseFloat(billAmount.getText().toString()) < 0)
                {
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(
                            R.string.error_negative_bill));
                }
                validationErrorMessage.append(getResources().getString(
                        R.string.error_end));
 
                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(getApplicationContext(),
                            validationErrorMessage.toString(),
                            Toast.LENGTH_LONG).show();
                    return;
                }
 
                restaurant = restaurantName.getText().toString();
                expense = Double.parseDouble(billAmount.getText().toString());
 
                alert.setMessage("Are you sure you want to enter a bill of $"
                        + billAmount.getText().toString() + " at "
                        + restaurantName.getText().toString() + "?");
 
                alert.create().show();
            }
        });
 
    }
     
    protected void onResume(){
        super.onResume();
         
        createAutoComplete();
         
    }
     
    private void generateRestaurant() {
         
        if(userRestaurants == null){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereEqualTo("user", User.getID());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> restList, ParseException e) {
                if (e == null) {
                    userRestaurants = restList;
                    // No restaurants for user
                    if (userRestaurants == null || userRestaurants.size() == 0) {
                        Restaurant rest = new Restaurant(restaurant, expense,
                                User.getID());
                        ParseUser.getCurrentUser().add("Restaurants", rest);
 
                        rest.saveEventually();
                        ParseUser.getCurrentUser().saveEventually();
                        return;
                    }
 
                    // Search for Restaurant
                    Restaurant tmpRest;
                    for (int i = 0; i < userRestaurants.size(); i++) {
                        tmpRest = (Restaurant) userRestaurants.get(i);
                        if (restaurant.equals(tmpRest.get("name"))) {
 
                            tmpRest.addTotalSpent(expense);
                            tmpRest.incNumberOfVisits();
 
                            tmpRest.saveEventually();
                            return;
                        }
                    }
 
                    // Create Restaurant if it doesn't exist
                    Restaurant rest = new Restaurant(restaurant, expense, User
                            .getID());
                    ParseUser.getCurrentUser().add("Restaurants", rest);
                    rest.saveEventually();
                    ParseUser.getCurrentUser().saveEventually();
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
 
                    tmpRest.addTotalSpent(expense);
                    tmpRest.incNumberOfVisits();
 
                    tmpRest.saveEventually();
                    return;
                }
            }
 
            // Create Restaurant if it doesn't exist
            Restaurant rest = new Restaurant(restaurant, expense, User
                    .getID());
            ParseUser.getCurrentUser().add("Restaurants", rest);
            rest.saveEventually();
            ParseUser.getCurrentUser().saveEventually();
        }
 
    }
     
    private void createAutoComplete(){
         
        restaurantName = (AutoCompleteTextView) findViewById(R.id.autocomplete_rest);
         
        restnames.clear();
         
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
        query.whereEqualTo("user", User.getID());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> restList, ParseException e) {
                if (e == null) {
                    userRestaurants = restList;
                    for(int i = 0; i < restList.size(); i++){
                        restnames.add(restList.get(i).getString("name"));
                    }
                     
                } else {
                    Log.d("restaurant", "Error: " + e.getMessage());
                }
            }
        });
 
        ArrayAdapter<String> adapter = 
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, restnames);
        restaurantName.setAdapter(adapter);
        return; 
    }
 
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
 
    private void initTypeFace() {
 
        Typeface didot = Typeface.createFromAsset(getAssets(),
                "fonts/didot_italic.ttf");
        Typeface effra = Typeface.createFromAsset(getAssets(),
                "fonts/Effra-Regular.ttf");
        Typeface sugarcube = Typeface.createFromAsset(getAssets(),
                "fonts/SugarcubesRegular.ttf");
        Typeface journal = Typeface.createFromAsset(getAssets(),
                "fonts/journal-webfont.ttf");
 
        TextView tnb = (TextView) findViewById(R.id.title_new_bill);
        tnb.setTypeface(journal);
 
        TextView bal = (TextView) findViewById(R.id.balance);
        bal.setTypeface(sugarcube);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        tv3.setTypeface(effra);
 
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setTypeface(effra);
 
        TextView sub_button = (TextView) findViewById(R.id.submit_button);
        sub_button.setTypeface(effra);
 
    }
 
}