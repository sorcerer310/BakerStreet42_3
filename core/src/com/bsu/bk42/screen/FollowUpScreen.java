package com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.ugame.gdx.tools.UGameScreen;

/**
 * Created by fengchong on 2015/8/22.
 */
public class FollowUpScreen extends UGameScreen {
    private Texture t_road1 = null;                                                                                 //华容道图片
    private Texture t_road2 = null;                                                                                 //大路图片
    private RoadButton rbutton1,rbutton2;
    public FollowUpScreen(){
        t_road1 = new Texture(Gdx.files.internal("followup/road1.png"));
        t_road2 = new Texture(Gdx.files.internal("followup/road2.png"));
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
    public RoadButton(Texture t){
        texture = t;
        pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(.0f,.0f,.0f,.3f);
        t_cover = new Texture(pixmap);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        //绘制遮罩层
        if (texture != null)
            batch.draw(t_cover, .0f, .0f, (float) texture.getWidth(), (float) texture.getHeight());
    }


    @Override
    public void dispose() {
        if(pixmap!=null)
            pixmap.dispose();
    }
}
