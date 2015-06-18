package com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ugame.gdx.tools.UGameScreen;

/**
 * 地图场景
 * Created by Administrator on 2015/5/27.
 */
public class MapScreen extends UGameScreen {
    private Texture tx_map = null;                                                                                   //要绘制的地图
    private Actor map = null;                                                                                        //承载地图的actor
    private ScrollPane sp = null;

    private Array<Image> clouds = null;                                                                              //保存所有的遮挡迷雾
    private Texture tx_cloud = null;

    private Group mapgroup = new Group();                                                                           //加载所有元素的group

    public MapScreen(){
        stage = new Stage(new StretchViewport(480.0F, 800.0f));

        Array<Image> clouds = setClouds();

        tx_map = new Texture(Gdx.files.internal("map.png"));
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
//        map = new Image(tx_map);
        mapgroup.setBounds(0,0,800,1280);
        mapgroup.addActor(map);
        for(int i=0;i<clouds.size;i++)
            mapgroup.addActor(clouds.get(i));

        sp = new ScrollPane(mapgroup,new ScrollPane.ScrollPaneStyle());
        sp.setBounds(0,0,720,1080);
        sp.setWidget(mapgroup);
        stage.addActor(sp);

    }

    /**
     * 设置迷雾云彩
     */
    public Array<Image> setClouds(){
        tx_cloud = new Texture(Gdx.files.internal("clouds/cloud1.png"));
        clouds = new Array<Image>();
        for(int i=0;i<3;i++) {
            Image cloud = new Image(tx_cloud);
            cloud.setColor(cloud.getColor().r,cloud.getColor().g,cloud.getColor().b,0.5f);
            clouds.add(cloud);
            stage.addActor(cloud);
        }
        clouds.get(0).setPosition(100,100);
        clouds.get(1).setPosition(200,200);
        clouds.get(2).setPosition(150,700);
        return clouds;
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }


}
