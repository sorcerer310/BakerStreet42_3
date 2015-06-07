package com.bsu.bk42;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bsu.bk42.screen.MapScreen;
import com.bsu.bk42.screen.StarScreen;

public class BakerStreet42 extends Game {
//	SpriteBatch batch;
//	Texture img;

	@Override
	public void create () {
//		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
		MapScreen ms = new MapScreen();
		StarScreen ss = new StarScreen();
		this.setScreen(ss);
	}

//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//	}
}
