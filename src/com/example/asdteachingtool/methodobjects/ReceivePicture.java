package com.example.asdteachingtool.methodobjects;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

public class ReceivePicture {
	private ImageView view;

	public ReceivePicture(ImageView view) {
		super();
		this.view = view;
	}

	public void receive(Bitmap bitmap) {
		setVisibility(bitmap);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			view.setTag(stream.toByteArray());
		}
	}

	public void receive(byte[] data) {
		setVisibility(data);
		if (data != null) {
			receive(BitmapFactory.decodeByteArray(data, 0, data.length));
		}
	}

	private void setVisibility(Object picture) {
		if (picture == null) {
			view.setVisibility(View.INVISIBLE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}
}
