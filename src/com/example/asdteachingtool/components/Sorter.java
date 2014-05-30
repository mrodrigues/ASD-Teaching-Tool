package com.example.asdteachingtool.components;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.activeandroid.ActiveAndroid;
import com.example.asdteachingtool.R;
import com.example.asdteachingtool.models.Sortable;

public class Sorter<T extends Sortable, U extends Activity> implements OnClickListener {

	public static final String EXTRA_MODEL_ID = "com.example.asdteachingtool.MODEL_ID";

	private Activity activity;
	private RadioGroup modelsList;
	private Integer lastSelectedIndex;
	private Class<T> modelClass;
	private Class<U> modelFormActivity;

	/**
	 * Creates a model sorter. You <b>must</b> call resume() when your activity
	 * has been resumed. Example:
	 * 
	 * <pre>
	 * protected void onStart() {
	 * 	super.onStart();
	 * 	sorter.resume();
	 * }
	 * </pre>
	 * 
	 * @param activity
	 *            Activity where the component must be included
	 * @param modelClass
	 *            Sortable model to be sorted
	 * @param modelFormActivity
	 *            Form to call for creating and editing the model
	 */
	public Sorter(Activity activity, Class<T> modelClass,
			Class<U> modelFormActivity) {
		super();
		this.activity = activity;
		this.modelClass = modelClass;
		this.modelFormActivity = modelFormActivity;

		activity.setContentView(R.layout.sorter);
		modelsList = (RadioGroup) activity.findViewById(R.id.modelsList);

		activity.findViewById(R.id.newModel).setOnClickListener(this);
		activity.findViewById(R.id.editModel).setOnClickListener(this);
		activity.findViewById(R.id.upModel).setOnClickListener(this);
		activity.findViewById(R.id.downModel).setOnClickListener(this);
	}

	public void resume() {
		lastSelectedIndex = null;
		update();
	}

	public void update() {
		modelsList.removeAllViews();
		final View up = activity.findViewById(R.id.upModel);
		final View down = activity.findViewById(R.id.downModel);
		final View edit = activity.findViewById(R.id.editModel);
		List<Sortable> models = null;
		try {
			models = (List<Sortable>) modelClass.getMethod("all").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Sortable model : models) {
			RadioButton questionView = new RadioButton(activity);
			questionView.setText(model.getName());
			questionView.setTag(model);
			questionView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					up.setEnabled(true);
					down.setEnabled(true);
					edit.setEnabled(true);
				}
			});
			modelsList.addView(questionView);
		}
		if (lastSelectedIndex != null) {
			((RadioButton) modelsList.getChildAt(lastSelectedIndex))
					.setChecked(true);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newModel:
			newModel(v);
			break;
		case R.id.editModel:
			editModel(v);
			break;
		case R.id.upModel:
			upModel(v);
			break;
		case R.id.downModel:
			downModel(v);
			break;
		}
	}

	public void newModel(View view) {
		activity.startActivity(new Intent(activity, modelFormActivity));
	}

	public void editModel(View v) {
		Intent intent = new Intent(v.getContext(), modelFormActivity);
		View selected = selectedQuestion();
		if (selected != null) {
			intent.putExtra(EXTRA_MODEL_ID,
					((Sortable) selected.getTag()).getId());
			activity.startActivity(intent);
		}
	}

	public void upModel(View v) {
		int index = selectedIndex();
		if (index > 0) {
			changePositions(index, index - 1);
		}
	}

	public void downModel(View v) {
		int index = selectedIndex();
		if (index < modelsList.getChildCount() - 1 && index > -1) {
			changePositions(index, index + 1);
		}
	}

	private void changePositions(int selectedIndex, int targetIndex) {
		RadioButton selectedButton = (RadioButton) modelsList
				.getChildAt(selectedIndex);
		RadioButton targetButton = (RadioButton) modelsList
				.getChildAt(targetIndex);
		Sortable selected = (Sortable) selectedButton.getTag();
		Sortable target = (Sortable) targetButton.getTag();
		ActiveAndroid.beginTransaction();
		try {
			Integer aux = selected.getPosition();
			selected.setPosition(target.getPosition());
			target.setPosition(aux);
			selected.secureSave();
			target.secureSave();
			ActiveAndroid.setTransactionSuccessful();
			lastSelectedIndex = targetIndex;
		} finally {
			ActiveAndroid.endTransaction();
		}
		update();
	}

	private RadioButton selectedQuestion() {
		int id = modelsList.getCheckedRadioButtonId();
		if (id < 0) {
			return null;
		} else {
			return (RadioButton) modelsList.findViewById(id);
		}
	}

	private int selectedIndex() {
		RadioButton selected = selectedQuestion();
		if (selected != null) {
			return modelsList.indexOfChild(selected);
		}
		return -1;
	}
}
