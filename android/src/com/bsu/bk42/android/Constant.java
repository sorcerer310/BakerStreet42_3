package com.bsu.bk42.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Constant {

	public static String mTextviewArray[] = {"AndroidLauncher", "AndroidLauncher", "Activity3", "Activity4"};
	
	public static Class mTabClassArray[]= {AndroidLauncher.class,
		AndroidLauncher.class,
		Activity3.class,
		Activity4.class};

	/**
	 * 切换Activity时附加的意图数据
	 * @param activity	源activity
	 * @param index		标识
	 * @return				返回携带数据的意图对象
	 */
	public static Intent getTabItemIntent(Activity activity,int index){
		Intent intent = new Intent(activity,mTabClassArray[index]);
		return intent;
	}
}
