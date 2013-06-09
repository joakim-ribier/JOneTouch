package fr.rjoakim.android.jonetouch.dialog;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Script;
import fr.rjoakim.android.jonetouch.service.ScriptService;
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
public abstract class AddScriptModelMyDialog extends MyDialog<String> {

	protected final ScriptService scriptService;

	public AddScriptModelMyDialog(Activity activity, ScriptService scriptService) {
		
		super(activity, R.layout.add_or_update_script_model_dialog);
		
		this.scriptService = scriptService;
		
		fillScriptModels(content);
		addScriptModelEvent(content);
	}

	@Override
	public void onFailed() {}
	
	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.add_script_model_dialog_title);
	}
	
	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
	
	@Override
	public void onPositiveButton(View v) {
		if (checkValues()) {
			Long id = createNewScriptModel();
			if (id != null) {
				dialog.dismiss();
				onSuccess(getTitleText());
			}
		}
	}
	
	private Long createNewScriptModel() {
		try {
			return scriptService.create(getTitleText(), getScriptText());
		} catch (ServiceException e) {
			Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
			setClipboardError(e.getMessage());
			return null;
		}
	}
	
	private void fillScriptModels(View content) {
		Spinner spinner = (Spinner) content.findViewById(R.id.addOrUpdateModelScriptSpinner);
		try {
			List<Script> scripts = scriptService.list();
			List<Script> values = Lists.newArrayList(buildFakeModel());
			values.addAll(scripts);
			
			ArrayAdapter<Script> dataAdapter = new ArrayAdapter<Script>(activity,
					R.layout.spinner_layout, values);
			
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
		} catch (ServiceException e) {
			Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
			setClipboardError(e.getMessage());
		}
	}
	
	private Script buildFakeModel() {
		return new Script(
				-1,
				getString(R.string.add_or_update_script_model_default_choice), null);
	}
	
	private void addScriptModelEvent(View content) {
		final Spinner spinner = (Spinner) content.findViewById(R.id.addOrUpdateModelScriptSpinner);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (position != 0) {
					Script script = (Script) spinner.getItemAtPosition(position);
					setTextToEditText(script);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	protected void setTextToEditText(Script script) {
		EditText editText = (EditText) content.findViewById(R.id.addOrUpdateModelScriptValueEditText);
		int cursorPos = editText.getSelectionStart();
		String text = editText.getText().toString();
		String beforeCursor = text.substring(0, cursorPos);
		String afterCursor = text.substring(cursorPos);
		editText.setText(beforeCursor + script.getValue() + "\n" + afterCursor);
		editText.setSelection(cursorPos + script.getValue().length());
	}
	
	protected String getScriptText() {
		return getEditTextValue(R.id.addOrUpdateModelScriptValueEditText);
	}
	
	protected String getTitleText() {
		return getEditTextValue(R.id.addOrUpdateModelScriptTitleEditText);
	}
	
	protected boolean checkValues() {
		if (Strings.isNullOrEmpty(getTitleText())
				|| Strings.isNullOrEmpty(getScriptText())) {
			String incorrectData = getString(R.string.incorrect_data);
			Toast.makeText(activity, incorrectData, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
