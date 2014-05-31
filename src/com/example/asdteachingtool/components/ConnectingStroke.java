package com.example.asdteachingtool.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ConnectingStroke extends View {
	
	private Boolean drawing;
	private Paint paint;
	
	private float startX;
	private float startY;
	
	private float endX;
	private float endY;

	public ConnectingStroke(Context context, int strokeColor) {
		super(context);
		drawing = false;
		paint = new Paint();
		paint.setColor(strokeColor);
		paint.setStrokeWidth(10);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (drawing) {
			canvas.drawLine(startX, startY, endX, endY, paint);
		}
	}
	
	public void draw(float x, float y) {
		if (drawing) {
			endX = x;
			endY = y;
		} else {
			startX = x;
			startY = y;
		}
		invalidate();
	}
	
	public void start() {
		drawing = true;
		endX = startX;
		endY = startY;
	}
	
	public void stop() {
		drawing = false;
	}
}
