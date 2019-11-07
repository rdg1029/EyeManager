package com.caret.eyemanager;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.*;
import java.util.*;
import android.app.*;
import android.support.v4.app.*;
import android.os.*;

public class NotificationService extends Service {

	SharedPreferences pref;
	int t = 30;
	NotificationManager nm;
	NotificationCompat.Builder notiBuilder;

    public void onCreate() {
        super.onCreate();
		
		pref = getSharedPreferences("setting", 0);
		
		if(pref.getInt("nt_time",0) == 1) {
			t = 15;
		}
		else if(pref.getInt("nt_time",0) == 2) {
			t = 30;
		}
		else if(pref.getInt("nt_time",0) == 3) {
			t = 60;
		}
		else if(pref.getInt("nt_time",0) == 4) {
			t = 120;
		}
		
		handler.sendEmptyMessageDelayed(0, t*60000);
    }

	public int onStartCommand(Intent intent, int flags, int startId) {

		BuildNotification();
		return START_STICKY;
	}
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			
			LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.customtoast, null);
			layout.setBackgroundColor(Color.argb(235, 92, 209, 126));
			TextView textView = (TextView)layout.findViewById(R.id.cttv);
			textView.setText(getResources().getString(R.string.noti));

			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 200);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();
			
			handler.sendEmptyMessageDelayed(0, t*60000);
		}
	};

	public void BuildNotification() {

		Intent i = new Intent(this, MainActivity.class);
		PendingIntent p = PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			NotificationChannel notificationChannel = new NotificationChannel("EyeManagerPush", "EyeManagerPush", NotificationManager.IMPORTANCE_DEFAULT);
			nm.createNotificationChannel(notificationChannel);
			notiBuilder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
		}
		else {
			notiBuilder = new NotificationCompat.Builder(getApplicationContext());
		}
		notiBuilder.setContentTitle("EyeManager 서비스")
				.setContentText("터치하면 앱을 실행합니다")
				.setSmallIcon(R.drawable.scf_ic)
				.setOngoing(true)
				.setPriority(Notification.PRIORITY_MIN)
				.setContentIntent(p).build();
		startForeground(1, notiBuilder.build());

	}

    @Override
    public void onDestroy() {
        super.onDestroy();
		if(handler!=null) {
			handler.removeMessages(0);
			
		}
    }
    @Override
    public IBinder onBind (Intent arg0) {
        return null;
    }
}

