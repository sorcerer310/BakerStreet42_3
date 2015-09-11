package com.bsu.bk42.screen;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.PlcCommHelper;
import com.ugame.gdx.tools.ParticlePoolHelper;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

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

    private Array<FirePoint> bw_fparray = new Array<FirePoint>();                                                           //拨望坡所有点火点.
    private Array<FirePoint> ts_fparray = new Array<FirePoint>();                                                           //铁锁连环所有点火点

    private int currScreen =0;                                                                                      //当前的场景,0表示博望坡,1表示铁锁连环
    public FireScreen(){
        screenWidth = 720.0f;                                                                                           //设置游戏界面的宽高
        screenHeight = 1280.0f;
        stage = new Stage(new StretchViewport(screenWidth, screenHeight));
        scaleWidth = Gdx.graphics.getWidth()/screenWidth;                                                               //获得游戏界面与设备间的比例
        scaleHeight = Gdx.graphics.getHeight()/screenHeight;

        this.setFPS(40.0f);

        initGroup();
        initScrollPane();

//        plcCommand(1);                  //测试铁锁连环
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
        //初始化博望坡的着火点
        initBoWangFirePoint();



        //铁锁连环组设置
        ts_group.setBounds(0, 0, tx_tiesuo.getWidth(), tx_tiesuo.getHeight());
        ts_group.addActor(bg_tiesuo);
        //初始化铁锁连环着火点
        tx_firepoint = new Texture("fire/firepoint.png");
        //初始化铁锁连环的着火点
        initTieSuoFirePoint();

        //默认设置博望坡的组
        sp_group.setBounds(0,0,bw_group.getWidth(), bw_group.getHeight());
        sp_group.addActor(bw_group);
    }

    /**
     * 初始化滚动控件
     */
    private void initScrollPane(){
        sp = new ScrollPane(sp_group,new ScrollPane.ScrollPaneStyle());
        sp.setBounds(0, 0, screenWidth, screenHeight);
        sp.setWidget(sp_group);
        stage.addActor(sp);
    }

    /**
     * 初始化博望坡点火点
     */
    private void initBoWangFirePoint(){
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 254, 200));
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 749, 182));
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 1393, 209));
        bw_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 1809, 220));

        for(FirePoint fp:bw_fparray) {
            //循环为每个
            fp.addCaptureListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    fireSuccess(bw_fparray);
                }
            });
            bw_group.addActor(fp);
        }
    }

    /**
     * 铁锁连环点火点
     */
    private void initTieSuoFirePoint(){
        ts_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 334, 468));
        ts_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 949, 282));
        ts_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 1410, 470));
        ts_fparray.add(FirePoint.makeFirePoint(tx_firepoint, 1679, 230));

        for(FirePoint fp:ts_fparray) {
            //循环为每个
            fp.addCaptureListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    fireSuccess(ts_fparray);
                }
            });
            ts_group.addActor(fp);
        }
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
        if(currScreen==0)
            PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=BFIRE");
        else if(currScreen==1)
            PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=TFIRE");
        return true;
    }

    /**
     * 获得androidpn发过来的命令,当为0时显示博望坡背景,当为1时显示铁锁连环背景
     * @param cmdi
     */
    public void plcCommand(int cmdi) {
        System.out.println("========cmdi:" + cmdi);
        currScreen = cmdi;
        switch (cmdi) {
            case 0:             //博网坡背景
                sp_group.removeActor(ts_group);                                                                      //移除铁锁组
                background = bg_bowang;
                sp_group.addActor(bw_group);                                                                         //增加博望坡组
                for(FirePoint fp:bw_fparray)                                                                          //让博望坡的着火点都显示
                    fp.setVisible(true);
                break;
            case 1:             //铁锁连环背景
                sp_group.removeActor(bw_group);                                                                      //移除博望坡组
                background = bg_tiesuo;
                sp_group.addActor(ts_group);                                                                         //移除铁锁组
                for(FirePoint fp:ts_fparray)                                                                          //让铁锁的着火点都显示
                    fp.setVisible(true);
                break;
            default:
                break;
        }
        sp_group.setWidth(background.getWidth());
        sp_group.setHeight(background.getHeight());
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
class FirePoint extends Image implements Disposable {
    private ParticlePoolHelper pph_fire;
    private boolean isFire = false;
    private TweenManager tm = new TweenManager();

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

    /**
     * 让标记闪烁
     */
    public void flash(){
        this.setOrigin(this.getWidth()/2,this.getHeight()/2);
        Timeline.createSequence()
                .push(Tween.to(this, ActorAccessor.SCALE_XY,.5f).target(.5f,.5f))
                .push(Tween.to(this, ActorAccessor.SCALE_XY,.7f).target(1.0f,1.0f))
                .repeat(-1,.0f)
                .start(tm);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        pph_fire.act(delta);
        tm.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        pph_fire.draw(batch, parentAlpha);
    }



    public boolean isFire() {
        return isFire;
    }

    @Override
    public void dispose() {
        pph_fire.dispose();
        tm.killAll();
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
//        fp.setVisible(false);                                                                                           //默认设置不可见,当切换到该界面的时候再显示
        fp.flash();
        return fp;
    }


}
