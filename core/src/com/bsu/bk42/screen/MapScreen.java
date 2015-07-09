package com.bsu.bk42.screen;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
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
import com.ugame.gdx.tween.accessor.ActorAccessor;

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

    private Group mapgroup = new Group();                                                                           //加载地图上所有元素的group

    public MapScreen(){
        stage = new Stage(new StretchViewport(700.0F, 1280.0f));

        //初始化地图元素组
        initMapGroup();
        //初始化滚动控件
        initScrollPane();

    }



    /**
     * 初始化地图组上的所有元素
     */
    private void initMapGroup(){
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
        mapgroup.setBounds(0, 0, tx_map.getWidth(), tx_map.getHeight());
        mapgroup.addActor(map);

//        Array<Image> clouds = setClouds();
//        for(int i=0;i<clouds.size;i++)
//            mapgroup.addActor(clouds.get(i));

        Mark mark = new Mark();
        mark.setPosition(100,100);
        mark.jump();
        mapgroup.addActor(mark);

    }

    /**
     * 初始化滚动控件
     */
    private void initScrollPane(){
        sp = new ScrollPane(mapgroup, new ScrollPane.ScrollPaneStyle());
        sp.setBounds(0, 0, 700, 1280);
        sp.setWidget(mapgroup);
        sp.invalidate();
        sp.validate();
        sp.setScrollPercentX(.6f);
        sp.setScrollPercentY(1.0f);
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

/**
 * 地图上的标记
 *
 */
class Mark extends Actor{
    private Texture tx = new Texture(Gdx.files.internal("mark.png"));
    private Timeline tl;
    public Mark(){
        super();
        Tween.registerAccessor(this.getClass(), new ActorAccessor());
    }

    /**
     * 跳动函数,让标记跳动,提示玩家
     */
    public void jump(){
        tl = Timeline.createSequence()
                .push(Tween.to(this,ActorAccessor.POS_XY,.3f).target(this.getX(),this.getY()-20.0f))
                .push(Tween.to(this,ActorAccessor.POS_XY,.6F).target(this.getX(),this.getY())
                    .ease(TweenEquations.easeNone)
                ).repeat(-1,.0f).start();
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl!=null)
            tl.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(tx, this.getX(), this.getY());
    }
}