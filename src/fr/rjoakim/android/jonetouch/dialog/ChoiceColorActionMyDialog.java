package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.util.ColorHexFactory;

/**
 * 
 * Copyright 2013 Joakim Ribier
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public abstract class ChoiceColorActionMyDialog extends MyDialog<String> {

	protected String color = "";
	
	public ChoiceColorActionMyDialog(final Activity activity) {
		super(activity, R.layout.choice_color_action_dialog);
		
		View blueLayout = content.findViewById(R.id.choiceColorActionViewBlueLayout);
		blueLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				content.setBackgroundColor(activity.getResources().getColor(R.color.action_background_choice_blue));
				color = getString(R.string.action_background_choice_blue);
			}
		});
		
		View purpleLayout = content.findViewById(R.id.choiceColorActionViewPurpleLayout);
		purpleLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				content.setBackgroundColor(activity.getResources().getColor(R.color.action_background_choice_purple));
				color = getString(R.string.action_background_choice_purple);
			}
		});
		
		View greenLayout = content.findViewById(R.id.choiceColorActionViewGreenLayout);
		greenLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				content.setBackgroundColor(activity.getResources().getColor(R.color.action_background_choice_green));
				color = getString(R.string.action_background_choice_green);
			}
		});
		
		View orangeLayout = content.findViewById(R.id.choiceColorActionViewOrangeLayout);
		orangeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				content.setBackgroundColor(activity.getResources().getColor(R.color.action_background_choice_orange));
				color = getString(R.string.action_background_choice_orange);
			}
		});
		
		View redLayout = content.findViewById(R.id.choiceColorActionViewRedLayout);
		redLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				content.setBackgroundColor(activity.getResources().getColor(R.color.action_background_choice_red));
				color = getString(R.string.action_background_choice_red);
			}
		});
	}

	@Override
	public void onFailed() {}

	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.choice_color_action_dialog_title);
	}

	@Override
	public void onPositiveButton(View v) {
		dialog.dismiss();
		onSuccess(color);
	}

	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
		onFailed();
	}
	
	public void show(String color) {
		super.show();
		this.content.setBackgroundColor(activity.getResources().getColor(ColorHexFactory.hexColorToInt(color)));
		this.color = color;
	}
}
