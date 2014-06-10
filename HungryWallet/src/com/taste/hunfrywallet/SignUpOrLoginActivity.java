/**
 * 
 */
package com.taste.hunfrywallet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.taste.hunfrywallet.LoginActivity;
import com.taste.hunfrywallet.SignUpActivity;



/**
 * Activity which displays a registration screen to the user.
 */
public class SignUpOrLoginActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up_or_log_in);
    initTypeFace();

    // Log in button click handler
    ((Button) findViewById(R.id.logInButton)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Starts an intent of the log in activity
        startActivity(new Intent(SignUpOrLoginActivity.this, LoginActivity.class));
      }
    });

    // Sign up button click handler
    ((Button) findViewById(R.id.signUpButton)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Starts an intent for the sign up activity
        startActivity(new Intent(SignUpOrLoginActivity.this, SignUpActivity.class));
      }
    });
  }
  
  private void initTypeFace() {
	  Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
	  Typeface journal = Typeface.createFromAsset(getAssets(), "fonts/journal-webfont.ttf");
      
      Typeface effra = Typeface.createFromAsset(getAssets(), "fonts/Effra-Regular.ttf");
      Typeface sugarcube = Typeface.createFromAsset(getAssets(), "fonts/SugarcubesRegular.ttf");
      TextView slogan = (TextView)findViewById(R.id.textView3);
      slogan.setTypeface(journal);
      slogan.setTextSize(40);
      
      TextView first = (TextView)findViewById(R.id.first_text);
      first.setTypeface(roboto);
      
      TextView second = (TextView)findViewById(R.id.second_text);
      second.setTypeface(roboto);
      
      TextView third = (TextView)findViewById(R.id.third_text);
      third.setTypeface(roboto);
      
      TextView login = (TextView)findViewById(R.id.logInButton);
      login.setTypeface(roboto);
      
      TextView signup = (TextView)findViewById(R.id.signUpButton);
      signup.setTypeface(roboto);
       

       
  }


 
}