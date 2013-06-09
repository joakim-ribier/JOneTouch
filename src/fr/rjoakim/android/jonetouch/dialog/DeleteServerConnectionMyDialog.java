package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
public abstract class DeleteServerConnectionMyDialog extends MyDialog<Void> {

	private final ServerService serverService;
	private final Server server;

	public DeleteServerConnectionMyDialog(final Activity activity, final Server server) {
		super(activity, R.layout.delete_server_connection_my_dialog);
		this.serverService = new ServerService(activity);
		this.server = server;
		
		TextView host = (TextView) content.findViewById(R.id.deleteServerConnectionMyDialogTextViewHost);
		String message = server.getHost() + "\n( ";
		switch (server.getAuthentication().getAuthenticationTypeEnum()) {
		case NO_AUTHENTICATION:
			message += activity.getString(R.string.delete_server_connection_my_dialog_type_no);
			break;
		case SSH_AUTHENTICATION_PASSWORD:
			message += activity.getString(R.string.delete_server_connection_my_dialog_type_auth);
			break;
		default:
			throw new RuntimeException(
					"authentication type:" + server.getAuthentication().getAuthenticationTypeEnum() + " not supported");
		}
		host.setText(message + " )");
	}

	@Override
	public String getTitleView(Activity activity) {
		return activity.getString(R.string.delete_server_connection_my_dialog_title);
	}
	
	@Override
	public void initMyDialogLayout(View dialogRootView) {
		Button yes = (Button) dialogRootView.findViewById(R.id.myDialogPositiveButton);
		yes.setText((activity.getString(R.string.no_button)));
		
		Button no = (Button) dialogRootView.findViewById(R.id.myDialogNegativeButton);
		no.setText((activity.getString(R.string.yes_button)));
	}
	
	@Override
	public void onPositiveButton(View v) {
		try {
			serverService.remove(server);
			onSuccess(null);
		} catch (ServiceException e) {
			onFailed();
		} finally {
			dialog.dismiss();
		}
	}
	
	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
}
