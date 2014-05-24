package com.example.asdteachingtool.factories;

import android.graphics.Bitmap;

public class BitmapFactory extends android.graphics.BitmapFactory {

	public static Bitmap decodeByteArray(byte[] data) {
		if (data != null) {
			return decodeByteArray(data, 0, data.length);
		}
		return null;
	}
}
