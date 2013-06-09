package fr.rjoakim.android.jonetouch.service;

import java.util.List;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import fr.rjoakim.android.jonetouch.bean.Script;
import fr.rjoakim.android.jonetouch.db.DBException;
import fr.rjoakim.android.jonetouch.db.ScriptDB;

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
public class ScriptService {

	private final ScriptDB scriptDB;

	public ScriptService(Context context) {
		this.scriptDB = new ScriptDB(context);
	}

	public long create(String title, String value) throws ServiceException {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value field is required");
		try {
			return scriptDB.insert(title, value);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public void update(long scriptId, String title, String value) throws ServiceException {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value field is required");
		try {
			scriptDB.update(scriptId, title, value);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public void remove(long scriptId) throws ServiceException {
		try {
			scriptDB.delete(scriptId);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public List<Script> list() throws ServiceException {
		try {
			return scriptDB.findAll();
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
}
