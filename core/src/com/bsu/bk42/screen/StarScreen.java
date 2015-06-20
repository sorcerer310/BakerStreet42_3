package com.bsu.bk42.screen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

/**
 * 星星解锁
 * Created by Administrator on 2015/5/27.
 */
public class StarScreen extends UGameScreen {
    public static float screenWidth,screenHeight,scaleWidth,scaleHeight;

    private Texture tx_star = null;                                                                                 //星星图案的纹理
    private Array<StarImage> stars = new Array<StarImage>();

    private enum DRAWSTATE {NOMAL,DRAW};                                                                            //连线的绘制状态
    private DRAWSTATE state = DRAWSTATE.NOMAL;                                                                       //当前连线状态
    private Vector2 movePoint = new Vector2();

    private Array<Vector2> linePoints = new Array<Vector2>();                                                       //要绘制的所有拐点
    private int lineWidth = 5;
    public StarScreen(){
        screenWidth = 480.0f;
        screenHeight = 800.0f;
        stage = new Stage(new StretchViewport(screenWidth, screenHeight));
//        stage.addCaptureListener(new DragListener(){});

        scaleWidth = Gdx.graphics.getWidth()/screenWidth;
        scaleHeight = Gdx.graphics.getHeight()/screenHeight;

        tx_star = new Texture(Gdx.files.internal("star.png"));
        stars = initStars(tx_star);
        stage.addListener(new DragListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //当鼠标点在某个星星范围内设置为绘制的第一个点.
                for(int i=0;i<stars.size;i++){
                    if(stars.get(i).hit(x-stars.get(i).getX(),y-stars.get(i).getY(),true)!=null) {
                        linePoints.clear();
                        StarImage si = stars.get(i);
                        si.setIsSelected(true);                                                                         //设置当前星星为选中
                        linePoints.add(new Vector2(si.getX()+si.getWidth()/2,si.getY()+si.getHeight()/2));
                    }
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                //当抬起鼠标时恢复绘状态
                state = DRAWSTATE.NOMAL;
                for(int i=0;i<stars.size;i++)
                    stars.get(i).setIsSelected(false);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if (state == DRAWSTATE.NOMAL)
                    state = DRAWSTATE.DRAW;                                                                           //切换到绘制状态
                movePoint.set(event.getStageX(), event.getStageY());                                                  //设置移动时的坐标
                //判断当前是否经过了其他星,如果经过了其他的则获得下个绘制拐点
                for(int i=0;i<stars.size;i++) {
                    StarImage si = stars.get(i);
                    if (!si.isSelected() && si.hit(x-stars.get(i).getX(),y-stars.get(i).getY(),true)!=null) {
                        linePoints.add(new Vector2(si.getX() + si.getWidth() / 2, si.getY() + si.getHeight() / 2));
                        si.setIsSelected(true);
                        System.out.println("setIsSelected "+linePoints.size);
                    }
                }
            }
        });
    }
    private ShapeRenderer srender = new ShapeRenderer();
    @Override
    public void render(float delta) {
        super.render(delta);
        if(state==DRAWSTATE.DRAW) {
            srender.begin(ShapeRenderer.ShapeType.Filled);
            for(int i=0;i<linePoints.size;i++) {
//                if(linePoints.size==2)
//                    System.out.println("render linePoint size"+linePoints.size);
                //绘制末端可移动的线
                if(i==linePoints.size-1) {
                    int index = linePoints.size==1?0:i;
                    srender.rectLine(linePoints.get(index).x * StarScreen.scaleWidth, linePoints.get(index).y * StarScreen.scaleHeight,
                            movePoint.x * StarScreen.scaleWidth, movePoint.y * StarScreen.scaleHeight, lineWidth);
                }
                //绘制点与点之间固定的线
                if(i!=0){
                    srender.rectLine(linePoints.get(i - 1).x * StarScreen.scaleWidth, linePoints.get(i - 1).y * StarScreen.scaleHeight,
                            linePoints.get(i).x * StarScreen.scaleWidth, linePoints.get(i).y * StarScreen.scaleHeight, lineWidth);

                }
            }
            srender.end();
        }
    }

    /**
     * 在该函数中初始化所有星星
     * @param t     带入初始化星星图形的纹理对象
     * @return      返回星星对象的数组
     */
    private Array<StarImage> initStars(Texture t){
        Array<StarImage> s = new Array<StarImage>();
        for(int i=0;i<7;i++) {
            StarImage si = new StarImage(t);
            stage.addActor(si);
            s.add(si);
        }
        s.get(0).setPosition(256,726);
        s.get(1).setPosition(160,626);
        s.get(2).setPosition(150,460);
        s.get(3).setPosition(200,330);
        s.get(4).setPosition(330,290);
        s.get(5).setPosition(346,149);
        s.get(6).setPosition(189,112);
        return s;
    }



    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }
}

/**
 * 星星对象
 */
class StarImage extends Image{
    private TweenManager tm = new TweenManager();
    private Vector2 movePoint = new Vector2();

    private boolean isSelected = false;

    public StarImage(Texture t){
        super(t);
        Tween.registerAccessor(Image.class, new ActorAccessor());
        Tween.to(this, ActorAccessor.ROTATION_CPOS_XY, 1.0f).target(360.0f)
                .repeat(-1, 0.0f).start(tm);
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        tm.update(delta);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}

