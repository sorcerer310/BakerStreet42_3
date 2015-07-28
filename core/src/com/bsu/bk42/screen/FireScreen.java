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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ugame.gdx.tools.ParticlePoolHelper;
import com.ugame.gdx.tools.UGameScreen;

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
    private Group group = new Group();

    private Image background = null;
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

        tx_tiesuo = new Texture("fire/firepoint.png");
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

        background = bg_bowang;                                                                                      //默认设置博望坡背景

        float v = (float)((float)screenHeight/(int)tx_bowang.getWidth());
//        bg_bowang.setScaleX(v);
//        bg_bowang.setRotation(-90.0f);
//        bg_bowang.setPosition(0, screenHeight);
        group.setBounds(0,0,tx_bowang.getWidth(),tx_bowang.getHeight());
        group.addActor(background);


        //初始化着火点
        tx_firepoint = new Texture("fire/firepoint.png");

        group.addActor(FirePoint.makeFirePoint(tx_firepoint,554,468));
        group.addActor(FirePoint.makeFirePoint(tx_firepoint, 1049, 582));
        group.addActor(FirePoint.makeFirePoint(tx_firepoint, 1893, 609));
        group.addActor(FirePoint.makeFirePoint(tx_firepoint, 2509, 420));

    }

    /**
     * 初始化滚动控件
     */
    private void initScrollPane(){
        sp = new ScrollPane(group,new ScrollPane.ScrollPaneStyle());
        sp.setBounds(0,0,screenWidth,screenHeight);
        sp.setWidget(group);
        stage.addActor(sp);
    }

    /**
     * 获得androidpn发过来的命令,当为0时显示博望坡背景,当为1时显示铁锁连环背景
     * @param cmdi
     */
    public void plcCommand(int cmdi) {
        System.out.println("========cmdi:"+cmdi);
        switch (cmdi) {
            case 0:             //博网坡背景
                background = bg_bowang;
                break;
            case 1:             //铁锁连环背景
                background = bg_tiesuo;
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
        this.addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!isFire) {
                    pph_fire.playAllEffect(FirePoint.this.getX()+FirePoint.this.getWidth()/2
                            , FirePoint.this.getY());
                    for(int i=0;i<3;i++) {
                        float rx = FirePoint.this.getX()+FirePoint.this.getWidth()/2+MathUtils.random(-300.0f,300.0f);
                        float ry = FirePoint.this.getY()+MathUtils.random(-300.0f,300.0f);
                        pph_fire.playAllEffect(rx,ry);
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
