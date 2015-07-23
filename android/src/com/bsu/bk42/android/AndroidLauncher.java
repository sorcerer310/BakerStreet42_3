package com.bsu.bk42.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bsu.bk42.BakerStreet42;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		if(MainTabActivity.game==null) {
			MainTabActivity.game = new BakerStreet42();
			System.out.println("======================AndroidLauncher onCreate init");
//		}
		System.out.println("======================AndroidLauncher onCreate");
		initialize(MainTabActivity.game, config);
	}
}
