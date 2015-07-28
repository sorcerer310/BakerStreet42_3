package com.bsu.bk42;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bsu.bk42.screen.FireScreen;
import com.bsu.bk42.screen.MapScreen;
import com.bsu.bk42.screen.StarScreen;

public class BakerStreet42 extends Game {
//	SpriteBatch batch;
//	Texture img;
	public static final int MAPSCREEN = 0;																			//地图场景的默认索引
	public static final int STARSCREEN = 1;																		//星星场景的默认索引
	public static final int FIRESCREEN = 2;																		//放火场景的默认索引

	public static MapScreen ms = null;																				//地图场景
	public static StarScreen ss = null;																				//星星场景
	public static FireScreen fs = null;																				//放火场景

	@Override
	public void create () {
		if(ms==null)
			ms = new MapScreen();
		if(ss==null)
			ss = new StarScreen();
		if(fs==null)
			fs = new FireScreen();
		this.setScreen(fs);
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
			case FIRESCREEN:
				this.setScreen(fs);
				break;
		}
	}

	/**
	 * 设置地图当前的显示索引
	 * @param id	地图当前显示到的机关索引
	 */
	public void setMapCurrIndex(String id){
		//0:初始.1:星盘.2:乌龟.3:插旗.4:军令.5:守关3处完成.6:追击.7:守关4处放火.8:铁锁连环.9:船舱门关 10:草船借箭.11:擂鼓助威.
		//12:宝剑咒语箱开.13:借东风.14:放火.15:选择大路追击.16:选择华容道追击
		int i = Integer.parseInt(id);
		ms.plcCommand(i);
	}

	/**
	 * 设置当前放火界面为哪个界面
	 * @param id	1为博望坡,2为铁锁连环
	 */
	public void setFireCurrIndex(String id){
		//0:博望坡,1:铁锁连环
		int i = Integer.parseInt(id);
		fs.plcCommand(i);
	}
}
