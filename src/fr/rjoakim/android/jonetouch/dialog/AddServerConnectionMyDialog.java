package fr.rjoakim.android.jonetouch.dialog;

import java.util.Collection;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.AuthenticationType;
import fr.rjoakim.android.jonetouch.bean.AuthenticationTypeEnum;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.service.AuthenticationTypeService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.util.CryptographyException;
import fr.rjoakim.android.jonetouch.util.CryptographyUtils;

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
public abstract class AddServerConnectionMyDialog extends MyDialog<Long> {

	private final MyAuthentication myAuthentication;

	protected AuthenticationTypeService authenticationTypeService;
	protected ServerService serverService;

	public AddServerConnectionMyDialog(final Activity activity, final ServerService serverService,
			final AuthenticationTypeService authenticationTypeService, MyAuthentication myAuthentication) {
		
		super(activity, R.layout.add_or_update_server_connection_dialog);
		
		this.serverService = serverService;
		this.authenticationTypeService = authenticationTypeService;
		this.myAuthentication = myAuthentication;
		
		fillSpinner(content);
	}
	
	@Override
	public void onFailed() {}
	
	@Override
	public String getTitleView(Activity activity) {
		return activity.getString(R.string.add_connection_dialog_title);
	}
	
	private void fillSpinner(View content) {
		Collection<AuthenticationType> listTypes = authenticationTypeService.list();
		Spinner typeSpinner = (Spinner) content.findViewById(R.id.serverConnectionAuthTypeSpinner);
		ArrayAdapter<AuthenticationType> dataAdapter = new ArrayAdapter<AuthenticationType>(activity,
				R.layout.spinner_layout, Lists.newArrayList(listTypes));
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(dataAdapter);
	}
	
	@Override
	public void onPositiveButton(View v) {
		if (checkValues()) {
			Long id = createNewConnection();
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
	
	protected boolean checkValues() {
		String title = getTitle();
		String host = getHost();
		Integer port = getPort();
		String description = getDescription();
		String login = getLogin();
		if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(host) || port == null
				|| Strings.isNullOrEmpty(description) || Strings.isNullOrEmpty(login)) {
			
			String failed = getString(R.string.incorrect_data);
			Toast.makeText(activity, failed, Toast.LENGTH_LONG).show();
			return false;
		}
		
		AuthenticationType authenticationType = getType();
		String password = getPassword();
		if (authenticationType.getType() == AuthenticationTypeEnum.SSH_AUTHENTICATION_PASSWORD) {
			if (Strings.isNullOrEmpty(password)) {
				String failed = getString(R.string.add_connection_dialog_validation_password);
				Toast.makeText(activity, failed, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}

	private Long createNewConnection() {
		try {
			String host = getHost();
			AuthenticationType type = getType();
			if (!serverService.isExists(host, type.getType())) {
				return serverService.create(getTitle(), host, getPort(),
						getDescription(), type, getLogin(), encryptPassword(getPassword()));
			} else {
				String failed = getString(R.string.add_connection_dialog_validation_connextion_exists);
				Toast.makeText(activity, failed, Toast.LENGTH_LONG).show();
				return null;
			}
		} catch (ServiceException e) {
			Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_LONG).show();
			return null;
		} catch (CryptographyException e) {
			Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
			return null;
		}
	}

	protected String encryptPassword(String password) throws CryptographyException {
		return CryptographyUtils.encrypt(password, myAuthentication.getKey());
	}
	
	protected String getPassword() {
		return getEditTextValue(R.id.serverConnectionPasswordEditText);
	}
	
	protected AuthenticationType getType() {
		Spinner type = (Spinner) content.findViewById(R.id.serverConnectionAuthTypeSpinner);
		return (AuthenticationType) type.getSelectedItem();
	}
	
	protected String getLogin() {
		return getEditTextValue(R.id.serverConnectionLoginEditText);
	}
	
	protected String getDescription() {
		return getEditTextValue(R.id.serverConnectionDescriptionEditText);
	}
	
	protected String getHost() {
		return getEditTextValue(R.id.serverConnectionHostEditText);
	}
	
	protected Integer getPort() {
		String port = getEditTextValue(R.id.serverConnectionPortEditText);
		try {
			return Integer.valueOf(port);	
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	protected String getTitle() {
		return getEditTextValue(R.id.serverConnectionTitleEditText);
	}
}
