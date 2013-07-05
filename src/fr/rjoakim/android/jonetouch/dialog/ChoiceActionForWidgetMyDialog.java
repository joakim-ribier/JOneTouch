package fr.rjoakim.android.jonetouch.dialog;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.common.primitives.Ints;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServiceException;

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
public abstract class ChoiceActionForWidgetMyDialog extends MyDialog<Action> {

	private List<Action> actions;

	public ChoiceActionForWidgetMyDialog(Activity activity, ActionService actionService) {
		super(activity, R.layout.choice_action_for_widget_dialog);
		try {
			actions = actionService.list();
			fillActionRadioGroup();
		} catch (ServiceException e) {
			setClipboardError(e.getMessage());
		}
	}

	private void fillActionRadioGroup() {
		RadioGroup radioGroup = getRadioGroupView();
		for (Action action: actions) {
			RadioButton radioButton = new RadioButton(activity);
			radioButton.setId(Ints.checkedCast(action.getId()));
			radioButton.setText(action.getTitle());
			radioGroup.addView(radioButton);
		}
	}

	private RadioGroup getRadioGroupView() {
		RadioGroup radioGroup = (RadioGroup) content;
		return radioGroup;
	}

	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.choice_action_for_widget_dialog_title);
	}

	@Override
	public void onPositiveButton(View v) {
		RadioGroup radioGroup = getRadioGroupView();
		int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
		if (checkedRadioButtonId != -1) {
			dialog.dismiss();
			onSuccess(
					getFromId(checkedRadioButtonId));
		} else {
			Toast.makeText(activity,
					getString(R.string.choice_action_for_widget_dialog_unknow),
					Toast.LENGTH_LONG).show();
		}
	}

	private Action getFromId(int checkedRadioButtonId) {
		for (Action action: actions) {
			if (action.getId() == checkedRadioButtonId) {
				return action;
			}
		}
		return null;
	}

	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
		onFailed();
	}
}
