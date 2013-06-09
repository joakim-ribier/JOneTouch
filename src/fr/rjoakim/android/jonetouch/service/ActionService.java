package fr.rjoakim.android.jonetouch.service;

import java.util.List;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.ActionScript;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.db.ActionDB;
import fr.rjoakim.android.jonetouch.db.DBException;

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
public class ActionService {

	private final ActionDB actionDB;

	public ActionService(Context context) {
		this.actionDB = new ActionDB(context);
	}

	public Long create(String title, String description, Server server) throws ServiceException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(description), "description field is required");
		Preconditions.checkNotNull(server, "server field is required");
		Preconditions.checkArgument(server.getId() != -1, "server field is required");
		try {
			return actionDB.insert(title, description, server);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	public Long create(String title, String description) throws ServiceException {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(description), "description field is required");
		try {
			return actionDB.insert(title, description);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public List<Action> list() throws ServiceException {
		try {
			return actionDB.findAll();
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	public void updateTitle(long id, String title) throws ServiceException {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title field is required");
		try {
			actionDB.updateTitle(id, title);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public void updateDescription(long id, String description) throws ServiceException {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(description), "description field is required");
		try {
			actionDB.updateDescription(id, description);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public void updateServerConnection(long id, Long serverOldId, Long serverNewId) throws ServiceException {
		try {
			actionDB.updateServerConnection(id, serverOldId, serverNewId);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public void remove(ActionScript actionScript) throws ServiceException {
		Preconditions.checkNotNull(actionScript, "actionScript field is required");
		try {
			actionDB.deleteScript(actionScript.getId());
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public void updateActionScripts(long actionId, List<ActionScript> actionScripts,
			List<String> newScripts) throws ServiceException {
		
		try {
			actionDB.updateActionScript(actionId, actionScripts, newScripts);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	public void remove(Action action) throws ServiceException {
		Preconditions.checkNotNull(action, "action field is required");
		try {
			actionDB.delete(action);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
}
