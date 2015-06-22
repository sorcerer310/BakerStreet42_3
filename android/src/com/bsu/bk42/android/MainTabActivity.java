package com.bsu.bk42.android;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import com.bsu.bk42.BakerStreet42;


public class MainTabActivity extends TabActivity {
    private TabHost m_tabHost;
    private RadioGroup m_radioGroup;

    public static BakerStreet42 game= null;                                             //全局game对象，保证在任何activity中都可以调用到。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        game = new BakerStreet42();
        init();
    }

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
                    case R.id.main_tab_weixin:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[0]);
                        MainTabActivity.game.setScreen(BakerStreet42.MAPSCREEN);            //设置当前为地图界面
                        break;
                    case R.id.main_tab_address:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[0]);
                        MainTabActivity.game.setScreen(BakerStreet42.STARSCREEN);           //设置当前为星星界面
                        break;
                    case R.id.main_tab_find_friend:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[2]);
                        break;
                    case R.id.main_tab_settings:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[3]);
                        break;
                }
            }
        });
        ((RadioButton) m_radioGroup.getChildAt(0)).toggle();
    }
}
