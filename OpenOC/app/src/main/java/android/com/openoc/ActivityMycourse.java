package android.com.openoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;


public class ActivityMycourse extends Activity {
	
	private final String TAG = "OnlineCummunity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycourse);

        final String url = "ws://52.11.111.157:3000";
		//for demo, only the first button can go to inclass activity
		ImageButton course1Button = (ImageButton) findViewById(R.id.course1_button); 		
		course1Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				try {
                    //Intent inClassIntent = new Intent(ActivityMycourse.this , ActivityInclass.class);
                    //startActivity(inClassIntent);
                    Intent intent = new Intent(ActivityMycourse.this, VideoCall.class);
                    intent.putExtra("ServerIP", url);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }				
			}
		});

		//for demo, go to create_course activity, but that activity is empty
		Button createButton = (Button) findViewById(R.id.button_createcourse); 		
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				try {
                    Intent newCourseIntent = new Intent(ActivityMycourse.this , ActivityCreateCourse.class);
                    startActivity(newCourseIntent);                    
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }				
			}
		});
					
	}
	
}
