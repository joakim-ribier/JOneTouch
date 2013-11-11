package fr.rjoakim.android.jonetouch.async;

import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.ActionScript;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.dialog.ChoiceRestoreDataMyDialog;
import fr.rjoakim.android.jonetouch.dialog.bean.BackupXmlParse;
import fr.rjoakim.android.jonetouch.service.ActionService;
import fr.rjoakim.android.jonetouch.service.ServerService;
import fr.rjoakim.android.jonetouch.service.ServiceException;
import fr.rjoakim.android.jonetouch.util.XMLParserUtilsException;
import fr.rjoakim.android.jonetouch.util.XMLReaderUtils;

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
public class RestoreBackupAsyncTask extends AsyncTask<String, String, String>{

	private final MyAuthentication myAuthentication;
	private final ChoiceRestoreDataMyDialog choiceRestoreDataMyDialog;
	private final ServerService serverService;
	private final ActionService actionService;
	
	public RestoreBackupAsyncTask(ChoiceRestoreDataMyDialog choiceRestoreDataMyDialog,
			MyAuthentication myAuthentication, ServerService serverService,
			ActionService actionService) {
		
		this.choiceRestoreDataMyDialog = choiceRestoreDataMyDialog;
		this.myAuthentication = myAuthentication;
		this.serverService = serverService;
		this.actionService = actionService;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		if (values[1].equals("time")) {
			choiceRestoreDataMyDialog.setTextInfoWithTime(values[0], values[2]);
		} else {
			choiceRestoreDataMyDialog.setTextInfoWithoutTime(values[0], values[2]);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String backup = params[0];
			String key = params[1];
			boolean isGetServers = Boolean.valueOf(params[2]);
			boolean isGetActions = Boolean.valueOf(params[3]);
			boolean isResetAllDatas = Boolean.valueOf(params[4]);
			
			publishProgress("success", "time",
					get(R.string.choice_restore_data_dialog_parameters_info));
			
			publishProgress("info", "notime", get(
					R.string.choice_restore_data_dialog_server_connection_info,
					booleanToYesOrNo(isGetServers)));
			
			publishProgress("info", "notime", get(
					R.string.choice_restore_data_dialog_action_info,
					booleanToYesOrNo(isGetActions)));
			
			publishProgress("info", "notime", get(
					R.string.choice_restore_data_dialog_reset_info,
					booleanToYesOrNo(isResetAllDatas)));
			
			publishProgress("success", "time",
					get(R.string.choice_restore_data_dialog_check_backup_info));
			
			BackupXmlParse backupXmlParse = XMLReaderUtils.parse(
					backup.trim(), myAuthentication, key, isGetServers, isGetActions);
			publishProgress("info", "notime", get(
					R.string.choice_restore_data_dialog_server_connection_info,
					backupXmlParse.getServers().size()));
			
			publishProgress("info", "notime", get(
					R.string.choice_restore_data_dialog_action_info,
					backupXmlParse.getActions().size()));
			
			if (isResetAllDatas) {
				publishProgress("success", "time",
						get(R.string.choice_restore_data_dialog_delete_connection_server_info));
				
				int serversConnectionRemoveNbr = removeAllServerConnection();
				publishProgress("info", "notime", get(
						R.string.choice_restore_data_dialog_server_connection_info,
						serversConnectionRemoveNbr));
				
				publishProgress("success", "time",
						get(R.string.choice_restore_data_dialog_delete_action_info));
				
				int actionsRemoveNbr = removeAllActions();
				publishProgress("info", "notime",  get(
						R.string.choice_restore_data_dialog_action_info,
						actionsRemoveNbr));
			}
			
			publishProgress("success", "time",
					get(R.string.choice_restore_data_dialog_restore_connection_server_info));
			
			Map<Long, Long> mapsOldIdWithNewId =
					insertNewServerConnections(backupXmlParse.getServers());
			publishProgress("info", "notime", get(
					R.string.choice_restore_data_dialog_server_connection_info,
					mapsOldIdWithNewId.size()));
			
			publishProgress("success", "time",
					get(R.string.choice_restore_data_dialog_restore_action_info));
			
			int size = insertNewActions(mapsOldIdWithNewId, backupXmlParse.getActions());
			publishProgress("info", "notime", get(
					R.string.choice_restore_data_dialog_action_info,
					size));
			
		} catch (XMLParserUtilsException e) {
			publishProgress("error", "time",
					get(R.string.choice_restore_data_dialog_failed_info));
			return e.getMessage();
		}
		return "success";
	}

	private String get(int resource) {
		return choiceRestoreDataMyDialog.getString(resource);
	}
	
	private String get(int resource, Object... values) {
		return choiceRestoreDataMyDialog.getString(resource, values);
	}
	
	private String booleanToYesOrNo(boolean value) {
		if (value) {
			return get(R.string.choice_restore_data_dialog_yes_info);
		} else {
			return get(R.string.choice_restore_data_dialog_no_info);
		}
	}
	private int insertNewActions(Map<Long, Long> mapsOldIdWithNewId, List<Action> actions) {
		int size = 0;
		for (Action action: actions) {
			try {
				Long serverId = action.getServerId();
				if (mapsOldIdWithNewId.containsKey(serverId)) {
					
					Long newServerId = mapsOldIdWithNewId.get(serverId);
					Long actionId = actionService.create(
							action.getTitle(), action.getDescription(),
							action.getBackgroundHexColor(), newServerId);
					insertNewActionScripts(action, actionId);
				} else {
					Long actionId = actionService.create(
							action.getTitle(), action.getDescription(),
							action.getBackgroundHexColor());
					insertNewActionScripts(action, actionId);
				}
				size ++;
			} catch (ServiceException e) {
				// try to insert next acions 
			}
		}
		return size;
	}

	private void insertNewActionScripts(Action action, Long actionId)
			throws ServiceException {
		actionService.updateActionScripts(
				actionId,
				Lists.<ActionScript>newArrayList(),
				Lists.<String>newArrayList(action.formatScriptsForExecCMD()));
	}

	private int removeAllActions() {
		try {
			return actionService.remove();
		} catch (ServiceException e) {
			// -- try to insert data
			return -1;
		}
	}

	private Map<Long, Long> insertNewServerConnections(List<Server> servers) {
		Map<Long, Long> mapsOldIdWithNewId = Maps.newHashMap();
		for (Server server: servers) {
			try {
				Long newId = serverService.create(
						server.getTitle(), server.getHost(), server.getPort(),
						server.getDescription(), server.getAuthentication().getAuthenticationTypeEnum(),
						server.getAuthentication().getLogin(),
						server.getAuthentication().getPassword());
				mapsOldIdWithNewId.put(server.getId(), newId);
			} catch (ServiceException e) {
				// try to insert next server connection 
			}
		}
		return mapsOldIdWithNewId;
	}

	private int removeAllServerConnection() {
		try {
			return serverService.removeAll();
		} catch (ServiceException e) {
			// -- try to insert data
			return -1;
		}
	}
}
