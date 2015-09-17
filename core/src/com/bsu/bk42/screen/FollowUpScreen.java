package com.bsu.bk42.screen;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.PlcCommHelper;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tools.WidgetFactory;
import com.ugame.gdx.tween.accessor.ActorAccessor;

/**
 * Created by fengchong on 2015/8/22.
 */
public class FollowUpScreen extends UGameScreen {
    public static float screenWidth,screenHeight,scaleWidth,scaleHeight;
    private Texture t_road1 = null;                                                                                  //华容道图片
    private Texture t_road2 = null;                                                                                  //大路图片
    private RoadButton rbutton1,rbutton2;
    private Group gbutton = new Group();                                                                             //设置按钮组

    private Texture t_guanyu = null;                                                                                 //关羽图片纹理资源

    private QuestionGroup gguanyu = null;                                                                            //问答题组

    private Texture t_success,t_failed = null;                                                                     //成功\失败的纹理
    private StampImage img_success,img_failed = null;                                                                   //成功失败的图

    private StateMachine stateMachine;                                                                               //状态机对象,用来控制界面状态.
    enum FollowUpScreenState implements State<FollowUpScreen> {
        STATE_NO_ENABLE(){
            @Override
            public void enter(FollowUpScreen entity) {
                entity.stage.getActors().clear();                                                                      //清除stage的内容
                entity.stage.addActor(entity.gbutton);                                                                //将道路选择的内容增加进去
                entity.rbutton1.setEnable(false);                                                                     //进入该状态后设置按钮不可用
                entity.rbutton2.setEnable(false);
                entity.gguanyu.resetQuestionGroup();
                entity.img_success.resetStampImage();
                entity.img_failed.resetStampImage();
                entity.rbutton1.setB_cover(false);
                entity.rbutton2.setB_cover(false);
            }
            @Override
            public void update(FollowUpScreen entity) {}
            @Override
            public void exit(FollowUpScreen entity) {}
            @Override
            public boolean onMessage(FollowUpScreen entity, Telegram telegram) {return false;}
        },
        STATE_NOMAL(){
            @Override
            public void enter(FollowUpScreen entity) {
                entity.rbutton1.setEnable(true);                                                                      //进入该状态设置按钮可用
                entity.rbutton2.setEnable(true);
            }
            @Override
            public void update(FollowUpScreen entity) {}
            @Override
            public void exit(FollowUpScreen entity) {}
            @Override
            public boolean onMessage(FollowUpScreen entity, Telegram telegram) {return false;}
        },
        STATE_QUESTION(){
            @Override
            public void enter(FollowUpScreen entity) {
                entity.stage.getActors().clear();                                                                      //清除stage的内容
                entity.stage.addActor(entity.gguanyu);                                                                //将道路选择的内容增加进去
            }
            @Override
            public void update(FollowUpScreen entity) {}
            @Override
            public void exit(FollowUpScreen entity) {}
            @Override
            public boolean onMessage(FollowUpScreen entity, Telegram telegram) {
                return false;}
        },
        STATE_END(){
            @Override
            public void enter(FollowUpScreen entity) {
                entity.gguanyu.setEnable(false);
            }
            @Override
            public void update(FollowUpScreen entity) {}
            @Override
            public void exit(FollowUpScreen entity) {}
            @Override
            public boolean onMessage(FollowUpScreen entity, Telegram telegram) {return false;}
        }
    }

    /**
     * 重设追击界面
     */
    public void resetFollowUpScreen(){
        stateMachine.changeState(FollowUpScreenState.STATE_NO_ENABLE);
    }

    public FollowUpScreen(){
        //视口初始化
        screenWidth = 720.0f;                                                                                           //设置游戏界面的宽高
        screenHeight = 1280.0f;
        stage = new Stage(new StretchViewport(screenWidth, screenHeight));
        scaleWidth = Gdx.graphics.getWidth()/screenWidth;                                                               //获得游戏界面与设备间的比例
        scaleHeight = Gdx.graphics.getHeight()/screenHeight;

        initRoadButton();
        initQuestion();
        initResultImage();
        //初始化状态机部分
        stateMachine = new DefaultStateMachine<FollowUpScreen>(this,FollowUpScreenState.STATE_NO_ENABLE);


    }

    /**
     * 初始化选择道路的按钮
     */
    private void initRoadButton(){
        t_road1 = new Texture(Gdx.files.internal("followup/road1.jpg"));
        t_road2 = new Texture(Gdx.files.internal("followup/road2.jpg"));

        rbutton1 = new RoadButton(t_road1);
        rbutton2 = new RoadButton(t_road2);

        rbutton1.setPosition(.0f, rbutton2.getHeight());

        //点击华容道按钮
        rbutton1.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(((RoadButton)event.getTarget()).isEnable()) {

                    if (stateMachine.isInState(FollowUpScreenState.STATE_NOMAL)) {
                        PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=HUARONG");
                        stateMachine.changeState(FollowUpScreenState.STATE_QUESTION);
                        rbutton1.setB_cover(true);
                    }
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        //点击大路按钮
        rbutton2.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (((RoadButton) event.getTarget()).isEnable()) {
                    if (stateMachine.isInState(FollowUpScreenState.STATE_NOMAL)) {
//                        ((RoadButton) (event.getTarget())).setB_cover(true);
                        stateMachine.changeState(FollowUpScreenState.STATE_END);
                        rbutton2.setB_cover(true);
                        PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=BIGROAD");
                    }
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        gbutton.addActor(rbutton1);
        gbutton.addActor(rbutton2);
        stage.addActor(gbutton);
    }

    /**
     * 初始化问题部分.
     */
    private void initQuestion() {
        t_guanyu = new Texture(Gdx.files.internal("followup/bg_guanyu.jpg"));
        gguanyu = new QuestionGroup(t_guanyu);
        gguanyu.setQuestionListener(new QuestionGroup.QuestionListener() {
            @Override
            public void success() {
//                System.out.println("success");
                img_success.drop(100, 100);
                stateMachine.changeState(FollowUpScreenState.STATE_END);
            }

            @Override
            public void failed() {
                img_failed.drop(100, 100);
                stateMachine.changeState(FollowUpScreenState.STATE_END);
            }
        });
    }

    /**
     * 初始化结果图
     */
    private void initResultImage(){
        t_success = new Texture(Gdx.files.internal("followup/vic.png"));
        t_failed = new Texture(Gdx.files.internal("followup/defeat.png"));
        img_success = new StampImage(t_success);
        img_failed = new StampImage(t_failed);


        img_success.setStampCompleteListener(new StampImage.StampComplete() {
            @Override
            public void stampComplete() {
//                shake();
                PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=HUARONGSUCCESS");
            }
        });
        img_failed.setStampCompleteListener(new StampImage.StampComplete() {
            @Override
            public void stampComplete() {
//                shake();
                PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=HUARONGFAILED");
            }
        });

        gguanyu.addActor(img_success);
        gguanyu.addActor(img_failed);

        tl_shake = Timeline.createSequence()
                .push(Tween.to((Actor)gguanyu, ActorAccessor.POS_XY, .0f).target(MathUtils.random(-5.0f, 5.0f) + img_failed.getX(), MathUtils.random(-5.0f, 5.0f) + img_failed.getY()))
                .pushPause(.1f)
                .push(Tween.to((Actor)gguanyu, ActorAccessor.POS_XY, .0f).target(MathUtils.random(-5.0f, 5.0f) + img_failed.getX(), MathUtils.random(-5.0f, 5.0f) + img_failed.getY()))
                .pushPause(.1f)
                .push(Tween.to((Actor)gguanyu, ActorAccessor.POS_XY, .0f).target(MathUtils.random(-5.0f, 5.0f) + img_failed.getX(), MathUtils.random(-5.0f, 5.0f) + img_failed.getY()))
                .pushPause(.1f)
                .push(Tween.to((Actor)gguanyu, ActorAccessor.POS_XY, .0f).target(MathUtils.random(-5.0f, 5.0f) + img_failed.getX(), MathUtils.random(-5.0f, 5.0f) + img_failed.getY()))
                .pushPause(.1f)
                .push(Tween.to((Actor)gguanyu, ActorAccessor.POS_XY, .0f).target(MathUtils.random(-5.0f, 5.0f) + img_failed.getX(), MathUtils.random(-5.0f, 5.0f) + img_failed.getY()))
                .pushPause(.1f)
                .push(Tween.to((Actor)gguanyu, ActorAccessor.POS_XY, .0f).target(img_failed.getX(), img_failed.getY()));



    }

    private Timeline tl_shake;
    /**
     * 摇晃屏幕
     */
    private void shake(){
        tl_shake.start();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stateMachine.update();
        if(tl_shake!=null)
            tl_shake.update(delta);
    }

    public void setFollowUpEnable(){
        stateMachine.changeState(FollowUpScreenState.STATE_NOMAL);
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
        t_guanyu.dispose();
        if(tl_shake!=null) {
            tl_shake.kill();
            tl_shake.free();
        }
    }
}

/**
 * 选择道路的大按钮
 */
class RoadButton extends Image implements Disposable {
    private boolean enable = true;                                                                                  //该按钮是否可用
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
    public void setEnable(boolean enable) {this.enable = enable;}
    public boolean isEnable() {return enable;}

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

/**
 * 问题组
 */
class QuestionGroup extends Group implements Disposable {
    private Texture texture = null;
    private Image bg_guanyu = null;                                                                                  //关羽背景图片
    private int currQuestionIndex = 0;                                                                              //当前问题的序号
    private Array<Question> questions = new Array<Question>();                                                       //所有问题
    private BitmapFont bf = null;                                                                                     //问题的文字
    private TextButton tb1,tb2;
    private Table table;
    private Label l;
    private QuestionListener listener = null;
    private boolean enable = true;

    public QuestionGroup(Texture t){
//        this.setSize(720,1024);
        texture = t;
        bf = new BitmapFont(Gdx.files.internal("followup/question.fnt"),Gdx.files.internal("followup/question.png"),false);
        bg_guanyu = new Image(texture);
        Color c_gy = bg_guanyu.getColor();
        bg_guanyu.setColor(c_gy.r, c_gy.g, c_gy.b, .7f);
        this.addActor(bg_guanyu);                                                                                     //增加背景


        questions.add(new Question("丞相！前方有关羽率军堵截，我军该如何应对？", 1,
                new String[]{"A.曹操:自此绝境，只好以死相拼。全军出击！", "B.程昱:关云长素以忠义著称，昔日丞相曾有恩于他，不妨上前哀告，或可脱此危难"}));                                               //增加答案
        questions.add(new Question("关羽奉军师将令,在此等候丞相多时.", 1,
                new String[]{"A.孤今日虽遭此大败，乃时运不济。纵然死于此处，实在难以心服。", "B.今日兵败于此，望将军以昔日情谊为重…"}));                                                 //增加答案
        questions.add(new Question("昔日，丞相却是待我不薄，然而我斩颜良诛文丑已报过丞相之恩，今日岂可以私废公？", 0,
                new String[]{"A.云长过五关斩将之时，孤并不曾派兵追赶，反而传令与将军放行，大丈夫应以信义为重，将军忍心杀害故交吗？", "B.倘若孤今日丧命于此，东吴岂会容汝等全身而退，以令兄玄德之军力，必为周瑜所图，届时，天下危矣，汉室危矣…"}));                                                 //增加答案
        questions.add(new Question("", 1, new String[]{"A.曹操缓步率军通过华容道.", "B.趁关羽犹豫，进一步晓之以理动之以情"}));                                                 //增加答案

        makeQuestion();
        this.removeActor(table);
        table = setQuestion(questions.get(currQuestionIndex));
        this.addActor(table);
    }

    public void makeQuestion(){
        l = WidgetFactory.makeLabel(bf, "");
        l.setWrap(true);
        l.setWidth(700);

        tb1 = WidgetFactory.makeTextButton("", bf);
        tb1.getLabel().setWrap(true);
        tb1.getLabel().setWidth(600);
        tb1.getLabel().setAlignment(Align.left);
        tb1.addCaptureListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!enable)
                    return false;
                tb1.getLabel().setFontScale(.9f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                tb1.getLabel().setFontScale(1.0f);
                judgeAnswer(0);
            }
        });

        tb2 = WidgetFactory.makeTextButton("",bf);
        tb2.getLabel().setWrap(true);
        tb2.getLabel().setWidth(600);
        tb2.getLabel().setAlignment(Align.left);
        tb2.addCaptureListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!enable)
                    return false;
                tb2.getLabel().setFontScale(.9f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                tb2.getLabel().setFontScale(1.0f);
                judgeAnswer(1);
            }
        });
    }

    /**
     * 重设问题组
     */
    public void resetQuestionGroup(){
        currQuestionIndex = 0;
        enable = true;
        this.removeActor(table);
        table = setQuestion(questions.get(0));
        this.addActor(table);
    }

    /**
     * 判断答案是否正确
     * @param correctIndex  正确答案的序号
     */
    private void judgeAnswer(int correctIndex){
        if(questions.get(currQuestionIndex).correctIndex==correctIndex) {
            if(currQuestionIndex==3){
                //胜利操作
                if(listener!=null)
                    listener.success();
                return;
            }

            currQuestionIndex++;
            QuestionGroup.this.removeActor(table);
            table = QuestionGroup.this.setQuestion(questions.get(currQuestionIndex));
            QuestionGroup.this.addActor(table);
        }
        else{
            //失败操作
            if(listener!=null)
                listener.failed();
        }
    }

    /**
     * 设置问题
     * @param q     问题对象,从中取出问题与答案的文字
     */
    private Table setQuestion(Question q) {
        l.setText(q.question);
        tb1.setText(q.answer.get(0));
        tb2.setText(q.answer.get(1));

        Table root = new Table();
        root.setSize(720, 1280);

        root.add(l).width(650).row();
        root.add(tb1).width(600).height(tb1.getLabel().getPrefHeight()).spaceBottom(50).spaceTop(80).row();
        root.add(tb2).width(600).height(tb2.getLabel().getPrefHeight());
        root.center();

        return root;
    }

    /**
     * 设置监听器,监听成功或者失败
     * @param listener  监听器对象
     */
    public void setQuestionListener(QuestionListener listener) {
        this.listener = listener;
    }

    /**
     * 设置是否可用
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void dispose() {
        if(bf!=null)
            bf.dispose();
    }

    /**
     * 用于存储问题及答案
     */
    class Question{
        public String question = "";                                                                                  //问题
        public Array<String> answer = new Array<String>();                                                            //答案
        public int correctIndex = 0;                                                                                //正确答案的序号
        public Question(String pq,int pci,String[] as){
            question = pq;                                                                                             //问题
            correctIndex = pci;                                                                                       //正确答案序号
            for(String s:as)
                answer.add(s);                                                                                         //所有的答案
        }
    }

    /**
     * 监听操作成功或者失败
     */
    static interface QuestionListener{
        void success();
        void failed();
    }
}

/**
 * 图章类
 */
class StampImage extends Image implements Disposable{
    private Timeline tl = null;
    private StampComplete listener = null;
    public StampImage(Texture t){
        super(t);
        Color color = this.getColor();
        this.setColor(color.r,color.g,color.b,.0f);
    }
    public void drop(int x,int y){
        this.setPosition(x, y);
        Color color = this.getColor();
        this.setColor(color.r, color.g, color.b, .0f);
        this.setScale(1.0f);
//        tl.start();
        tl = Timeline.createParallel()
                .push(
                        Tween.to(this, ActorAccessor.OPACITY, 1.0f).target(1.0f)
                ).push(
                        Timeline.createSequence()
                                .push(
                                        Tween.to(this, ActorAccessor.SCALE_XY, 1.5f).target(.7f, .7f).ease(TweenEquations.easeInOutExpo)
                                ).push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                if (listener != null)
                                    listener.stampComplete();
                            }
                        }))
                ).start();
    }

    public void resetStampImage(){
        Color color = this.getColor();
        this.setColor(color.r,color.g,color.b,.0f);
        this.setScale(1.0f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl!=null)
            tl.update(delta);
    }


    @Override
    public void dispose() {
        if(tl!=null) {
            tl.kill();
            tl.free();
        }
    }

    /**
     * 设置监听器
     * @param listener
     */
    public void setStampCompleteListener(StampComplete listener) {
        this.listener = listener;
    }

    static interface StampComplete{
        void stampComplete();
    }
}