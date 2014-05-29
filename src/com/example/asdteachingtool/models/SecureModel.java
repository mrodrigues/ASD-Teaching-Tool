package com.example.asdteachingtool.models;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.activeandroid.Cache;
import com.activeandroid.Model;

public abstract class SecureModel extends Model {

	private static final String LOG_TAG = "SecureModel";

	public void beforeSave() {
	};

	public void beforeDelete() {
	};

	public void afterSave() {
	};

	public void afterDelete() {
	};

	public void secureSave() {
		beforeSave();
		save();
		afterSave();
	}

	public void secureDelete() {
		if (isPersisted()) {
			beforeDelete();
			delete();
			afterDelete();
		}
	}

	public boolean isPersisted() {
		return getId() != null;
	}

	public <T extends Model> List<T> secureGetMany(Class<T> type) {
		return secureGetMany(type, getClass().getSimpleName());
	}

	public <T extends Model> List<T> secureGetMany(Class<T> type, String column) {
		if (!isPersisted()) {
			return new ArrayList<T>();
		}
		return getMany(type, column);
	}

	protected <T extends Model> T first(Class<T> type) {
		return first(type, getClass().getSimpleName());
	}

	protected <T extends Model> T first(Class<T> type, String column) {
		List<T> query = secureGetMany(type, column);
		T model = null;
		if (query.isEmpty()) {
			try {
				model = type.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				Log.e(LOG_TAG, e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				Log.e(LOG_TAG, e.getMessage());
			}
		} else {
			model = query.get(0);
		}
		return model;
	}
}
