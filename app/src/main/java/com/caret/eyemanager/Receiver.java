package com.caret.eyemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.*;

public class Receiver extends BroadcastReceiver {
	
    public void onReceive(Context context, Intent intent) {
		
		SharedPreferences sp = context.getSharedPreferences("setting", 0);
		String action = intent.getAction();
		
		if(sp.getInt("scf_restart", 0) == 1) {
			if(action.equals("android.intent.action.BOOT_COMPLETED")) {
				intent = new Intent(context, ScreenfilterService.class);
				context.startService(intent);
			}
		}
    }
}
