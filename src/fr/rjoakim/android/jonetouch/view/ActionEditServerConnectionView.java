package fr.rjoakim.android.jonetouch.view;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.dialog.ResultView;
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
public abstract class ActionEditServerConnectionView implements ResultView<Void> {

	private final View view;
	private final ActionService actionService;
	private final ServerService serverService;
	private final Activity activity;
	
	public abstract void onCancel(View view);
	
	public ActionEditServerConnectionView(Activity activity, ActionService actionService, ServerService serverService) {
		this.activity = activity;
		this.actionService = actionService;
		this.serverService = serverService;
		this.view = activity.getLayoutInflater()
				.inflate(R.layout.action_detail_view_server_connection_layout, null);
	}
	
	public View build(final Action action) {
		final Spinner spinner = (Spinner) view.findViewById(R.id.actionDetailViewServerConnectionSpinner);
		
		fillSpinnerWithServerConnections(spinner, action);

		final CheckBox checkbox = (CheckBox) view.findViewById(R.id.actionDetailViewServerConnectionCheckBox);
		if (action.getServerId() == null) {
			checkbox.setChecked(true);
			spinner.setSelection(0);
			spinner.setEnabled(false);
		}
		addCheckBoxAskExecutionEvent(spinner);
		
		final View button = view.findViewById(R.id.actionDetailViewServerConnectionValidateButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Server server = (Server) spinner.getSelectedItem();
				if (server.getId() == -1 && checkbox.isChecked() == false) {
					Toast.makeText(v.getContext(), activity
							.getString(R.string.incorrect_data), Toast.LENGTH_LONG).show();
				} else {
					try {
						Long serverNewId = null;
						if (server.getId() != -1) {
							serverNewId = server.getId();
						}
						actionService.updateServerConnection(action.getId(), action.getServerId(), serverNewId);
						onSuccess(null);
					} catch (ServiceException e) {
						onFailed();
					}
				}
			}
		});
		
		View undo = view.findViewById(R.id.actionDetailViewServerConnectionUndoButton);
		undo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCancel(view);
			}
		});
		
		return view;
	}
	
	private void fillSpinnerWithServerConnections(Spinner spinner, Action action) {
		try {
			final List<Server> servers = serverService.list();
			Server server = null;
			if (action.getServerId() != null) {
				server = serverService.get(action.getServerId());
			}
			
			List<Server> values = Lists.newArrayList(
					new Server(-1, activity.getString(R.string.add_or_update_action_server_spinner_label), null, 0, null, null));
			values.addAll(servers);
			ArrayAdapter<Server> dataAdapter = new ArrayAdapter<Server>(activity,
					R.layout.spinner_layout, values);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			
			if (server != null) {
				spinner.setSelection(getServerConnectionPositionOnSpinner(servers, server));
			}
		} catch (ServiceException e) {
			Toast.makeText(activity, activity.getString(R.string.failed), Toast.LENGTH_LONG).show();
		}
	}

	private int getServerConnectionPositionOnSpinner(List<Server> servers, Server server) {
		int cpt = 0;
		for (Server value: servers) {
			if (value.getId() == server.getId()) {
				cpt ++;
				return cpt;
			}
		}
		return cpt;
	}
	
	private void addCheckBoxAskExecutionEvent(final Spinner spinner) {
		CheckBox checkBox = (CheckBox) view
				.findViewById(R.id.actionDetailViewServerConnectionCheckBox);
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
}
