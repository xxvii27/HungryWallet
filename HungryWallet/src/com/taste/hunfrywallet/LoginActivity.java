/**
 * 
 */
package com.taste.hunfrywallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends Activity {
  // UI references.
  private EditText usernameView;
  private EditText passwordView;
  private Button resetPassword;
  private EditText emailView;
  
  final Context context = this;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity);

    initTypeFace();

    // Set up the login form.
    usernameView = (EditText) findViewById(R.id.username);
    passwordView = (EditText) findViewById(R.id.password);
    resetPassword = (Button) findViewById(R.id.reset);
    emailView = (EditText) findViewById(R.id.emailreq);
    
    // Set up the submit button click handler
    findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        // Validate the log in data
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
        validationErrorMessage.append(getResources().getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
          Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
              .show();
          return;
        }
          
        // Set up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(LoginActivity.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Logging in.  Please wait.");
        dlg.show();
        // Call the Parse login method
        ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText()
            .toString(), new LogInCallback() {

          @Override
          public void done(ParseUser user, ParseException e) {
            dlg.dismiss();
            if (e != null) {
              // Show the error message
              Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } else {
              // Start an intent for the dispatch activity
              Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            }
          }
        });
      }
    });
    
    
    // Set up the reset password
    resetPassword.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.dialog_email, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set prompts.xml to alert dialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.emailreq);
			
			alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Send", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	if(isEmpty(userInput)){
			          Toast.makeText(context, "Enter a valid email", Toast.LENGTH_LONG).show();
			          return;
			    	}
			    	
			    	String email = userInput.getText().toString();
			    	ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
						public void done(ParseException e) {
							if (e == null) {
						      Toast.makeText(context, "Request sent to email ", Toast.LENGTH_LONG).show();
							} else {
							  Toast.makeText(context, "Invalid email", Toast.LENGTH_LONG).show();
							}
						}
					});
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

		}
    });
  }

  protected void onResume(){
	  super.onResume();
	  usernameView.setSelection(0);

  }
  
  private boolean isEmpty(EditText etText) {
    if (etText.getText().toString().trim().length() > 0) {
      return false;
    } else {
      return true;
    }
  }
  
  private void initTypeFace(){
	  Typeface didot = Typeface.createFromAsset(getAssets(), "fonts/didot_italic.ttf");
      
      Typeface effra = Typeface.createFromAsset(getAssets(), "fonts/Effra-Regular.ttf");
      Typeface sugarcube = Typeface.createFromAsset(getAssets(), "fonts/SugarcubesRegular.ttf");
      TextView usrnm = (TextView)findViewById(R.id.username);
      usrnm.setTypeface(effra);
      TextView password = (TextView)findViewById(R.id.password);
      password.setTypeface(effra);
      
      TextView actButton = (TextView)findViewById(R.id.action_button);
      actButton.setTypeface(sugarcube);
      
      TextView wb = (TextView)findViewById(R.id.welcome_back);
      wb.setTypeface(didot);
      
      
  }
  
}