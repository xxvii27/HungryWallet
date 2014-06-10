package com.taste.hunfrywallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {
	
	 private EditText usernameView;
	 private EditText passwordView;
	 private EditText passwordAgainView;
	 private EditText emailView;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.signup_activity);
	    initTypeFace();

	    // Set up the signup form.
	    usernameView = (EditText) findViewById(R.id.username);
	    passwordView = (EditText) findViewById(R.id.password);
	    passwordAgainView = (EditText) findViewById(R.id.passwordAgain);
	    emailView = (EditText) findViewById(R.id.email);

	    // Set up the submit button click handler
	    findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
	      public void onClick(View view) {

	        // Validate the sign up data
	        boolean validationError = false;
	        StringBuilder validationErrorMessage =
	            new StringBuilder(getResources().getString(R.string.error_intro));
	        if (isEmpty(usernameView)) {
	          validationError = true;
	          validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
	        }
	        if (isEmpty(passwordView)) {
	          if (validationError) {
	            validationErrorMessage.append(getResources().getString(R.string.error_join));
	          }
	          validationError = true;
	          validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
	        }
	        if(isEmpty(emailView)){
	        	if(validationError){
		          validationErrorMessage.append(getResources().getString(R.string.error_join));
	        	}
	            validationError = true; 
	            validationErrorMessage.append(getResources().getString(R.string.error_blank_email));

	        }
	        if (!isMatching(passwordView, passwordAgainView)) {
	          if (validationError) {
	            validationErrorMessage.append(getResources().getString(R.string.error_join));
	          }
	          validationError = true;
	          validationErrorMessage.append(getResources().getString(
	              R.string.error_mismatched_passwords));
	        }
	        
	        validationErrorMessage.append(getResources().getString(R.string.error_end));

	        // If there is a validation error, display the error
	        if (validationError) {
	          Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
	              .show();
	          return;
	        }

	        // Set up a progress dialog
	        final ProgressDialog dlg = new ProgressDialog(SignUpActivity.this);
	        dlg.setTitle("Please wait.");
	        dlg.setMessage("Signing up.  Please wait.");
	        dlg.show();

	        // Set up a new Parse user
	        ParseUser user = new ParseUser();
	        user.setUsername(usernameView.getText().toString());
	        user.setPassword(passwordView.getText().toString());
	        user.setEmail(emailView.getText().toString());
	        
	        // Call the Parse signup method
	        user.signUpInBackground(new SignUpCallback() {

	          @Override
	          public void done(ParseException e) {
	            dlg.dismiss();
	            if (e != null) {
	              // Show the error message
	              Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
	            } else {
	              // Start an intent for the dispatch activity
	              //Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
		          Intent intent = new Intent(SignUpActivity.this, SubmitBudgetActivity.class);
	              //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
	              startActivity(intent);
	            }
	          }
	        });
	      }
	    });
	  }
	   @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu; this adds items to the action bar if it is present.
	      getMenuInflater().inflate(R.menu.main, menu);
	      return true;
	   }
	   
	   private boolean isEmpty(EditText etText) {
		    if (etText.getText().toString().trim().length() > 0) {
		      return false;
		    } else {
		      return true;
		    }
		  }

		  private boolean isMatching(EditText etText1, EditText etText2) {
		    if (etText1.getText().toString().equals(etText2.getText().toString())) {
		      return true;
		    } else {
		      return false;
		    }
		  }
		  
		  
			 private void initTypeFace() {
			      Typeface didot = Typeface.createFromAsset(getAssets(), "fonts/didot_italic.ttf");
			      TextView titleText = (TextView)findViewById(R.id.signupbanner);
			      titleText.setTypeface(didot);
			      
			      Typeface effra = Typeface.createFromAsset(getAssets(), "fonts/Effra-Regular.ttf");
			      Typeface sugarcube = Typeface.createFromAsset(getAssets(), "fonts/SugarcubesRegular.ttf");
			      TextView email = (TextView)findViewById(R.id.email);
			      email.setTypeface(effra);
			      
			      TextView pwd = (TextView)findViewById(R.id.password);
			      pwd.setTypeface(effra);
			      
			      TextView usrnm = (TextView)findViewById(R.id.username);
			      usrnm.setTypeface(effra);
			      
			      TextView pwdAgain = (TextView)findViewById(R.id.passwordAgain);
			      pwdAgain.setTypeface(effra);
			      
			      TextView signup = (TextView)findViewById(R.id.signup);
			      signup.setTypeface(sugarcube);
			       

			       
			  }
}
