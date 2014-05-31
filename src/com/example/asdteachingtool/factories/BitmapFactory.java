package com.example.asdteachingtool.factories;

import android.graphics.Bitmap;

public class BitmapFactory extends android.graphics.BitmapFactory {

	public static Bitmap decodeByteArray(byte[] data) {
		if (data != null) {
			return decodeByteArray(data, 0, data.length);
		}
		return null;
	}

	public static Bitmap decodeScaledByteArray(byte[] data, int width,
			int height) {
		if (data != null) {
			return Bitmap.createScaledBitmap(decodeByteArray(data), width, height,
					true);
		}
		return null;
	}
}
