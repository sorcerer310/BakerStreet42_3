package com.bsu.bk42.screen;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
    private Texture[] tx_clouds = null;

    private Array<Mark> marks = null;
    private Texture tx_mark = null;

    private Group mapgroup = new Group();                                                                           //加载地图上所有元素的group
    private int cloudsWidth = 8;
    private int cloudsHeight = 11;
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

                this.addListener(new InputListener() {

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        System.out.println("x:"+x+"   y:"+y);
                        return super.touchDown(event, x, y, pointer, button);
                    }
                });

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
        marks = makeMarks(new int[][]{
                {724, 598},                                          //星盘
                {587, 883}, {495, 202},                                //乌龟,插旗
                {284, 156}, {417, 256}, {121, 441}, {134, 889}, {413, 889},  //军令台,4个脚踏
                {282, 974}, {282, 1335},                               //通道两小门
                {115, 1652}, {368, 1652},                              //两侧墙壁铁索连环
                {568, 2036}, {661, 1715}, {683, 1475},                   //船舱门,草船借箭,擂鼓
                {137, 2118},                                         //借东风
                {736, 2121},                                         //追击小门
                {920, 2121}, {825, 1914}                               //大路小门,华容道小门
        });
        for(Mark m:marks)
            mapgroup.addActor(m);

//        clouds = makeClouds(new int[][]{
//                {825,889},{725,575},{893,576},{810,376},{772,200},{877,69},               //房间1
//                {548,889},{589,625},{555,426},{466,176},                                  //茅庐
//                {230,848},{230,599},{230,329},{230,150},                                  //博望坡
//                {279,1129},{280,1264},                                                    //通道
//
//        });

        int[][] cpoints = new int[cloudsWidth*cloudsHeight][2];
        for(int i=0;i<cloudsWidth;i++){
            for(int j=0;j<cloudsHeight;j++){
                cpoints[j*cloudsWidth+i][0] = i*150;
                cpoints[j*cloudsWidth+i][1] = j*200;
//                System.out.println("x:"+i*300+"  y:"+j*200);
            }
        }
        clouds = makeClouds(cpoints);
        dispareClouds(0);
        dispareClouds(1);
        dispareClouds(2);
        dispareClouds(3);
        dispareClouds(4);
        dispareClouds(5);
//        dispareClouds(6);
        dispareClouds(7);

        for(Image c:clouds)
            mapgroup.addActor(c);
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
    public Array<Image> makeClouds(int[][] points){
        tx_clouds = new Texture[]{new Texture(Gdx.files.internal("clouds/cloud1.png"))
                ,new Texture(Gdx.files.internal("clouds/cloud2.png"))
                ,new Texture(Gdx.files.internal("clouds/cloud3.png"))
        };
        Array<Image> clouds = new Array<Image>();
        for(int i=0;i<points.length;i++) {
            Texture tx_cloud = tx_clouds[MathUtils.random(0,2)];
            Image cloud = new Image(tx_cloud);
            cloud.setOrigin(cloud.getWidth() / 2, cloud.getHeight() / 2);
            cloud.setScale(MathUtils.random(1.2f, 1.2f));
//            cloud.setColor(cloud.getColor().r, cloud.getColor().g, cloud.getColor().b,0.5f);
//            cloud.setPosition(points[i][0]-tx_cloud.getWidth()/2, points[i][1]-tx_cloud.getHeight()/2);
              cloud.setPosition(points[i][0], points[i][1]);
            clouds.add(cloud);
        }
        return clouds;
    }

    /**
     * 按房间索引消失每组云彩++
     * @param roomi 房间索引
     */
    public void dispareClouds(int roomi){
        int[] di = null;
        switch(roomi){
            //房间1消失云彩
            case 0:
                di = new int[]{4+0*cloudsWidth,4+1*cloudsWidth,4+2*cloudsWidth,4+3*cloudsWidth,4+4*cloudsWidth
                        ,5+0*cloudsWidth,5+1*cloudsWidth,5+2*cloudsWidth,5+3*cloudsWidth,5+4*cloudsWidth
                        ,3+0*cloudsWidth,3+1*cloudsWidth,3+2*cloudsWidth,3+3*cloudsWidth,3+4*cloudsWidth
                };
                break;
            //茅庐消失云彩
            case 1:
                di = new int[]{
                        2+0*cloudsWidth,2+1*cloudsWidth,2+2*cloudsWidth,2+3*cloudsWidth,2+4*cloudsWidth
                };
                break;
            //博望坡消失云彩
            case 2:
                di = new int[]{
                        1+0*cloudsWidth,1+1*cloudsWidth,1+2*cloudsWidth,1+3*cloudsWidth,1+4*cloudsWidth,
                        0+0*cloudsWidth,0+1*cloudsWidth,0+2*cloudsWidth,0+3*cloudsWidth,0+4*cloudsWidth
                };
                break;
            //通道云彩
            case 3:
                di = new int[]{
                        2+5*cloudsWidth,1+5*cloudsWidth,0+5*cloudsWidth,
                        2+6*cloudsWidth,1+6*cloudsWidth,0+6*cloudsWidth
                };
                break;
            //铁锁连环
            case 4:
                di = new int[]{
                        2+7*cloudsWidth,1+7*cloudsWidth,0+7*cloudsWidth,
                        2+8*cloudsWidth,1+8*cloudsWidth,0+8*cloudsWidth,
                        2+9*cloudsWidth,1+9*cloudsWidth,0+9*cloudsWidth,
                        4+10*cloudsWidth,3+10*cloudsWidth,2+10*cloudsWidth,1+10*cloudsWidth,0+10*cloudsWidth
                };
                break;
            //草船借箭
            case 5:
                di = new int[]{
                        3+6*cloudsWidth,4+6*cloudsWidth,
                        3+7*cloudsWidth,4+7*cloudsWidth,
                        3+8*cloudsWidth,4+8*cloudsWidth,
                        3+9*cloudsWidth,4+9*cloudsWidth,

                };
                break;
            //华容道
            case 6:
                di = new int[]{
                        5+6*cloudsWidth,6+6*cloudsWidth,
                        5+7*cloudsWidth,6+7*cloudsWidth,
                        5+8*cloudsWidth,6+8*cloudsWidth,
                        5+9*cloudsWidth,6+9*cloudsWidth,
                        5+10*cloudsWidth
                };
                break;
            //大路
            case 7:
                di = new int[]{
                        7+6*cloudsWidth,6+6*cloudsWidth,
                        7+7*cloudsWidth,6+7*cloudsWidth,
                        7+8*cloudsWidth,6+8*cloudsWidth,
                        7+9*cloudsWidth,6+9*cloudsWidth,
                        7+10*cloudsWidth,6+10*cloudsWidth,
                        5+10*cloudsWidth
                };
                break;
        }
        for(int i=0;i<di.length;i++)
            clouds.get(di[i]).setVisible(false);
    }

    /**
     * 生成所有地图标记
     * @return  返回一组标记,用于标识地图上的关键点
     */
    public Array<Mark> makeMarks(int[][] points){
        tx_mark = new Texture(Gdx.files.internal("mark.png"));
        tx_mark.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Array<Mark> marks = new Array<Mark>();
        for(int i=0;i<points.length;i++){
            Mark mark = new Mark(tx_mark);
            mark.setPosition(points[i][0]-tx_mark.getWidth()/2, points[i][1]);
//            mark.setVisible(false);
            marks.add(mark);
        }
        return marks;
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }


}

/**
 * 地图上的标记
 */
class Mark extends Image{
    private Timeline tl;

    public Mark(Texture t){
        super(t);
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
}