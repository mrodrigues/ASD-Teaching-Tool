package com.example.asdteachingtool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.example.asdteachingtool.models.Option;
import com.example.asdteachingtool.models.Question;

public class QuestionActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.asdteachingtool.MESSAGE";
	public final static String EXTRA_QUESTION_ID = "com.example.asdteachingtool.QUESTION_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);
		// ---- TEST SAMPLE ------
		Question question = null;
		if (Question.all().size() == 0) {
			question = new Question();
			question.title = "Feliz";
			question.save();
			
			Option option1 = new Option();
			option1.question = question;
			option1.text = "Rosto sorridente";
			option1.correct = true;
			option1.save();
	
			Option option2 = new Option();
			option2.question = question;
			option2.text = "Rosto triste";
			option2.correct = false;
			option2.save();
		} else {
			question = Question.all().get(0);
		}
		// -----------------------
		
		setTitle(question.title);
		
		TextView questionnaireView = (TextView) findViewById(R.id.questionnaire_view);
		questionnaireView.setTextSize(40);
		questionnaireView.setText(question.title);
		
		ViewGroup optionsContainer = (ViewGroup) findViewById(R.id.options_container);
		optionsContainer.removeAllViews();
		for (Option option : question.options()) {
			Button optionView = new Button(this);
			optionView.setTextSize(20);
			optionView.setText(option.text);
			optionView.setId(option.getId().intValue());
			optionView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Button button = (Button) v;
					button.setTextColor(Color.RED);
					
					Option option = Option.load(Option.class, button.getId());
					if (option.isCorrect()) {
						button.setText("ACERTOU!");
					} else {
						Intent intent = new Intent(v.getContext(), QuestionsListActivity.class);
						v.getContext().startActivity(intent);
					}
				}
			});
			optionView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			optionsContainer.addView(optionView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questionnaire, menu);
		return true;
	}

	public void selectOption(View view) {
		
	}
	
}
