package fr.rjoakim.android.jonetouch.service;

import java.util.List;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import fr.rjoakim.android.jonetouch.bean.AuthenticationTypeEnum;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.db.DBException;
import fr.rjoakim.android.jonetouch.db.ServerDB;

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
public class ServerService {
	
	private final ServerDB serverDB;

	public ServerService(Context context) {
		this.serverDB = new ServerDB(context);
	}

	public Long create(String title, String host, int port, String description, AuthenticationTypeEnum type,
			String login, String password) throws ServiceException {
		
		Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "host field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(description), "description field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login), "login field is required");
		Preconditions.checkNotNull(type, "type is required");
		
		switch (type) {
		case SSH_AUTHENTICATION_PASSWORD:
			Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
			return createServerWithSSHAuthenticationPassword(title, host, port, description, login, password);
		case NO_AUTHENTICATION:
			return createServerWithNoAuthentication(title, host, port, description, login);
		default:
			throw new ServiceException("authentication type not found.");
		}
	}
	
	private Long createServerWithSSHAuthenticationPassword(String title, String host,
			Integer port, String description, String login, String password) throws ServiceException {
		
		try {
			Long id = serverDB.insert(
					title, host, port, description, AuthenticationTypeEnum.SSH_AUTHENTICATION_PASSWORD, login, password);
			if (id != -1l) {
				return id;
			}
			throw new ServiceException("error to create server with ssh authentication password");
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private Long createServerWithNoAuthentication(String title, String host,
			Integer port, String description, String login) throws ServiceException {
		
		try {
			Long id = serverDB.insert(title, host, port, description, AuthenticationTypeEnum.NO_AUTHENTICATION, login);
			if (id != -1l) {
				return id;
			}
			throw new ServiceException("error to create server with no authentication");
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public List<Server> list() throws ServiceException {
		try {
			return serverDB.findAll();
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public Server get(long id) throws ServiceException {
		try {
			return serverDB.findFromId(id);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public boolean isExists(String host, AuthenticationTypeEnum authenticationTypeEnum) throws ServiceException {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "host field is required");
		Preconditions.checkNotNull(authenticationTypeEnum);
		try {
			return serverDB.findByHostAndAuthenticationType(
					host, authenticationTypeEnum) != null;
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	public void remove(Server server) throws ServiceException {
		Preconditions.checkNotNull(server, "server field is required");
		Preconditions.checkNotNull(server.getAuthentication(), "server authentication field is required");
		try {
			serverDB.delete(server.getId(), server.getAuthentication().getAuthenticationTypeEnum());
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	public Long update(Server server, String title, String host, Integer port,
			String description, AuthenticationTypeEnum type, String login,
			String password) throws ServiceException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "host field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(description), "description field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login), "login field is required");
		Preconditions.checkNotNull(type, "type is required");
		try {
			return serverDB.update(server,
					title, host, port, description, type, login, password);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
}
