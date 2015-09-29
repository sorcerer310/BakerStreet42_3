package com.bsu.bk42.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bsu.bk42.BakerStreet42;
import com.bsu.bk42.screen.FollowUpScreen;

public class AndroidLauncher extends AndroidApplication {
	private final String PREFERENCES_CLEAR_PASSWORD = "12345";													//系统数据重置密码

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//用来处理android.os.NetworkOnMainThreadException异常
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		if(MainTabActivity.game==null) {
			MainTabActivity.game = new BakerStreet42();
			System.out.println("======================AndroidLauncher onCreate init");
//		}
		System.out.println("======================AndroidLauncher onCreate");
		initialize(MainTabActivity.game, config);
		initResetGameDialog();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//截获back键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		if (KeyEvent.KEYCODE_HOME == keyCode)
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.action_settings){
			Toast.makeText(this, "action_setting", Toast.LENGTH_SHORT).show();
			dlg_rstgame.show();
			et.setText("");
//			((EditText)LayoutInflater.from(this).inflate(R.layout.game_reset_dialog, null).findViewById(R.id.et_password)).setText("");
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	//对话框对象
	private AlertDialog dlg_rstgame;
	private EditText et;
	/**
	 * 初始化输入密码对话框
	 */
	private void initResetGameDialog(){
		//获得密码编辑框的view
		LayoutInflater li = LayoutInflater.from(this);
		View gameResetDialogView = li.inflate(R.layout.game_reset_dialog, null);

		//获得密码编辑框
		et = (EditText) gameResetDialogView.findViewById(R.id.et_password);
		et.setText("");

		//定义输入密码的对话框
		dlg_rstgame = new AlertDialog.Builder(this)
				.setTitle("游戏重置密码")
				.setView(gameResetDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						if(et.getText().toString().equals(PREFERENCES_CLEAR_PASSWORD)){
//							Toast.makeText(AndroidLauncher.this, "重置游戏成功", Toast.LENGTH_SHORT).show();
							//密码正确先重置服务器数据
							try {
								//向服务器发送命令重置换服务器各项数据

								byte[] bytes = Utils.sendPostRequestByForm("http://192.168.1.112:8080/pgc2/plc_init_serial", "");
								String retstr = new String(bytes);
								Toast.makeText(AndroidLauncher.this, "重置服务器状态成功:"+retstr, Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								Toast.makeText(AndroidLauncher.this, "重置服务器状态失败:"+e.toString(), Toast.LENGTH_SHORT).show();
							}
							//密码正确则清除数据
							//重置客户端数据
							resetGame();
//							Toast.makeText(AndroidLauncher.this,retstr,Toast.LENGTH_LONG).show();
						}
						else
							Toast.makeText(AndroidLauncher.this, "密码错误", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}})
				.setNeutralButton("取消", null)
				.create();
	}

//	private Handler handler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			try {
//				byte[] bytes  = Utils.sendPostRequestByForm("http://192.168.1.112:8080/pgc2/plc_init_serial","");
//				String ret = new String(bytes);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};
//	private AlertDialog.Builder builder ;
//	/**
//	 * 初始化场景的一些东西
//	 */
//	private void initScreen(){
//		builder = new AlertDialog.Builder(AndroidLauncher.this);
//		MainTabActivity.game.getFollorUpScreen().setFollorUpListener(new FollowUpScreen.FollowUpListener() {
//			@Override
//			public void confirm(FollowUpScreen fus, final boolean isBigRoad) {
//				//接收到确认消息后弹出选择对话框确认是否选择该道路
//				builder.setTitle("选择道路");
//
//				if (isBigRoad)
//					builder.setMessage("确认从乌林撤退么?");
//				else
//					builder.setMessage("确认从华容道撤退么?");
//
//				builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						MainTabActivity.game.getFollorUpScreen().selectRoad(isBigRoad);
//						dialog.dismiss();
//					}
//				});
//				builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//				builder.setCancelable(false);
//				builder.create().show();
//			}
//		});
//	}

	/**
	 * 重设游戏
	 */
	private void resetGame(){
		MainTabActivity.game.getMapScreen().resetMap();
		MainTabActivity.game.getStarScreen().resetStars();
		MainTabActivity.game.getFireScreen().resetFireScreen();
		MainTabActivity.game.getFollorUpScreen().resetFollowUpScreen();
//		MainTabActivity.game.resetServer();
	}


}
