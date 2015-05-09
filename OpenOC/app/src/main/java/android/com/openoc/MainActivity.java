package android.com.openoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private final String TAG = "OnlineCummunity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Restore any saved instance state
		super.onCreate(savedInstanceState);
		
		// Set content view
		setContentView(R.layout.activity_main);
		
		// Initialize UI elements
		final Button button_login = (Button)findViewById(R.id.login_button);
		final Button button_signup = (Button)findViewById(R.id.sign_up_button);
		//final Button facebook_login = (Button)findViewById(R.id.facebook_login_button);
		//final Button twitter_login = (Button)findViewById(R.id.twitter_login_button);
		
		//Action of Login Button
        button_login.setOnClickListener(new OnClickListener() {
            
            // Called when user clicks the Login button
            public void onClick(View v) {
                try {
                    Intent loginIntent = new Intent(MainActivity.this , ActivityLogin.class);
                    startActivity(loginIntent);                    
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                MainActivity.this.finish();
            }
        });
        
		//Action of Signup Button
        button_signup.setOnClickListener(new OnClickListener() {           
            // Called when user clicks the Signup button
            public void onClick(View v) {
                try {
                    Intent signupIntent = new Intent(MainActivity.this , ActivitySignup.class);
                    startActivity(signupIntent);                    
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                MainActivity.this.finish();
            }
        });
        
        //Action of Facebook login Button
        
        //Action of Twitter login Button		
	}



}
