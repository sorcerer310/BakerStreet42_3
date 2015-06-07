package com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ugame.gdx.tools.UGameScreen;

/**
 * 地图场景
 * Created by Administrator on 2015/5/27.
 */
public class MapScreen extends UGameScreen {
    private Texture tx_map = null;                                                                                  //要绘制的地图
    private Actor map = null;                                                                                       //承载地图的actor

    private ScrollPane sp = null;
    public MapScreen(){
        stage = new Stage(new StretchViewport(480.0F, 800.0f));
        tx_map = new Texture(Gdx.files.internal("selectmap.png"));
        map = new Actor(){
            {
                this.setWidth(tx_map.getWidth());
                this.setHeight(tx_map.getHeight());
            }
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                if(tx_map!=null)
                    batch.draw(tx_map, this.getX(), this.getY());
            }
        };
        sp = new ScrollPane(new Image(tx_map),new ScrollPane.ScrollPaneStyle());
        sp.setBounds(0,0,100,100);
        sp.setWidget(map);
        stage.addActor(sp);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }


}
