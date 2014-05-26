package com.example.asdteachingtool.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.example.asdteachingtool.ThermometerActivity;

public class LaunchThermometer extends SimpleOnGestureListener {
	private Context context;

	public LaunchThermometer(Context context) {
		super();
		this.context = context;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		context.startActivity(new Intent(context, ThermometerActivity.class));
		return super.onFling(e1, e2, velocityX, velocityY);
	}
}
