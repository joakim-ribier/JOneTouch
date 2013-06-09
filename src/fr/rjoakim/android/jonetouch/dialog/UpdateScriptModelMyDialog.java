package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
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
public abstract class UpdateScriptModelMyDialog extends AddScriptModelMyDialog {

	public UpdateScriptModelMyDialog(Activity activity, ScriptService scriptService) {
		super(activity, scriptService);
		
		defineTextToExtraButton(getString(R.string.delete_button));
		
		EditText titleEditText = (EditText) content.findViewById(R.id.addOrUpdateModelScriptTitleEditText);
		titleEditText.setHint(R.string.add_or_update_script_model_default_choice);
		
		EditText scriptEditText = (EditText) content.findViewById(R.id.addOrUpdateModelScriptValueEditText);
		scriptEditText.setHint(R.string.add_or_update_script_model_default_choice);
		
		setEnable(titleEditText, false);
		setEnable(scriptEditText, false);
	}

	@Override
	public void onFailed() {}
	
	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.update_script_model_dialog_title);
	}
	
	@Override
	public void onPositiveButton(View v) {
		if (checkValues()) {
			Script selectedScript = getSelectedScript();
			if (selectedScript.getId() != -1) {
				try {
					scriptService.update(selectedScript.getId(), getTitleText(), getScriptText());
					Toast.makeText(activity,
							getString(
									R.string.update_script_model_success, selectedScript.getTitle()),
									Toast.LENGTH_LONG).show();
					
					dialog.dismiss();
				} catch (ServiceException e) {
					Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
					setClipboardError(e.getMessage());
				}
			} else {
				Toast.makeText(activity,
						getString(R.string.update_script_model_unselected), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	protected void onExtraButton(View v) {
		Script selectedScript = getSelectedScript();
		if (selectedScript.getId() != -1) {
			try {
				scriptService.remove(selectedScript.getId());
				Toast.makeText(activity,
						getString(
								R.string.delete_script_model_success, selectedScript.getTitle()),
								Toast.LENGTH_LONG).show();
				
				dialog.dismiss();
			} catch (ServiceException e) {
				Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
				setClipboardError(e.getMessage());
			}
		} else {
			Toast.makeText(activity,
					getString(R.string.update_script_model_unselected), Toast.LENGTH_LONG).show();
		}
	}
	
	private Script getSelectedScript() {
		final Spinner spinner = (Spinner) content.findViewById(R.id.addOrUpdateModelScriptSpinner);
		return (Script) spinner.getSelectedItem();
	}
	
	private void setEnable(View view, boolean enabled) {
		view.setEnabled(enabled);
	}
	
	@Override
	protected void setTextToEditText(Script script) {
		final EditText titleEditText = (EditText) content.findViewById(R.id.addOrUpdateModelScriptTitleEditText);
		final EditText scriptEditText = (EditText) content.findViewById(R.id.addOrUpdateModelScriptValueEditText);
		
		setEnable(titleEditText, true);
		setEnable(scriptEditText, true);
		
		titleEditText.setText(script.getTitle());
		scriptEditText.setText(script.getValue());
	}
}
