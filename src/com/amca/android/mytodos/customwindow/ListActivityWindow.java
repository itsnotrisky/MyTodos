package com.amca.android.mytodos.customwindow;

import com.amca.android.mytodos.R;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ListActivityWindow extends ListActivity {
	protected TextView title;
    protected ImageView icon;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    
        setContentView(R.layout.activity_todos);
 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
 
        title = (TextView) findViewById(R.id.title);
        //icon  = (ImageView) findViewById(R.id.icon);
	}

}
