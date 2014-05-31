package com.example.asdteachingtool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.asdteachingtool.models.Category;

public class CategoriesListActivity extends Activity {

	public static final String EXTRA_CATEGORY_ID = "com.example.asdteachingtool.CATEGORY_ID";
	public static final String EXTRA_CATEGORY_NAME = "com.example.asdteachingtool.CATEGORY_NAME";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories_list);
		ViewGroup categoriesList = (ViewGroup) findViewById(R.id.categoriesList);
		for (Category category : Category.all()) {
			Button categoryButton = new Button(this);
			categoryButton.setTag(category);
			categoryButton.setText(category.getName());
			categoryButton.setWidth(R.dimen.start_button);
			categoryButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), CardsListActivity.class);
					Category category = (Category) v.getTag();
					intent.putExtra(EXTRA_CATEGORY_ID, category.getId());
					intent.putExtra(EXTRA_CATEGORY_NAME, category.getName());
					startActivity(intent);
				}
			});
			categoriesList.addView(categoryButton);
		}
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
