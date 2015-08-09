package com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.PlcCommHelper;
import com.ugame.gdx.tools.ParticlePoolHelper;
import com.ugame.gdx.tools.UGameScreen;

import java.util.Iterator;

/**
 * 点火界面,分为博望坡和铁锁连环两个场景
 * Created by FC on 2015/7/20.
 */
public class FireScreen extends UGameScreen {
    public static float screenWidth,screenHeight,scaleWidth,scaleHeight;

    //博望坡纹理与图片
    private Texture tx_bowang;
    private Image bg_bowang;
    //铁锁连环纹理与图片
    private Texture tx_tiesuo;
    private Image bg_tiesuo;

    private Texture tx_firepoint;

    private ParticlePoolHelper pph_fire;
    private ScrollPane sp;
    private Group bw_group = new Group();                                                                           //博望坡组
    private Group ts_group = new Group();                                                                           //铁锁连环组
    private Group sp_group = new Group();                                                                           //加入scrollpane组件的组

    private Image background = null;

    private Array<FirePoint> bw_fparray = new Array<FirePoint>();                                                    //拨望坡所有点火点.
    private Array<FirePoint> ts_fparray = new Array<FirePoint>();                                                    //铁锁连环所有点火点

    public FireScreen(){
        screenWidth = 720.0f;                                                                                           //设置游戏界面的宽高
        screenHeight = 1280.0f;
        stage = new Stage(new StretchViewport(screenWidth, screenHeight));
        scaleWidth = Gdx.graphics.getWidth()/screenWidth;                                                               //获得游戏界面与设备间的比例
        scaleHeight = Gdx.graphics.getHeight()/screenHeight;

        this.setFPS(40.0f);

        initGroup();
        initScrollPane();
    }

    /**
     * 初始化组,用于装入滚动控件中
     */
    private void initGroup(){
        //设置背景
        tx_bowang = new Texture("fire/bowang.jpg");
        bg_bowang = new Image(tx_bowang){
            {
                this.addCaptureListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        System.out.println("x:"+x+"   y:"+y);
                        return super.touchDown(event, x, y, pointer, button);
                    }
                });
            }
        };

        tx_tiesuo = new Texture("fire/tiesuo.jpg");
        bg_tiesuo = new Image(tx_tiesuo){
            {
                this.addCaptureListener((new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        System.out.println("x:"+x+"   y:"+y);
                        return super.touchDown(event, x, y, pointer, button);
                    }
                }));
            }
        };

        //博望坡组设置
        bw_group.setBounds(0, 0, tx_bowang.getWidth(), tx_bowang.getHeight());
        bw_group.addActor(bg_bowang);
        //初始化博望坡着火点
        tx_firepoint = new Texture("fire/firepoint.png");


        //铁锁连环组设置
        ts_group.setBounds(0, 0, tx_tiesuo.getWidth(), tx_tiesuo.getHeight());
        ts_group.addActor(bg_tiesuo);
        //初始化铁锁连环着火点
        tx_firepoint = new Texture("fire/firepoint.png");

        ts_group.addActor(FirePoint.makeFirePoint(tx_firepoint, 554, 468));
        ts_group.addActor(FirePoint.makeFirePoint(tx_firepoint, 1049, 582));
        ts_group.addActor(FirePoint.makeFirePoint(tx_firepoint, 1893, 609));
        ts_group.addActor(FirePoint.makeFirePoint(tx_firepoint, 2509, 420));

        //默认设置博望坡的组
        sp_group.setBounds(0,0,bw_group.getWidth(),bw_group.getHeight());
        sp_group.addActor(bw_group);

    }

    /**
     * 初始化滚动控件
     */
    private void initScrollPane(){
        sp = new ScrollPane(sp_group,new ScrollPane.ScrollPaneStyle());
        sp.setBounds(0,0,screenWidth,screenHeight);
        sp.setWidget(sp_group);
        stage.addActor(sp);
    }

    /**
     * 初始化博望坡点火点
     */
    private void initBoWangFirePoint(){
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 554, 468));
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 1049, 582));
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 1893, 609));
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 2509, 420));

        //未完成,到此处
        bw_fparray.get(0).addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                fireSuccess(bw_fparray);
            }
        });
        //未完成,到此出

        bw_group.addActor(bw_fparray.get(0));
        bw_group.addActor(bw_fparray.get(1));
        bw_group.addActor(bw_fparray.get(2));
        bw_group.addActor(bw_fparray.get(3));

    }

    /**
     * 判断点火成功
     * @param fps   4个点火点
     */
    private boolean fireSuccess(Array<FirePoint> fps){
        Iterator<FirePoint> it = fps.iterator();
        while(it.hasNext()){
            FirePoint fp = it.next();
            //其中有一个失败,则返回false
            if(!fp.isFire())
                return false;
        }
        //循环成功发送点火消息,并返回成功
//        if()
//        PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd="+);
        return true;
    }

    /**
     * 获得androidpn发过来的命令,当为0时显示博望坡背景,当为1时显示铁锁连环背景
     * @param cmdi
     */
    public void plcCommand(int cmdi) {
        System.out.println("========cmdi:"+cmdi);
        switch (cmdi) {
            case 0:             //博网坡背景
                sp_group.removeActor(ts_group);
                background = bg_bowang;
                sp_group.addActor(bw_group);
                break;
            case 1:             //铁锁连环背景
                sp_group.removeActor(bw_group);
                background = bg_tiesuo;
                sp_group.addActor(ts_group);
                break;
            default:
                break;
        }
    }

    @Override
    public void show(){
        super.show();
        Gdx.input.setInputProcessor(stage);
    }
}

/**
 * 着火点
 */
class FirePoint extends Image{
    private ParticlePoolHelper pph_fire;

    private boolean isFire = false;

    public FirePoint(final Texture tx){
        super(tx);
        pph_fire = new ParticlePoolHelper("particle/fire.p","particle");
        this.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!isFire) {
                    pph_fire.playAllEffect(FirePoint.this.getX() + FirePoint.this.getWidth() / 2
                            , FirePoint.this.getY());
                    for (int i = 0; i < 3; i++) {
                        float rx = FirePoint.this.getX() + FirePoint.this.getWidth() / 2 + MathUtils.random(-300.0f, 300.0f);
                        float ry = FirePoint.this.getY() + MathUtils.random(-300.0f, 300.0f);
                        pph_fire.playAllEffect(rx, ry);
                    }
                    isFire = true;
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        pph_fire.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        pph_fire.draw(batch, parentAlpha);
    }

    public boolean isFire() {
        return isFire;
    }



    /**
     * 静态方法,用来生成该类的对象
     * @param tx        对象使用的纹理
     * @param x         设置该对象x坐标
     * @param y         设置该对象y坐标
     * @return          返回一个着火点对象
     */
    public static FirePoint makeFirePoint(Texture tx,float x,float y){
        FirePoint fp = new FirePoint(tx);
        fp.setPosition(x,y);
        return fp;
    }
}
