package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
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
public abstract class UpdateActionMyDialog extends AddActionMyDialog {

	private Action action;

	public UpdateActionMyDialog(Activity activity, ServerService serverService,
			ActionService actionService) {

		super(activity, serverService, actionService);
	}

	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.update_action_dialog_title);
	}
	
	@Override
	public void onPositiveButton(View v) {
		if (checkValues()) {
			if (action != null) {
				try {
					Server server = getServerConnectionValue();
					if (server.getId() != -1) {
						actionService.update(action.getId(), getTitleText(), getDescriptionText(), action.getServerId(), server.getId());
					} else {
						actionService.update(action.getId(), getTitleText(), getDescriptionText(), action.getServerId(), null);
					}
					dialog.dismiss();
					onSuccess(action.getId());
				} catch (ServiceException e) {
					Toast.makeText(activity,
							getString(R.string.failed), Toast.LENGTH_LONG).show();
					setClipboardError(e.getMessage());
				}
			}
		}
	}
	
	public void show(Action action) {
		super.show();

		this.action = action;
		
		setServerConnection(action);

		TextView titleTextView = (TextView) content.findViewById(R.id.addActionViewTitleEditText);
		titleTextView.setText(action.getTitle());
		EditText descrTextView = (EditText) content.findViewById(R.id.addActionViewDescriptionEditText);
		descrTextView.setText(action.getDescription());
	}
	
	private void setServerConnection(Action action) {
		if (action.getServerId() != null) {
			SpinnerAdapter adpater = spinner.getAdapter();
			int count = adpater.getCount();
			for (int cpt = 0; cpt < count; cpt ++) {
				Server server = (Server) adpater.getItem(cpt);
				if (server.getId() == action.getServerId()) {
					spinner.setSelection(cpt);
				}
			}
		} else {
			checkBox.setChecked(true);
		}
	}
}
