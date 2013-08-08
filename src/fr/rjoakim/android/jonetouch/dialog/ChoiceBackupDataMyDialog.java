package fr.rjoakim.android.jonetouch.dialog;

import java.util.List;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.util.XMLWriterUtils;
import fr.rjoakim.android.jonetouch.util.XMLWriterUtilsException;

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
public abstract class ChoiceBackupDataMyDialog extends MyDialog<Void> {

	private ActionService actionService;
	private ServerService serverService;
	private MyAuthentication myAuthentication;

	public ChoiceBackupDataMyDialog(Activity activity, MyAuthentication myAuthentication,
			ServerService serverService, ActionService actionService) {
		
		super(activity, R.layout.choice_backup_data_dialog);
		this.myAuthentication = myAuthentication;
		this.serverService = serverService;
		this.actionService = actionService;
	}

	private RadioGroup getRadioGroupView() {
		return (RadioGroup) content.findViewById(R.id.choiceBackupViewRadioGroup);
	}

	@Override
	public void onFailed() {}
	
	@Override
	public void onSuccess(Void v) {}
	
	@Override
	public String getTitleView(Activity activity) {
		return getString(R.string.choice_backup_data_dialog_title);
	}

	@Override
	public void onPositiveButton(View v) {
		RadioGroup radioGroup = getRadioGroupView();
		int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
		if (checkedRadioButtonId != -1) {
			Toast.makeText(activity, getString(R.string.in_process), Toast.LENGTH_LONG).show();
			CheckBox checkBox = (CheckBox) content.findViewById(R.id.choiceBackupViewIfEncryptPassword);
			chooser(checkedRadioButtonId, checkBox.isChecked());
		}
	}

	private void chooser(int checkedRadioButtonId, boolean isEncryptedPassword) {
		try {
			List<Server> servers = serverService.list();
			List<Action> actions = actionService.list();
			
			String backup = getBackup(isEncryptedPassword, servers, actions);
			
			if (checkedRadioButtonId == R.id.choiceBackupViewRadioGroupButtonChoice1) {
				startEmailApplicationIndent(backup);
				dialog.dismiss();
			} else {
				setClipboardMessage("backup-datas", backup);
				Toast.makeText(activity, getString(R.string.choice_backup_data_dialog_set_clipboard),
						Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		} catch (ServiceException e) {
			Toast.makeText(activity,
					getString(R.string.failed), Toast.LENGTH_LONG).show();
			setClipboardError(e.getMessage());
		} catch (XMLWriterUtilsException e) {
			Toast.makeText(activity,
					getString(R.string.failed), Toast.LENGTH_LONG).show();
			setClipboardError(e.getMessage());
		} catch (NameNotFoundException e) {
			Toast.makeText(activity,
					getString(R.string.failed), Toast.LENGTH_LONG).show();
			setClipboardError(e.getMessage());
		}
	}

	private String getBackup(boolean isEncryptedPassword, List<Server> servers,
			List<Action> actions) throws XMLWriterUtilsException, NameNotFoundException {
		
		PackageInfo packageInfo = activity.getPackageManager(
				).getPackageInfo(activity.getPackageName(), 0);
		
		int versionCode = packageInfo.versionCode;
		String versionName = packageInfo.versionName;
	
		String datas = "";
		if (isEncryptedPassword) {
			datas = XMLWriterUtils.write(servers, actions, versionCode, versionName);
		} else {
			datas = XMLWriterUtils.write(
					myAuthentication.getKey(), servers, actions, versionCode, versionName);
		}
		Log.v("backup", datas);
		return datas;
	}

	private void startEmailApplicationIndent(String xml) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String subject = getString(
				R.string.choice_backup_data_dialog_by_email_subject,
				DateTime.now().toString());
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, xml);
		emailIntent.setType("message/rfc822");
		activity.startActivity(Intent.createChooser(emailIntent,
				getString(R.string.choice_backup_data_dialog_chooser_client)));
	}

	@Override
	public void onNegativeButton(View v) {
		dialog.dismiss();
	}
}
