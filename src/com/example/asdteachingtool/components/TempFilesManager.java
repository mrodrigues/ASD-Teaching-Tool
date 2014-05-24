package com.example.asdteachingtool.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class TempFilesManager {
	
	private static final String LOG_TAG = "TempFilesManager";
	private List<File> tempFiles;
	
	private static TempFilesManager instance;
	
	public static TempFilesManager getInstance() {
		if (instance == null) {
			instance = new TempFilesManager();
		}
		return instance;
	}
	
	public TempFilesManager() {
		super();
		this.tempFiles = new ArrayList<File>();
	}

	public String createTempFile(String prefix, String suffix, File directory) {
		try {
			File tempFile = File.createTempFile(prefix, suffix, directory);
			tempFiles.add(tempFile);
			return tempFile.getPath();
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException");
			return null;
		}
	}
	
	public Boolean clean() {
		Boolean allClean = true;
		for (File tempFile : tempFiles) {
			if (!tempFile.delete()) {
				allClean = false;
				Log.e(LOG_TAG, "File not deleted: " + tempFile.getPath());
			}
		}
		tempFiles.clear();
		return allClean;
	}
}
