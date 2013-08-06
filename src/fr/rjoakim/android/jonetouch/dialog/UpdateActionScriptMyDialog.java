package fr.rjoakim.android.jonetouch.dialog;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.ActionScript;

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
public abstract class UpdateActionScriptMyDialog extends MyDialog<List<String>> {

	private final static String SEPARATOR = "#script suivant#";

	public UpdateActionScriptMyDialog(Activity activity) {
		super(activity, R.layout.add_or_update_action_script_dialog);
		addCheckBoxSeparatorEvent();
	}

	@Override
	public void onFailed() {}
	
	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.update_action_script_dialog_title);
	}
	
	@Override
	public void onPositiveButton(View v) {
		if (checkValues()) {
			dialog.dismiss();
			onSuccess(formatScriptValues());
		}
	}
	
	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
	
	private void insertTextAtCursorPosition(EditText editText, String value) {
		int cursorPos = editText.getSelectionStart();
		String text = editText.getText().toString();
		String beforeCursor = text.substring(0, cursorPos);
		String afterCursor = text.substring(cursorPos);
		editText.setText(beforeCursor + value + afterCursor);
		editText.setSelection(cursorPos + value.length());
	}
	
	private void addCheckBoxSeparatorEvent() {
		final EditText editText = (EditText) content.findViewById(R.id.addOrUpdateActionScriptEditText);
		final CheckBox checkBox = (CheckBox) content.findViewById(R.id.addOrUpdateActionSeparatorCheckBox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					insertTextAtCursorPosition(editText, SEPARATOR + "\n");
					checkBox.setChecked(false);
				}
			}
		});
	}

	private boolean checkValues() {
		if (Strings.isNullOrEmpty(getEditTextValue(R.id.addOrUpdateActionScriptEditText))) {
			String incorrectData = getString(R.string.incorrect_data);
			Toast.makeText(activity, incorrectData, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private List<String> formatScriptValues() {
		String values = getEditTextValue(R.id.addOrUpdateActionScriptEditText);
		Iterable<String> iterable = Splitter.on(SEPARATOR).split(values);
		if (Iterables.isEmpty(iterable)) {
			return Lists.newArrayList(values.trim());
		} else {
			List<String> scripts = Lists.newArrayList();
			for (String s: iterable) {
				if (!Strings.isNullOrEmpty(s.trim())) {
					scripts.add(s);
				}
			}
			return scripts;
		}
	}
	
	public void show(Action action) {
		super.show();
		EditText editText = (EditText) content.findViewById(R.id.addOrUpdateActionScriptEditText);
		for (ActionScript actionScript: action.getActionScripts()) {
			String s = editText.getText().toString();
			s = s + actionScript.getScript() + "\n" + SEPARATOR + "\n";
			editText.setText(s);
		}
	}
}
