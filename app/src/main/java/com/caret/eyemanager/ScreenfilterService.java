package com.caret.eyemanager;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.*;
import android.widget.*;

public class ScreenfilterService extends Service {

	LinearLayout view;
	//public View screenFilter;
    public WindowManager.LayoutParams params;
    public WindowManager windowManager;
    int flag;
	NotificationManager nm;
	NotificationCompat.Builder notiBuilder;
	
	//Handler handler;
	//Thread thread;
	SharedPreferences sp, sp_filter;
	int a;
	int r = 172;
    int g = 121;
    int b = 24;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		sp_filter = getSharedPreferences("filter", 0);
		windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
		ColorPreferences();
		setScreenFilter();
		/*
		params =  new WindowManager.LayoutParams(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
			| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
			PixelFormat.TRANSLUCENT);
		
		windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
		screenFilter = new ScreenFilter(this);
		windowManager.addView(screenFilter, params);
		*/
		//UpdateWindow();
		
        return START_STICKY;
	}

    @Override
    public void onCreate() {
        super.onCreate();
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		BuildNotification();
		//setWindow();
	}

	public void ColorPreferences() {
		a = sp_filter.getInt("scf_transparency", 0);
		if(sp_filter.getInt("color", 0) == 1) {
			r = 255;
			g = 36;
			b = 0;
		}
		if(sp_filter.getInt("color", 0) == 2) {
			r = 255;
			g = 146;
			b = 0;
		}
		if(sp_filter.getInt("color", 0) == 3) {
			r = 172;
			g = 121;
			b = 0;
		}
	}

	public void setScreenFilter() {

		view = new LinearLayout(this);
		view.setBackgroundColor(Color.rgb(r,g,b));
		view.getBackground().setAlpha(2*a);
		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				OverlayType(),
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
						| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT );
		windowManager.addView(view, params);

	}

	int OverlayType() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			flag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		}
		else
		{
			flag = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		}
		return flag;
	}

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
	/*
	public void UpdateWindow() {
		
		if(a != 0) {
			try {
				windowManager.updateViewLayout(screenFilter, params);
			}
			catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
			}
		}
		
		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				windowManager.updateViewLayout(screenFilter, params);

				handler.sendEmptyMessageDelayed(0, 100);
			}
		};
		
	}
	*/
	
    @Override
    public IBinder onBind (Intent arg0){
        return null;
    }
    public void onDestroy() {

        super.onDestroy();
		if(windowManager == null) return;
		windowManager.removeView(view);
		windowManager = null;
    }
}
