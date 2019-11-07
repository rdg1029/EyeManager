package com.caret.eyemanager;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import android.widget.SeekBar.*;
import com.fsn.cauly.*;
import com.google.android.gms.ads.*;

import android.app.AlertDialog;

public class MainActivity extends Activity implements CaulyCloseAdListener {

    ToggleButton toggleButton_NTS, toggleButton_SCFS, ToggleButton_NMS;
    SharedPreferences sp, sp_filter;
    String scf_AlphaToString, nm_AlphaToString, scf_progress, nm_progress;
    SeekBar scf_transparency, nm_transparency;

    private AdView adView;
    private AdRequest adRequest;

    private static final String APP_CODE = "GRI6CDIZ";
    CaulyCloseAd CloseAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(i);
            finish();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_msg), Toast.LENGTH_LONG).show();
        }

        sp = getSharedPreferences("setting", 0);

        CaulyAdInfo CloseAdInfo = new CaulyAdInfoBuilder(APP_CODE).build();
        CloseAd = new CaulyCloseAd();
        CloseAd.setAdInfo(CloseAdInfo);
        CloseAd.setCloseAdListener(this);

        adView = (AdView)findViewById(R.id.ad);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        toggleButton_NTS = (ToggleButton)findViewById(R.id.tb_notificationservice);
        toggleButton_SCFS = (ToggleButton)findViewById(R.id.tb_screenfilterservice);
        ToggleButton_NMS = (ToggleButton)findViewById(R.id.tb_nightmode);

        statusBarColor();
        FirstDialog();

        // Service Running State Check
        final ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if ("com.caret.eyemanager.NotificationService".equals(service.service.getClassName())) {
                toggleButton_NTS.setChecked(true);
                toggleButton_NTS.setText("on");
                toggleButton_NTS.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
            }

            if ("com.caret.eyemanager.ScreenfilterService".equals(service.service.getClassName())) {
                toggleButton_SCFS.setChecked(true);
                toggleButton_SCFS.setText("on");
                toggleButton_SCFS.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
            }

            if ("com.caret.eyemanager.NightmodeService".equals(service.service.getClassName())) {
                ToggleButton_NMS.setChecked(true);
                ToggleButton_NMS.setText("on");
                ToggleButton_NMS.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
            }
        }

        sp_filter = getSharedPreferences("filter", 0);
        final SharedPreferences.Editor edit = sp_filter.edit();

        final Intent scfService = new Intent(this,ScreenfilterService.class);
        final TextView scf_val_transparency = (TextView)findViewById(R.id.scf_val_transparency);
        scf_progress = String.valueOf(sp_filter.getInt("scf_transparency", 0));
        scf_val_transparency.setText(scf_progress);
        scf_transparency = (SeekBar)findViewById(R.id.scf_transparency); // ScreenFilter transparency seekbar
        scf_transparency.setProgress(sp_filter.getInt("scf_transparency", 0));
        scf_transparency.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar p1, int a, boolean p3)
            {
                scf_AlphaToString = String.valueOf(a);
                scf_val_transparency.setText(scf_AlphaToString);
                scf_transparency.setProgress(sp_filter.getInt("scf_transparency", 0));
                edit.putInt("scf_transparency", a);
                edit.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1)
            {
                // TODO: Implement this method
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1)
            {
                if(toggleButton_SCFS.isChecked() == false) return;
                stopService(scfService);
                startService(scfService);
                toggleButton_SCFS.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
            }
        });

        final Intent nmService = new Intent(this,NightmodeService.class);
        final TextView nm_val_transparency = (TextView)findViewById(R.id.nm_val_transparency);
        nm_progress = String.valueOf(sp_filter.getInt("nm_transparency", 0));
        nm_val_transparency.setText(nm_progress);
        nm_transparency = (SeekBar)findViewById(R.id.nm_transparency); //Nightmode transparency seekbar
        nm_transparency.setProgress(sp_filter.getInt("nm_transparency", 0));
        nm_transparency.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar p1, int a, boolean p3)
            {
                nm_AlphaToString = String.valueOf(a);
                nm_val_transparency.setText(nm_AlphaToString);
                nm_transparency.setProgress(sp_filter.getInt("nm_transparency", 0));
                edit.putInt("nm_transparency", a);
                edit.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1)
            {
                // TODO: Implement this method
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1)
            {
                if(ToggleButton_NMS.isChecked() == false) return;
                stopService(nmService);
                startService(nmService);
                ToggleButton_NMS.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
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

    public void FirstDialog()
    {
        try{
            SharedPreferences mPref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);

            Boolean bFirst = mPref.getBoolean("isFirst", false);
            if(bFirst == false)
            {
                SharedPreferences.Editor editor = mPref.edit();
                editor.putBoolean("isFirst", true);
                editor.commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.help_title)).setMessage(getResources().getString(R.string.help_message))
                        .setPositiveButton(getResources().getString(R.string.help_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }
        catch(Exception e) {}
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(CloseAd != null) {
            CloseAd.resume(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(CloseAd.isModuleLoaded()) {
                CloseAd.show(this);
            }
            else {
                showDefaultClosePopup();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showDefaultClosePopup()
    {
        new AlertDialog.Builder(this).setTitle("").setMessage("종료 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니요",null)
                .show();

    }

    // CaulyCloseAdListener
    @Override
    public void onFailedToReceiveCloseAd(CaulyCloseAd ad, int errCode,String errMsg) {
    }
    // CloseAd의 광고를 클릭하여 앱을 벗어났을 경우 호출되는 함수이다.
    @Override
    public void onLeaveCloseAd(CaulyCloseAd ad) {
    }
    // CloseAd의 request()를 호출했을 때, 광고의 여부를 알려주는 함수이다.
    @Override
    public void onReceiveCloseAd(CaulyCloseAd ad, boolean isChargable) {

    }
    //왼쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    @Override
    public void onLeftClicked(CaulyCloseAd ad) {

    }
    //오른쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    //Default로는 오른쪽 버튼이 종료로 설정되어있다.
    @Override
    public void onRightClicked(CaulyCloseAd ad) {
        finish();
    }
    @Override
    public void onShowedCloseAd(CaulyCloseAd ad, boolean isChargable) {
    }

    public void onStart() {
        super.onStart();
        adView = (AdView)findViewById(R.id.ad);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void onDestroy() {
        super.onDestroy();

        adView.setAdListener(null);
        adView.removeView(adView);
        adView.destroy();

    }

    public void nts (View v) {

        ToggleButton toggleButton = (ToggleButton) v;
        final Intent service = new Intent(this, NotificationService.class);

        if(toggleButton.isChecked()) {
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
            toggleButton.setText("on");
            stopService(service);
            startService(service);
        }
        else {
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_off));
            toggleButton.setText("off");
            stopService(service);

        }
    }

    public void scfs (View v) {
        ToggleButton toggleButton = (ToggleButton) v;
        final Intent service = new Intent(this,ScreenfilterService.class);

        if(toggleButton.isChecked()) {
            startService(service);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
            toggleButton.setText("on");
        }
        else {
            stopService(service);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_off));
            toggleButton.setText("off");
        }
    }

    public void nms (View v) {
        ToggleButton toggleButton = (ToggleButton) v;
        final Intent service = new Intent(this, NightmodeService.class);

        if(toggleButton.isChecked()) {
            startService(service);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));
            toggleButton.setText("on");
        }
        else {
            stopService(service);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_off));
            toggleButton.setText("off");

        }
    }

    public void colorselect1(View v) {
        SharedPreferences.Editor editor = sp_filter.edit();
        editor.remove("color");
        editor.commit();
        editor.putInt("color", 1);
        editor.commit();
        AfterSelectColor();
    }

    public void colorselect2(View v) {
        SharedPreferences.Editor editor = sp_filter.edit();
        editor.remove("color");
        editor.commit();
        editor.putInt("color", 2);
        editor.commit();
        AfterSelectColor();
    }

    public void colorselect3(View v) {
        SharedPreferences.Editor editor = sp_filter.edit();
        editor.remove("color");
        editor.commit();
        editor.putInt("color", 3);
        editor.commit();
        AfterSelectColor();
    }

    public void AfterSelectColor() {
        toggleButton_SCFS.setText("on");
        toggleButton_SCFS.setChecked(true);

        Intent scfservice = new Intent(this, ScreenfilterService.class);
        stopService(scfservice);
        startService(scfservice);
        toggleButton_SCFS.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toggle_on));

    }

    public void setting(View v) {
        Intent intent = new Intent(this,setting.class);
        startActivity(intent);
    }

    public void help(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.help_title)).setMessage(getResources().getString(R.string.help_message))
                .setPositiveButton(getResources().getString(R.string.help_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
