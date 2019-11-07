package com.caret.eyemanager;
import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.support.v4.app.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;

public class NightmodeService extends Service
{
	LinearLayout view;
	public WindowManager.LayoutParams params;
    public WindowManager windowManager;
    int flag;
	NotificationManager nm;
	NotificationCompat.Builder notiBuilder;
	SharedPreferences sp_filter;
	
	int a, color;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		BuildNotification();
		setWindow();
		
		return START_STICKY;
	}
	
	public void setWindow() {
		
		sp_filter = getSharedPreferences("filter", 0);
		a = sp_filter.getInt("nm_transparency", 0);

		color = Color.rgb(0,0,0);

		view = new LinearLayout(this);
		view.setBackgroundColor(color);
		view.getBackground().setAlpha(2*a);
		params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT,
			OverlayType(),
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
			| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
			PixelFormat.TRANSLUCENT );
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
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

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(windowManager == null) return;
		windowManager.removeView(view);
		windowManager = null;
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}

}
