package com.example.asdteachingtool.methodobjects;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class ReceivePicture extends AsyncTask<Bitmap, Void, Void> {
	private ImageView view;

	public ReceivePicture(ImageView view) {
		super();
		this.view = view;
	}

	public void receive(Bitmap bitmap) {
		doInBackground(bitmap);
	}

	public void receive(byte[] data) {
		setVisibility(data);
		if (data != null) {
			receive(BitmapFactory.decodeByteArray(data, 0, data.length));
		}
	}

	public void receive(String path) {
		new AsyncTask<String, Void, Void>() {
			public Void doInBackground(String... params) {
				receive(BitmapFactory.decodeFile(params[0]));
				return null;
			}
		}.doInBackground(path);
	}

	private void setVisibility(Object picture) {
		if (picture == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected Void doInBackground(Bitmap... params) {
		Bitmap bitmap = params[0];
		setVisibility(bitmap);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			view.setTag(stream.toByteArray());
		}
		return null;
	}
}
