package com.bsu.bk42.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bsu.bk42.BakerStreet42;

public class AndroidLauncher extends AndroidApplication {
	private BakerStreet42 game = null;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(MainTabActivity.game, config);
	}
}
