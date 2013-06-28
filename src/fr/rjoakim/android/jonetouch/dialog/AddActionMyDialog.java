package fr.rjoakim.android.jonetouch.dialog;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServerService;
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
public abstract class AddActionMyDialog extends MyDialog<Long> {

	private final ServerService serverService;
	protected final ActionService actionService;
	
	protected final Spinner spinner;
	protected final CheckBox checkBox;

	public AddActionMyDialog(Activity activity, ServerService serverService,
			ActionService actionService) {
		
		super(activity, R.layout.add_action_dialog);
		
		this.serverService = serverService;
		this.actionService = actionService;
		
		this.spinner = (Spinner) content.findViewById(R.id.addActionViewServerConnectionSpinner);
		this.checkBox = (CheckBox) content.findViewById(R.id.addActionViewAskCheckbox);
		
		fillSpinnerWithServerConnections();
		addCheckBoxAskExecutionEvent();
	}

	@Override
	public void onFailed() {}
	
	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.add_action_dialog_title);
	}
	
	@Override
	public void onPositiveButton(View v) {
		if (checkValues()) {
			Long id = createNewAction();
			if (id != null) {
				dialog.dismiss();
				onSuccess(id);
			}
		}
	}
	
	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
	
	private void fillSpinnerWithServerConnections() {
		try {
			final List<Server> servers = serverService.list();
			List<Server> values = Lists.newArrayList(
					new Server(-1, activity.getString(R.string.add_or_update_action_server_spinner_label), null, 0, null, null));
			
			values.addAll(servers);
			ArrayAdapter<Server> dataAdapter = new ArrayAdapter<Server>(activity,
					R.layout.spinner_layout, values);
			
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
		} catch (ServiceException e) {
			Toast.makeText(activity, activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
		}
	}

	private void addCheckBoxAskExecutionEvent() {
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					spinner.setSelection(0);
					spinner.setEnabled(false);
				} else {
					spinner.setEnabled(true);
				}
			}
		});
	}
	
	protected String getTitleText() {
		return getEditTextValue(R.id.addActionViewTitleEditText);
	}
	
	protected String getDescriptionText() {
		return getEditTextValue(R.id.addActionViewDescriptionEditText);
	}
	
	protected Server getServerConnectionValue() {
		return (Server) spinner.getSelectedItem();
	}
	
	protected boolean checkValues() {
		if (Strings.isNullOrEmpty(getTitleText()) ||
				Strings.isNullOrEmpty(getDescriptionText())) {

			Toast.makeText(activity,
					getString(R.string.incorrect_data), Toast.LENGTH_LONG).show();
			return false;
		}
		Server server = getServerConnectionValue();
		if (server.getId() == -1 && checkBox.isChecked() == false) {
			Toast.makeText(activity,
					getString(R.string.incorrect_data), Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private Long createNewAction() {
		try {
			Server server = getServerConnectionValue();
			if (server.getId() != -1) {
				return actionService.create(getTitleText(), getDescriptionText(), server);
			} else {
				return actionService.create(getTitleText(), getDescriptionText());
			}
		} catch (ServiceException e) {
			Toast.makeText(activity,
					getString(R.string.failed), Toast.LENGTH_LONG).show();
			setClipboardError(e.getMessage());
			return null;
		}
	}
}
