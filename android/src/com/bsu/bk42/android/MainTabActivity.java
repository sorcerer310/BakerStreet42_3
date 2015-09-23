package com.bsu.bk42.android;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import com.badlogic.gdx.Gdx;
import com.bsu.bk42.BakerStreet42;
import com.bsu.bk42.screen.FollowUpScreen;
import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;

import java.util.List;


public class MainTabActivity extends TabActivity {
    //程序列表持久数据，防止玩家退出程序再进入获得的数据不对，如要重置需要在游戏重置功能操作
    private SharedPreferences settings;

    private TabHost m_tabHost;
    public static RadioGroup m_radioGroup;

    public static BakerStreet42 game= null;                                             //全局game对象，保证在任何activity中都可以调用到。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        init();
        initservice();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        game.resume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String urivalue = intent.getStringExtra(Constants.NOTIFICATION_URI);
        if(urivalue==null || urivalue.equals(""))
            return;

        vibrate(this,500);                                                                                              //震动500ms
        if(urivalue.contains(":")){
            String[] ss  = urivalue.split(":");
            if(ss.length<2)
                return;
            //如果发来的消息为地图
            if(ss[0].equals("map")){
                m_radioGroup.check(R.id.main_tab_map);
                //0:初始.1:星盘.2:乌龟.3:插旗.4:军令.5:守关3处完成.6:追击.7:守关4处放火.8:铁锁连环.9:船舱门关 10:草船借箭.11:擂鼓助威.
                //12:宝剑咒语箱开.13:借东风.14:放火.15:选择大路追击.16:选择华容道追击
                game.setMapCurrIndex(ss[1]);
//                System.out.println("===============================" + ss[1]);
            }
            //如果发来的消息为放火
            else if(ss[0].equals("fire")) {
                m_radioGroup.check(R.id.main_tab_fire);
                game.setFireCurrIndex(ss[1]);
            }

        }else{
            //如果发来的消息为星盘
            if(urivalue.equals("star"))
                m_radioGroup.check(R.id.main_tab_star);
            //如果发来的消息为追击
            else if(urivalue.equals("followup")) {
                m_radioGroup.check(R.id.main_tab_followup);
                game.setFollowupEnable();
            }

        }
    }
    private AlertDialog confirmDialog;
    /**
     * 初始化Tab界面
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
                        MainTabActivity.game.setScreen(BakerStreet42.FIRESCREEN);           //设置当前为放火界面
                        break;
                    case R.id.main_tab_followup:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[3]);
                        MainTabActivity.game.setScreen(BakerStreet42.FOLLOWUP);             //设置当前界面为追击界面

                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainTabActivity.this);
                        MainTabActivity.game.getFollorUpScreen().setFollorUpListener(new FollowUpScreen.FollowUpListener() {
                            @Override
                            public void confirm(FollowUpScreen fus, final boolean isBigRoad) {
                                //接收到确认消息后弹出选择对话框确认是否选择该道路
                                builder.setTitle("选择道路");

                                if (isBigRoad)
                                    builder.setMessage("确定从\"乌林\"撤退么?");
                                else
                                    builder.setMessage("确定从\"华容道\"撤退么?");

                                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainTabActivity.game.getFollorUpScreen().selectRoad(isBigRoad);
                                        dialog.dismiss();
                                        MainTabActivity.game.getFollorUpScreen().stopAllSound();                        //确认界面消失停止所有声音

                                    }
                                });
                                builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        MainTabActivity.game.getFollorUpScreen().stopAllSound();                        //确认界面消失停止所有声音
                                    }
                                });
                                builder.setCancelable(false);
                                //需要在子线程中处理窗口的创建与显示
                                new Thread(){
                                    public void run() {
                                        Looper.prepare();
                                        //显示选择窗口
                                        confirmDialog = builder.create();
                                        confirmDialog.show();
                                        Looper.loop();
                                    };
                                }.start();
                            }
                        });
                        break;
                }
            }
        });
        ((RadioButton) m_radioGroup.getChildAt(0)).toggle();
    }

    /**
     * 初始化配置参数
     */
    private void initPreferences(){
        settings = this.getSharedPreferences("StatusDatas", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        //地图的状态
        //0:初始.1:星盘.2:乌龟.3:插旗.4:军令.5:守关3处完成.6:追击.7:守关4处放火.8:铁锁连环.9:船舱门关 10:草船借箭.11:擂鼓助威.
        //12:宝剑咒语箱开.13:借东风.14:放火.15:选择大路追击.16:选择华容道追击
        editor.putInt("map", 0);

        //星盘完成状态
        editor.putBoolean("star",false);

        //放火
        //0:初始状态. 1:博望坡放火完成 2:铁锁连环放火完成
        editor.putInt("fire",0);

        //追击
        //0:初始. 1:选择大路追击 2:选择小路追击
        editor.putInt("fllowup", 0);
    }

    /**
     * 开启接收消息的服务
     */
    private void initservice(){
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
    }

    /**
     * 手机震动函数
     * @param activity
     * @param milliseconds
     */
    private void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

}