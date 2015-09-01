package com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.PlcCommHelper;
import com.ugame.gdx.tools.UGameScreen;

/**
 * Created by fengchong on 2015/8/22.
 */
public class FollowUpScreen extends UGameScreen {
    public static float screenWidth,screenHeight,scaleWidth,scaleHeight;
    private Texture t_road1 = null;                                                                                 //华容道图片
    private Texture t_road2 = null;                                                                                 //大路图片
    private RoadButton rbutton1,rbutton2;

    private StateMachine stateMachine;                                                                              //状态机对象,用来控制界面状态.
    enum FollowUpScreenState implements State<FollowUpScreen> {
        STATE_NOMAL(){
            @Override
            public void enter(FollowUpScreen entity) {}
            @Override
            public void update(FollowUpScreen entity) {}
            @Override
            public void exit(FollowUpScreen entity) {}
            @Override
            public boolean onMessage(FollowUpScreen entity, Telegram telegram) {return false;}
        },
        STATE_SELECTED(){
            @Override
            public void enter(FollowUpScreen entity) {}
            @Override
            public void update(FollowUpScreen entity) {}
            @Override
            public void exit(FollowUpScreen entity) {}
            @Override
            public boolean onMessage(FollowUpScreen entity, Telegram telegram) {return false;}
        }
    }



    public FollowUpScreen(){
        //视口初始化
        screenWidth = 720.0f;                                                                                           //设置游戏界面的宽高
        screenHeight = 1280.0f;
        stage = new Stage(new StretchViewport(screenWidth, screenHeight));
        scaleWidth = Gdx.graphics.getWidth()/screenWidth;                                                               //获得游戏界面与设备间的比例
        scaleHeight = Gdx.graphics.getHeight()/screenHeight;

        //初始化状态机部分
        stateMachine = new DefaultStateMachine<FollowUpScreen>(this,FollowUpScreenState.STATE_NOMAL);

        t_road1 = new Texture(Gdx.files.internal("followup/road1.jpg"));
        t_road2 = new Texture(Gdx.files.internal("followup/road2.jpg"));

        rbutton1 = new RoadButton(t_road1);
        rbutton2 = new RoadButton(t_road2);

        rbutton1.setPosition(.0f,rbutton2.getHeight());

        //点击华容道按钮
        rbutton1.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("===================touchDown1");
                if(stateMachine.isInState(FollowUpScreenState.STATE_NOMAL)) {
                    PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=HUARONG");
                    stateMachine.changeState(FollowUpScreenState.STATE_SELECTED);
                    rbutton1.setB_cover(true);
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        //点击大路按钮
        rbutton2.addCaptureListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("===================touchDown2");
                rbutton2.setB_cover(true);
                if(stateMachine.isInState(FollowUpScreenState.STATE_NOMAL)) {
                    PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=BIGROAD");
                    stateMachine.changeState(FollowUpScreenState.STATE_SELECTED);
                    ((RoadButton)(event.getTarget())).setB_cover(true);
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        stage.addActor(rbutton1);
        stage.addActor(rbutton2);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stateMachine.update();
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        super.dispose();
        t_road1.dispose();
        t_road2.dispose();
    }
}

/**
 * 选择道路的大按钮
 */
class RoadButton extends Image implements Disposable {
    private Texture texture = null;
    private Pixmap pixmap = null;
    private Texture t_cover = null;
    private boolean b_cover = false;
    public RoadButton(Texture t){
        super(t);
        texture = t;
        pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(.0f, .0f, .0f, .5f);
        pixmap.fillRectangle(0, 0, 1, 1);
        t_cover = new Texture(pixmap);


    }

    /**
     * 设置是否显示遮罩层
     * @param b_cover
     */
    public void setB_cover(boolean b_cover) {this.b_cover = b_cover;}

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        //绘制遮罩层
        if (texture != null && !b_cover)
            batch.draw(t_cover, this.getX(), this.getY(), (float) texture.getWidth(), (float) texture.getHeight());
    }

    @Override
    public void dispose() {
        if(pixmap!=null)
            pixmap.dispose();
    }
}

