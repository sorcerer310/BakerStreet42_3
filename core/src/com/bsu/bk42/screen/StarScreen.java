package com.bsu.bk42.screen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

/**
 * 星星解锁
 * Created by Administrator on 2015/5/27.
 */
public class StarScreen extends UGameScreen {
    private Texture tx_star = null;
    private Image star = null;
    private TweenManager tm = new TweenManager();
    public StarScreen(){
        stage = new Stage(new StretchViewport(480.0F, 800.0f));

        tx_star = new Texture(Gdx.files.internal("star.png"));
        star = new Image(tx_star){

            {

                Tween.registerAccessor(Image.class,new ActorAccessor());
                Tween.to(this,ActorAccessor.ROTATION_CPOS_XY,1.0f).target(360.0f)
                        .repeat(-1,0.0f).start(tm);
            }
            @Override
            public void act(float delta) {
                super.act(delta);
                tm.update(delta);
            }
        };
        star.setPosition(100,70);
        stage.addActor(star);



    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }
}
