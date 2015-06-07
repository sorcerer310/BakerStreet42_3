package com.bsu.bk42.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bsu.bk42.BakerStreet42;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "贝克街42号";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new BakerStreet42(), config);
	}
}
