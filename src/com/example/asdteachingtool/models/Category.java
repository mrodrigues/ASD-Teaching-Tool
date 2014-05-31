package com.example.asdteachingtool.models;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;

import com.example.asdteachingtool.R;
import com.example.asdteachingtool.SelectGameActivity;

public class Category {

	private String name;
	private Integer id;

	private Category(String name, Integer id) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	private static List<Category> categories;
	private static Category subjects;
	private static Category verbs;
	private static Category objects;

	private static String t(int id) {
		return SelectGameActivity.getContext().getString(id);
	}

	public static List<Category> all() {
		if (categories == null) {
			categories = new ArrayList<Category>();
			categories.add(getSubjects());
			categories.add(getVerbs());
			categories.add(getObjects());
		}
		return categories;
	}

	public static Category getSubjects() {
		if (subjects == null) {
			subjects = new Category(t(R.string.category_subjects), 0);
		}
		return subjects;
	}

	public static Category getVerbs() {
		if (verbs == null) {
			verbs = new Category(t(R.string.category_verbs), 1);
		}
		return verbs;
	}

	public static Category getObjects() {
		if (objects == null) {
			objects = new Category(t(R.string.category_objects), 2);
		}
		return objects;
	}

}
