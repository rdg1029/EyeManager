package com.caret.eyemanager;

import android.app.*;
import android.graphics.Color;
import android.os.*;
import android.widget.*;
import android.widget.RadioGroup.*;
import android.content.*;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.view.*;

public class setting extends Activity {
	
	CheckBox scf_restart;
	RadioButton nt_15, nt_30, nt_1h, nt_2h;
	
	private AdView adView;
    private AdRequest adRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
		
		adView = (AdView)findViewById(R.id.ad);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        statusBarColor();
		
		SharedPreferences pref = getSharedPreferences("setting", 0);
		final SharedPreferences.Editor editor = pref.edit();
		
		scf_restart = (CheckBox)findViewById(R.id.setting_scf_restart);
		Boolean scfrestart = pref.getBoolean("cb_scf_restart", true);
		scf_restart.setChecked(scfrestart);
		scf_restart.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					if(((CheckBox)v).isChecked()) {
						editor.remove("scf_restart");
						editor.commit();
						editor.putInt("scf_restart", 1);
						editor.commit();
					}
					else {
						editor.remove("scf_restart");
						editor.commit();
						editor.putInt("scf_restart", 2);
						editor.commit();
					}
				}
				
		});
		
		nt_15 = (RadioButton)findViewById(R.id.setting_nt_15);
		Boolean nt15 = pref.getBoolean("nt_15", false);
		nt_15.setChecked(nt15);
		nt_30 = (RadioButton)findViewById(R.id.setting_nt_30);
		Boolean nt30 = pref.getBoolean("nt_30", true);
		nt_30.setChecked(nt30);
		nt_1h= (RadioButton)findViewById(R.id.setting_nt_1h);
		Boolean nt1h = pref.getBoolean("nt_1h", false);
		nt_1h.setChecked(nt1h);
		nt_2h = (RadioButton)findViewById(R.id.setting_nt_2h);
		Boolean nt2h = pref.getBoolean("nt_2h", false);
		nt_2h.setChecked(nt2h);
		
		RadioGroup nt_time = (RadioGroup)findViewById(R.id.setting_nt_time);
		nt_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup p1, int p2)
				{
					switch(p2) {
						case R.id.setting_nt_15 :
							editor.remove("nt_time");
							editor.commit();
							editor.putInt("nt_time", 1);
							editor.commit();

							break;

						case R.id.setting_nt_30 :
							editor.remove("nt_time");
							editor.commit();
							editor.putInt("nt_time", 2);
							editor.commit();

							break;
						
						case R.id.setting_nt_1h :
							editor.remove("nt_time");
							editor.commit();
							editor.putInt("nt_time", 3);
							editor.commit();
							
							break;
							
						case R.id.setting_nt_2h :
							editor.remove("nt_time");
							editor.commit();
							editor.putInt("nt_time", 4);
							editor.commit();
							
							break;
					}
				}
			});
    }

	public void statusBarColor()
	{
		View view = getWindow().getDecorView();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (view != null) {
				// 23 버전 이상일 때
				view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
			}
		}else if (Build.VERSION.SDK_INT >= 21) {
			// 21 버전 이상일 때
			getWindow().setStatusBarColor(Color.BLACK);
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();
		SharedPreferences pref = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = pref.edit();
		scf_restart = (CheckBox)findViewById(R.id.setting_scf_restart);
		nt_15 = (RadioButton)findViewById(R.id.setting_nt_15);
		nt_30 = (RadioButton)findViewById(R.id.setting_nt_30);
		nt_1h= (RadioButton)findViewById(R.id.setting_nt_1h);
		nt_2h = (RadioButton)findViewById(R.id.setting_nt_2h);
		editor.putBoolean("cb_scf_restart", scf_restart.isChecked());
		editor.putBoolean("nt_15", nt_15.isChecked());
		editor.putBoolean("nt_30", nt_30.isChecked());
		editor.putBoolean("nt_1h", nt_1h.isChecked());
		editor.putBoolean("nt_2h", nt_2h.isChecked());
		editor.commit();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		adView.setAdListener(null);
        adView.removeView(adView);
        adView.destroy();
	}
	
	
}
