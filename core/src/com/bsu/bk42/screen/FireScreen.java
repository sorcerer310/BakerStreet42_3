package com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ugame.gdx.tools.UGameScreen;

/**
 * 点火界面,分为博望坡和铁锁连环两个场景
 * Created by FC on 2015/7/20.
 */
public class FireScreen extends UGameScreen {
    public static float screenWidth,screenHeight,scaleWidth,scaleHeight;

    private Texture tx_bowang;
    private Image bg_bowang;
    public FireScreen(){
        screenWidth = 720.0f;                                                                                           //设置游戏界面的宽高
        screenHeight = 1280.0f;
        stage = new Stage(new StretchViewport(screenWidth, screenHeight));
        scaleWidth = Gdx.graphics.getWidth()/screenWidth;                                                               //获得游戏界面与设备间的比例
        scaleHeight = Gdx.graphics.getHeight()/screenHeight;


        tx_bowang = new Texture("fire/bowang.jpg");
        bg_bowang = new Image(tx_bowang);
//        bg_bowang.setOrigin(tx_bowang.getWidth()/2,tx_bowang.getHeight()/2);
        bg_bowang.setScaleX(scaleHeight/tx_bowang.getWidth());
System.out.println(scaleHeight/((float)tx_bowang.getWidth()));
        bg_bowang.setRotation(-90.0f);
        bg_bowang.setPosition(0, scaleHeight);

        stage.addActor(bg_bowang);
    }
}
