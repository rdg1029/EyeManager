package com.caret.eyemanager;
import android.content.*;
import android.graphics.*;
import android.view.*;

public class ScreenFilter extends View
{
	SharedPreferences sp, sp_filter;
	int a;
	int r = 172;
    int g = 121;
    int b = 24;
	
	public ScreenFilter(Context context)
	{
		super(context);
		SharedPreferences sp = context.getSharedPreferences("setting", 0);
		sp_filter = context.getSharedPreferences("filter", 0);
		a = sp_filter.getInt("scf_transparency", 0);

		if(sp.getInt("color", 0) == 1) {
			r = 255;
			g = 36;
			b = 0;
		}
		if(sp.getInt("color", 0) == 2) {
			r = 255;
			g = 146;
			b = 0;
		}
		if(sp.getInt("color", 0) == 3) {
			r = 172;
			g = 121;
			b = 0;
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawARGB(2*a, r, g, b);
		invalidate();
	}

	@Override
	protected void onLayout(boolean p1, int p2, int p3, int p4, int p5)
	{
		// TODO: Implement this method
	}

}
