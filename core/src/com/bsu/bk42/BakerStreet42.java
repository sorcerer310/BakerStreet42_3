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
	public static final int MAPSCREEN = 0;																			//地图场景的默认索引
	public static final int STARSCREEN = 1;																		//星星场景的默认索引

	private MapScreen ms = null;																						//地图场景
	private StarScreen ss = null;																						//星星场景

	@Override
	public void create () {
		ms = new MapScreen();
		ss = new StarScreen();
		this.setScreen(ss);
	}

	/**
	 * 设置当前场景
	 * @param map	对应场景的索引
	 */
	public void setScreen(int map){
		switch(map) {
			case MAPSCREEN:
				this.setScreen(ms);
				break;
			case STARSCREEN:
				this.setScreen(ss);
				break;
		}
	}
}
