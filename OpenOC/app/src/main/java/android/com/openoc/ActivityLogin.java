package android.com.openoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class ActivityLogin extends Activity {
	
	private final String TAG = "OnlineCummunity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
				
		Button loginButton = (Button) findViewById(R.id.login_button2); 
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//first need to verify the email and password
				
				//then go to user_main activity			
				try {
                    Intent mycourseIntent = new Intent(ActivityLogin.this , ActivityMycourse.class);
                    startActivity(mycourseIntent);                    
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                ActivityLogin.this.finish();
			}
		});
		
	}	

}
