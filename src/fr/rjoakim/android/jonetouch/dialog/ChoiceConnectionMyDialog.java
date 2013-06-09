package fr.rjoakim.android.jonetouch.dialog;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.common.primitives.Ints;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Server;
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
public abstract class ChoiceConnectionMyDialog extends MyDialog<Server> {

	private List<Server> servers;

	public ChoiceConnectionMyDialog(Activity activity, ServerService serverService) {
		super(activity, R.layout.choice_server_connection_dialog);
		try {
			servers = serverService.list();
			fillChoiceConnectionRadioGroup();
		} catch (ServiceException e) {
			setClipboardError(e.getMessage());
		}
	}

	private void fillChoiceConnectionRadioGroup() {
		final RadioGroup radioGroup = getRadioGroupView();
		for (Server server : servers) {
			RadioButton radioButton = new RadioButton(activity);
			radioButton.setId(Ints.checkedCast(server.getId()));
			radioButton.setText(server.getTitle());
			radioGroup.addView(radioButton);
		}
	}

	private RadioGroup getRadioGroupView() {
		RadioGroup radioGroup = (RadioGroup) content;
		return radioGroup;
	}

	@Override
	public void onFailed() {}
	
	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.choice_connection_dialog_title);
	}

	@Override
	public void onPositiveButton(View v) {
		dialog.dismiss();
		RadioGroup radioGroup = getRadioGroupView();
		int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
		if (checkedRadioButtonId != -1) {
			onSuccess(
					getFromId(checkedRadioButtonId));
		} else {
			Toast.makeText(activity,
					getString(R.string.choice_connection_dialog_unknow),
					Toast.LENGTH_LONG).show();
		}
	}

	private Server getFromId(int checkedRadioButtonId) {
		for (Server server: servers) {
			if (server.getId() == checkedRadioButtonId) {
				return server;
			}
		}
		return null;
	}

	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
}
