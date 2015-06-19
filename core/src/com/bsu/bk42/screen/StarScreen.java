package com.bsu.bk42.screen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
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
    private Array<Image> stars = null;
    private TweenManager tm = new TweenManager();
    public StarScreen(){
        stage = new Stage(new StretchViewport(480.0F, 800.0f));

        tx_star = new Texture(Gdx.files.internal("star.png"));
        star = new StarImage(tx_star);
        star.setPosition(100,70);
        stage.addActor(star);



    }

    private Array<Image> initStars(Texture t){
        Array<Image> s = new Array<Image>();
        for(int i=0;i<3;i++)
            s.add(new StarImage(tx_star));
        s.get(0).setPosition(100,100);
        s.get(1).setPosition(100,200);
        s.get(2).setPosition(300,300);
        return s;
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }
}

class StarImage extends Image{
    private enum DRAWSTATE {NOMAL,DRAW,ENDDRAW};
    private DRAWSTATE state = DRAWSTATE.NOMAL;
    private TweenManager tm = new TweenManager();
    private Vector2 movePoint = new Vector2();

    private Pixmap line = new Pixmap(720,1280,Pixmap.Format.RGBA8888);                                               //该点对应的线
    private Texture tline = new Texture(720,1280,Pixmap.Format.RGBA8888);
    private TextureRegion region = null;
    public StarImage(Texture t){
        super(t);
        Tween.registerAccessor(Image.class, new ActorAccessor());
        Tween.to(this, ActorAccessor.ROTATION_CPOS_XY, 1.0f).target(360.0f)
                .repeat(-1, 0.0f).start(tm);

        Gdx.gl20.glLineWidth(5.0f);
        line.setColor(Color.WHITE);

//        line.drawLine(0,0,300,300);
//        tline.draw(line,0,0);
        region = new TextureRegion(tline, 0, 0, 480, 800);

        this.addCaptureListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("==============touch down ");
                if(state==DRAWSTATE.NOMAL || state==DRAWSTATE.ENDDRAW)
                    state = DRAWSTATE.DRAW;                                                                           //切换到绘制状态
//                return super.touchDown(event, x, y, pointer, button);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                state = DRAWSTATE.ENDDRAW;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if(state == DRAWSTATE.DRAW)
                    movePoint.set(x,y);                                                                          //设置移动时的坐标
                System.out.println("================dragged:"+x+" "+y);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                System.out.println("================mouseMoved:"+x+" "+y);

            return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
            }
        });
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

//        line.fillRectangle(0,0,200,200);
//        batch.draw(new Texture(line),0,0);
//        line.drawLine(0,0,200,200);
        if(state == DRAWSTATE.DRAW){

//            line.setColor(Color.WHITE);
//            line.drawLine((int)this.getX(),(int)this.getY(),(int)movePoint.x,(int)movePoint.y);
//            tline.draw(line,0,0);
//            region.setTexture(tline);
//            batch.draw(region,0,0);


//            int dx = (int)(movePoint.x-this.getX());
//            int dy = (int)(movePoint.y-this.getY());
//            float dist = (float)Math.sqrt(dx*dx + dy*dy);
//            float rad = (float)Math.atan2(dy, dx);
//            batch.draw(rect, (this.getX()), this.getY(), dist, 5, 0, 0, rad);


        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tm.update(delta);
    }


}

