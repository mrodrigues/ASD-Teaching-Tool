package com.example.asdteachingtool.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class TempFilesManager {

	private static final String LOG_TAG = "TempFilesManager";
	private static final String DIRECTORY_SUFFIX = "/ASDTeachingTool";
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

	public File createTempFile(String prefix, String suffix, File directory) {
		try {
			File tempFile = File.createTempFile(prefix, suffix, getDirectory(directory));
			tempFiles.add(tempFile);
			return tempFile;
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException");
			return null;
		}
	}

	public static File createUnmanagedTempFile(String prefix, String suffix,
			File directory) {
		try {
			System.err.println("===================");
			System.err.println(directory.getPath());
			System.err.println(directory.exists());
			System.err.println(getDirectory(directory).getPath());
			System.err.println(getDirectory(directory).exists());
			File tempFile = File.createTempFile(prefix, suffix, getDirectory(directory));
			System.err.println(tempFile.getPath());
			System.err.println(tempFile.exists());
			System.err.println("===================");
			return tempFile;
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException");
			return null;
		}
	}
	
	private static File getDirectory(File directory) {
		File newDirectory = new File(directory.getPath() + DIRECTORY_SUFFIX);
		if (!newDirectory.exists()) {
			newDirectory.mkdirs();
		}
		return newDirectory;
	}

	public Boolean clean() {
		System.err.println("========  clean() ==========");
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
