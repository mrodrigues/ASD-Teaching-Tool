package com.example.asdteachingtool.listeners;

import java.util.List;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.asdteachingtool.components.ConnectingStroke;

public class SelectListener implements OnTouchListener {
	private ConnectingStroke stroke;
	private List<View> targets;
	private int selectColor;

	public SelectListener(ConnectingStroke stroke, int selectColor, List<View> targets) {
		super();
		this.stroke = stroke;
		this.targets = targets;
		this.selectColor = selectColor;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getRawX();
		float y = event.getRawY();
		stroke.draw(x, y);
		for (View target : targets) {
			target.setBackgroundColor(Color.TRANSPARENT);
		}
		View selected = getTargetUnder(x, y);
		if (selected != null) {
			selected.setBackgroundColor(selectColor);
		}
		switch (MotionEventCompat.getActionMasked(event)) {
		case MotionEvent.ACTION_DOWN:
			stroke.start();
			v.setBackgroundColor(selectColor);
			break;
		case MotionEvent.ACTION_UP:
			stroke.stop();
			v.setBackgroundColor(Color.TRANSPARENT);
			if (selected != null) {
				selected.performClick();
			}
			break;
		}
		return true;
	}
	
	private View getTargetUnder(float x, float y) {
		for (View target : targets) {
			Rect bounds = new Rect();
			target.getGlobalVisibleRect(bounds);
			if (bounds.contains((int) x, (int) y)) {
				return target;
			}
		}
		return null;
	}
}
