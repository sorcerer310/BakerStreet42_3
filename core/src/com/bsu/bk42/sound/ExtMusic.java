package com.bsu.bk42.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Timer;

/**
 * 扩展的Sound类
 * Created by fc on 2015/9/22.
 */
public class ExtMusic {
//    private Sound s;                                                                                                   //声音对象
    private Music s;
    private long slength;                                                                                            //声音长度
    private ExtMusicListener listener;
    private boolean isPlay = false;                                                                                 //是否正在播放
    public ExtMusic(String spath, long time){
//        s = Gdx.audio.newSound(Gdx.files.internal(spath));
        s = Gdx.audio.newMusic(Gdx.files.internal(spath));
        slength = time;
    }

    /**
     * 播放声音,并在播放完执行事件
     * @param l ExtSound的监听器
     */
    public void play(ExtMusicListener l){
        listener = l;
        s.play();
        isPlay = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isPlay = false;
                if(listener!=null)
                    listener.playend(ExtMusic.this);
            }
        }, slength);
    }

    public void stop(){
        s.stop();
        isPlay = false;
    }

    public Music getS() {
        return s;
    }

    public long getSlength() {
        return slength;
    }

    public boolean isPlay() {
        return isPlay;
    }

    /**
     * 设置监听器
     * @param listener
     */
    public void setExtSoundListener(ExtMusicListener listener) {
        this.listener = listener;
    }

    /**
     * 扩展声音类的监听器
     */
    public static interface ExtMusicListener {
        void playend(ExtMusic s);
    }
}
