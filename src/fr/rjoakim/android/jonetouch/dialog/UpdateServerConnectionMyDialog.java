package fr.rjoakim.android.jonetouch.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.bean.NoAuthentication;
import fr.rjoakim.android.jonetouch.bean.SSHAuthenticationPassword;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.service.AuthenticationTypeService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.util.CryptographyException;

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
public abstract class UpdateServerConnectionMyDialog extends AddServerConnectionMyDialog {

	private final Server server;

	public UpdateServerConnectionMyDialog(final Activity activity, final ServerService serverService,
			final AuthenticationTypeService authenticationTypeService, final Server server, MyAuthentication myAuthentication) {
		
		super(activity, serverService, authenticationTypeService, myAuthentication);
		this.server = server;
		
		initEditText(server.getTitle(), R.id.serverConnectionTitleEditText);
		initEditText(server.getHost(), R.id.serverConnectionHostEditText);
		initEditText(String.valueOf(server.getPort()), R.id.serverConnectionPortEditText);
		initEditText(server.getDescription(), R.id.serverConnectionDescriptionEditText);
		
		Spinner spinner = (Spinner) content.findViewById(R.id.serverConnectionAuthTypeSpinner);
		switch (server.getAuthentication().getAuthenticationTypeEnum()) {
		case NO_AUTHENTICATION:
			NoAuthentication no = (NoAuthentication) server.getAuthentication();
			initEditText(no.getLogin(), R.id.serverConnectionLoginEditText);
			spinner.setSelection(0);
			break;
		case SSH_AUTHENTICATION_PASSWORD:
			SSHAuthenticationPassword ssh = (SSHAuthenticationPassword) server.getAuthentication();
			initEditText(ssh.getLogin(), R.id.serverConnectionLoginEditText);
			try {
				initEditText(ssh.getDecryptPassword(myAuthentication.getKey()), R.id.serverConnectionPasswordEditText);
			} catch (CryptographyException e) {
				Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
			}	
			spinner.setSelection(1);
			break;
		default:
			throw new RuntimeException("authentication type not supported");
		}
	}
	
	private void initEditText(String text, int resource) {
		EditText editText = (EditText) content.findViewById(resource);
		editText.setText(text);
	}
	
	@Override
	public String getTitleView(Activity activity) {
		return activity.getString(R.string.update_server_connection_my_dialog_title);
	}
	
	@Override
	public void onPositiveButton(View v) {
		try {
			if (checkValues()) {
				Long id = updateServerConnection();
				if (id != null) {
					String success = getString(R.string.update_server_connection_my_dialog_success);
					Toast.makeText(activity, success, Toast.LENGTH_LONG).show();
					dialog.dismiss();
					onSuccess(id);
				}
			}
		} catch (ServiceException e) {
			Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
		} catch (CryptographyException e) {
			Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
		}
	}
	
	private Long updateServerConnection() throws ServiceException, CryptographyException {
		return serverService.update(server, getTitle(), getHost(), getPort(),
				getDescription(), getType().getType(), getLogin(), encryptPassword(getPassword()));
	}
}
