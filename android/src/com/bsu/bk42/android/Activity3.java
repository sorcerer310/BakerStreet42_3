package com.bsu.bk42.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bsu.bk42.BakerStreet42;

public class Activity3 extends Activity{

	private final static String TAG = "Activity3";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3_layout);
        Log.i(TAG, "=============>onCreate");
//		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new BakerStreet42(1),config);
    }


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		 Log.i(TAG, "=============>onResume");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		 Log.i(TAG, "=============>onDestroy");
	}
	
}