package com.bsu.bk42.screen;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.PlcCommHelper;
import com.ugame.gdx.tools.ParticlePoolHelper;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;
import com.ugame.net.UGameNetInstance;

import java.util.List;


/**
 * 星星解锁
 * Created by Administrator on 2015/5/27.
 */
public class StarScreen extends UGameScreen {
    public static float screenWidth,screenHeight,scaleWidth,scaleHeight;

    private Texture tx_star = null;                                                                                  //星星图案的纹理
    private Array<StarImage> currStars = new Array<StarImage>();
    private Array<Array<StarImage>> all3ScreenStars = new Array<Array<StarImage>>();                                              //3屏幕的所有星星
    private int currScreen = 0;                                                                                      //当前屏幕的索引
    private int disappearCount = 0;                                                                                 //当前屏幕星星消失的数量
    private Texture tx_starbackground = null;                                                                      //星星背景
    private StarBackgroundImage sbi;                                                                                  //星星背景对象

    private enum DRAWSTATE {NOMAL,DRAW,END};                                                                             //连线的绘制状态
    private DRAWSTATE state = DRAWSTATE.NOMAL;                                                                       //当前连线状态
    private Vector2 movePoint = new Vector2();

    private Array<Vector2> linePoints = new Array<Vector2>();                                                             //要绘制的所有拐点
    private int lineWidth = 5;                                                                                       //绘制的线段宽度

    private ParticlePoolHelper pph_star = new ParticlePoolHelper("particle/star.p","particle");                          //星星粒子

    public StarScreen(){
        screenWidth = 720.0f;                                                                                           //设置游戏界面的宽高
        screenHeight = 1280.0f;
        stage = new Stage(new StretchViewport(screenWidth, screenHeight));
        scaleWidth = Gdx.graphics.getWidth()/screenWidth;                                                               //获得游戏界面与设备间的比例
        scaleHeight = Gdx.graphics.getHeight()/screenHeight;

        this.setFPS(40.0f);


        tx_starbackground = new Texture(Gdx.files.internal("star/starbackground.png"));
        tx_star = new Texture(Gdx.files.internal("star/star.png"));

        //初始化背景
        sbi = new StarBackgroundImage(tx_starbackground){
            {
                //背景移动完后把当前屏幕的星星切换为下一屏幕的星星.
                this.setMoveListener(new MoveListener() {
                    @Override
                    public void completed(StarBackgroundImage sbi) {
                        for(StarImage sii:currStars) {                                                                //增加新屏幕的星星
                            sii.setColor(sii.getColor().r,sii.getColor().g,sii.getColor().b,.0f);                       //设置星星的透明度为0
                            sii.appear();
                            stage.addActor(sii);
                        }
                    }
                });
            }
        };                                                           //设置背景图对象

        stage.addActor(sbi);
        //初始化所有的星星

        all3ScreenStars = init3ScreenStars(tx_star);
        currStars = all3ScreenStars.get(currScreen);
        for(StarImage si:currStars) {
            Color color = si.getColor();
            si.setColor(color.r,color.g,color.b,.0f);
            si.appear();
            stage.addActor(si);
        }


        stage.addListener(new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(state == DRAWSTATE.END)
                    return true;
                //当鼠标点在某个星星范围内设置为绘制的第一个点.
                for (int i = 0; i < currStars.size; i++) {
                    if (currStars.get(i).hit(x - currStars.get(i).getX(), y - currStars.get(i).getY(), true) != null) {
                        linePoints.clear();
                        StarImage si = currStars.get(i);
                        si.setIsSelected(true);                                                                         //设置当前星星为选中
                        linePoints.add(new Vector2(si.getX() + si.getWidth() / 2, si.getY() + si.getHeight() / 2));
                    }
                }
                pph_star.playAllEffect(x, y);
                System.out.println("x:" + x + " y:" + y);
//                sbi.goRight();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if(state==DRAWSTATE.END)
                    return;
                //当抬起鼠标时恢复绘状态
                state = DRAWSTATE.NOMAL;
                for (int i = 0; i < currStars.size; i++)
                    currStars.get(i).setIsSelected(false);                                                            //设置所有的星星为未选中

                //判断当前所有连接拐点与顺序一致,如果一致则正确切换到下一屏
                boolean right = true;
                if (linePoints.size == currStars.size) {
                    for (int i = 0; i < currStars.size; i++)
                        if (currStars.get(i).hit(linePoints.get(i).x - currStars.get(i).getX()
                                , linePoints.get(i).y - currStars.get(i).getY(), true) == null) {
                            right = false;
                            break;
                        }
                } else {
                    right = false;
                }


//                System.out.println("time:" + (System.currentTimeMillis() - start));
//                start = System.currentTimeMillis();

                //如果所有的点都正确,切到下一屏,并设置当前的星星为下一屏幕的星星
                if (right) {
                    if (currScreen < 2) {
                        //当屏幕为前两个屏幕时移动到下个屏幕
                        for (StarImage si : currStars) {                                                                       //先移除当前屏幕的星星
                            si.setOpacityListener(new StarImage.OpacityListener() {
                                @Override
                                public void disappearCompleted(StarImage si) {
                                    if (++disappearCount == currStars.size) {
                                        sbi.goRight();                                                                  //背景向右移动
                                        disappearCount = 0;                                                           //消失的星星统计归0
                                        currStars = all3ScreenStars.get(++currScreen);                             //切到下一屏的星星
//                                        stage.getActors().removeValue(si, true);                                       //动画完成移除所有星星
                                    }
                                }

                                @Override
                                public void appearCompleted(StarImage si) {

                                }
                            });
                            si.disappear();

                        }
                    } else {
                        //当前屏幕为最后一个屏幕时整个星图成功
                        for (StarImage si : currStars) {                                                                       //先移除当前屏幕的星星
                            si.setOpacityListener(new StarImage.OpacityListener() {
                                @Override
                                public void disappearCompleted(StarImage si) {
                                    if (++disappearCount == currStars.size) {
                                        sbi.goScale();
//                                        stage.getActors().removeValue(si, true);                                           //动画完成移除所有星星
                                    }
                                }

                                @Override
                                public void appearCompleted(StarImage si) {

                                }
                            });
                            si.disappear();
                            state = DRAWSTATE.END;                                                                      //将状态设置为绘制结束
                        }
                    }

                    String sparam = "";                                                                                 //星盘一段亮起参数
                    if(currScreen==0)
                        sparam = "S0-2";
                    else if(currScreen==1)
                        sparam = "S3-10";
                    else if(currScreen == 2)
                        sparam = "S11-21";

                    System.out.println("++++++++right+++++++++"+"/plc_send_serial?plccmd=" + sparam);
                    PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=" + sparam);

                } else {
                    System.out.println("not right");

                    String nsparam = "";
                    if(currScreen==0)
                        nsparam = "NS0-2";
                    else if(currScreen==1)
                        nsparam = "NS3-10";
                    else if(currScreen == 2)
                        nsparam = "NS11-21";

                    System.out.println("++++++++not right+++++++++"+"/plc_send_serial?plccmd=" + nsparam);
                    PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=" + nsparam);

                }
                linePoints.clear();                                                                                   //设置要绘制的拐点为空
                pph_star.stopEffect();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if(state==DRAWSTATE.END)
                    return;
                if (state == DRAWSTATE.NOMAL)
                    state = DRAWSTATE.DRAW;                                                                           //切换到绘制状态
                movePoint.set(event.getStageX(), event.getStageY());                                                  //设置移动时的坐标
                //判断当前是否经过了其他星,如果经过了其他的则获得下个绘制拐点
                for (int i = 0; i < currStars.size; i++) {
                    StarImage si = currStars.get(i);
                    if (!si.isSelected() && si.hit(x - currStars.get(i).getX(), y - currStars.get(i).getY(), true) != null) {
                        linePoints.add(new Vector2(si.getX() + si.getWidth() / 2, si.getY() + si.getHeight() / 2));
                        si.setIsSelected(true);
                    }
                }

                pph_star.moveEffect(x, y);
            }
        });

        stage.addActor(pph_star);                                                                                     //星星粒子加入到stage中
    }

    //渲染连线
    private ShapeRenderer srender = new ShapeRenderer();
    @Override
    public void render(float delta) {
        super.render(delta);
        if(state==DRAWSTATE.DRAW) {
            srender.begin(ShapeRenderer.ShapeType.Filled);
            for(int i=0;i<linePoints.size;i++) {
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

    private int sumStarCount = 0;                                                                                   //统计当前生成了星星的数量,用来给星星做id标识
    /**
     * 初始化一屏的星图
     * @param t         星星的纹理
     * @param count    要初始化的星星的数量
     * @param p         所有星星的坐标,2维数组 第1维数据是x y坐标,第2维数据表示哪颗5星星
     * @return          返回一屏设置好的星星
     */
    private Array<StarImage> init1ScreenStars(Texture t,int count,int[][] p){
        Array<StarImage> s = new Array<StarImage>();
        for(int i=0;i<count;i++) {
            StarImage si = new StarImage(t);
            si.setId("S" + sumStarCount++);
            si.setPosition(p[i][0]-si.getWidth()/2,p[i][1]-si.getHeight()/2);
            s.add(si);
        }
        return s;
    }

    /**
     * 3屏所有的星星,在不同的屏幕对应显示不同的星星组
     * @param t 要实现星星的纹理图片
     * @return  返回3屏幕星星数组
     */
    private Array<Array<StarImage>> init3ScreenStars(Texture t){
        Array<Array<StarImage>> sstars = new Array<Array<StarImage>>();
        sstars.add(init1ScreenStars(t,3,new int[][]{{393,673},{126,478},{231,297}}));
        sstars.add(init1ScreenStars(t,8,new int[][]{{658,364},{562,480},{388,505},{633,870},{418,947},{178,577}  ,{58,380},{153,265}}));
        sstars.add(init1ScreenStars(t,10,new int[][]{{412,352},{268,534},{526,548},{579,675},{523,742},{604,793} ,{603,860},{350,860},{348,676},{472,809}}));
        return sstars;
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * 重设整个星图
     */
    public void resetStars(){
        sbi.resetBackground();                                                                                          //重设背景


        disappearCount=0;
        currScreen = 0;
        currStars = all3ScreenStars.get(currScreen);
        for(int i=0;i<all3ScreenStars.size;i++){
            for(StarImage si:all3ScreenStars.get(i)){
                Color color = si.getColor();
                si.setColor(color.r,color.g,color.b,.0f);
                si.setIsSelected(false);
                si.resetStar();
                if(i==currScreen){
                    si.appear();
                }
            }
        }
        state = DRAWSTATE.NOMAL;                                                                                      //设置绘制状态为普通状态
    }

    @Override
    public void dispose() {
        super.dispose();
        tx_star.dispose();
        tx_starbackground.dispose();
    }
}

/**
 * 星星对象
 */
class StarImage extends Image implements Disposable {
    private TweenManager tm = new TweenManager();                                                                     //动画管理器
    private Timeline tl = null;
    private Tween tl_dis,tl_app = null;
    private Vector2 movePoint = new Vector2();                                                                       //移动的点
    private String id ="";                                                                                            //当前星星的标识
    private boolean isSelected = false;                                                                            //标识是否被选择
    private OpacityListener listener = null;                                                                        //监听消失操作是否完成


    public StarImage(Texture t) {
        super(t);
        this.setOrigin(t.getWidth() / 2, t.getHeight() / 2);
        Tween.registerAccessor(Image.class, new ActorAccessor());
        makeTween();
//        tl.start();
    }

    /**
     * 创建所有动画
     */
    private void makeTween(){
        float delay = MathUtils.random(0.5f,0.9f);
        float minval = MathUtils.random(0.2f,0.6f);

        tl = Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                                .push(Tween.to(this, ActorAccessor.OPACITY, delay).target(minval)
                                                .ease(TweenEquations.easeNone)
                                )
                                .push(Tween.to(this, ActorAccessor.SCALE_XY, delay).target(minval, minval)
                                                .ease(TweenEquations.easeNone)
                                )
                )
                .push(
                        Timeline.createParallel()
                                .push(Tween.to(this, ActorAccessor.OPACITY, delay).target(1.0f)
                                                .ease(TweenEquations.easeNone)
                                )
                                .push(Tween.to(this, ActorAccessor.SCALE_XY, delay).target(1.0f, 1.0f)
                                                .ease(TweenEquations.easeNone)
                                )
                ).repeat(-1, .0f);
        tl_dis = Tween.to(this, ActorAccessor.OPACITY, .6f).target(.0f)
                .ease(TweenEquations.easeNone).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        tl.kill();
                        tl.free();
                        tl_dis.kill();
                        tl_dis.free();
                        tl_app.kill();
                        tl_app.free();
                        if (i == TweenCallback.COMPLETE && listener != null)
                            listener.disappearCompleted(StarImage.this);
                    }
                });

        tl_app = Tween.to(this,ActorAccessor.OPACITY,.6f).target(1.0f)
                .ease(TweenEquations.easeNone).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        tl.start();
                        if (i == TweenCallback.COMPLETE && listener != null) {
                            listener.appearCompleted(StarImage.this);
                        }
                    }
                });

    }

    /**
     * 消失动作
     */
    public void disappear(){
        tl_dis.start();
    }

    /**
     * 出现动作
     */
    public void appear(){
        tl_app.start();
    }

    /**
     * 重设星星
     */
    public void resetStar(){
        isSelected = false;
        makeTween();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl!=null)
            tl.update(delta);
        if(tl_dis!=null)
            tl_dis.update(delta);
        if(tl_app!=null)
            tl_app.update(delta);
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        //当星星选中为true时发送url数据到服务器
        if(isSelected) {
            PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd="+this.getId());
            System.out.println("+++++++++++++++++" +UGameNetInstance.URL_PATH+ "/plc_send_serial?plccmd="+this.getId());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setOpacityListener(OpacityListener listener) {
            this.listener = listener;
    }

    @Override
    public void dispose() {
        tl.kill();
//        tm.killAll();
    }

    /**
     * 消失完成监听
     */
    static interface OpacityListener {
        public void disappearCompleted(StarImage si);
        public void appearCompleted(StarImage si);
    }
}

/**
 *  星盘的背景图
 */
class StarBackgroundImage extends Image implements Disposable{
    private TweenManager tm = new TweenManager();
//    private Texture sbg1 = new Texture(Gdx.files.internal("starbackground1.png"));
//    private Texture sbg2 = new Texture(Gdx.files.internal("starbackground2.png"));
    private MoveListener listener = null;
    public StarBackgroundImage(Texture t){
        super(t);
        this.setPosition(-this.getWidth() / 3 * 2, (StarScreen.screenHeight - this.getHeight()) / 2);
        Tween.registerAccessor(Image.class, new ActorAccessor());
    }

    /**
     * 向左移动一屏
     */
    public void goLeft(){
        //如果移动位置超过了自身的2/3就不能向左移动了
        if(this.getX()>-this.getWidth()/3*2)
            Tween.to(this,ActorAccessor.POS_XY,1.0f).target(this.getX() - this.getWidth() / 3, this.getY())
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            if (i == TweenCallback.COMPLETE && listener != null)
                                listener.completed(StarBackgroundImage.this);
                        }
                    }).start(tm);
    }


    /**
     * 向右移动一屏
     */
    public void goRight(){
        //如果移动位置超过了0,就不能向右移动了
        if(this.getX()<0) {
            Tween.to(this, ActorAccessor.POS_XY, 1.0f)
                    .target(this.getX() + this.getWidth() / 3, this.getY())
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            if(i==TweenCallback.COMPLETE && listener!=null)
                                listener.completed(StarBackgroundImage.this);
                        }
                    }).start(tm);
        }
    }

    /**
     * 将画面缩小,显示全图
     */
    public void goScale(){
//        this.setOrigin(this.getWidth()/2,this.getHeight()/2);
        Timeline.createParallel()
                .beginParallel()
                .push(
                    Tween.to(this, ActorAccessor.SCALE_XY, 1.0f)
                        .target(.3333f, .3333f)
                )
                .push(
                        Tween.to(this, ActorAccessor.POS_XY,1.0f)
                            .target(this.getX(),this.getY()+this.getHeight()*0.3333f)
                )
                .end()
                .start(tm);
    }

    /**
     * 重设背景缩放和位置.
     */
    public void resetBackground(){
        this.setScale(1.0f);
        this.setPosition(-this.getWidth() / 3 * 2, (StarScreen.screenHeight - this.getHeight()) / 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tm.update(delta);
    }

    public void setMoveListener(MoveListener listener) {
        this.listener = listener;
    }

    @Override
    public void dispose() {
        tm.killAll();
    }

    /**
     * 移动监听器
     */
    static interface MoveListener {
        public void completed(StarBackgroundImage sbi);
    }
}