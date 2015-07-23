package com.bsu.bk42.android;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import com.bsu.bk42.BakerStreet42;
import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;


public class MainTabActivity extends TabActivity {
    //程序列表持久数据，防止玩家退出程序再进入获得的数据不对，如要重置需要在游戏重置功能操作
    private SharedPreferences settings;

    private TabHost m_tabHost;
    private RadioGroup m_radioGroup;

    public static BakerStreet42 game= null;                                             //全局game对象，保证在任何activity中都可以调用到。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        System.out.println("===================BakerStreet42 create");
        if(game==null)
            game = new BakerStreet42();
        game.setScreen(BakerStreet42.MAPSCREEN);
        init();
        initservice();
        initIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.resume();
    }

    /**
     * 鍒濆鍖栦富鐣岄潰
     */
    private void init(){
        m_tabHost = getTabHost();
        int count = Constant.mTabClassArray.length;
        for(int i=0;i<count;i++){
            TabHost.TabSpec tabSpec = m_tabHost.newTabSpec(Constant.mTextviewArray[i]).
                    setIndicator(Constant.mTextviewArray[i]).
                    setContent(Constant.getTabItemIntent(this, i));
            m_tabHost.addTab(tabSpec);
        }
        m_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
        m_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.main_tab_map:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[0]);
                        MainTabActivity.game.setScreen(BakerStreet42.MAPSCREEN);            //设置当前为地图界面
                        break;
                    case R.id.main_tab_star:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[0]);
                        MainTabActivity.game.setScreen(BakerStreet42.STARSCREEN);           //设置当前为星星界面
                        break;
                    case R.id.main_tab_fire:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[2]);
                        break;
                    case R.id.main_tab_followup:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[3]);
                        break;
                }
            }
        });
        ((RadioButton) m_radioGroup.getChildAt(0)).toggle();
    }

    /**
     * 初始化意图
     */
    private void initIntent(){
        Intent intent = this.getIntent();
        String urivalue = intent.getStringExtra(Constants.NOTIFICATION_URI);
        if(urivalue==null)
            return;
        if(urivalue.contains(":")){
            String[] ss  = urivalue.split(":");
            //如果发来的消息为地图
            if(ss[0].equals("map")){
                m_radioGroup.check(R.id.main_tab_map);
                //0:初始.1:星盘.2:乌龟.3:插旗.4:军令.5:守关3处完成.6:追击.7:守关4处放火.8:铁锁连环.9:船舱门关 10:草船借箭.11:擂鼓助威.
                //12:宝剑咒语箱开.13:借东风.14:放火.15:选择大路追击.16:选择华容道追击
                game.setMapCurrIndex(ss[1]);
//                System.out.println("===============================" + ss[1]);
            }
        }else{
            //如果发来的消息为放火
            if(urivalue.equals("fire"))
                m_radioGroup.check(R.id.main_tab_fire);
            //如果发来的消息为追击
            else if(urivalue.equals("followup"))
                m_radioGroup.check(R.id.main_tab_followup);
        }
    }

    /**
     * 初始化配置参数
     */
    private void initPreferences(){
        settings = this.getSharedPreferences("StatusDatas",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        //地图的状态
        //0:初始.1:星盘.2:乌龟.3:插旗.4:军令.5:守关3处完成.6:追击.7:守关4处放火.8:铁锁连环.9:船舱门关 10:草船借箭.11:擂鼓助威.
        //12:宝剑咒语箱开.13:借东风.14:放火.15:选择大路追击.16:选择华容道追击
        editor.putInt("map", 0);

        //星盘完成状态
        editor.putBoolean("stars",false);

        //放火
        //0:初始状态. 1:博望坡放火完成 2:铁锁连环放火完成
        editor.putInt("fire",0);

        //追击
        //0:初始. 1:选择大路追击 2:选择小路追击
        editor.putInt("fllowup",0);
    }

    /**
     * 开启接收消息的服务
     */
    private void initservice(){
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
    }
}