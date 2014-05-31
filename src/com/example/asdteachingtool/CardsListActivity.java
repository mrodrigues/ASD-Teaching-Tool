package com.example.asdteachingtool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.example.asdteachingtool.components.Sorter;
import com.example.asdteachingtool.models.Card;

public class CardsListActivity extends Activity {

	private Integer categoryId;
	private Sorter<Card, CardFormActivity> sorter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra(
				CategoriesListActivity.EXTRA_CATEGORY_NAME));
		categoryId = getIntent().getIntExtra(
				CategoriesListActivity.EXTRA_CATEGORY_ID, -1);
		System.err.println("==================");
		System.err.println("onCreate");
		System.err.println("==================");
		sorter = new Sorter<Card, CardFormActivity>(this, Card.class,
				CardFormActivity.class, "byCategory", categoryId);
		Bundle extras = new Bundle();
		extras.putInt(CardFormActivity.EXTRAS_CATEGORY_ID, categoryId);
		sorter.setExtras(extras);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	@Override
	protected void onStart() {
		super.onStart();
		sorter.resume();
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
		getMenuInflater().inflate(R.menu.pecs_list, menu);
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
